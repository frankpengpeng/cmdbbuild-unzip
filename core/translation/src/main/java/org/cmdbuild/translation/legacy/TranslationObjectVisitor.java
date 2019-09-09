package org.cmdbuild.translation.legacy;

import org.cmdbuild.translation.legacy.ClassAttributeDescription;
import org.cmdbuild.translation.legacy.ClassAttributeGroup;
import org.cmdbuild.translation.legacy.ClassDescription;
import org.cmdbuild.translation.legacy.DomainAttributeDescription;
import org.cmdbuild.translation.legacy.DomainDescription;
import org.cmdbuild.translation.legacy.DomainDirectDescription;
import org.cmdbuild.translation.legacy.DomainInverseDescription;
import org.cmdbuild.translation.legacy.DomainMasterDetailLabel;
import org.cmdbuild.translation.legacy.FilterDescription;
import org.cmdbuild.translation.legacy.InstanceName;
import org.cmdbuild.translation.legacy.LookupDescription;
import org.cmdbuild.translation.legacy.MenuItemDescription;
import org.cmdbuild.translation.legacy.ReportDescription;
import org.cmdbuild.translation.legacy.ViewDescription;
import org.cmdbuild.translation.legacy.WidgetLabel;

public interface TranslationObjectVisitor {

	void visit(ClassAttributeDescription classAttributeDescription);

	void visit(ClassAttributeGroup classAttributeGroup);

	void visit(ClassDescription classDescription);

	void visit(DomainAttributeDescription domainAttributeDescription);

	void visit(DomainDescription domainDescription);

	void visit(DomainDirectDescription domainDirectDescription);

	void visit(DomainInverseDescription domainInverseDescription);

	void visit(DomainMasterDetailLabel domainMasterDetailDescription);

	void visit(FilterDescription filterDescription);

	void visit(InstanceName instanceNameTranslation);

	void visit(LookupDescription lookupDescription);

	void visit(MenuItemDescription menuItemDescription);

	void visit(NullTranslationObject translationObject);

	void visit(ReportDescription reportDescription);

	void visit(ViewDescription viewDescription);

	void visit(WidgetLabel widgetLabel);

}
