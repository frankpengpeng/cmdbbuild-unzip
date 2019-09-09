/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.cmdbuild.common.Constants.BASE_DOMAIN_NAME;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryTypeOrAttribute;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.nullToVoid;
import org.springframework.jdbc.core.JdbcTemplate;

public interface DaoService extends QueryBuilderService, AttributeRepository, ClasseRepository, FunctionRepository, DomainRepository {

    static final String ALL = "*", COUNT = "card_count", ROW_NUMBER = "row_number";

    <T> T create(T model);

    <T> T update(T model);

    <T> long createOnly(T model);

    <T> void updateOnly(T model);

    long createOnly(Card card);

    void updateOnly(Card card);

    void delete(Class model, long cardId);

    Card create(Card card);

    Card update(Card card);

    default CMRelation getRelation(long relationId) {
        CMRelation relation = getRelation(BASE_DOMAIN_NAME, relationId);
        return getRelation(relation.getType(), relationId);
    }

    default CMRelation getRelation(String domainId, long relationId) {
        return getRelation(getDomain(domainId), relationId);
    }

    default CMRelation getRelation(Domain domain, long relationId) {
        return selectAll().from(domain).where(ATTR_ID, EQ, relationId).getRelation();
    }

    CMRelation create(CMRelation relation);

    CMRelation update(CMRelation relation);

    void delete(CMRelation relation);

    default CMRelation getRelation(String domain, long sourceId, long targetId) {
        return selectAll().fromDomain(domain).where(ATTR_IDOBJ1, EQ, sourceId).where(ATTR_IDOBJ2, EQ, targetId).getRelation();
    }

    @Nullable
    default CMRelation getRelationOrNull(Domain domain, long sourceId, long targetId) {
        return selectAll().from(domain).where(ATTR_IDOBJ1, EQ, sourceId).where(ATTR_IDOBJ2, EQ, targetId).getRelationOrNull();
    }

    default void deleteRelation(String domain, long sourceId, long targetId) {
        delete(getRelation(domain, sourceId, targetId));
    }

    default CMRelation createRelation(Domain domain, CardIdAndClassName source, CardIdAndClassName target) {
        return create(RelationImpl.builder().withType(domain).withSourceCard(source).withTargetCard(target).build());
    }

    default CMRelation createRelation(String domain, CardIdAndClassName source, CardIdAndClassName target) {
        return createRelation(getDomain(domain), source, target);
    }

    default CMRelation createRelation(Domain domain, CardIdAndClassName source, CardIdAndClassName target, Object... data) {
        return create(RelationImpl.builder().withType(domain).withSourceCard(source).withTargetCard(target).withAttributes(map(data)).build());
    }

    default CMRelation createRelation(String domain, CardIdAndClassName source, CardIdAndClassName target, Object... data) {
        return createRelation(getDomain(domain), source, target, data);
    }

    JdbcTemplate getJdbcTemplate();

    default @Nullable
    ResultRow getByIdOrNull(Class model, long cardId) {
        return getOnlyElement(selectAll().from(model).where(ATTR_ID, EQ, cardId).run(), null);
    }

    default ResultRow getById(Classe classe, long cardId) {
        return getOnlyElement(selectAll().from(classe).where(ATTR_ID, EQ, cardId).run(), null);
    }

    default ResultRow getById(Class model, long cardId) {
        return checkNotNull(getByIdOrNull(model, cardId), "card not found for class = %s id = %s", nullToVoid(model).getName(), cardId);
    }

    default ResultRow getById(String classId, long cardId) {
        return getById(getClasse(classId), cardId);
    }

    default QueryBuilder select(Collection<String> attrs) {
        return query().select(attrs);
    }

    default QueryBuilder select(String... attrs) {
        return query().select(attrs);
    }

    default QueryBuilder selectDistinct(String attr) {
        return query().selectDistinct(attr);
    }

    default Card getCardOrNull(Classe classe, long cardId) {
        return selectAll().from(classe).where(ATTR_ID, WhereOperator.EQ, cardId).getCardOrNull();
    }

    default Card getCard(Classe thisClass, long cardId) {
        return checkNotNull(getCardOrNull(thisClass, cardId), "card not found for class = %s cardId = %s", thisClass, cardId);
    }

    default Card getCard(String classId, long cardId) {
        return getCard(getClasse(classId), cardId);
    }

    default Card getCard(long cardId) {
        return getCard(getCard(getRootClass(), cardId).getType(), cardId);
    }

    void delete(DatabaseRecord record);

    void delete(Object model);

    default void delete(Classe classe, long cardId) {
        delete(getCard(classe, cardId));
    }

    default void delete(String classId, long cardId) {
        delete(getCard(getClasse(classId), cardId));
    }

    List<CMRelation> getServiceRelationsForCard(CardIdAndClassName card);

    PreparedQuery selectFunction(StoredFunction function, List<Object> input, List<StoredFunctionOutputParameter> outputParameters);

    default PreparedQuery selectFunction(String function, List<Object> input, List<StoredFunctionOutputParameter> outputParameters) {
        return selectFunction(getFunctionByName(function), input, outputParameters);
    }

    default PreparedQuery selectFunction(StoredFunction function, List input) {
        return selectFunction(function, input, function.getOutputParameters());
    }

    default PreparedQuery selectFunction(StoredFunction function, Map<String, ?> input) {
        checkArgument(equal(set(function.getInputParameterNames()), input.keySet()), "expected exactly these params = %s, found instead these = %s", function.getInputParameterNames(), input.keySet());
        return selectFunction(function, function.getInputParameterNames().stream().map(input::get).collect(toList()));
    }

    default void clearDataAndDeleteEntryTypeOrAttribute(EntryTypeOrAttribute item) {
        checkNotNull(item);
        if (item instanceof Attribute) {
            deleteAttribute((Attribute) item);
        } else if (item instanceof Classe) {
            Classe classe = (Classe) item;
            selectAll().from(classe).getCards().forEach(this::delete);
            deleteClass(classe);
        } else if (item instanceof Domain) {
            Domain domain = (Domain) item;
            selectAll().from(domain).getRelations().forEach(this::delete);
            deleteDomain(domain);
        } else {
            throw new IllegalArgumentException(format("unable to delete item = %s of type = %s: unsupported delete for this type", item, item.getClass().getName()));
        }
    }

    default Classe getType(CardIdAndClassName card) {
        checkNotNull(card);
        return card instanceof Card ? ((Card) card).getType() : getClasse(card.getClassName());
    }

}
