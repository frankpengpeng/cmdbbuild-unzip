package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.cmdbuild.common.Constants.DATETIME_TWO_DIGIT_YEAR_FORMAT;
import static org.cmdbuild.common.Constants.DATE_FOUR_DIGIT_YEAR_FORMAT;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.math.BigDecimal;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import static java.util.Collections.emptyMap;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.function.Function.identity;
import javax.annotation.Nullable;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.AttributePermission;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.report.ReportException;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

/**
 * 
 * Wrapper for user-defined Jasper Parameter
 * 
 * AVAILABLE FORMATS FOR JRPARAMETER NAME 1) reference: "label.class.attribute"
 * - ie: User.Users.Description 2) lookup: "label.lookup.lookuptype" - ie:
 * Brand.Lookup.Brands 3) simple: "label" - ie: My parameter
 * 
 * Notes: - The description property overrides the label value - Reference or
 * lookup parameters will always be integers while simple parameters will match
 * original parameter class - All custom parameters are required; set a property
 * (in iReport) with name="required" and value="false" to override
 * 
 */
public abstract class ReportParameter {

    private static final String DUMMY_REPORT_PARAM_OWNER = "dummy_report_param_owner";

    private final JRParameter jrParameter;
    private final String simpleName;
    private final CardAttributeType<?> cardAttributeType;

    private ReportParameter(JRParameter jrParameter, String simpleName, CardAttributeType<?> cardAttributeType) {
        this.jrParameter = checkNotNull(jrParameter);
        this.simpleName = checkNotBlank(simpleName);
        this.cardAttributeType = checkNotNull(cardAttributeType);
    }

