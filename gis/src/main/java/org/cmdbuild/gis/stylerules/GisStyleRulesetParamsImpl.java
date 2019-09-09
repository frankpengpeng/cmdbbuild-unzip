/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public class GisStyleRulesetParamsImpl implements GisStyleRulesetParams {

    private final GisStyleRulesetAnalysisType analysisType;

    private GisStyleRulesetParamsImpl(GisStyleRulesetParamsImplBuilder builder) {
        this.analysisType = builder.analysisType;
    }

    @JsonCreator
    public GisStyleRulesetParamsImpl(@Nullable @JsonProperty("analysisType") String analysisType) {
        this.analysisType = parseEnumOrNull(analysisType, GisStyleRulesetAnalysisType.class);
    }

    @Nullable
    @Override
    public GisStyleRulesetAnalysisType getAnalysisType() {
        return analysisType;
    }

    @Nullable
    @JsonProperty("analysisType")
    public String getAnalysisTypeStr() {
        return serializeEnum(analysisType);
    }

    public static GisStyleRulesetParamsImplBuilder builder() {
        return new GisStyleRulesetParamsImplBuilder();
    }

    public static GisStyleRulesetParamsImplBuilder copyOf(GisStyleRulesetParams source) {
        return new GisStyleRulesetParamsImplBuilder()
                .withAnalysisType(source.getAnalysisType());
    }

    public static class GisStyleRulesetParamsImplBuilder implements Builder<GisStyleRulesetParamsImpl, GisStyleRulesetParamsImplBuilder> {

        private GisStyleRulesetAnalysisType analysisType;

        public GisStyleRulesetParamsImplBuilder withAnalysisType(GisStyleRulesetAnalysisType analysisType) {
            this.analysisType = analysisType;
            return this;
        }

        @Override
        public GisStyleRulesetParamsImpl build() {
            return new GisStyleRulesetParamsImpl(this);
        }

    }
}
