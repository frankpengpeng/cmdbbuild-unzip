/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.dms.dao.PgDocumentImpl.PGDMSDOC_ATTR_CARD_ID;
import static org.cmdbuild.dms.dao.PgDocumentImpl.PGDMSDOC_ATTR_VERSION;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

@Component
public class DocumentInfoRepositoryImpl implements DocumentInfoRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public DocumentInfoRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public boolean hasContent() {
        return dao.getJdbcTemplate().queryForObject("SELECT EXISTS (SELECT 1 FROM \"_Document\" LIMIT 1)", Boolean.class);
    }

    @Override
    public List<PgDocument> getAllByCardId(long cardId) {
        return dao.selectAll().from(PgDocument.class).where(PGDMSDOC_ATTR_CARD_ID, EQ, cardId).asList();
    }

    @Override
    public PgDocument getOne(long cardId, String documentId) {
        checkNotBlank(documentId);
        PgDocument document = getById(toLong(documentId));
        checkArgument(equal(cardId, document.getCardId()));
        return document;
    }

    @Override
    public PgDocument getById(long documentCardId) {
        return dao.getById(PgDocument.class, documentCardId).toModel();
    }

    @Override
    public List<PgDocument> getAllVersions(long documentCardId) {
        return dao.selectAll().from(PgDocument.class).includeHistory().where(ATTR_CURRENTID, EQ, documentCardId).asList();
    }

    @Override
    public PgDocument create(PgDocument localDocumentInfo) {
        logger.info("create document info in local repository");
        return dao.create(PgDocumentImpl.copyOf(localDocumentInfo).build());
    }

    @Override
    public PgDocument update(PgDocument localDocumentInfo) {
        PgDocument current = getOne(localDocumentInfo.getCardId(), localDocumentInfo.getDocumentId());
        return dao.update(PgDocumentImpl.copyOf(localDocumentInfo).withId(current.getId()).build());
    }

    @Override
    public void delete(PgDocument document) {
        dao.delete(document);
    }

    @Override
    public PgDocument getByIdAndVersion(long documentCardId, String version) {
        return dao.selectAll().from(PgDocument.class).includeHistory().where(ATTR_CURRENTID, EQ, documentCardId).where(PGDMSDOC_ATTR_VERSION, EQ, checkNotBlank(version)).getOne();
    }

    @Override
    public List<PgDocument> getAll() {
        return dao.selectAll().from(PgDocument.class).asList();
    }

}
