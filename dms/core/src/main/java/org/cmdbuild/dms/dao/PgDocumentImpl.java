/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

@CardMapping("_Document")
public class PgDocumentImpl implements PgDocument {

	public final static String PGDMSDOC_ATTR_FILE_NAME = "FileName",
			PGDMSDOC_ATTR_MIME_TYPE = "MimeType",
			PGDMSDOC_ATTR_VERSION = "Version",
			PGDMSDOC_ATTR_CATEGORY = "Category",
			PGDMSDOC_ATTR_FILE_SIZE = "Size",
			PGDMSDOC_ATTR_CARD_ID = "CardId",
			DOCUMENT_ATTR_HASH = "Hash",
			PGDMSDOC_ATTR_CREATED = "CreationDate";

	private final String author, description, fileName, mimeType, version, category, hash;
	private final int size;
	private final long cardId;
	private final ZonedDateTime created, modified;
	private final Long id;

	private PgDocumentImpl(PgDocumentImplBuilder builder) {
		this.author = builder.author;
		this.id = builder.id;
		this.description = builder.description;
		this.fileName = checkNotNull(builder.fileName);
		this.mimeType = checkNotNull(builder.mimeType);
		this.version = checkNotNull(builder.version);
		this.category = builder.category;
		this.size = checkNotNull(builder.size);
		this.hash = trimAndCheckNotBlank(builder.hash);
		this.created = checkNotNull(builder.created);
		this.modified = checkNotNull(builder.modified);
		this.cardId = checkNotNull(builder.cardId);
	}

	@Override
	@Nullable
	@CardAttr(ATTR_ID)
	public Long getId() {
		return id;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_CARD_ID)
	public long getCardId() {
		return cardId;
	}

	@Override
	@CardAttr(ATTR_USER)
	public String getAuthor() {
		return author;
	}

	@Override
	@CardAttr(ATTR_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_FILE_NAME)
	public String getFileName() {
		return fileName;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_MIME_TYPE)
	public String getMimeType() {
		return mimeType;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_VERSION)
	public String getVersion() {
		return version;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_FILE_SIZE)
	public int getFileSize() {
		return size;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_CATEGORY)
	public String getCategory() {
		return category;
	}

	@Override
	@CardAttr(PGDMSDOC_ATTR_CREATED)
	public ZonedDateTime getCreated() {
		return created;
	}

	@Override
	@CardAttr(ATTR_BEGINDATE)
	public ZonedDateTime getModified() {
		return modified;
	}

	@Override
	@CardAttr(DOCUMENT_ATTR_HASH)
	public String getHash() {
		return hash;
	}

	public static PgDocumentImplBuilder builder() {
		return new PgDocumentImplBuilder();
	}

//	public static PgDocumentImplBuilder copyOf(StoredDocumentInfo source) {
//		return builder()
//				.withAuthor(source.getAuthor())
//				.withCategory(source.getCategory())
//				.withCreated(source.getCreated())
//				.withDescription(source.getDescription())
//				.withDmsFileName(source.getDmsFileName())
//				.withFileName(source.getDmsFileName())
//				.withDocumentId(source.getDocumentId())
//				.withModified(source.getModified())
//				.withVersion(source.getVersion());
//	}
	public static PgDocumentImplBuilder copyOf(PgDocument source) {
		return builder()
				.withAuthor(source.getAuthor())
				.withCardId(source.getCardId())
				.withCategory(source.getCategory())
				.withCreated(source.getCreated())
				.withDescription(source.getDescription())
				.withFileName(source.getFileName())
				.withHash(source.getHash())
				.withId(source.getId())
				.withMimeType(source.getMimeType())
				.withModified(source.getModified())
				.withFileSize(source.getFileSize())
				.withVersion(source.getVersion());
	}

	public static class PgDocumentImplBuilder implements Builder<PgDocumentImpl, PgDocumentImplBuilder> {

		private String author, description, fileName, mimeType, version, category, hash;
		private Integer size;
		private Long cardId;
		private ZonedDateTime created, modified;
		private Long id;

		public PgDocumentImplBuilder withAuthor(String author) {
			this.author = author;
			return this;
		}

		public PgDocumentImplBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public PgDocumentImplBuilder withCardId(Long cardId) {
			this.cardId = cardId;
			return this;
		}

		public PgDocumentImplBuilder withFileName(String filename) {
			this.fileName = filename;
			return this;
		}

		public PgDocumentImplBuilder withMimeType(String mimetype) {
			this.mimeType = mimetype;
			return this;
		}

		public PgDocumentImplBuilder withVersion(String version) {
			this.version = version;
			return this;
		}

		public PgDocumentImplBuilder withCategory(String category) {
			this.category = category;
			return this;
		}

		public PgDocumentImplBuilder withHash(String hash) {
			this.hash = hash;
			return this;
		}

		public PgDocumentImplBuilder withFileSize(Integer size) {
			this.size = size;
			return this;
		}

		public PgDocumentImplBuilder withCreated(ZonedDateTime created) {
			this.created = created;
			return this;
		}

		public PgDocumentImplBuilder withModified(ZonedDateTime modified) {
			this.modified = modified;
			return this;
		}

		public PgDocumentImplBuilder withId(Long infoId) {
			this.id = infoId;
			return this;
		}

		@Override
		public PgDocumentImpl build() {
			return new PgDocumentImpl(this);
		}

	}

}
