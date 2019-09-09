package org.cmdbuild.extcomponents.custompage;

import java.util.List;
import javax.activation.DataHandler;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;

public interface CustomPageService {

    List<ExtComponentInfo> getAll();

    List<ExtComponentInfo> getForCurrentUser();

    List<ExtComponentInfo> getActiveForCurrentUser();

    ExtComponentInfo get(long id);

    PrivilegeSubjectWithInfo getCustomPageAsPrivilegeSubjectById(long id);

    ExtComponentInfo create(byte[] data);

    ExtComponentInfo createOrUpdate(byte[] data);

    ExtComponentInfo update(long id, byte[] data);

    ExtComponentInfo update(ExtComponentInfo customPage);

    byte[] getCustomPageFile(String code, String path);

    DataHandler getCustomPageData(String code);

    void delete(long id);

    ExtComponentInfo getByName(String code);

    boolean isAccessibleByName(String code);

}
