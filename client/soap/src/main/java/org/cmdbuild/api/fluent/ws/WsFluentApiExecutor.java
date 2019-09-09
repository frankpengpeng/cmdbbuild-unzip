package org.cmdbuild.api.fluent.ws;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.api.fluent.ws.ClassAttribute.classAttribute;
import static org.cmdbuild.api.fluent.ws.FunctionInput.functionInput;
import static org.cmdbuild.api.fluent.ws.FunctionOutput.functionOutput;
import static org.cmdbuild.api.fluent.ws.ReportHelper.DEFAULT_TYPE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.toIntExact;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.cmdbuild.api.fluent.Attachment;
import org.cmdbuild.api.fluent.AttachmentDescriptor;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.CreateReport;
import org.cmdbuild.api.fluent.DownloadedReport;
import org.cmdbuild.api.fluent.DummyFluentApiExecutor;
import org.cmdbuild.api.fluent.ExecutorBasedFluentApi;
import org.cmdbuild.api.fluent.ExistingCard;
import org.cmdbuild.api.fluent.ExistingProcessInstance;
import org.cmdbuild.api.fluent.ExistingRelation;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.Function;
import org.cmdbuild.api.fluent.FunctionCall;
import org.cmdbuild.api.fluent.Lookup;
import org.cmdbuild.api.fluent.NewCard;
import org.cmdbuild.api.fluent.NewProcessInstance;
import org.cmdbuild.api.fluent.NewRelation;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptor;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptorImpl;
import org.cmdbuild.api.fluent.QueryAllLookup;
import org.cmdbuild.api.fluent.QueryClass;
import org.cmdbuild.api.fluent.QuerySingleLookup;
import org.cmdbuild.api.fluent.Relation;
import org.cmdbuild.api.fluent.RelationImpl;
import org.cmdbuild.api.fluent.RelationsQuery;
import org.cmdbuild.common.Constants;
import org.cmdbuild.common.utils.TempDataSource;
import org.cmdbuild.services.soap.client.beans.Attribute;
import org.cmdbuild.services.soap.client.beans.AttributeSchema;
import org.cmdbuild.services.soap.client.beans.CardList;
import org.cmdbuild.services.soap.client.beans.CqlQuery;
import org.cmdbuild.services.soap.client.beans.Filter;
import org.cmdbuild.services.soap.client.beans.FilterOperator;
import org.cmdbuild.services.soap.client.beans.Order;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.services.soap.client.beans.Query;
import org.cmdbuild.services.soap.client.beans.ReportParams;
import org.cmdbuild.services.soap.client.beans.WorkflowWidgetSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsFluentApiExecutor implements FluentApiExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final FluentApiExecutor NEVER_USED_EXECUTOR = DummyFluentApiExecutor.INSTANCE;

    private static final String OPERATOR_EQUALS = "EQUALS";
    private static final String OPERATOR_AND = "AND";

    private static final List<Attribute> ALL_ATTRIBUTES = null;
    private static final Query NO_QUERY = null;
    private static final List<Order> NO_ORDERING = null;
