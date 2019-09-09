/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;

@CardMapping("_DocumentData")
public class DocumentData {

	private final Long id;
	private final byte[] data;
	private final Long documentId;

	private DocumentData(DocumentDataBuilder builder) {
		this.id = builder.id;
		this.data = checkNotNull(builder.data);
		this.documentId = checkNotNull(builder.documentId);
	}

	@CardAttr(ATTR_ID)
	@Nullable
	public Long getId() {
		return id;
	}

	@CardAttr
	public byte[] getData() {
		return data;
	}

	@CardAttr
	public Long getDocumentId() {
		return documentId;
	}

	public static DocumentDataBuilder builder() {
		return new DocumentDataBuilder();
	}

	public static DocumentDataBuilder copyOf(DocumentData source) {
		return new DocumentDataBuilder()
				.withId(source.getId())
				.withData(source.getData())
				.withDocumentId(source.getDocumentId());
	}

	public static class DocumentDataBuilder implements Builder<DocumentData, DocumentDataBuilder> {

		private byte[] data;
		private Long documentId, id;

		public DocumentDataBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public DocumentDataBuilder withData(byte[] data) {
			this.data = data;
			return this;
		}

		public DocumentDataBuilder withDocumentId(Long documentId) {
			this.documentId = documentId;
			return this;
		}

		@Override
		public DocumentData build() {
			return new DocumentData(this);
		}

	}
}
