package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.lang.String.format;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.inner.WorkflowTypesConverter;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.cmdbuild.workflow.WorkflowTypeDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.workflow.core.utils.WfWidgetUtils;
import org.cmdbuild.workflow.inner.WfReference;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.view.DataView;

/**
 *
 * @deprecated to be replaced with RiverTypeConverter
 */
@Deprecated
@Component
public class SharkTypesConverter implements WorkflowTypesConverter {

	private final IntegerAttributeType ID_TYPE = new IntegerAttributeType();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataView dataView;
	private final LookupRepository lookupStore;

	public SharkTypesConverter(@Qualifier(SYSTEM_LEVEL_TWO) DataView dataView, LookupRepository lookupStore) {
		this.dataView = checkNotNull(dataView);
		this.lookupStore = checkNotNull(lookupStore);
	}

	@Override
	public Object fromWorkflowType(Object value) {
		return WfWidgetUtils.convertValueForWidget(value);
	}

	@Override
	public Object toWorkflowType(CardAttributeType<?> attributeType, Object obj) {
		if (attributeType != null) {
			Object value;
			if (obj instanceof Lookup) {
				value = Lookup.class.cast(obj).getId();
			} else if (obj instanceof WfReference) {
				value = WfReference.class.cast(obj).getId();
			} else {
				value = obj;
			}
			return cmdbValueToFlowValue(attributeType, rawToSystem(attributeType, value));
		} else if (obj != null) {
			return convertSharkOnlyVariable(obj);
		} else {
			return null;
		}
	}

	private Object cmdbValueToFlowValue(CardAttributeType<?> attributeType, Object obj) {
		ToSharkTypesConverter converter = new ToSharkTypesConverter(obj);
		attributeType.accept(converter);
		return converter.output;
	}

	/**
	 * Tries to convert the values that are present only in Shark, so the
	 * attributeType is null. We can only guess the type when the value is not
	 * null.
	 *
	 * @param native value
	 * @return shark value
	 */
	private Object convertSharkOnlyVariable(Object obj) {
		if (obj instanceof Integer) {
			return Integer.class.cast(obj).longValue();
		} else if (obj instanceof DateTime) {
			return convertDateTime(obj);
		} else if (obj instanceof BigDecimal) {
			return BigDecimal.class.cast(obj).doubleValue();
		} else if (obj instanceof Lookup) {
			Lookup lookup = Lookup.class.cast(obj);
			return convertLookup(lookup);
		} else if (obj instanceof WfReference) {
			WfReference reference = WfReference.class.cast(obj);
			return convertReference(reference);
		} else if (obj instanceof WfReference[]) {
			WfReference[] references = WfReference[].class.cast(obj);
			return convertReferenceArray(references);
		} else {
			return obj;
		}
	}

	private Object convertDateTime(Object obj) {
		long instant = DateTime.class.cast(obj).getMillis();
		return new Date(instant);
	}

	private LookupType convertLookup(Lookup lookup) {
		return (lookup == null) ? WorkflowTypeDefaults.defaultLookup() : convertLookup(lookup.getId());
	}

	private LookupType convertLookup(Long id) {
		logger.debug("getting lookup with id '{}'", id);
		if (id == null) {
			return WorkflowTypeDefaults.defaultLookup();
		}
		try {
			org.cmdbuild.lookup.Lookup lookupFromStore = lookupStore.getById(id);
			LookupType lookupType = new LookupType();
			lookupType.setType(lookupFromStore.getType().getName());
			lookupType.setId(objectIdToInt(lookupFromStore.getId()));
			lookupType.setCode(lookupFromStore.getCode());
			lookupType.setDescription(lookupFromStore.getDescription());
			return lookupType;
		} catch (Exception e) {
			logger.error("cannot get lookup", e);
			return WorkflowTypeDefaults.defaultLookup();
		}
	}

	private ReferenceType[] convertReferenceArray(WfReference[] references) {
		List<ReferenceType> referenceTypes = newArrayListWithCapacity(references.length);
		for (WfReference reference : references) {
			referenceTypes.add(convertReference(reference));
		}
		return referenceTypes.toArray(new ReferenceType[referenceTypes.size()]);
	}

