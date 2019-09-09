package org.cmdbuild.data2.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.model.data.EntryTypeBean;
import org.joda.time.DateTime;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.beans.Card;
//import org.cmdbuild.dao.entrytype.Attribute;

public class Utils {

//	public static abstract class CMAttributeWrapper implements AttributeDefinition {
//
//		private final org.cmdbuild.dao.entrytype.Attribute delegate;
//
//		protected CMAttributeWrapper(final org.cmdbuild.dao.entrytype.Attribute delegate) {
//			this.delegate = delegate;
//		}
//
//		@Override
//		public String getName() {
//			return delegate.getName();
//		}
//
//		@Override
//		public EntryType getOwner() {
//			return delegate.getOwner();
//		}
//
//		@Override
//		public CardAttributeType<?> getType() {
//			return delegate.getType();
//		}
//
//		@Override
//		public String getDescription() {
//			return delegate.getDescription();
//		}
//
//		@Override
//		public String getDefaultValue() {
//			return delegate.getDefaultValue();
//		}
//
//		@Override
//		public boolean showInGrid() {
//			return delegate.showInGrid();
//		}
//
//		@Override
//		public boolean isMandatory() {
//			return delegate.isMandatory();
//		}
//
//		@Override
//		public boolean isUnique() {
//			return delegate.isUnique();
//		}
//
//		@Override
//		public Boolean isActive() {
//			return delegate.isActive();
//		}
//
//		@Override
//		public AttributePermissionMode getMode() {
//			return delegate.getMode();
//		}
//
//		@Override
//		public Integer getIndex() {
//			return delegate.getIndex();
//		}
//
//		@Override
//		public String getGroupId() {
//			return delegate.getGroupNameOrNull();
//		}
//
//		@Override
//		public Integer getClassOrder() {
//			return delegate.getClassOrder();
//		}
//
//		@Override
//		public String getEditorType() {
//			return delegate.getEditorType();
//		}
//
//		@Override
//		public String getFilter() {
//			return delegate.getFilter();
//		}
//
////		@Override
////		public String getForeignKeyDestinationClassName() {
////			return delegate.getForeignKeyDestinationClassName();
////		}
//	}

	public static ClassDefinition definitionForNew(final EntryTypeBean entryType, final Classe parentClass) {
		throw new UnsupportedOperationException("unsupported method, use class definition directly");
//		return ClassDefinitionImpl.builder()
//				.withName(entryType.getName())
//				.withDescription(entryType.getDescription())
//				.withParent(parentClass)
//				.withSuperclass(entryType.isSuperClass())
//				.withType(entryType.isHoldingHistory() ? ClassType.STANDARD : ClassType.SIMPLE)
//				.withActive(entryType.isActive())
//				.withAttachmentTypeLookup(parentClass == null ? null : parentClass.getAttachmentTypeLookupTypeOrNull())
//				.withAttachmentDescriptionMode(parentClass == null ? AttachmentDescriptionMode.MANDATORY : parentClass.getAttachmentDescriptionMode())
//				//TODO userstoppable, for processes
//				.build();
	}

	public static ClassDefinition definitionForExisting(EntryTypeBean entryType, Classe existingClass) {
		throw new UnsupportedOperationException("unsupported method, use class definition directly");
//		return ClassDefinitionImpl.from(existingClass)
//				.withDescription(entryType.getDescription())
//				.withActive(entryType.isActive())
//				//TODO userstoppable, for processes
//				.build();
	}

