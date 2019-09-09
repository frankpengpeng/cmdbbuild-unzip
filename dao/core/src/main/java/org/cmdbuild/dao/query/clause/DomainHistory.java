package org.cmdbuild.dao.query.clause;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainMetadata;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.dao.graph.ClasseHierarchy;

public class DomainHistory implements Domain, HistoricEntryType<Domain> {

	private final Domain delegate;

	private DomainHistory(Domain current) {
		this.delegate = checkNotNull(current);
	}

	public static Domain history(Domain current) {
		return of(current);
	}

	public static Domain of(Domain current) {
		return new DomainHistory(current);
	}

	@Override
	public Domain getType() {
		return delegate;
	}

	@Override
	public Long getId() {
		return delegate.getId();
	}

	@Override
	public String getName() {
		return delegate.getName() + " HISTORY";
	}

	@Override
	public Map<String, Attribute> getAllAttributesAsMap() {
		return delegate.getAllAttributesAsMap();
	}

	@Override
	public DomainMetadata getMetadata() {
		return delegate.getMetadata();
	}

	@Override
	public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
		return delegate.getPermissionsMap();
	}

//
//	@Override
//	public ClasseHierarchy getSourceClassHierarchy() {
//		return delegate.getSourceClassHierarchy();
//	}
//
//	@Override
//	public ClasseHierarchy getTargetClassHierarchy() {
//		return delegate.getTargetClassHierarchy();
//	}
	@Override
	public Classe getSourceClass() {
		return delegate.getSourceClass();
	}

	@Override
	public Classe getTargetClass() {
		return delegate.getTargetClass();
	}

}
