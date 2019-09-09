/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_ONE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DomainCardinalityUtils {

	private final static BiMap<DomainCardinality, String> MAP = ImmutableBiMap.of(
			ONE_TO_ONE, "1:1",
			ONE_TO_MANY, "1:N",
			MANY_TO_ONE, "N:1",
			MANY_TO_MANY, "N:N"
	);

	public static String serializeDomainCardinality(DomainCardinality cardinality) {
		checkNotNull(cardinality);
		return checkNotBlank(MAP.get(cardinality));
	}

	public static DomainCardinality parseDomainCardinality(String value) {
		return checkNotNull(MAP.inverse().get(checkNotBlank(value, "unable to deserialize domain cardinality from blank value").trim().toUpperCase()), "unable to deserialize domain cardinality from value = %s", value);
	}

}
