package org.cmdbuild.extcomponents.custompage;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.authorization.CustomPageAsPrivilegeSubject;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;
import org.cmdbuild.extcomponents.commons.ExtComponentInfoImpl;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.getCodeFromExtComponentData;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.getComponentFile;
import static org.cmdbuild.extcomponents.commons.ExtComponentUtils.parseExtComponentData;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;

@Component
public class CustomPageServiceImpl implements CustomPageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomPageRepository repository;
    private final OperationUserSupplier userStore;
    private final Holder<List<ExtComponentInfo>> allInfo;
    private final CmCache<ExtComponentInfo> infoById, infoByName;
    private final CmCache<byte[]> processedFileByIdPath;
    private final CustomPageConfiguration config;

    public CustomPageServiceImpl(CustomPageRepository repository, OperationUserSupplier userStore, CacheService cacheService, CustomPageConfiguration config) {
        this.repository = checkNotNull(repository);
        this.userStore = checkNotNull(userStore);
        this.config = checkNotNull(config);
        allInfo = cacheService.newHolder("custom_page_all_info");
        infoById = cacheService.newCache("custom_page_info_by_id");
        infoByName = cacheService.newCache("custom_page_info_by_name");
        processedFileByIdPath = cacheService.newCache("custom_page_file_by_id_path");
    }

    private void invalidateCache() {
        allInfo.invalidate();
        infoById.invalidateAll();
        infoByName.invalidateAll();
        processedFileByIdPath.invalidateAll();
    }

    @ConfigListener(CustomPageConfiguration.class)
    public void handleConfigReload() {
        processedFileByIdPath.invalidateAll();
    }

    @Override
    public List<ExtComponentInfo> getAll() {
        return allInfo.get(this::doGetAll);
    }

    private List<ExtComponentInfo> doGetAll() {
        return repository.getAll().stream().map(this::toCustomPageInfo).collect(toList());
    }

    @Override
    public List<ExtComponentInfo> getForCurrentUser() {
        return getAll().stream().filter(this::canRead).collect(toList());
    }

    @Override
    public List<ExtComponentInfo> getActiveForCurrentUser() {
        return getAll().stream().filter(ExtComponentInfo::getActive).collect(toList());
    }

    @Override
    public boolean isAccessibleByName(String code) {
        ExtComponentInfo customPage = doGetByName(code);
        return canRead(customPage);
    }

    @Override
    public ExtComponentInfo get(long id) {
        ExtComponentInfo customPage = doGetById(id);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", id);
        return customPage;
    }

    @Override
    public PrivilegeSubjectWithInfo getCustomPageAsPrivilegeSubjectById(long id) {
        return new CustomPageAsPrivilegeSubject(doGetById(id));
    }

    @Override
    public ExtComponentInfo getByName(String code) {
        ExtComponentInfo customPage = doGetByName(code);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", code);
        return customPage;
    }

    @Override
    public ExtComponentInfo create(byte[] data) {
        CustomPageData customPage = CustomPageDataImpl.builder().withData(data).withName(getCodeFromExtComponentData(data)).build();
        checkArgument(repository.getByNameOrNull(customPage.getName()) == null, "cannot create custom page with name = '%s', a custom page with this name already exists", customPage.getName());
        return create(customPage);
    }

    @Override
    public ExtComponentInfo createOrUpdate(byte[] data) {
        CustomPageData current = repository.getByNameOrNull(getCodeFromExtComponentData(data));
        if (current == null) {
            return create(data);
        } else {
            return update(current.getId(), data);
        }
    }

    @Override
    public ExtComponentInfo update(long id, byte[] data) {
        CustomPageData current = repository.getById(id);
        String name = getCodeFromExtComponentData(data);
        checkArgument(equal(name, current.getName()), "uploaded custom page name = '%s' does not match name = '%s' of this custom page with id = %s", name, current.getName(), id);
        CustomPageData newData = CustomPageDataImpl.copyOf(current).withData(data).build();
        return update(newData);
    }

    @Override
    public ExtComponentInfo update(ExtComponentInfo customPage) {
        CustomPageData data = repository.getById(customPage.getId());
        data = CustomPageDataImpl.copyOf(data)
                .withDescription(customPage.getDescription())//description is the only mutable attr
                .build();
        return update(data);
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
        invalidateCache();
    }

    @Override
    public byte[] getCustomPageFile(String code, String path) {
        CustomPageData customPage = repository.getByName(code);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", code);
        return processedFileByIdPath.get(key(String.valueOf(customPage.getId()), path), () -> doGetCustomPageFile(customPage, path));
    }

    @Override
    public DataHandler getCustomPageData(String code) {
        CustomPageData customPage = repository.getByName(code);
        checkArgument(canRead(customPage), "unable to access custom page = %s: permission denied", code);
        return newDataHandler(customPage.getData(), "application/zip", format("%s.zip", normalize(customPage.getName())));
    }

    private ExtComponentInfo doGetByName(String code) {
        return infoByName.get(code, () -> doReadOne(code));
    }

    private ExtComponentInfo doGetById(long id) {
        return infoById.get(id, () -> doReadOne(id));
    }

    private ExtComponentInfo update(CustomPageData data) {
        logger.info("update custom page = {}", data);
        repository.update(data);
        invalidateCache();
        return get(data.getId());
    }

    private ExtComponentInfo doReadOne(long id) {
        try {
            return getAll().stream().filter((i) -> i.getId() == id).collect(onlyElement());
        } catch (Exception ex) {
            throw new CustomPageException(ex, "custom page not found for id = %s", id);
        }
    }

    private boolean canRead(ExtComponentInfo value) {
        return canRead(new CustomPageAsPrivilegeSubject(value));
    }

    private boolean canRead(CustomPageData value) {
        return canRead(new CustomPageAsPrivilegeSubject(value));
    }

    private boolean canRead(CustomPageAsPrivilegeSubject value) {
        return userStore.getUser().getPrivilegeContext().hasReadAccess(value);
    }

    private ExtComponentInfo create(CustomPageData customPage) {
        logger.info("create custom page = {}", customPage);
        customPage = repository.create(customPage);
        invalidateCache();
        return get(customPage.getId());
    }

    private ExtComponentInfo doReadOne(String code) {
        checkNotBlank(code);
        try {
            return getAll().stream().filter((i) -> i.getName().equals(code)).collect(onlyElement());
        } catch (Exception ex) {
            throw new CustomPageException(ex, "custom page not found for name = %s", code);
        }
    }

    private byte[] doGetCustomPageFile(CustomPageData customPage, String path) {
        return getComponentFile(customPage, customPage.getData(), path, config.isJsCompressionEnabled());
    }

    private ExtComponentInfo toCustomPageInfo(CustomPageData data) {
        return ExtComponentInfoImpl.builder()
                .accept(parseExtComponentData(data.getData()))
                .withId(data.getId())
                .withActive(data.getActive())
                .withDescription(data.getDescription())
                .build();
    }

}
