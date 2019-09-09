/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;

public interface UserClassService {

    ExtendedClass getExtendedUserClass(String classId);

    ExtendedClass getExtendedClass(Classe classe);

    ExtendedClass createClass(ExtendedClassDefinition definition);

    ExtendedClass updateClass(ExtendedClassDefinition definition);

    boolean userCanModify(String classId);

    boolean userCanRead(Classe classe);

    boolean isActiveAnduserCanRead(String classId);

    default void checkUserCanModify(String classId, String message, Object... args) {
        checkArgument(userCanModify(classId), message, args);
    }

    void deleteClass(String classId);

    default List<Classe> getActiveUserClasses() {
        return getAllUserClasses().stream().filter(Classe::isActive).collect(toList());
    }

    List<Classe> getAllUserClasses();

    Classe getUserClass(String classId);

    Attribute getUserAttribute(String classId, String attrId);

    List<Attribute> getUserAttributes(String classId);

    Attribute createAttribute(Attribute attribute);

    Attribute updateAttribute(Attribute attribute);

    void deleteAttribute(String classId, String attrId);

    void updateAttributes(List<Attribute> attributes);

}
