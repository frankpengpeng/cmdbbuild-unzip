/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_NONE;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_ALL;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CLONE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CREATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_DELETE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_UPDATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import org.cmdbuild.auth.grant.GroupOfPrivilegesImpl.GroupOfPrivilegesImplBuilder;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_IMPORT_EXPORT_TEMPLATE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.privileges.PrivilegeProcessor;
import org.cmdbuild.utils.privileges.PrivilegeProcessorImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class GrantUtils {

    private final static PrivilegeProcessor<GrantPrivilege> GRANT_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<GrantPrivilege>builder()
            .withPrivilegeImplicationMap(map(GP_ALL, set(GP_WRITE),
                    GP_WRITE, set(GP_READ, GP_CREATE, GP_UPDATE, GP_DELETE, GP_CLONE)
            )).build();

    private final static PrivilegeProcessor<GrantAttributePrivilege> GRANT_ATTRIBUTE_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<GrantAttributePrivilege>builder()
            .withNullPrivilegeValues(GAP_NONE)
            .withPrivilegeImplicationMap(map(GAP_WRITE, set(GAP_READ))).build();

    private final static Set<GrantAttributePrivilege> GRANT_ATTRIBUTE_WRITE_DEFAULT_EXPANDED = ImmutableSet.copyOf(expandPrivileges(GAP_WRITE));

    public static String serializeGrantPrivilege(GrantPrivilege grantPrivilege) {
        return grantPrivilege.name().toLowerCase().replaceFirst("gp_", "");
    }

    public static GrantPrivilege parseGrantPrivilege(String value) {
        return parseEnum(value, GrantPrivilege.class);
    }

    public static GrantAttributePrivilege parseGrantAttributePrivilege(String value) {
        switch (checkNotBlank(value).toLowerCase()) {
            case "write":
                return GAP_WRITE;
            case "read":
                return GAP_READ;
            case "none":
                return GAP_NONE;
            default:
                throw runtime("unable to parse grant attr privilege from value = %s", value);
        }
    }

    public static Set<GrantAttributePrivilege> expandPrivileges(GrantAttributePrivilege... privileges) {
        return GRANT_ATTRIBUTE_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static String serializePrivilegedObjectType(PrivilegedObjectType type) {
        switch (type) {
            case POT_CLASS:
                return "Class";
            case POT_CUSTOMPAGE:
                return "CustomPage";
            case POT_FILTER:
                return "Filter";
            case POT_VIEW:
                return "View";
            case POT_REPORT:
                return "Report";
            case POT_IMPORT_EXPORT_TEMPLATE:
                return "IETemplate";
            default:
                throw new UnsupportedOperationException("unsupported priv obj type = " + type);
        }
    }

    public static PrivilegedObjectType parsePrivilegedObjectType(String type) {
        switch (checkNotBlank(type).toLowerCase()) {
            case "ietemplate":
                return POT_IMPORT_EXPORT_TEMPLATE;
            default:
                return parseEnum(type, PrivilegedObjectType.class);
        }
    }

    public static Set<GrantPrivilege> expandPrivileges(GrantPrivilege... privileges) {
        return GRANT_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static GroupOfPrivilegesImplBuilder mergePrivilegeGroups(GroupOfPrivileges... groupOfPrivileges) {
        return mergePrivilegeGroups(list(groupOfPrivileges));
    }

    public static GroupOfPrivilegesImplBuilder mergePrivilegeGroups(Collection<GroupOfPrivileges> groupOfPrivileges) {
        if (groupOfPrivileges.isEmpty()) {
            return GroupOfPrivilegesImpl.copyOf(GroupOfNoPrivileges.INSTANCE);
        } else if (groupOfPrivileges.size() == 1) {
            return GroupOfPrivilegesImpl.copyOf(getOnlyElement(groupOfPrivileges)).withFilter((String) null);
        } else {
            Set<GrantPrivilege> mergedPrivileges = groupOfPrivileges.stream().map(GroupOfPrivileges::getPrivileges).map(GRANT_PRIVILEGE_PROCESSOR::expandPrivileges).flatMap(Set::stream).collect(toSet());
            Set<String> grantAttrPrivKeys = groupOfPrivileges.stream().map(GroupOfPrivileges::getAttributePrivileges).map(CmMapUtils::nullToEmpty).map(Map::entrySet).flatMap(Set::stream).map(Entry::getKey).collect(toSet());
            Map<String, Set<GrantAttributePrivilege>> mergedAttrPrivileges = grantAttrPrivKeys.stream().collect(toMap(identity(), (attr) -> groupOfPrivileges.stream()
                    .map(GroupOfPrivileges::getAttributePrivileges).map(CmMapUtils::nullToEmpty).map((m) -> m.getOrDefault(attr, GRANT_ATTRIBUTE_WRITE_DEFAULT_EXPANDED)).flatMap(Set::stream).collect(toSet())));

            return GroupOfPrivilegesImpl.builder()
                    .withPrivileges(mergedPrivileges)
                    .withAttributePrivileges(mergedAttrPrivileges);
        }
    }
}