	public static ClassDefinition unactive(Classe existingClass) {
		throw new UnsupportedOperationException("unsupported method, use class definition directly");
//		return ClassDefinitionImpl.from(existingClass)
//				.withActive(false)
//				//				//TODO userstoppable, for processes
//				//				final boolean userStoppable;
//				//				if (existingClass instanceof PlanClasse) {
//				//					userStoppable = PlanClasse.class.cast(existingClass).isUserStoppable();
//				//				} else {
//				//					userStoppable = false;
//				//				}
//				//				return userStoppable;
//				.build();
	}

//	public static AttributeDefinition definitionForNew(final Attribute attribute, final EntryType owner) {
//		return new AttributeDefinition() {
//
//			@Override
//			public String getName() {
//				return attribute.getName();
//			}
//
//			@Override
//			public EntryType getOwner() {
//				return owner;
//			}
//
//			@Override
//			public CardAttributeType<?> getType() {
//				return attribute.getType();
//			}
//
//			@Override
//			public String getDescription() {
//				return attribute.getDescription();
//			}
//
//			@Override
//			public String getDefaultValue() {
//				return attribute.getDefaultValue();
//			}
//
//			@Override
//			public Boolean isDisplayableInList() {
//				return attribute.isDisplayableInList();
//			}
//
//			@Override
//			public boolean isMandatory() {
//				return attribute.isMandatory();
//			}
//
//			@Override
//			public boolean isUnique() {
//				return attribute.isUnique();
//			}
//
//			@Override
//			public Boolean isActive() {
//				return attribute.isActive();
//			}
//
//			@Override
//			public AttributePermissionMode getMode() {
//				return attribute.getMode();
//			}
//
//			@Override
//			public Integer getIndex() {
//				return attribute.getIndex();
//			}
//
//			@Override
//			public String getGroupId() {
//				return attribute.getGroup();
//			}
//
//			@Override
//			public Integer getClassOrder() {
//				return attribute.getClassOrder();
//			}
//
//			@Override
//			public String getEditorType() {
//				return attribute.getEditorType();
//			}
//
//			@Override
//			public String getFilter() {
//				return attribute.getFilter();
//			}
//
//			@Override
//			public String getForeignKeyDestinationClassName() {
//				return attribute.getForeignKeyDestinationClassName();
//			}
//
//		};
//	}
//
//	public static AttributeDefinition definitionForExisting(final org.cmdbuild.dao.entrytype.Attribute delegate, final Attribute attribute) {
//		return new CMAttributeWrapper(delegate) {
//
//			@Override
//			/*
//			 * Some info about the attributes are stored in the CMAttributeType,
//			 * so for String, Lookup and Decimal use the new attribute type to
//			 * update these info
//			 */
//			public CardAttributeType<?> getType() {
//				if (delegate.getType() instanceof LookupAttributeType
//						&& attribute.getType() instanceof LookupAttributeType) {
//					return attribute.getType();
//				} else if (delegate.getType() instanceof StringAttributeType
//						&& attribute.getType() instanceof StringAttributeType) {
//					return attribute.getType();
//				} else if (delegate.getType() instanceof DecimalAttributeType
//						&& attribute.getType() instanceof DecimalAttributeType) {
//					return attribute.getType();
//				} else if (delegate.getType() instanceof IpAddressAttributeType
//						&& attribute.getType() instanceof IpAddressAttributeType) {
//					return attribute.getType();
//				} else {
//					return delegate.getType();
//				}
//			}
//
//			@Override
//			public String getDescription() {
//				return notEqual(super.getDescription(), attribute.getDescription()) ? attribute.getDescription() : null;
//			}
//
//			@Override
//			public Boolean isDisplayableInList() {
//				return notEqual(super.isDisplayableInList(), attribute.isDisplayableInList()) ? attribute
//						.isDisplayableInList() : null;
//			}
//
//			@Override
//			public boolean isMandatory() {
//				return attribute.isMandatory();
//			}
//
//			@Override
//			public boolean isUnique() {
//				return attribute.isUnique();
//			}
//
//			@Override
//			public Boolean isActive() {
//				return notEqual(super.isActive(), attribute.isActive()) ? attribute.isActive() : null;
//			}
//
//			@Override
//			public AttributePermissionMode getMode() {
//				return notEqual(super.getMode(), attribute.getMode()) ? attribute.getMode() : null;
//			}
//
//			@Override
//			public String getGroupId() {
//				return notEqual(super.getGroupId(), attribute.getGroup()) ? attribute.getGroup() : null;
//			}
//
//			@Override
//			public Integer getIndex() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Integer getClassOrder() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getEditorType() {
//				return notEqual(super.getEditorType(), attribute.getEditorType()) ? attribute.getEditorType() : null;
//			}
//
//			@Override
//			public String getFilter() {
//				return notEqual(super.getFilter(), attribute.getFilter()) ? attribute.getFilter() : null;
//			}
//
//		};
//	}
//
//	public static AttributeDefinition withIndex(final org.cmdbuild.dao.entrytype.Attribute delegate, final int index) {
//		return new CMAttributeWrapper(delegate) {
//
//			@Override
//			public String getDescription() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Boolean isDisplayableInList() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public boolean isMandatory() {
//				return delegate.isMandatory();
//			}
//
//			@Override
//			public boolean isUnique() {
//				return delegate.isUnique();
//			}
//
//			@Override
//			public Boolean isActive() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public AttributePermissionMode getMode() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getGroupId() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Integer getClassOrder() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getEditorType() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getFilter() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Integer getIndex() {
//				return index;
//			}
//
//		};
//	}
//	public static AttributeDefinition withClassOrder(final org.cmdbuild.dao.entrytype.Attribute delegate, final int classOrder) {
//		return new CMAttributeWrapper(delegate) {
//
//			@Override
//			public String getDescription() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Boolean showInGrid() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public boolean isMandatory() {
//				return delegate.isMandatory();
//			}
//
//			@Override
//			public boolean isUnique() {
//				return delegate.isUnique();
//			}
//
//			@Override
//			public Boolean isActive() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public AttributePermissionMode getMode() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getGroupId() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getEditorType() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public String getFilter() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Integer getIndex() {
//				// not changed
//				return null;
//			}
//
//			@Override
//			public Integer getClassOrder() {
//				return classOrder;
//			}
//
//		};
//	}
//
//	public static AttributeDefinition unactive(final org.cmdbuild.dao.entrytype.Attribute delegate) {
//		return new CMAttributeWrapper(delegate) {
//
//			@Override
//			public Boolean isActive() {
//				return false;
//			}
//
//		};
//	}
//
//	/**
//	 *
//	 * @param domain
//	 * @param class1
//	 * @param class2
//	 * @return
//	 * @deprecated remove for 30
//	 */
//	@Deprecated
//	public static DomainDefinition definitionForNew(final DomainBean domain, final Classe class1, final Classe class2) {
//		return new DomainDefinition() {
//
//			@Override
//			public NameAndSchema getIdentifier() {
//				return fromName(domain);
//			}
//
//			@Override
//			public Long getId() {
//				return null;
//			}
//
//			@Override
//			public Classe getSourceClass() {
//				return class1;
//			}
//
//			@Override
//			public Classe getTargetClass() {
//				return class2;
//			}
//
//			@Override
//			public String getDescription() {
//				return domain.getDescription();
//			}
//
//			@Override
//			public String getDirectDescription() {
//				return domain.getDirectDescription();
//			}
//
//			@Override
//			public String getInverseDescription() {
//				return domain.getInverseDescription();
//			}
//
//			@Override
//			public String getCardinality() {
//				return domain.getCardinality();
//			}
//
//			@Override
//			public boolean isMasterDetail() {
//				return domain.isMasterDetail();
//			}
//
//			@Override
//			public String getMasterDetailDescription() {
//				return domain.getMasterDetailDescription();
//			}
//
//			@Override
//			public boolean isActive() {
//				return domain.isActive();
//			}
//
//			@Override
//			public Collection<String> getDisabledSourceDescendants() {
//				return domain.getDisabled1();
//			}
//
//			@Override
//			public Collection<String> getDisabledTargetDescendants() {
//				return domain.getDisabled2();
//			}
//
//			@Override
//			public int getIndexForSource() {
//				return DEFAULT_INDEX_VALUE; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//			@Override
//			public int getIndexForTarget() {
//				return DEFAULT_INDEX_VALUE; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//			@Override
//			public String getMasterDetailFilter() {
//				return null; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//		};
//	}
//
//	private static NameAndSchema fromName(final DomainBean domain) {
//		return cmIdentifier(domain.getName(),
//				null // TODO must be done ASAP
//		);
//	}
//
//	/**
//	 *
//	 * @param domainWithChanges
//	 * @param existing
//	 * @return
//	 * @deprecated scheduled for removal in 30
//	 */
//	@Deprecated
//	public static DomainDefinition definitionForExisting(final DomainBean domainWithChanges, final Domain existing) {
//		return new DomainDefinition() {
//
//			@Override
//			public NameAndSchema getIdentifier() {
//				return existing.getIdentifier();
//			}
//
//			@Override
//			public Long getId() {
//				return existing.getId();
//			}
//
//			@Override
//			public Classe getSourceClass() {
//				return existing.getSourceClass();
//			}
//
//			@Override
//			public Classe getTargetClass() {
//				return existing.getTargetClass();
//			}
//
//			@Override
//			public String getDescription() {
//				return domainWithChanges.getDescription();
//			}
//
//			@Override
//			public String getDirectDescription() {
//				return domainWithChanges.getDirectDescription();
//			}
//
//			@Override
//			public String getInverseDescription() {
//				return domainWithChanges.getInverseDescription();
//			}
//
//			@Override
//			public String getCardinality() {
//				return existing.getCardinality();
//			}
//
//			@Override
//			public boolean isMasterDetail() {
//				return domainWithChanges.isMasterDetail();
//			}
//
//			@Override
//			public String getMasterDetailDescription() {
//				return domainWithChanges.getMasterDetailDescription();
//			}
//
//			@Override
//			public boolean isActive() {
//				return domainWithChanges.isActive();
//			}
//
//			@Override
//			public Collection<String> getDisabledSourceDescendants() {
//				return domainWithChanges.getDisabled1();
//			}
//
//			@Override
//			public Collection<String> getDisabledTargetDescendants() {
//				return domainWithChanges.getDisabled2();
//			}
//
//			@Override
//			public int getIndexForSource() {
//				return DEFAULT_INDEX_VALUE; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//			@Override
//			public int getIndexForTarget() {
//				return DEFAULT_INDEX_VALUE; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//			@Override
//			public String getMasterDetailFilter() {
//				return null; //TODO this is broken, but this method is scheduled for removal so we'll leave it like that
//			}
//
//		};
//	}