    public String getDefaultValue() {
        if (jrParameter.getDefaultValueExpression() != null) {
            GroovyShell shell = new GroovyShell();
            Script sc = shell.parse(jrParameter.getDefaultValueExpression().getText());
            Object result = sc.run();

            if (result != null) {
                if (jrParameter.getValueClass() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FOUR_DIGIT_YEAR_FORMAT);
                    return sdf.format(result);
                } else if (jrParameter.getValueClass() == Timestamp.class || jrParameter.getValueClass() == Time.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_TWO_DIGIT_YEAR_FORMAT);
                    return sdf.format(result);
                }
                return result.toString();
            }
        }
        return null;
    }

    public boolean hasDefaultValue() {
        return (jrParameter.getDefaultValueExpression() != null
                && jrParameter.getDefaultValueExpression().getText() != null && !jrParameter
                .getDefaultValueExpression().getText().equals(""));
    }

    public JRParameter getJrParameter() {
        return jrParameter;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getName() {
        return jrParameter.getName();
    }

    public String getDescription() {
        return defaultString(jrParameter.getDescription(), getSimpleName());
    }

    public @Nullable
    Object parseValue(@Nullable Object value) {
        return convert(value, jrParameter.getValueClass());
    }

    public CardAttributeType<?> getCardAttributeType() {
        return cardAttributeType;
    }

    public Attribute toCardAttribute() {
        return new ReportAttribute(cardAttributeType, this);
    }

    public boolean isRequired() {
        return new CustomProperties(jrParameter.getPropertiesMap()).isRequired();
    }

    public boolean isOptional() {
        return !isRequired();
    }

    public static ReportParameter dummyParameter(String name) {
        return new FakeReportParameter(name);
    }

    // create the right subclass
    public static ReportParameter parseJrParameter(JRParameter jrParameter) {
        if (jrParameter == null || jrParameter.getName() == null || jrParameter.getName().equals("")) {
            throw new ReportException("invalid parameter format for param = %s", jrParameter);
        }

        String iReportParamName = jrParameter.getName();
        ReportParameter output;
        if (!iReportParamName.contains(".")) {
            CustomProperties customProperties = new CustomProperties(jrParameter.getPropertiesMap());
            if (customProperties.hasLookupType()) {
                String lookupType = customProperties.getLookupType();
                output = new LookupReportParameterImpl(jrParameter, iReportParamName, lookupType);
            } else if (customProperties.hasTargetClass()) {
                String targetClass = customProperties.getTargetClass();
                output = new ReferenceReportParameter(jrParameter, iReportParamName, targetClass);
            } else {
                output = new SimpleReportParameter(jrParameter, iReportParamName);
            }
        } else {
            // LEGACY stuff
            if (!iReportParamName.matches("[\\w\\s]*\\.\\w*\\.[\\w\\s]*")) {
                throw new ReportException("invalid parameter format for legacy param = %s", jrParameter);
            }
            String[] split = iReportParamName.split("\\.");
            if (split[1].equalsIgnoreCase("lookup")) {
                output = new LookupReportParameterImpl(jrParameter, split[0], split[2]);
            } else {
                output = new ReferenceReportParameter(jrParameter, split[0], split[1]);
            }
        }
        return output;
    }

    private static CardAttributeType<?> cardAttrTypeFromJrParameter(JRParameter jrParameter) {
        Class valueClass = jrParameter.getValueClass();
        if (String.class.equals(valueClass)) {
            return new StringAttributeType(100);
        } else if (set(Integer.class, Long.class, Short.class, BigDecimal.class, Number.class).contains(valueClass)) {
            return new IntegerAttributeType();
        } else if (Date.class.equals(valueClass)) {
            return new DateAttributeType();
        } else if (Timestamp.class.equals(valueClass)) {
            return new DateTimeAttributeType();
        } else if (Time.class.equals(valueClass)) {
            return new TimeAttributeType();
        } else if (set(Double.class, Float.class).contains(valueClass)) {
            return new DoubleAttributeType();
        } else if (Boolean.class.equals(valueClass)) {
            return new BooleanAttributeType();
        } else {
            throw new ReportException("invalid value class = %s", valueClass);
        }
    }

    private static class LookupReportParameterImpl extends ReportParameter {

        public LookupReportParameterImpl(JRParameter jrParameter, String simpleName, String lookupType) {
            super(jrParameter, simpleName, new LookupAttributeType(lookupType));
        }

    }

    private static class ReferenceReportParameter extends ReportParameter {

        protected ReferenceReportParameter(JRParameter jrParameter, String simpleName, String className) {
            super(jrParameter, simpleName, new ForeignKeyAttributeType(className));
        }

    }

    private static class FakeReportParameter extends ReportParameter {

        protected FakeReportParameter(String name) {
            super(new JRDesignParameter() {
                {
                    setName(name);
                    setDescription(name);
                }
            }, name, new StringAttributeType(100));
        }

        @Override
        public boolean isRequired() {
            return false;
        }

    }

    private static class SimpleReportParameter extends ReportParameter {

        protected SimpleReportParameter(JRParameter jrParameter, String simpleName) {
            super(jrParameter, simpleName, cardAttrTypeFromJrParameter(jrParameter));
        }

    }

    private static class CustomProperties {

        private static final String FILTER = "filter",
                FILTER_PREFIX = FILTER + ".",
                REQUIRED = "required",
                LOOKUP_TYPE = "lookupType",
                TARGET_CLASS = "targetClass";

        private final Map<String, String> map;

        public CustomProperties(JRPropertiesMap delegate) {
            this.map = list(delegate.getPropertyNames()).stream().collect(toMap(identity(), delegate::getProperty)).immutable();
        }

        public boolean isRequired() {
            return toBoolean(map.get(REQUIRED));
        }

        @Nullable
        public String getFilter() {
            return map.get(FILTER);
        }

        public Map<String, String> getFilterParameters() {
            return map.entrySet().stream().filter(e -> e.getKey().startsWith(FILTER_PREFIX)).collect(toMap(e -> trim(e.getKey().substring(FILTER_PREFIX.length())), Entry::getValue));
        }

        public boolean hasLookupType() {
            return isNotBlank(getLookupType());
        }

        @Nullable
        public String getLookupType() {
            return map.get(LOOKUP_TYPE);
        }

        public boolean hasTargetClass() {
            return isNotBlank(getTargetClass());
        }

        @Nullable
        public String getTargetClass() {
            return map.get(TARGET_CLASS);
        }

    }

    private static class ReportAttribute implements Attribute {

//		private static final EntryType UNSUPPORTED = newProxy(EntryType.class, unsupported("should not be used on this fake object"));
        private final CardAttributeType<?> type;
        private final ReportParameter rp;

        public ReportAttribute(CardAttributeType<?> type, ReportParameter rp) {
            this.type = type;
            this.rp = rp;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public EntryType getOwner() {
            return ClasseImpl.builder()
                    //					.withHierarchy(DummyClasseHierarchy.INSTANCE)
                    .withId(0l)
                    .withName(DUMMY_REPORT_PARAM_OWNER)
                    .build();//TODO check this
        }

        @Override
        public CardAttributeType<?> getType() {
            return type;
        }

        @Override
        public String getName() {
            return rp.getName();
        }

        @Override
        public String getDescription() {
            return rp.getDescription();
        }

        @Override
        public boolean hasNotServiceListPermission() {
            return false;
        }

        @Override
        public boolean isInherited() {
            return false;
        }

        @Override
        public boolean showInGrid() {
            return true;
        }

        @Override
        public boolean isMandatory() {
            return rp.isRequired();
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public AttributePermissionMode getMode() {
            return AttributePermissionMode.APM_WRITE;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public String getDefaultValue() {
            if (rp.hasDefaultValue()) {
                return rp.getDefaultValue();
            }
            return EMPTY;
        }

        @Override
        @Nullable
        public AttributeGroupInfo getGroupOrNull() {
            return null;
        }

        @Override
        public int getClassOrder() {
            return 0;
        }

        @Override
        public String getEditorType() {
            return EMPTY;
        }

        @Override
        public String getFilter() {
            return new CustomProperties(rp.getJrParameter().getPropertiesMap()).getFilter();
        }

        @Override
        public String getForeignKeyDestinationClassName() {
            return EMPTY;
        }

        @Override
        public AttributeMetadata getMetadata() {
            return new AttributeMetadataImpl(emptyMap());//TODO fix this
        }

        @Override
        public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
            return getMetadata().getPermissionMap();
        }
    }
}
