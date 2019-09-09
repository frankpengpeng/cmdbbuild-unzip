/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

public class StoredFunctionOutputParameterImpl extends StoredFunctionParameterImpl implements StoredFunctionOutputParameter {

    /**
     * defaults to true if not explicitly set to false via Metadata
     */
    private final boolean basedsp;

    public StoredFunctionOutputParameterImpl(String name, CardAttributeType<?> type, @Nullable Boolean basedsp) {
        super(name, type);
        this.basedsp = toBooleanOrDefault(basedsp, true);
    }

    public StoredFunctionOutputParameterImpl(String name, CardAttributeType<?> type) {
        this(name, type, null);
    }

    @Override
    public Boolean getBasedsp() {
        return basedsp;
    }

}
