/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.widget.model.WidgetData;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.utils.lang.Builder;

public class ExtendedClassImpl implements ExtendedClass {

    private final Classe classe;
    private final List<FormTrigger> formTriggers;
    private final List<ContextMenuItem> contextMenuItems;
    private final List<WidgetData> widgets;
    private final List<AttributeGroupInfo> attributeGroups;

    private ExtendedClassImpl(ExtendedClassImplBuilder builder) {
        this.classe = checkNotNull(builder.classe);
        this.formTriggers = ImmutableList.copyOf(builder.formTriggers);
        this.contextMenuItems = ImmutableList.copyOf(builder.contextMenuItems);
        this.widgets = ImmutableList.copyOf(builder.widgets);
        this.attributeGroups = ImmutableList.copyOf(builder.attributeGroups);
    }

    @Override
    public Classe getClasse() {
        return classe;
    }

    @Override
    public List<FormTrigger> getFormTriggers() {
        return formTriggers;
    }

    @Override
    public List<ContextMenuItem> getContextMenuItems() {
        return contextMenuItems;
    }

    @Override
    public List<WidgetData> getWidgets() {
        return widgets;
    }

    @Override
    public List<AttributeGroupInfo> getAttributeGroups() {
        return attributeGroups;
    }

    public static ExtendedClassImplBuilder builder() {
        return new ExtendedClassImplBuilder();
    }

    public static ExtendedClassImplBuilder copyOf(ExtendedClass source) {
        return new ExtendedClassImplBuilder()
                .withClasse(source.getClasse())
                .withFormTriggers(source.getFormTriggers())
                .withContextMenuItems(source.getContextMenuItems())
                .withWidgets(source.getWidgets())
                .withAttributeGroups(source.getAttributeGroups());
    }

    public static ExtendedClassImplBuilder copyOf(ExtendedClassData source) {
        return new ExtendedClassImplBuilder()
                .withFormTriggers(source.getFormTriggers())
                .withContextMenuItems(source.getContextMenuItems())
                .withWidgets(source.getWidgets())
                .withAttributeGroups(source.getAttributeGroups());
    }

    public static class ExtendedClassImplBuilder implements Builder<ExtendedClassImpl, ExtendedClassImplBuilder> {

        private Classe classe;
        private List<FormTrigger> formTriggers;
        private List<ContextMenuItem> contextMenuItems;
        private List<WidgetData> widgets;
        private List<AttributeGroupInfo> attributeGroups;

        public ExtendedClassImplBuilder withClasse(Classe classe) {
            this.classe = classe;
            return this;
        }

        public ExtendedClassImplBuilder withFormTriggers(List<FormTrigger> formTriggers) {
            this.formTriggers = formTriggers;
            return this;
        }

        public ExtendedClassImplBuilder withContextMenuItems(List<ContextMenuItem> contextMenuItems) {
            this.contextMenuItems = contextMenuItems;
            return this;
        }

        public ExtendedClassImplBuilder withWidgets(List<WidgetData> widgets) {
            this.widgets = widgets;
            return this;
        }

        public ExtendedClassImplBuilder withAttributeGroups(List<AttributeGroupInfo> attributeGroups) {
            this.attributeGroups = attributeGroups;
            return this;
        }

        @Override
        public ExtendedClassImpl build() {
            return new ExtendedClassImpl(this);
        }

    }
}
