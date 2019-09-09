/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.utils.lang.Builder;

public class DocumentInfoAndDetailImpl implements DocumentInfoAndDetail {

	private final String author, description, documentId, fileName, mimeType, version, category, hash;
	private final int size;
	private final ZonedDateTime created, modified;
	private final boolean hasContent;

	private DocumentInfoAndDetailImpl(DocumentInfoAndDetailImplBuilder builder) {
		this.author = builder.author;
		this.description = builder.description;
		this.documentId = checkNotNull(builder.documentId);
		this.fileName = checkNotNull(builder.fileName);
		this.mimeType = checkNotNull(builder.mimeType);
		this.version = checkNotNull(builder.version);
		this.category = builder.category;
		this.size = checkNotNull(builder.size);
		this.hash = trimToNull(builder.hash);
		this.created = checkNotNull(builder.created);
		this.modified = checkNotNull(builder.modified);
		this.hasContent = builder.hasContent;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDocumentId() {
		return documentId;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public int getFileSize() {
		return size;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public ZonedDateTime getCreated() {
		return created;
	}

	@Override
	public ZonedDateTime getModified() {
		return modified;
	}

	@Override
	public boolean hasContent() {
		return hasContent;
	}

	@Override
	public String getHash() {
		return hash;
	}

	public static DocumentInfoAndDetailImplBuilder builder() {
		return new DocumentInfoAndDetailImplBuilder();
	}

	public static DocumentInfoAndDetailImplBuilder copyOf(DocumentInfoAndDetail source) {
		return builder()
				.withAuthor(source.getAuthor())
				.withCategory(source.getCategory())
				.withCreated(source.getCreated())
				.withDescription(source.getDescription())
				.withFileName(source.getFileName())
				.withHash(source.getHash())
				.withDocumentId(source.getDocumentId())
				.withMimeType(source.getMimeType())
				.withModified(source.getModified())
				.withFileSize(source.getFileSize())
				.withVersion(source.getVersion());
	}

	public static class DocumentInfoAndDetailImplBuilder implements Builder<DocumentInfoAndDetailImpl, DocumentInfoAndDetailImplBuilder> {

		private String author, description, documentId, fileName, mimeType, version, category, hash;
		private Integer size;
		private ZonedDateTime created, modified;
		private boolean hasContent = false;

		public DocumentInfoAndDetailImplBuilder withAuthor(String author) {
			this.author = author;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withDocumentId(String name) {
			this.documentId = name;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withFileName(String filename) {
			this.fileName = filename;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withMimeType(String mimetype) {
			this.mimeType = mimetype;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withVersion(String version) {
			this.version = version;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withCategory(String category) {
			this.category = category;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withHash(String hash) {
			this.hash = hash;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withFileSize(Integer size) {
			this.size = size;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withCreated(ZonedDateTime created) {
			this.created = created;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder withModified(ZonedDateTime modified) {
			this.modified = modified;
			return this;
		}

		public DocumentInfoAndDetailImplBuilder hasContent(boolean hasContent) {
			this.hasContent = hasContent;
			return this;
		}

		@Override
		public DocumentInfoAndDetailImpl build() {
			return new DocumentInfoAndDetailImpl(this);
		}

	}

}
