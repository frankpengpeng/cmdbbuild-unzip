package org.cmdbuild.workflow.core.fluentapi;

import java.util.Map;

import org.cmdbuild.api.fluent.ExistingCard;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.Lookup;
import org.cmdbuild.api.fluent.QueryAllLookup;
import org.cmdbuild.api.fluent.QuerySingleLookup;
import org.cmdbuild.lookup.LookupService;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Streams.stream;
import java.io.File;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.activation.DataHandler;
import org.cmdbuild.api.fluent.Attachment;
import org.cmdbuild.api.fluent.AttachmentDescriptor;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.CreateReport;
import org.cmdbuild.api.fluent.DownloadedReport;
import org.cmdbuild.api.fluent.ExistingProcessInstance;
import org.cmdbuild.api.fluent.ExistingRelation;
import org.cmdbuild.api.fluent.FunctionCall;
import org.cmdbuild.api.fluent.NewCard;
import org.cmdbuild.api.fluent.NewProcessInstance;
import org.cmdbuild.api.fluent.NewRelation;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptor;
import org.cmdbuild.api.fluent.ProcessInstanceDescriptorImpl;
import org.cmdbuild.api.fluent.QueryClass;
import org.cmdbuild.api.fluent.Relation;
import org.cmdbuild.api.fluent.RelationsQuery;
import org.cmdbuild.api.fluent.ws.AttachmentDescriptorImpl;
import org.cmdbuild.core.api.fluent.LookupWrapper;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.function.FunctionCallService;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.core.utils.WfWidgetUtils;
import org.cmdbuild.data.filter.beans.AttributeFilterImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.view.DataView;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.urlToByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.cmdbuild.workflow.WorkflowService;
import static org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy.SET_ALL_CLASS_VARIABLES;
import org.cmdbuild.workflow.model.Flow;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class LocalFluentApiExecutorImpl implements FluentApiExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static Function<org.cmdbuild.lookup.Lookup, Lookup> STORE_TO_API_LOOKUP = (org.cmdbuild.lookup.Lookup input) -> new LookupWrapper(input);

    private final LookupService lookupService;
    private final FunctionCallService functionService;
    private final DataView dataView;
    private final WorkflowTypeConverter typeConverter;
    private final DaoService dao;
    private final ReportService reportService;
    private final WorkflowService workflowService;
    private final DmsService dmsService;

    public LocalFluentApiExecutorImpl(LookupService lookupService, FunctionCallService functionService, DataView dataView, WorkflowTypeConverter typeConverter, DaoService dao, ReportService reportService, WorkflowService workflowService, DmsService dmsService) {
        this.lookupService = checkNotNull(lookupService);
        this.functionService = checkNotNull(functionService);
        this.dataView = checkNotNull(dataView);
        this.typeConverter = checkNotNull(typeConverter);
        this.dao = checkNotNull(dao);
        this.reportService = checkNotNull(reportService);
        this.workflowService = checkNotNull(workflowService);
        this.dmsService = checkNotNull(dmsService);
    }

    @Override
    public void update(ExistingCard card) {
        dao.update(toModelCard(card));
        updateAttachments(card);
    }

    private org.cmdbuild.dao.beans.Card toModelCard(ExistingCard input) {
        String className = input.getClassName();
        return CardImpl.builder()
                .withType(dataView.getClasse(className))
                .withAttributes(input.getAttributes())
                .withId(input.getId())
                .build();
    }

    @Override
    public Iterable<Lookup> fetch(QueryAllLookup queryLookup) {
        Iterable<org.cmdbuild.lookup.Lookup> allLookup = lookupService.getAllLookup(queryLookup.getType());
        Iterable<Lookup> result = Iterables.transform(allLookup, STORE_TO_API_LOOKUP);
        return result;
    }

    @Override
    public Lookup fetch(QuerySingleLookup querySingleLookup) {
        Integer id = querySingleLookup.getId();
        org.cmdbuild.lookup.Lookup input = lookupService.getLookup(Long.valueOf(id));
        Lookup result = STORE_TO_API_LOOKUP.apply(input);
        return result;
    }

    @Override
    public CardDescriptor create(NewCard card) {
        return toApiCard(dao.create(CardImpl.buildCard(dao.getClasse(card.getClassName()), card.getAttributes())));
    }

    @Override
    public void delete(ExistingCard card) {
        dao.delete(card.getClassName(), card.getId());
    }

    @Override
    public Card fetch(ExistingCard card) {
        return toApiCard(dao.getById(card.getClassName(), card.getId()).toCard());
    }

    private Card toApiCard(org.cmdbuild.dao.beans.Card card) {
        Map<String, Object> map = map(card.getAllValuesAsMap());
        card.getAllValuesAsMap().forEach((key, value) -> {
            if (card.hasAttribute(key)) {
                value = typeConverter.cardValueToFlowValue(value, card.getType().getAttribute(key));
            }
            map.put(key, value);
        });
        return new org.cmdbuild.api.fluent.CardImpl(card.getClassName(), card.getId(), map);
    }

    @Override
    public List<Card> fetchCards(QueryClass card) {//TODO verify this method code, adapted from DataAccessLogicHelper and PrivateImpl and WsFluentApiExecutor
        Classe targetClass = dataView.getClasse(card.getClassName());

        CmdbFilter filter = null;
        if (!card.getAttributes().isEmpty()) {
            Map<String, Object> attrs = card.getAttributes();
            try {
                attrs = map(transformValues(attrs, WfWidgetUtils::convertValueForWidget));

                if (attrs.size() == 1) {
                    Map.Entry<String, Object> entry = getOnlyElement(attrs.entrySet());
                    filter = AttributeFilterConditionImpl.eq(entry.getKey(), entry.getValue()).toAttributeFilter().toCmdbFilters();
                } else {
                    filter = AttributeFilterImpl.and(attrs.entrySet().stream().map((entry) -> {
                        return AttributeFilterConditionImpl.eq(entry.getKey(), entry.getValue()).toAttributeFilter();
                    }).collect(toList())).toCmdbFilters();
//TODO filter
                }
            } catch (Exception ex) {
                throw runtime(ex, "error building filter form attributes = %s", attrs);
            }
        }

//		JSONObject filterJsonObject = toJsonObject(filter);
//		QueryOptionsImpl queryOptions = QueryOptionsImpl.builder()
//				.filter(filter)
//				.build(); //TODO
//	private PagedElements<Card> cardList(String className, Attribute[] attributeList, Query queryType,
//			Order[] orderType, Integer limit, Integer offset, String fullTextQuery,
//			CQLQuery cqlQuery) {
//		Classe targetClass = dataView.findClasse(className);
//		SimpleQueryOptions queryOptions = new GuestFilter(authenticationStore, dataView) //
//				.apply(targetClass,
//						SimpleQueryOptions.newQueryOption() //
//								.limit(limit != null ? limit : Integer.MAX_VALUE) //
//								.offset(offset != null ? offset : 0) //
//								.filter(soapToJsonUtilsService.createJsonFilterFrom(queryType, fullTextQuery, cqlQuery, targetClass,
//										lookupStore)) //
//								.orderBy(toJsonArray(orderType, attributeList)) //
//								.onlyAttributes(namesOf(attributeList)) //
//								.parameters(parametersOf(cqlQuery)) //
//								.build());
        return dao.selectAll().from(targetClass).where(filter).getCards().stream().map(this::toApiCard).collect(toList());
//				(c) -> {

//			return new ExecutorBasedFluentApi(DummyFluentApiExecutor.INSTANCE).existingCard(c.getClassName(), c.getId());
        //TODO
//		}
//).collect(toList());
    }

    @Override
    public void create(NewRelation relation) {
        dao.create(RelationImpl.builder()
                .withType(dao.getDomain(relation.getDomainName()))
                .withSourceCard(dao.getCard(relation.getClassName1(), relation.getCardId1()))
                .withTargetCard(dao.getCard(relation.getClassName2(), relation.getCardId2()))
                //				.withAttributes(relation.) TODO
                .build());
    }

    @Override
    public void delete(ExistingRelation relation) {
        CMRelation toDelete = dao.selectAll().from(dao.getDomain(relation.getDomainName()))
                .where(ATTR_IDOBJ1, EQ, relation.getCardId1())
                .where(ATTR_IDOBJ2, EQ, relation.getCardId2())
                //				.where(ATTR_IDCLASS1, EQ, relation.getCardId1()) TODO
                //				.where(ATTR_IDCLASS2, EQ, relation.getCardId2()) TODO
                .getRelation();
        dao.delete(toDelete);
    }

    @Override
    public List<Relation> fetch(RelationsQuery query) {//TODO verify this method code, adapted from DataAccessLogicHelper and PrivateImpl and WsFluentApiExecutor
        String domainName = query.getDomainName(),
                className = query.getClassName();
        Long cardId = (long) query.getCardId();

        Domain domain = dataView.findDomain(domainName);
        Classe cmClass = dataView.findClasse(className);
//		DomainWithSource dom;
//		if (domainName != null) {
//			if (cmClass == null) {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._1.toString());
//			} else if (domain.getSourceClass().isAncestorOf(cmClass)) {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._1.toString());
//			} else {
//				dom = DomainWithSource.create(domain.getId(), QueryDomain.Source._2.toString());
//			}
//		} else {
//			dom = null;
//		}

//		List<Relation> relations = list();
//		if(isBlank(className)){
//			className=domain.getSourceClass().getName();//meh
//		}
        return dao.selectAll().from(domain).whereExpr("( \"IdClass1\" = ?::regclass AND \"IdObj1\" = ? ) OR ( \"IdClass2\" = ?::regclass AND \"IdObj2\" = ? )",
                entryTypeToSqlExpr(cmClass), cardId, entryTypeToSqlExpr(cmClass), cardId)
                .getRelations().stream().map((r) -> new org.cmdbuild.api.fluent.RelationImpl( //TODO check this
                r.getType().getName(),
                new CardDescriptorImpl(r.getSourceCard().getClassName(), r.getSourceCard().getId()),
                new CardDescriptorImpl(r.getTargetCard().getClassName(), r.getTargetCard().getId())
        )).collect(toList());
//		}else{

//		GetRelationListResponse relationList = dataAccessLogic.getRelationList(CardIdAndClassNameImpl.card((className == null) ? domain.getSourceClass().getName() : className, cardId), dom);
//		for (DomainInfo domainInfo : relationList) {
//			for (RelationInfo relationInfo : domainInfo) {
//				Relation relation = new Relation(domainInfo.getQueryDomain().getDomain().getName());
//				if (domainInfo.getQueryDomain().getQuerySource().equals(Source._1.toString())) {
//					relation.setCard1(domain.getSourceClass().getName(), relationInfo.getSourceId());
//					relation.setCard2(relationInfo.getTargetCard().getType().getName(), relationInfo.getTargetId());
//				} else {
//					relation.setCard1(relationInfo.getTargetCard().getType().getName(), relationInfo.getTargetId());
//					relation.setCard2(domain.getTargetClass().getName(), relationInfo.getSourceId());
//				}
//				relations.add(relation);
//			}
//		}
//		return relations;
    }

    @Override
    public Map<String, Object> execute(FunctionCall functionCallParams) {
        StoredFunction function = functionService.getFunction(functionCallParams.getFunctionName());
        Map<String, Object> rawOutput = functionService.callFunction(function, functionCallParams.getInputs()); //TODO conversion of input params (??)

        Map<String, Object> output = map();
        function.getOutputParameters().forEach((param) -> {
            Object value = rawOutput.get(param.getName());
            value = typeConverter.cardValueToFlowValue(value, param.getType());
            output.put(param.getName(), value);
        });

        logger.trace("function output = \n\n{}\n", mapToLoggableStringLazy(output));

        return output;
    }

    @Override
    public DownloadedReport download(CreateReport report) {
        DataHandler dataHandler = reportService.executeReportAndDownload(report.getTitle(), ReportFormat.valueOf(report.getFormat().toUpperCase()), report.getParameters());
        File tempFile = new File(tempDir(), dataHandler.getName());//TODO
        CmIoUtils.copy(dataHandler, tempFile);
        return new DownloadedReport(tempFile);
    }

    @Override
    public ProcessInstanceDescriptor createProcessInstance(NewProcessInstance processCard, AdvanceProcess advance) {
        Flow flow = workflowService.startProcess(processCard.getClassName(), processCard.getAttributes(), SET_ALL_CLASS_VARIABLES, AdvanceProcess.YES.equals(advance)).getFlowCard();
        return new ProcessInstanceDescriptorImpl(flow.getClassName(), flow.getCardId(), flow.getFlowId());
    }

    @Override
    public void updateProcessInstance(ExistingProcessInstance processCard, AdvanceProcess advance) {
        workflowService.updateProcessWithOnlyTask(processCard.getClassName(), processCard.getId(), processCard.getAttributes(), SET_ALL_CLASS_VARIABLES, equal(AdvanceProcess.YES, advance));
    }

    @Override
    public void suspendProcessInstance(ExistingProcessInstance processCard) {
        workflowService.suspendProcess(processCard.getClassName(), processCard.getId());
    }

    @Override
    public void resumeProcessInstance(ExistingProcessInstance processCard) {
        workflowService.resumeProcess(processCard.getClassName(), processCard.getId());
    }

    @Override
    public Iterable<AttachmentDescriptor> fetchAttachments(CardDescriptor source) {
        List<DocumentInfoAndDetail> attachments = dmsService.getCardAttachments(source.getClassName(), source.getId());
        List<AttachmentDescriptorImpl> output = list();
        attachments.forEach((a) -> output.add(new AttachmentDescriptorImpl(a.getFileName(), a.getDescription(), a.getCategory())));
        return output.stream().collect(toList());
    }

    @Override
    public void upload(CardDescriptor source, Iterable<? extends Attachment> attachments) {
        throw new UnsupportedOperationException("TODO: not implemented yet!");
    }

    @Override
    public Iterable<Attachment> download(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        throw new UnsupportedOperationException("TODO: not implemented yet!");
    }

    @Override
    public void delete(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        throw new UnsupportedOperationException("TODO: not implemented yet!");
    }

    @Override
    public void copy(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        Map<String, DocumentInfoAndDetail> sourceDocumentsByName = map(dmsService.getCardAttachments(source.getClassName(), source.getId()), DocumentInfoAndDetail::getFileName);
        List<DocumentInfoAndDetail> documents = stream(attachments).map(a -> checkNotNull(sourceDocumentsByName.get(a.getName()), "document not found by name =< %s >", a.getName())).collect(toList());
        documents.forEach(d -> dmsService.create(destination.getClassName(), destination.getId(),
                DocumentDataImpl.copyOf(d).withData(dmsService.getDocumentContent(d.getDocumentId())).build()));
    }

    @Override
    public void move(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        throw new UnsupportedOperationException("TODO: not implemented yet!");
    }

    @Override
    public void abortProcessInstance(ExistingProcessInstance processCard) {
        workflowService.abortProcess(processCard.getClassName(), processCard.getId());
    }

    private void updateAttachments(ExistingCard card) {
        card.getAttachments().forEach(a -> updateAttachment(card, a));
    }

    private void updateAttachment(ExistingCard card, Attachment a) {
        DocumentInfoAndDetail current = dmsService.getCardAttachmentOrNull(card.getClassName(), card.getId(), a.getName());
        byte[] data = urlToByteArray(a.getUrl());
        DocumentData documentData = DocumentDataImpl.builder().withFilename(a.getName()).withCategory(a.getCategory()).withDescription(a.getDescription()).withData(data).build();
        if (current == null) {
            dmsService.create(card.getClassName(), card.getId(), documentData);
        } else {
            dmsService.updateDocumentWithAttachmentId(card.getClassName(), card.getId(), current.getDocumentId(), documentData);//TODO check this
        }
    }
}