	private ReferenceType convertReference(WfReference reference) {
		return convertReference(reference.getId(), reference.getClassName());
	}

	private ReferenceType convertReference(Long id) {
		return convertReference(id, null);
	}

	private ReferenceType convertReference(Long id, String className) {
		if (id == null) {
			return WorkflowTypeDefaults.defaultReference();
		}
		try {
			// TODO improve performances
			String _className = (className == null) ? Constants.BASE_CLASS_NAME : className;
			Classe queryClass = dataView.findClasse(_className);
			Card card = dataView.select(attribute(queryClass, org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION)) //
					.from(queryClass) //
					.where(condition(attribute(queryClass, org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID), eq(id))) //
					.limit(1) //
					.skipDefaultOrdering() //
					.run() //
					.getOnlyRow() //
					.getCard(queryClass);
			ReferenceType referenceType = new ReferenceType();
			referenceType.setId(objectIdToInt(card.getId()));
			referenceType.setIdClass(objectIdToInt(card.getType().getId()));
			referenceType.setDescription(String.class.cast(card.getDescription()));
			return referenceType;
		} catch (Exception e) {
			logger.error("cannot get reference", e);
			return WorkflowTypeDefaults.defaultReference();
		}
	}

	/**
	 * Converts an object identifier to the integer representation or -1 if it
	 * is null (YEAH!)
	 *
	 * @return legacy id standard
	 */
	private int objectIdToInt(Long objId) {
		Integer id = rawToSystem(ID_TYPE, objId);
		if (id == null) {
			return -1;
		} else {
			return id;
		}
	}

	private class ToSharkTypesConverter implements CMAttributeTypeVisitor {

		private final Object input;
		private Object output;

		private ToSharkTypesConverter(Object input) {
			this.input = input;
		}

		@Override
		public void visit(BooleanAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultBoolean();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(CharAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultString();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(DateTimeAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultDate();
			} else {
				output = convertDateTime(input);
			}
		}

		@Override
		public void visit(DateAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultDate();
			} else {
				output = convertDateTime(input);
			}
		}

		@Override
		public void visit(DecimalAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultDouble();
			} else {
				output = BigDecimal.class.cast(input).doubleValue();
			}
		}

		@Override
		public void visit(DoubleAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultDouble();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(RegclassAttributeType attributeType) {
			notifyIllegalType(attributeType);
		}

		@Override
		public void visit(ForeignKeyAttributeType attributeType) {
			throwIllegalType(attributeType);
		}

		@Override
		public void visit(IntegerAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultInteger();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(LongAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultInteger();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(IpAddressAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultString();
			} else {
				output = input.toString();
			}
		}

		@Override
		public void visit(LookupAttributeType attributeType) {
			output = (input == null) ? null : convertLookup(rawToSystem(attributeType, input).getId());
		}

		@Override
		public void visit(ReferenceAttributeType attributeType) {
			output = (input == null) ? null : convertReference(rawToSystem(attributeType, input).getId());
		}

		@Override
		public void visit(StringArrayAttributeType attributeType) {
			notifyIllegalType(attributeType);
		}

		@Override
		public void visit(StringAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultString();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(TextAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultString();
			} else {
				output = input;
			}
		}

		@Override
		public void visit(TimeAttributeType attributeType) {
			if (input == null) {
				output = WorkflowTypeDefaults.defaultDate();
			} else {
				output = convertDateTime(input);
			}
		}

		private void notifyIllegalType(CardAttributeType<?> attributeType) {
			logger.warn(illegalTypeMessage(attributeType));
		}

		private void throwIllegalType(CardAttributeType<?> attributeType) {
			throw new IllegalArgumentException(illegalTypeMessage(attributeType));
		}

		private String illegalTypeMessage(CardAttributeType<?> attributeType) {
			return format("cannot send a '%s' to Shark", attributeType.getClass());
		}

	}
}
