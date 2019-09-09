/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_CLASSES_MODIFY;
import org.cmdbuild.classe.ExtendedClassDefinition.Direction;
import org.cmdbuild.classe.ExtendedClassImpl;
import static org.cmdbuild.classe.access.UserClassUtils.applyPrivilegesToClass;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.formtrigger.FormTriggerService;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.WidgetData;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.AttributeGroupService;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.dao.user.UserDaoHelperService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class UserClassServiceImpl implements UserClassService {

    private final UserDaoHelperService userHelper;
    private final ClasseRepository classeRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeGroupService attributeGroupService;
    private final FormTriggerService formTriggerService;
    private final ContextMenuService contextMenuService;
    private final WidgetService widgetService;
    private final MetadataValidatorService validatorService;

    public UserClassServiceImpl(UserDaoHelperService userHelper, ClasseRepository classeRepository, AttributeRepository attributeRepository, AttributeGroupService attributeGroupService, FormTriggerService formTriggerService, ContextMenuService contextMenuService, WidgetService widgetService, MetadataValidatorService validatorService) {
        this.userHelper = checkNotNull(userHelper);
        this.classeRepository = checkNotNull(classeRepository);
        this.attributeRepository = checkNotNull(attributeRepository);
        this.attributeGroupService = checkNotNull(attributeGroupService);
        this.formTriggerService = checkNotNull(formTriggerService);
        this.contextMenuService = checkNotNull(contextMenuService);
        this.widgetService = checkNotNull(widgetService);
        this.validatorService = checkNotNull(validatorService);
    }

    @Override
    public boolean userCanModify(String classId) {
        return getUserClass(classId).hasServiceModifyPermission();
    }

    @Override
    public boolean userCanRead(Classe classe) {
        return toUserClass(classe).hasServiceReadPermission();
    }

    @Override
    public boolean isActiveAnduserCanRead(String classId) {
        Classe classe = classeRepository.getClasse(classId);
        return classe.isActive() && userCanRead(classe);
    }

    @Override
    public ExtendedClass getExtendedUserClass(String classId) {
        Classe classe = toUserClass(classeRepository.getClasse(classId));
        checkArgument(classe.hasServiceListPermission(), "permission denied: user not authorized to list class = %s", classId);
        return getExtendedClass(classe);
    }

    @Override
    public ExtendedClass getExtendedClass(Classe classe) {
        List<FormTrigger> triggers = formTriggerService.getFormTriggersForClass(classe);
        List<ContextMenuItem> contextMenuItems = contextMenuService.getContextMenuItems(classe);
        List<WidgetData> widgets = widgetService.getAllWidgetsForClass(classe);
        return ExtendedClassImpl.builder()
                .withClasse(classe)
                .withContextMenuItems(contextMenuItems)
                .withFormTriggers(triggers)
                .withWidgets(widgets)
                .withAttributeGroups((List) attributeGroupService.getAttributeGroupsForEntryType(classe))
                .build();

    }

    @Override
    public ExtendedClass createClass(ExtendedClassDefinition definition) {
        checkArgument(userHelper.hasPrivileges(RP_ADMIN_CLASSES_MODIFY), "permission denied: user not authorized to create class");
        Classe classe = classeRepository.createClass(validateClassMetadata(definition.getClassDefinition()));
        return updateClassExtendedStuff(classe, definition, false);
    }

    @Override
    public ExtendedClass updateClass(ExtendedClassDefinition definition) {
        checkUserCanModify(definition.getClassDefinition().getName(), "permission denied: user not authorized to modify class");
        Classe classe = classeRepository.updateClass(validateClassMetadata(definition.getClassDefinition()));
        return updateClassExtendedStuff(classe, definition, true);
    }

    private ExtendedClass updateClassExtendedStuff(Classe classe, ExtendedClassDefinition definition, boolean forUpdate) {
        formTriggerService.updateFormTriggersForClass(classe, definition.getFormTriggers());
        contextMenuService.updateContextMenuItems(classe, definition.getContextMenuItems());
        widgetService.updateWidgetsForClass(classe, definition.getWidgets());
        updateDefaultOrder(classe, definition.getDefaultClassOrdering());
        if (forUpdate) {
            attributeGroupService.updateAttributeGroupsForEntryType(classe, definition.getAttributeGroups());
        }
        return getExtendedUserClass(classe.getName());
    }

    @Override
    public void deleteClass(String classId) {
        Classe classe = getUserClass(classId);
        checkArgument(classe.hasServiceModifyPermission(), "permission denied: user not authorized to drop class");
        formTriggerService.deleteForClass(classe);
        contextMenuService.deleteForClass(classe);
        widgetService.deleteForClass(classe);
        classeRepository.deleteClass(classe);
    }

    @Override
    public List<Classe> getAllUserClasses() {
        return classeRepository.getAllClasses().stream().filter(not(Classe::isProcess)).map(this::toUserClass).filter(Classe::hasServiceListPermission).collect(toList());
    }

    @Override
    public Attribute getUserAttribute(String classId, String attrId) {
        Attribute attribute = getUserClass(classId).getAttribute(attrId);
        checkArgument(attribute.hasServiceListPermission(), "permission denied: user not authorized to read attribute = %s.%s", classId, attrId);
        return attribute;
    }

    @Override
    public List<Attribute> getUserAttributes(String classId) {
        return getUserClass(classId).getServiceAttributes();
    }

    @Override
    public Attribute createAttribute(Attribute attribute) {
//		Classe classe = getUserClass(attribute.getOwner().getName());
        checkUserCanModify(attribute.getOwner().getName(), "permission denied: user not authorized to modify class");
//		checkArgument(classe.getAttributeOrNull(data.getName()) == null, "attribute already present in class = %s for name = %s", classId, data.getName()); TODO move to inner repo
        attributeRepository.createAttribute(attribute);
        return getUserAttribute(attribute.getOwner().getName(), attribute.getName());
    }

    @Override
    public Attribute updateAttribute(Attribute data) {
        Attribute attribute = getUserAttribute(data.getOwner().getName(), data.getName());
        checkArgument(attribute.hasServiceModifyPermission(), "permission denied: user not authorized to modify attribute = %s", attribute);
        attributeRepository.updateAttribute(data);
        return getUserAttribute(data.getOwner().getName(), data.getName());
    }

    @Override
    public void deleteAttribute(String classId, String attrId) {
        Attribute attribute = getUserAttribute(classId, attrId);
        checkArgument(attribute.hasServiceModifyPermission(), "permission denied: user not authorized to delete attribute = %s", attribute);
        attributeRepository.deleteAttribute(attribute);
    }

    @Override
    public void updateAttributes(List<Attribute> attributes) {
        attributes.forEach((data) -> {;
            Attribute attribute = getUserAttribute(data.getOwner().getName(), data.getName());
            checkArgument(attribute.hasServiceModifyPermission(), "permission denied: user not authorized to modify attribute = %s", attribute);
        });
        attributeRepository.updateAttributes(attributes);
    }

    private Classe toUserClass(Classe classe) {
        return applyPrivilegesToClass(userHelper.getRolePrivileges(), userHelper.getPrivilegesForObject(classe).getMaxPrivilegesForSomeRecords(), classe);
    }

    @Override
    public Classe getUserClass(String classId) {
        Classe classe = toUserClass(classeRepository.getClasse(classId));
        checkArgument(classe.hasServiceReadPermission(), "permission denied: user not authorized to read class = %s", classId);
        return classe;
    }

    private void updateDefaultOrder(Classe classe, List<Pair<String, Direction>> defaultOrder) {
        List<Attribute> allAttributes = list(classe.getAllAttributes());
        List<Attribute> attributesPreviouslyUsedInOrder = allAttributes.stream().filter((a) -> a.getClassOrder() != 0).collect(toList());

        List<Attribute> changedAttributes = list();
        for (int index = 0; index < defaultOrder.size(); index++) {
            int newClassOrder = index + 1;
            Pair<String, Direction> record = defaultOrder.get(index);
            Direction order = record.getRight();
            switch (order) {
                case ASC:
                    //nothing to do
                    break;
                case DESC:
                    newClassOrder = -newClassOrder;
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported order direction = " + order);
            }
            Attribute attr = classe.getAttribute(record.getLeft());
            if (attr.getClassOrder() != newClassOrder) {
                changedAttributes.add(AttributeImpl.copyOf(attr).withClassOrderInMeta(newClassOrder).build());
            }
        }

        Set<String> newOrderNames = defaultOrder.stream().map(Pair::getLeft).collect(toSet());
        checkArgument(newOrderNames.size() == defaultOrder.size());

        attributesPreviouslyUsedInOrder.forEach((attr) -> {
            if (!newOrderNames.contains(attr.getName())) {
                changedAttributes.add(AttributeImpl.copyOf(attr).withClassOrderInMeta(0).build());
            }
        });

        attributeRepository.updateAttributes(changedAttributes);
    }

    private ClassDefinition validateClassMetadata(ClassDefinition classDefinition) {
        ClassMetadata metadata = classDefinition.getMetadata();
        validatorService.validateMedata(classDefinition.getName(), metadata);
        return classDefinition;
    }

}
