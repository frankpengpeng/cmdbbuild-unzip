/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload.ftp;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.toIntExact;
import static java.util.Collections.emptyList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.user.UnencryptedPasswordSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.FtpServiceConfiguration;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.services.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import org.cmdbuild.services.PostStartup;
import org.cmdbuild.services.PreShutdown;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

@Component
@MinionComponent(name = "Ftp Service", configBean = FtpServiceConfiguration.class)
public class EasyuploadFtpService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasyuploadService easyuploadService;
    private final UserRoleService userRepository;
    private final UnencryptedPasswordSupplier passwordSupplier;
    private final FtpServiceConfiguration config;

    private FtpServer server;

    public EasyuploadFtpService(EasyuploadService easyuploadService, UserRoleService userRepository, UnencryptedPasswordSupplier passwordSupplier, FtpServiceConfiguration configuration) {
        this.easyuploadService = checkNotNull(easyuploadService);
        this.userRepository = checkNotNull(userRepository);
        this.passwordSupplier = checkNotNull(passwordSupplier);
        this.config = checkNotNull(configuration);
    }

    public MinionStatus getServiceStatus() {
        if (!config.isEnabled()) {
            return MS_DISABLED;
        } else if (server != null) {
            return MS_READY;
        } else {
            return MS_NOTRUNNING;
        }
    }

    @PostStartup
    public void startFtpServerIfEnabled() {
        if (config.isEnabled()) {
            try {
                logger.info("start ftp service");
                FtpServerFactory serverFactory = new FtpServerFactory();
                ListenerFactory listener = new ListenerFactory();

                listener.setPort(config.getPort());
                serverFactory.addListener("default", listener.createListener());

                PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
                UserManager userManager = userManagerFactory.createUserManager();

                Role role = userRepository.getGroupWithNameOrNull("SuperUser");
                List<UserData> users = role == null ? emptyList() : userRepository.getAllWithRole(role.getId());//TODO fix this, use superuser permissions and not roles
                users.forEach(rethrowConsumer(u -> {//TODO improve this, do not load all users at startup

                    BaseUser user = new BaseUser();
                    user.setName(u.getUsername());
                    user.setPassword(passwordSupplier.getUnencryptedPassword(LoginUserIdentity.build(u.getUsername())));//TODO improve this
                    user.setHomeDirectory("/");
//        List<Authority> authorities = new ArrayList<Authority>();
//        authorities.add(new WritePermission());
//        user.setAuthorities(authorities);
                    userManager.save(user);
                }));

                serverFactory.setUserManager(userManager);

                serverFactory.setFileSystem(new EasyuploadFileSystemFactory());

                server = serverFactory.createServer();
                server.start();
            } catch (FtpException ex) {
                stopSafe();
                throw runtime(ex);
            }
        } else {
            logger.debug("ftp server not enabled, skipping");
        }
    }

    @PreShutdown
    public void stopSafe() {
        if (server != null) {
            try {
                logger.info("stop ftp service");
                server.stop();
            } catch (Exception ex) {
                logger.warn("error stopping ftp server", ex);
            }
            server = null;
        }
    }

    @ConfigListener(FtpServiceConfiguration.class)
    public void reload() {
        stopSafe();
        startFtpServerIfEnabled();
    }

    private class EasyuploadFileSystemFactory implements FileSystemFactory {

        @Override
        public FileSystemView createFileSystemView(User user) throws FtpException {
            return new EasyuploadFileSystemView();
        }
    }

    private class EasyuploadFileSystemView implements FileSystemView {

        private String currDir = "/";

        @Override
        public FtpFile getHomeDirectory() {
            return new EasyuploadFtpDirectory("/");
        }

        @Override
        public FtpFile getWorkingDirectory() {
            return new EasyuploadFtpDirectory(currDir);
        }

        @Override
        public FtpFile getFile(String file) {
            logger.debug("get file =< {} >", file);
            checkNotBlank(file);
            file = normalizePath(new File(currDir, file).getPath());
            if (equal(".", file)) {
                file = currDir;
            } else if (equal("..", file)) {
                file = normalizePath(new File(currDir).getParent());
            }
            EasyuploadItem item = easyuploadService.getByPathOrNull(file);
            if (item == null) {
                return isBlank(FilenameUtils.getExtension(file)) ? new EasyuploadFtpDirectory(file) : new EasyuploadFtpFile(file);//TODO improve this
            } else {
                return new EasyuploadFtpFile(item);
            }
        }

        @Override
        public boolean changeWorkingDirectory(String dir) {
            currDir = dir;//TODO normalize (??)
            return true;
        }

        @Override
        public boolean isRandomAccessible() {
            return true;
        }

        @Override
        public void dispose() {
            //nothing to do
        }

    }

    private class EasyuploadFtpDirectory implements FtpFile {

        private final String path;

        public EasyuploadFtpDirectory(String directory) {
            this.path = normalizePath(directory);
            logger.debug("create handle for dir =< {} >", path);
        }

        @Override
        public String getAbsolutePath() {
            return path;
        }

        @Override
        public String getName() {
            return FilenameUtils.getName(path);
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean doesExist() {
            return true;
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public boolean isRemovable() {
            return false;
        }

        @Override
        public String getOwnerName() {
            return "system";
        }

        @Override
        public String getGroupName() {
            return "system";
        }

        @Override
        public int getLinkCount() {
            return 3;//?
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public boolean setLastModified(long time) {
            logger.error("setLastModified operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public Object getPhysicalFile() {
            return path;
        }

        @Override
        public boolean mkdir() {
            logger.error("mkdir operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public boolean delete() {
            logger.error("delete operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public boolean move(FtpFile destination) {
            logger.error("move operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public List<? extends FtpFile> listFiles() {
            logger.debug("list files for dir = {}", path);
            return listOf(FtpFile.class).accept(l -> {
                List<String> dirs = easyuploadService.getSubdirsForDir(path);
                logger.debug("list of sub dirs = {}", dirs);
                dirs.stream().map(d -> new EasyuploadFtpDirectory(d)).forEach(l::add);

                List<EasyuploadItemInfo> files = easyuploadService.getByDir(path);
                logger.debug("list files = {}", files);
                files.stream().map(f -> new EasyuploadFtpFile(f)).forEach(l::add);
            });
        }

        @Override
        public OutputStream createOutputStream(long offset) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream createInputStream(long offset) throws IOException {
            throw new UnsupportedOperationException();
        }

    }

    private class EasyuploadFtpFile implements FtpFile {

        private final String path;
        private EasyuploadItemInfo item;

        public EasyuploadFtpFile(EasyuploadItemInfo item) {
            this.path = item.getPath();
            this.item = item;
            logger.debug("create handle for file =< {} > with item =< {} >", path, item);
        }

        public EasyuploadFtpFile(String path) {
            this.path = normalizePath(path);
            logger.debug("create handle for new file =< {} >", path);
        }

        @Override
        public String getAbsolutePath() {
            return path;
        }

        @Override
        public String getName() {
            return FilenameUtils.getName(path);
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean doesExist() {
            return item != null;
        }

        @Override
        public long getSize() {
            return item.getSize();
        }

        @Override
        public String getOwnerName() {
            return "system";
        }

        @Override
        public String getGroupName() {
            return "system";
        }

        @Override
        public int getLinkCount() {
            return 1;
        }

        @Override
        public long getLastModified() {
            return 0;//TODO
        }

        @Override
        public boolean setLastModified(long time) {
            return false;//TODO
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public boolean isRemovable() {
            return true;
        }

        @Override
        public boolean delete() {
            try {
                easyuploadService.delete(item.getId());
                return true;
            } catch (Exception ex) {
                logger.error("error deleting file = {} path =< {} >", item, path, ex);
                return false;
            }
        }

        @Override
        public boolean move(final FtpFile dest) {
            logger.error("move operation not supported yet");
            return false;
        }

        @Override
        public boolean mkdir() {
            logger.error("mkdir operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public String getPhysicalFile() {
            return path;
        }

        @Override
        public List<FtpFile> listFiles() {
            return null;
        }

        @Override
        public OutputStream createOutputStream(long offset) throws IOException {

            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    byte[] data = toByteArray();

                    if (offset > 0) {
                        byte[] currentData = item == null ? new byte[]{} : easyuploadService.getById(item.getId()).getContent(),
                                fullData = new byte[toIntExact(offset) + data.length];
                        for (int i = 0; i < offset; i++) {
                            if (i < currentData.length) {
                                fullData[i] = currentData[i];
                            } else {
                                fullData[i] = 0;
                            }
                        }
                        for (int i = 0; i < data.length; i++) {
                            fullData[i + toIntExact(offset)] = data[i];
                        }
                        data = fullData;
                    }

                    if (item != null) {
                        item = easyuploadService.update(item.getId(), data, item.getDescription());
                    } else {
                        item = easyuploadService.create(newDataHandler(data), path, null);
                    }
                }

            };
        }

        @Override
        public InputStream createInputStream(long offset) throws IOException {
            return new ByteArrayInputStream(easyuploadService.getById(item.getId()).getContent(), toIntExact(offset), Integer.MAX_VALUE);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FtpFile) {
                return equal(((FtpFile) obj).getAbsolutePath(), getAbsolutePath());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }
}
