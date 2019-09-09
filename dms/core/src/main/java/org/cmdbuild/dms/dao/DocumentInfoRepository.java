/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import java.util.List;

public interface DocumentInfoRepository {

    List<PgDocument> getAllByCardId(long cardId);

    PgDocument getById(long documentCardId);

    PgDocument getOne(long cardId, String documentId);

    PgDocument create(PgDocument localDocumentInfo);

    PgDocument update(PgDocument localDocumentInfo);

    void delete(PgDocument document);

    List<PgDocument> getAllVersions(long documentCardId);

    boolean hasContent();

    PgDocument getByIdAndVersion(long documentCardId, String version);

    List<PgDocument> getAll();

}
