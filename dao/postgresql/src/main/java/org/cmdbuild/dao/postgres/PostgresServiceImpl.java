package org.cmdbuild.dao.postgres;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import javax.annotation.Nullable;

import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.driver.repository.FunctionRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.event.AfterCardCreateEvent;
import org.cmdbuild.event.AfterCardUpdateEvent;
import org.cmdbuild.event.BeforeCardDeleteEvent;
import org.cmdbuild.event.BeforeCardUpdateEvent;
import org.cmdbuild.event.CardEventService;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;

/**
 * postgres driver factory; will cache stuff that does not depends on tenant (ie
 * operation user) at this level, while tenant dependant stuff will be included
 * within {@link PostgresServiceImpl} instances
 *
 */
@Component
@Qualifier(SYSTEM_LEVEL_TWO)
public class PostgresServiceImpl implements PostgresService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final PostgresDatabaseAdapterService databaseAdapterService;

    private final ClasseRepository classeRepository;
    private final DomainRepository domainRepository;
    private final EntryUpdateService entryUpdateService;
    private final FunctionRepository functionRepository;
    private final CardEventService cardEventService;
    private final AttributeRepository attributeRepository;

    public PostgresServiceImpl(EntryUpdateService entryUpdateService, AttributeRepository attributeRepository, FunctionRepository functionRepository, ClasseRepository classeRepository, DomainRepository domainRepository, PostgresDatabaseAdapterService databaseAdapterService, AttributeGroupRepository attributeGroupRepository, CardEventService cardEventService) {
        this.databaseAdapterService = checkNotNull(databaseAdapterService);
        this.cardEventService = checkNotNull(cardEventService);
        this.domainRepository = checkNotNull(domainRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.functionRepository = checkNotNull(functionRepository);
        this.attributeRepository = checkNotNull(attributeRepository);
        this.entryUpdateService = checkNotNull(entryUpdateService);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return databaseAdapterService.getJdbcTemplate();
    }

    @Override
    public Classe getClasseOrNull(long id) {
        return classeRepository.getClasseOrNull(id);
    }

    @Override
    public Domain getDomainOrNull(Long id) {
        return domainRepository.getDomainOrNull(id);
    }

    @Override
    public @Nullable
    Domain getDomainOrNull(@Nullable String localname) {
        return domainRepository.getDomainOrNull(localname);
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        return functionRepository.getAllFunctions();
    }

    @Override
    public @Nullable
    StoredFunction getFunctionOrNull(@Nullable String name) {
        return functionRepository.getFunctionOrNull(name);
    }

    @Override
    public List<Classe> getAllClasses() {
        return classeRepository.getAllClasses();
    }

    @Override
    public Classe getClasseOrNull(String localname) {
        return classeRepository.getClasseOrNull(localname);
    }

    @Override
    public Classe createClass(ClassDefinition definition) {
        return classeRepository.createClass(definition);
    }

    @Override
    public Classe updateClass(ClassDefinition definition) {
        return classeRepository.updateClass(definition);
    }

    @Override
    public void deleteClass(Classe classe) {
        classeRepository.deleteClass(classe);
    }

    @Override
    public Attribute createAttribute(Attribute definition) {
        return attributeRepository.createAttribute(definition);
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> definitions) {
        return attributeRepository.updateAttributes(definitions);
    }

    @Override
    public void deleteAttribute(Attribute attribute) {
        attributeRepository.deleteAttribute(attribute);
    }

    @Override
    public List<Domain> getAllDomains() {
        return domainRepository.getAllDomains();
    }

    @Override
    public Domain createDomain(DomainDefinition definition) {
        return domainRepository.createDomain(definition);
    }

    @Override
    public Domain updateDomain(DomainDefinition definition) {
        return domainRepository.updateDomain(definition);
    }

    @Override
    public void deleteDomain(Domain dbDomain) {
        domainRepository.deleteDomain(dbDomain);
    }

    @Override
    public List<Domain> getDomainsForClasse(Classe classe) {
        return domainRepository.getDomainsForClasse(classe);
    }

    @Override
    public Long create(DatabaseRecord entry) {
        logger.debug("create entry for type = {}", entry.getType());

        Long id = entryUpdateService.executeInsertAndReturnKey(entry);
        logger.debug("created entry for type = {} id = {}", entry.getType(), id);
        if (entry instanceof Card) {
            cardEventService.post((AfterCardCreateEvent) () -> (Card) entry);
        }
        return id;
    }

    @Override
    public void update(DatabaseRecord entry) {
        logger.debug("updating entry with type = {} id = {}", entry.getType(), entry.getId());
        if (entry instanceof Card) {
            cardEventService.post(new BeforeCardUpdateEvent() {
                @Override
                public Card getNextCard() {
                    return (Card) entry;//TODO
                }

                @Override
                public Card getCurrentCard() {
                    return (Card) entry;
                }
            });
        }
        entryUpdateService.executeUpdate(entry);
        if (entry instanceof Card) {
            cardEventService.post(new AfterCardUpdateEvent() {
                @Override
                public Card getPreviousCard() {
                    return (Card) entry;//TODO
                }

                @Override
                public Card getCurrentCard() {
                    return (Card) entry;//TODO
                }
            });
        }
    }

    @Override
    public void delete(DatabaseRecord entry) {
        logger.debug("deleting record with id = {} for type = {}", entry.getId(), entry.getType());
        if (entry instanceof Card) {
            cardEventService.post((BeforeCardDeleteEvent) () -> (Card) entry);
        }
        databaseAdapterService.getJdbcTemplate().queryForObject("SELECT _cm3_card_delete(?::regclass,?)", Object.class, entryTypeToSqlExpr(entry.getType()), checkNotNull(entry.getId(), "unable to delete entry = %s, missing entry id", entry));
    }

    @Override
    public void truncate(EntryType type) {
        logger.info("clearing type = {}", type);
        // truncate all subclasses as well
        databaseAdapterService.getJdbcTemplate().execute(format("TRUNCATE TABLE %s CASCADE", entryTypeToSqlExpr(type)));
    }

}
