/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype.attributetype;

public class AttributeUtils {

    public static CardAttributeType newAttributeTypeByName(AttributeTypeName name) {
        switch (name) {
            case BOOLEAN:
                return new BooleanAttributeType();
            case BYTEARRAY:
                return new ByteArrayAttributeType();
            case CHAR:
                return new CharAttributeType();
            case DATE:
                return new DateAttributeType();
            case DECIMAL:
                return new DecimalAttributeType();
            case DOUBLE:
                return new DoubleAttributeType();
            case REGCLASS:
                return RegclassAttributeType.INSTANCE;
            case FOREIGNKEY:
                return new ForeignKeyAttributeType((String) null);
            case INET:
                return new IpAddressAttributeType(null);
            case INTEGER:
                return new IntegerAttributeType();
            case LONG:
                return LongAttributeType.INSTANCE;
            case LOOKUP:
                return new LookupAttributeType((String) null);
//			case REFERENCE:
//				return new ReferenceAttributeType((String) null);
            case STRING:
                return new StringAttributeType();
            case JSON:
                return JsonAttributeType.INSTANCE;
            case STRINGARRAY:
                return new StringArrayAttributeType();
            case BYTEAARRAY:
                return new ByteaArrayAttributeType();
            case TEXT:
                return new TextAttributeType();
            case TIME:
                return new TimeAttributeType();
            case TIMESTAMP:
                return new DateTimeAttributeType();
            case UNKNOWN:
            default:
//				return UndefinedAttributeType.undefined();
                throw new UnsupportedOperationException("unsupported attribute type = " + name);
        }
    }

}
