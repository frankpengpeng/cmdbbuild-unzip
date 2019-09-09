package org.cmdbuild.dao.query.clause;

import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.PermissionScope;

public class ClassHistory implements Classe, HistoricEntryType<Classe> {

	private final Classe current;

	private ClassHistory(Classe current) {
		checkNotNull(current);
		checkArgument(!current.isSuperclass(), "cannot access class history for superclass %s (a superclass does not have a class history!)", current.getName());
		this.current = current;
	}

	public static Classe history(Classe current) {
		return of(current);
	}

	public static Classe of(Classe current) {
		return new ClassHistory(current);
	}

	@Override
	public void accept(final CMEntryTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
		return current.getPermissionsMap();
	}

	@Override
	public Classe getType() {
		return current;
	}

	@Override
	public String getPrivilegeId() {
		return current.getPrivilegeId();
	}

	@Override
	public Long getId() {
		return current.getId();
	}

	@Override
	public String getName() {
		return current.getName() + " HISTORY";
	}

	@Override
	public List<String> getAncestors() {
		return current.getAncestors();
	}

	@Override
	public ClassMetadata getMetadata() {
		return current.getMetadata();
	}

	@Override
	public Map<String, Attribute> getAllAttributesAsMap() {
		return current.getAllAttributesAsMap();
	}

}