//	private static final int NO_LIMIT = 0;
//	private static final int OFFSET_BEGINNING = 0;
    private static final String NO_FULLTEXT = null;
    private static final CqlQuery NO_CQL = null;

    private static final ReportParams UNUSED_REPORT_PARAMS = new ReportParams() {
        {
            setKey("__unused__");
        }
    };

    private static final EntryTypeConverter IDENTITY_ENTRY_TYPE_CONVERTER = new EntryTypeConverter() {

        @Override
        public String toClientType(EntryTypeAttributeImpl entityAttribute, String wsValue) {
            return wsValue;
        }

        @Override
        public String toWsType(EntryTypeAttributeImpl entityAttribute, Object value) {
            return IDENTITY_RAW_TYPE_CONVERTER.toWsType(WsType.UNKNOWN, value);
        }

    };

    private static final RawTypeConverter IDENTITY_RAW_TYPE_CONVERTER = (WsType wsType, Object value) -> (value != null) ? value.toString() : StringUtils.EMPTY;

    private final static EntityAttributeCreator cardAttributeCreator = (String entryTypeName, String attributeName) -> classAttribute(entryTypeName, attributeName);

    private final static EntityAttributeCreator functionInputCreator = (String entryTypeName, String attributeName) -> functionInput(entryTypeName, attributeName);

    private final static EntityAttributeCreator functionOutputCreator = (String entryTypeName, String attributeName) -> functionOutput(entryTypeName, attributeName);

    private final Private proxy;
    private EntryTypeConverter entryTypeConverter;
    private RawTypeConverter rawTypeConverter;

    public WsFluentApiExecutor(Private proxy) {
        this(proxy, IDENTITY_ENTRY_TYPE_CONVERTER, IDENTITY_RAW_TYPE_CONVERTER);
    }

    public WsFluentApiExecutor(Private proxy, EntryTypeConverter entryTypeConverter, RawTypeConverter rawTypeConverter) {
        this.proxy = proxy;
        this.entryTypeConverter = defaultIfNull(entryTypeConverter, IDENTITY_ENTRY_TYPE_CONVERTER);
        this.rawTypeConverter = defaultIfNull(rawTypeConverter, IDENTITY_RAW_TYPE_CONVERTER);
    }

    @Override
    public CardDescriptor create(NewCard card) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(card);
        long id = proxy.createCard(soapCard);
        return new CardDescriptorImpl(card.getClassName(), id);
    }

    @Override
    public void update(ExistingCard card) {
        if (!card.getAttributes().isEmpty()) {
            org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(card);
            soapCard.setId(card.getId());
            proxy.updateCard(soapCard);
        }

        for (Attachment attachment : card.getAttachments()) {
            try {
                DataSource dataSource = new URLDataSource(new URL(attachment.getUrl()));
                DataHandler dataHandler = new DataHandler(dataSource);
                proxy.uploadAttachment( //
                        card.getClassName(), //
                        card.getId(), //
                        dataHandler, //
                        attachment.getName(), //
                        attachment.getCategory(), //
                        attachment.getDescription());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void delete(ExistingCard card) {
        proxy.deleteCard(card.getClassName(), card.getId());
    }

    @Override
    public Card fetch(ExistingCard card) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = proxy.getCardWithLongDateFormat( //
                card.getClassName(), //
                card.getId(), //
                requestedAttributesFor(card.getRequestedAttributes()));
        return cardFor(soapCard);
    }

    @Override
    public List<Card> fetchCards(QueryClass card) {
        CardList cardList = proxy.getCardListWithLongDateFormat( //
                card.getClassName(), //
                requestedAttributesFor(card.getRequestedAttributes()), //
                queriedAttributesFor(card), //
                NO_ORDERING, //
                0l, //
                0l, //
                NO_FULLTEXT, //
                NO_CQL);
        return cardsFor(cardList);
    }

    private List<Attribute> requestedAttributesFor(Set<String> names) {
        List<Attribute> output;
        if (names.isEmpty()) {
            output = ALL_ATTRIBUTES;
        } else {
            List<Attribute> attributeNames = new ArrayList<>();
            for (String attributeName : names) {
                Attribute attribute = new Attribute();
                attribute.setName(attributeName);
                attributeNames.add(attribute);
            }
            output = attributeNames;
        }
        return output;
    }

    private Query queriedAttributesFor(Card card) {
        Query output;
        if (card.getAttributes().isEmpty()) {
            output = NO_QUERY;
        } else {
            List<Query> queries = queriesFor(card);
            if (queries.size() == 1) {
                return queries.get(0);
            }
            FilterOperator filterOperator = new FilterOperator();
            filterOperator.setOperator(OPERATOR_AND);
            filterOperator.getSubquery().addAll(queriesFor(card));
            output = queryFor(filterOperator);
        }
        return output;
    }

    private List<Query> queriesFor(Card card) {
        List<Query> queries = new ArrayList<>();
        for (String name : card.getAttributeNames()) {
            String wsValue = entryTypeConverter.toWsType(classAttribute(card.getClassName(), name),
                    card.get(name));
            Query attributeQuery = new Query() {
                {
                    setFilter(wsEqualsFilter(name, wsValue));
                }
            };
            queries.add(attributeQuery);
        }
        return queries;
    }

    private Query queryFor(FilterOperator filterOperator) {
        Query query = new Query();
        query.setFilterOperator(filterOperator);
        return query;
    }

    private List<Card> cardsFor(CardList cardList) {
        List<Card> cards = new ArrayList<>();
        for (org.cmdbuild.services.soap.client.beans.Card soapCard : cardList.getCards()) {
            cards.add(cardFor(soapCard));
        }
        return unmodifiableList(cards);
    }

    private Card cardFor(org.cmdbuild.services.soap.client.beans.Card soapCard) {
        ExistingCard card = existingCardFrom(soapCard);
        for (Attribute attribute : soapCard.getAttributeList()) {
            String attributeName = attribute.getName();
            String wsValue = wsValueFor(attribute);
            card.with( //
                    attributeName, //
                    entryTypeConverter.toClientType(classAttribute(soapCard.getClassName(), attributeName), wsValue));
        }
        return card;
    }

    private ExistingCard existingCardFrom(org.cmdbuild.services.soap.client.beans.Card soapCard) {
        return new ExecutorBasedFluentApi(NEVER_USED_EXECUTOR).existingCard(soapCard.getClassName(), soapCard.getId());
    }

    @Override
    public void create(NewRelation relation) {
        proxy.createRelation(soapRelationFor(relation));
    }

    @Override
    public void delete(ExistingRelation relation) {
        proxy.deleteRelation(soapRelationFor(relation));
    }

    private org.cmdbuild.services.soap.client.beans.Relation soapRelationFor(Relation relation) {
        org.cmdbuild.services.soap.client.beans.Relation soapRelation = new org.cmdbuild.services.soap.client.beans.Relation();
        soapRelation.setDomainName(relation.getDomainName());
        soapRelation.setClass1Name(relation.getClassName1());
        soapRelation.setCard1Id(relation.getCardId1());
        soapRelation.setClass2Name(relation.getClassName2());
        soapRelation.setCard2Id(relation.getCardId2());
        return soapRelation;
    }

    @Override
    public List<Relation> fetch(RelationsQuery query) {
        List<org.cmdbuild.services.soap.client.beans.Relation> soapRelations = proxy.getRelationList( //
                query.getDomainName(), //
                query.getClassName(), //
                query.getCardId());
        List<Relation> relations = new ArrayList<>();
        for (org.cmdbuild.services.soap.client.beans.Relation soapRelation : soapRelations) {
            relations.add(relationFor(soapRelation));
        }
        return unmodifiableList(relations);
    }

    private Relation relationFor(org.cmdbuild.services.soap.client.beans.Relation soapRelation) {
        Relation relation = new RelationImpl(soapRelation.getDomainName());
        relation.setCard1(soapRelation.getClass1Name(), soapRelation.getCard1Id());
        relation.setCard2(soapRelation.getClass2Name(), soapRelation.getCard2Id());
        return relation;
    }

    @Override
    public Map<String, Object> execute(FunctionCall function) {
        List<Attribute> outputs = proxy.callFunction(function.getFunctionName(), wsInputAttributesFor(function));
        return unmodifiableMap(clientAttributesFor(function, outputs));
    }

    @Override
    public DownloadedReport download(CreateReport report) {
        ReportHelper helper = new ReportHelper(proxy);
        org.cmdbuild.services.soap.client.beans.Report soapReport = helper.getReport(DEFAULT_TYPE, report.getTitle());
        List<AttributeSchema> paramSchemas = helper.getParamSchemas(soapReport, report.getFormat());
        List<ReportParams> reportParams = compileParams(paramSchemas, report.getParameters());
        DataHandler dataHandler = helper.getDataHandler(soapReport, report.getFormat(), reportParams);
        File file = helper.temporaryFile(report.getTitle(), report.getFormat());
        helper.saveToFile(dataHandler, file);
        return new DownloadedReport(file);
    }

    private List<ReportParams> compileParams(List<AttributeSchema> paramSchemas, Map<String, Object> params) {
        List<ReportParams> reportParameters = new ArrayList<>();
        for (AttributeSchema attributeSchema : paramSchemas) {
            String paramName = attributeSchema.getName();
            Object paramValue = params.get(paramName);
            WsType wsType = WsType.from(attributeSchema.getType());
            ReportParams parameter = new ReportParams();
            parameter.setKey(paramName);
            parameter.setValue(rawTypeConverter.toWsType(wsType, paramValue));
            reportParameters.add(parameter);
        }
        if (reportParameters.isEmpty()) {
            reportParameters.add(UNUSED_REPORT_PARAMS);
        }
        return reportParameters;
    }

    @Override
    public ProcessInstanceDescriptor createProcessInstance(NewProcessInstance processCard, AdvanceProcess advance) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(processCard);
        boolean advanceProcess = (advance == AdvanceProcess.YES);
        List<WorkflowWidgetSubmission> emptyWidgetsSubmission = emptyList();
        org.cmdbuild.services.soap.client.beans.Workflow workflowInfo = proxy.updateWorkflow(soapCard, advanceProcess, emptyWidgetsSubmission);
        return new ProcessInstanceDescriptorImpl(processCard.getClassName(), workflowInfo.getProcessid(), workflowInfo.getProcessinstanceid());
    }

    @Override
    public void updateProcessInstance(ExistingProcessInstance processCard, AdvanceProcess advance) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(processCard);
        boolean advanceProcess = (advance == AdvanceProcess.YES);
        List<WorkflowWidgetSubmission> emptyWidgetsSubmission = emptyList();
        proxy.updateWorkflow(soapCard, advanceProcess, emptyWidgetsSubmission);
    }

    @Override
    public void suspendProcessInstance(ExistingProcessInstance processCard) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(processCard);
        proxy.suspendWorkflow(soapCard);
    }

    @Override
    public void resumeProcessInstance(ExistingProcessInstance processCard) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(processCard);
        proxy.resumeWorkflow(soapCard);
    }

    @Override
    public Iterable<Lookup> fetch(QueryAllLookup queryLookup) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Lookup fetch(QuerySingleLookup querySingleLookup) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Iterable<AttachmentDescriptor> fetchAttachments(CardDescriptor source) {
        List<org.cmdbuild.services.soap.client.beans.Attachment> soapAttachments = proxy.getAttachmentList(source.getClassName(), source.getId());
        return from(soapAttachments) //
                .transform((org.cmdbuild.services.soap.client.beans.Attachment input) -> {
                    AttachmentDescriptorImpl output = new AttachmentDescriptorImpl();
                    output.setName(input.getFilename());
                    output.setDescription(input.getDescription());
                    output.setCategory(input.getCategory());
                    return output;
                });
    }

    @Override
    public void upload(CardDescriptor source, Iterable<? extends Attachment> attachments) {
        try {
            for (Attachment attachment : attachments) {
                DataSource dataSource = new URLDataSource(new URL(attachment.getUrl()));
                DataHandler dataHandler = new DataHandler(dataSource);
                boolean success = proxy.uploadAttachment(source.getClassName(), source.getId(), dataHandler, attachment.getName(),
                        attachment.getCategory(), attachment.getDescription());
                Validate.isTrue(success, "uploadAttachment failed (server error)");
            }
        } catch (Exception e) {
            logger.error("error uploading attachments", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Attachment> download(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        Collection<Attachment> downloaded = newArrayList();
        for (AttachmentDescriptor attachment : attachments) {
            downloaded.add(downloadQuietly(source, attachment));
        }
        return downloaded;
    }

    private Attachment downloadQuietly(CardDescriptor source, AttachmentDescriptor attachment) {
        try {
            return download(source, attachment);
        } catch (Exception e) {
            logger.error("error uploading attachment", e);
            throw new RuntimeException(e);
        }
    }

    private Attachment download(CardDescriptor source, AttachmentDescriptor attachment) throws IOException, MalformedURLException {
        DataHandler remote = proxy.downloadAttachment(source.getClassName(), source.getId(), attachment.getName());

        TempDataSource tempDataSource = TempDataSource.newInstance() //
                .withName(attachment.getName()) //
                .build();
        DataHandler local = new DataHandler(tempDataSource);
        InputStream inputStream = remote.getInputStream();
        OutputStream outputStream = local.getOutputStream();
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);

        AttachmentImpl output = new AttachmentImpl();
        output.setName(attachment.getName());
        output.setDescription(attachment.getDescription());
        output.setCategory(attachment.getCategory());
        output.setUrl(tempDataSource.getFile().toURI().toURL().toString());

        return output;
    }

    @Override
    public void delete(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        for (AttachmentDescriptor attachment : attachments) {
            proxy.deleteAttachment(source.getClassName(), source.getId(), attachment.getName());
        }
    }

    @Override
    public void copy(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments,
            CardDescriptor destination) {
        for (AttachmentDescriptor attachment : attachments) {
            proxy.copyAttachment(source.getClassName(), toIntExact(source.getId()), attachment.getName(),
                    destination.getClassName(), toIntExact(destination.getId()));
        }
    }

    @Override
    public void move(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments,
            CardDescriptor destination) {
        for (AttachmentDescriptor attachment : attachments) {
            proxy.moveAttachment(source.getClassName(), source.getId(), attachment.getName(),
                    destination.getClassName(), destination.getId());
        }
    }

    @Override
    public void abortProcessInstance(ExistingProcessInstance processCard) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = soapCardFor(processCard);
        proxy.abortWorkflow(soapCard);
    }

    /*
	 * Utils
     */
    private List<Attribute> wsInputAttributesFor(Function function) {
        return wsAttributesFor(functionInputCreator, function.getFunctionName(), function.getInputs());
    }

    private Map<String, Object> clientAttributesFor(Function function, List<Attribute> wsAttributes) {
        return clientAttributesFor(functionOutputCreator, function.getFunctionName(), wsAttributes);
    }

    private org.cmdbuild.services.soap.client.beans.Card soapCardFor(Card card) {
        org.cmdbuild.services.soap.client.beans.Card soapCard = new org.cmdbuild.services.soap.client.beans.Card();
        soapCard.setClassName(card.getClassName());
        if (card.getId() != null) {
            soapCard.setId(card.getId());
        }
        soapCard.getAttributeList().addAll(wsAttributesFor(card));
        return soapCard;
    }

    private List<Attribute> wsAttributesFor(Card card) {
        return wsAttributesFor(cardAttributeCreator, card.getClassName(), card.getAttributes());
    }

    private List<Attribute> wsAttributesFor(EntityAttributeCreator attributeCreator, String className,
            Map<String, Object> attributes) {
        List<Attribute> wsAttributes = new ArrayList<>(attributes.size());
        for (Map.Entry<String, Object> e : attributes.entrySet()) {
            String wsValue = entryTypeConverter.toWsType(attributeCreator.attributeFor(className, e.getKey()),
                    e.getValue());
            wsAttributes.add(wsAttribute(e.getKey(), wsValue));
        }
        return wsAttributes;
    }

    private Map<String, Object> clientAttributesFor(EntityAttributeCreator attributeCreator,
            String entryTypeName, List<Attribute> wsAttributes) {
        Map<String, Object> clientAttributes = new HashMap<>();
        for (Attribute attribute : wsAttributes) {
            String attributeName = attribute.getName();
            String wsValue = wsValueFor(attribute);
            clientAttributes.put( //
                    attributeName, //
                    entryTypeConverter.toClientType(attributeCreator.attributeFor(entryTypeName, attributeName),
                            wsValue) //
            );
        }
        return clientAttributes;
    }

    /*
	 * WS object factories
     */
    private String wsValueFor(Attribute wsAttribute) {
        return isReferenceOrLookup(wsAttribute) ? wsAttribute.getCode() : wsAttribute.getValue();
    }

    private boolean isReferenceOrLookup(Attribute wsAttribute) {
        return isNotBlank(wsAttribute.getCode());
    }

    private static Filter wsEqualsFilter(String attributeName, String attibuteValue) {
        return new Filter() {
            {
                setName(attributeName);
                setOperator(OPERATOR_EQUALS);
                getValue().add(attibuteValue);
            }
        };
    }

    public static Attribute wsAttribute(String attributeName, String attributeValue) {
        return new Attribute() {
            {
                setName(attributeName);
                setValue(attributeValue);
            }
        };
    }

    public enum WsType {

        BOOLEAN(Constants.Webservices.BOOLEAN_TYPE_NAME), //
        CHAR(Constants.Webservices.CHAR_TYPE_NAME), //
        DATE(Constants.Webservices.DATE_TYPE_NAME), //
        DECIMAL(Constants.Webservices.DECIMAL_TYPE_NAME), //
        DOUBLE(Constants.Webservices.DOUBLE_TYPE_NAME), //
        FOREIGNKEY(Constants.Webservices.FOREIGNKEY_TYPE_NAME), //
        INET(Constants.Webservices.INET_TYPE_NAME), //
        INTEGER(Constants.Webservices.INTEGER_TYPE_NAME), //
        LOOKUP(Constants.Webservices.LOOKUP_TYPE_NAME), //
        REFERENCE(Constants.Webservices.REFERENCE_TYPE_NAME), //
        STRING(Constants.Webservices.STRING_TYPE_NAME), //
        TEXT(Constants.Webservices.TEXT_TYPE_NAME), //
        TIMESTAMP(Constants.Webservices.TIMESTAMP_TYPE_NAME), //
        TIME(Constants.Webservices.TIME_TYPE_NAME), //
        UNKNOWN(Constants.Webservices.UNKNOWN_TYPE_NAME);

        private final String wsTypeName;

        private WsType(String wsTypeName) {
            this.wsTypeName = wsTypeName;
        }

        public static WsType from(String wsTypeName) {
            for (WsType wsType : values()) {
                if (wsType.wsTypeName.equals(wsTypeName)) {
                    return wsType;
                }
            }
            return UNKNOWN;
        }

    }

    public interface EntryTypeConverter {

        Object toClientType(EntryTypeAttributeImpl entryTypeAttribute, String wsValue);

        String toWsType(EntryTypeAttributeImpl entryTypeAttribute, Object clientValue);

    }

    public interface RawTypeConverter {

        String toWsType(WsType wsType, Object value);

    }

    private static interface EntityAttributeCreator {

        EntryTypeAttributeImpl attributeFor(String entryTypeName, String attributeName);

    }
}
