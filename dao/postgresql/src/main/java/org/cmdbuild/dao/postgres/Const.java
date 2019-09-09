package org.cmdbuild.dao.postgres;

import static org.cmdbuild.dao.constants.SystemAttributes.*;

public interface Const {

	/*
	 * Constants
	 */
	public enum SystemAttributes {
		Id(ATTR_ID), //
		//		IdClass(ATTR_IDCLASS, SQL_CAST_REGCLASS), //
		//		ClassId1(ATTR_IDCLASS1, SQL_CAST_REGCLASS), //
		//		ClassId2(ATTR_IDCLASS2, SQL_CAST_REGCLASS), //
		IdClass(ATTR_IDCLASS), //
		ClassId1(ATTR_IDCLASS1), //
		ClassId2(ATTR_IDCLASS2), //
		CurrentId(ATTR_CURRENTID), //
		//		DomainId(ATTR_IDDOMAIN, SQL_CAST_REGCLASS), //
		DomainId(ATTR_IDDOMAIN), //
		DomainId1(ATTR_IDOBJ1), //
		DomainId2(ATTR_IDOBJ2), //
		Code(ATTR_CODE), //
		Description(ATTR_DESCRIPTION), //
		Notes(ATTR_NOTES), //
		BeginDate(ATTR_BEGINDATE), //
		EndDate(ATTR_ENDDATE), //
		User(ATTR_USER), //
		Status(ATTR_STATUS),
		// Fake attributes
		DomainQuerySource("_Src"), //
		DomainQueryTargetId("_DstId"), //
		RowNumber("_Row"), //
		RowsCount("_RowsCount"), //
		;

		final String dbName;
//		final String castSuffix;
//
//		SystemAttributes(final String dbName, final String typeCast) {
//			this.dbName = dbName;
//			this.castSuffix = typeCast;
//		}

		SystemAttributes(String dbName) {
//			this(dbName, null);
			this.dbName = dbName;
		}

		public String getDBName() {
			return dbName;
		}

//		public String getCastSuffix() {
//			return castSuffix;
//		}
	}

	static final Object NULL = "NULL";

	static final String OPERATOR_EQ = "=";
	static final String OPERATOR_LT = "<";
	static final String OPERATOR_LT_EQ = "<=";
	static final String OPERATOR_GT = ">";
	static final String OPERATOR_GT_EQ = ">=";
	static final String OPERATOR_ILIKE = "ILIKE";
	static final String OPERATOR_NULL = "IS NULL";
	static final String OPERATOR_IN = "IN";

	static final String BASE_DOMAIN_TABLE_NAME = "Map";
	static final String DOMAIN_PREFIX = "Map_";
	static final String HISTORY_SUFFIX = "_history";

	static final String COMMENT_DESCR = "DESCR";
	static final String COMMENT_MODE = "MODE";
	static final String COMMENT_ACTIVE = "ACTIVE";
//	static final String COMMENT_STATUS = "STATUS";

//	static final String COMMENT_STATUS_ACTIVE = "active";
//	static final String COMMENT_STATUS_NOACTIVE = "noactive";

	static final String COMMENT_SUPERCLASS = "SUPERCLASS";
	static final String COMMENT_TYPE = "TYPE";
	static final String COMMENT_MULTITENANT_MODE = "MTMODE";
	static final String COMMENT_ATTACHMENT_TYPE_LOOKUP = "ATTACHMENT_TYPE_LOOKUP";
	static final String COMMENT_ATTACHMENT_DESCRIPTION_MODE = "ATTACHMENT_DESCRIPTION_MODE";

	static final String COMMENT_USERSTOPPABLE = "USERSTOPPABLE";
	static final String COMMENT_FLOW_STATUS_ATTR = "WFSTATUSATTR";
	static final String COMMENT_FLOW_SAVE_BUTTON_ENABLED = "WFSAVE";

	static final String COMMENT_TYPE_CLASS = "class";
	static final String COMMENT_TYPE_SIMPLECLASS = "simpleclass";
}