	/**
	 * Read from the given card the attribute with the given name. If null
	 * return an empty String, otherwise cast the object to string
	 *
	 * @param card
	 * @param attributeName
	 * @return
	 *
	 * @deprecated use {@link StringUtils} functions.
	 */
	@Deprecated
	public static String readString(final Card card, final String attributeName) {
		final Object value = card.get(attributeName);
		final String output;
		if (value == null) {
			output = EMPTY;
		} else {
			output = (String) value;
		}
		return output;
	}

	/**
	 * Read from the given card the attribute with the given name. If null
	 * return an false, otherwise cast the object to boolean
	 *
	 * @param card
	 * @param attributeName
	 * @return
	 *
	 * @deprecated use {@link BooleanUtils} functions.
	 */
	@Deprecated
	public static boolean readBoolean(final Card card, final String attributeName) {
		final Object value = card.get(attributeName);
		final boolean output;
		if (value == null) {
			output = false;
		} else {
			output = (Boolean) value;
		}
		return output;
	}

	/**
	 * Read from the given card the attribute with the given name. If null
	 * return null, otherwise try to cast the object to org.joda.time.DateTime
	 *
	 * @param card
	 * @param attributeName
	 * @return
	 */
	public static DateTime readDateTime(final Card card, final String attributeName) {
		final Object value = card.get(attributeName);

		if (value instanceof DateTime) {
			return (DateTime) value;
		} else if (value instanceof java.sql.Date) {
			return new DateTime(((java.util.Date) value).getTime());
		}

		return null;
	}

	private Utils() {
		// prevents instantiation
	}

}
