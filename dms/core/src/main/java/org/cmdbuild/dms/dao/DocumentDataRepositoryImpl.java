/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;

@Component
public class DocumentDataRepositoryImpl implements DocumentDataRepository {

	private final DaoService dao;

	public DocumentDataRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	@Nullable
	public byte[] getDocumentDataOrNull(long id) {
		return Optional.ofNullable(getDocumentDataCardOrNull(id)).map(DocumentData::getData).orElse(null);
	}

	@Override
	public void updateDocumentData(long id, byte[] data) {
		dao.getJdbcTemplate().update("UPDATE \"_DocumentData\" SET \"DocumentId\" = ( SELECT \"Id\" FROM \"_Document_history\" WHERE \"CurrentId\" = ? AND \"Status\" = 'U' ORDER BY \"EndDate\" DESC LIMIT 1 ) WHERE \"DocumentId\" = ?", id, id);
		createDocumentData(id, data);
	}

	@Override
	public void createDocumentData(long id, byte[] data) {
		dao.create(DocumentData.builder().withDocumentId(id).withData(data).build());
	}

	private @Nullable
	DocumentData getDocumentDataCardOrNull(long id) {
		return dao.selectAll().from(DocumentData.class).where("DocumentId", EQ, id).getOneOrNull();
	}

}
