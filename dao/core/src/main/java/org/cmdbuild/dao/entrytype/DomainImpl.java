package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DomainImpl extends EntryTypeImpl implements Domain {

	private final ClassPermissions permissions;
	private final DomainMetadata metadata;
	private final Classe class1, class2;

	private DomainImpl(DomainImplBuilder builder) {
		super(builder.name, builder.id, builder.attributes);
		this.metadata = checkNotNull(builder.metadata);
		this.class1 = checkNotNull(builder.class1);
		this.class2 = checkNotNull(builder.class2);
		this.permissions = firstNotNull(builder.permissions, ClassPermissionsImpl.builder().withPermissions(metadata.getPermissions()).build());
	}

	@Override
	public DomainMetadata getMetadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "DomainImpl{" + "name=" + getName() + '}';
	}

	@Override
	public Classe getSourceClass() {
		return class1;
	}

	@Override
	public Classe getTargetClass() {
		return class2;
	}

	@Override
	public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
		return permissions.getPermissionsMap();
	}

	public static DomainImplBuilder builder() {
		return new DomainImplBuilder();
	}

	public static DomainImplBuilder copyOf(Domain domain) {
		return builder()
				.withAllAttributes(domain.getAllAttributes())
				.withClass1(domain.getSourceClass())
				.withClass2(domain.getTargetClass())
				.withId(domain.getId())
				.withMetadata(domain.getMetadata())
				.withName(domain.getName())
				.withPermissions(domain);
	}

	public static class DomainImplBuilder implements Builder<DomainImpl, DomainImplBuilder> {

		private final List<AttributeWithoutOwner> attributes = list();
		private ClassPermissions permissions;
		private String name;
		private Long id;
		private DomainMetadata metadata = new DomainMetadataImpl();
		private Classe class1, class2;

		public DomainImplBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public DomainImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public DomainImplBuilder withMetadata(DomainMetadata metadata) {
			this.metadata = metadata;
			return this;
		}

		public DomainImplBuilder withAllAttributes(Collection<? extends AttributeWithoutOwner> attributes) {
			this.attributes.addAll(attributes);
			return this;
		}

		public DomainImplBuilder withAttribute(AttributeWithoutOwner attribute) {
			this.attributes.add(attribute);
			return this;
		}

		public DomainImplBuilder withClass1(Classe dbClass) {
			this.class1 = dbClass;
			return this;
		}

		public DomainImplBuilder withClass2(Classe dbClass) {
			this.class2 = dbClass;
			return this;
		}

		public DomainImplBuilder withPermissions(ClassPermissions permissions) {
			this.permissions = permissions;
			return this;
		}

		@Override
		public DomainImpl build() {
			return new DomainImpl(this);
		}

	}

}
