package org.cmdbuild.dao.view;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.user.OperationUserSupplier;

import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardDefinitionImpl;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import static org.cmdbuild.common.Constants.BASE_PROCESS_CLASS_NAME;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.beans.RelationDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.PostgresService;

@Component("systemDataView")
@Qualifier(SYSTEM_LEVEL_TWO)
@Primary
public class DataViewImpl implements DataView {

    private static final Iterable<? extends WhereClause> TRUE_ONLY_WHERE_CLAUSES = Arrays.asList(trueWhereClause());

    private final PostgresService service;
    private final OperationUserSupplier user;

    public DataViewImpl(PostgresService service, OperationUserSupplier user) {
        this.service = checkNotNull(service);
        this.user = checkNotNull(user);
    }

    @Override
    public Classe getClassOrNull(Long id) {
        return service.getClasseOrNull(id);
    }

    @Override
    public Classe findClasse(String name) {
        return service.getClasseOrNull(name);
    }

//	@Override
//	@Deprecated
//	public Classe findClass(NameAndSchema identifier) {
//		return service.getClasseOrNull(identifier.getName());
//	}
    @Override
    public List<Classe> getUserClasses() {
        return service.getAllClasses().stream().filter(this::userCanListClass).map(this::applyUserPrivileges).collect(toList());
    }

    @Override
    public Classe create(ClassDefinition definition) {
        return service.createClass(definition);
    }

    @Override
    public Classe update(ClassDefinition definition) {
        return service.updateClass(definition);
    }

//	@Override
//	public Attribute createAttribute(AttributeDefinition definition) {
//		return service.createAttribute(definition);
//	}
//
//	@Override
//	public Attribute updateAttribute(AttributeDefinition definition) {
//		return service.updateAttribute(definition);
//	}
//
//	@Override
//	public List<Attribute> updateAttributes(List<AttributeDefinition> attributes) {
//		return service.updateAttributes(attributes);
//	}
    @Override
    public void deleteAttribute(Attribute attribute) {
        service.deleteAttribute(attribute);
    }

    @Override
    public List<Domain> getDomains() {
        return service.getAllDomains();
    }

    @Override
    public Domain findDomain(Long id) {
        return service.getDomainOrNull(id);
    }

    @Override
    public Domain findDomain(String name) {
        return service.getDomainOrNull(name);
    }

//	@Override
//	public Domain findDomain(NameAndSchema identifier) {
//		return service.getDomainOrNull(identifier.getName());
//	}
    @Override
    public Domain create(DomainDefinition definition) {
        return service.createDomain(definition);
    }

    @Override
    public Domain update(DomainDefinition definition) {
        return service.updateDomain(definition);
    }

    @Override
    public void delete(Domain domain) {
        service.deleteDomain(domain);
    }

    @Override
    public List<StoredFunction> findAllFunctions() {
        return service.getAllFunctions();
    }

    @Override
    public StoredFunction findFunctionByName(String name) {
        return service.getFunctionOrNull(name);
    }

    @Override
    public void delete(EntryType entryType) {
        if (entryType == null) {
            return;
        }
        entryType.accept(new CMEntryTypeVisitor() {

            @Override
            public void visit(Classe type) {
                service.deleteClass(type);
            }

            @Override
            public void visit(Domain type) {
                service.deleteDomain(type);
            }

            @Override
            public void visit(CMFunctionCall type) {
                throw new UnsupportedOperationException("function calls cannot be deleted");
            }

        });
    }

    @Override
    public CardDefinitionImpl createCardFor(Classe type) {
        Classe dbType = getClassOrNull(type.getId());
        return CardDefinitionImpl.newInstance(service, dbType);
    }

    @Override
    public CardDefinitionImpl update(Card card) {
        throw new UnsupportedOperationException("BROKEN - TODO");
//		NameAndSchema identifier = card.getType().getIdentifier();
//		Classe dbType = findClass(identifier);
//		CardDefinitionImpl dbCard = CardDefinitionImpl.newInstance(service, dbType, card.getId());
//		for (Entry<String, Object> entry : card.getRawValues()) {
//			dbCard.set(entry.getKey(), entry.getValue());
//		}
//		return dbCard;
    }

    @Override
    public void delete(Card card) {
        service.delete(card);
    }

    @Override
    public QueryResult executeQuery(QuerySpecs querySpecs) {
//		return service.query(querySpecs);
        throw new UnsupportedOperationException("BROKEN - TODO");
    }

    @Override
    public RelationDefinition createRelationFor(Domain domain) {
        throw new UnsupportedOperationException("BROKEN - TODO");
//		Domain dom = service.getDomainOrNull(domain.getId());
//		return LegacyRelationImpl.newInstance(service, dom);
    }

    @Override
    public CMRelation getRelation(Domain domain, long relationId) {
        throw new UnsupportedOperationException("BROKEN - TODO");
//		Classe source = domain.getSourceClass();
//
//		Alias SRC_ALIAS = nameAlias("SRC");
//		Alias DOM_ALIAS = nameAlias("DOM");
//		Alias DST_ALIAS = nameAlias("DST");
//
//		QueryResult result = select(attribute(SRC_ALIAS, ATTR_CODE), attribute(SRC_ALIAS, ATTR_DESCRIPTION), anyAttribute(DOM_ALIAS),
//				attribute(DST_ALIAS, ATTR_CODE), attribute(DST_ALIAS, ATTR_DESCRIPTION)) //
//				.from(source, (SRC_ALIAS)) //
//				.join(anyClass(), (DST_ALIAS), over(domain, (DOM_ALIAS))) //
//				.orderBy(attribute(DST_ALIAS, ATTR_DESCRIPTION), OrderByClause.Direction.ASC) //
//				.count()
//				.where(condition(attribute(DOM_ALIAS, ATTR_ID), eq(relationId)))
//				.run();
//
//		return result.getOnlyRow().getRelation(DOM_ALIAS).getRelation();
    }

    @Override
    public RelationDefinition update(CMRelation relation) {
        throw new UnsupportedOperationException("BROKEN - TODO");
//		return LegacyRelationImpl.newInstance(service, relation);
    }

    @Override
    public void delete(CMRelation relation) {
        service.delete(relation);
    }

    @Override
    public void clear(EntryType type) {
        service.truncate(type);
    }

    // Some method to retrieve system classes
    @Override
    public Classe getActivityClass() {
        return findClasse(BASE_PROCESS_CLASS_NAME);
    }

    @Override
    public Iterable<? extends WhereClause> getAdditionalFiltersFor(EntryType classToFilter) {
        return TRUE_ONLY_WHERE_CLAUSES;
    }

    @Override
    public PostgresService getDbDriver() {
        return service;
    }

    private boolean userCanListClass(Classe classe) {
        return classe.hasServiceListPermission() && user.getPrivileges().hasReadAccess(classe);
    }

    private boolean userCanReadClass(Classe classe) {
        return classe.hasServiceReadPermission() && user.getPrivileges().hasReadAccess(classe);
    }

    private Classe applyUserPrivileges(Classe classe) {
        checkArgument(userCanReadClass(classe), "user % is not authorized to access class %", user.getUsername(), classe.getName());
        return classe;//TODO filter attributes
    }

}
