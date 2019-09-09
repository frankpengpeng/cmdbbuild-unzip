/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Domain;

public class DomainWithSource {

	public final Long domainId;
	public final String querySource;

	private DomainWithSource(Long domainId, @Nullable String querySource) {
		this.domainId = checkNotNull(domainId);
		this.querySource = querySource;
	}

	@Override
	public String toString() {
		return String.format("%s.%s", domainId, querySource);
	}

	public static DomainWithSource create(Domain domain) {
		return new DomainWithSource(domain.getId(), null);
	}

	public static @Nullable
	DomainWithSource create(@Nullable Long domainId, @Nullable String querySource) {
		DomainWithSource dom;
		if (domainId != null && querySource != null) {
			dom = new DomainWithSource(domainId, querySource);
		} else {
			dom = null;
		}
		return dom;
	}
}
