/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.lang.Builder;

public class SqlFunctionImpl implements SqlFunction {

	private final String signature, hash, content, requiredPatchVersion;

	private SqlFunctionImpl(SqlFunctionImplBuilder builder) {
		this.signature = checkNotNull(builder.signature);
		this.content = checkNotNull(builder.content);
		this.requiredPatchVersion = checkNotNull(builder.requiredPatchVersion);
		this.hash = hash(content);
		checkArgument(isBlank(builder.hash) || equal(this.hash, builder.hash), "hash mismatch");
	}

	@Override
	public String getSignature() {
		return signature;
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Override
	public String getFunctionDefinition() {
		return content;
	}

	@Override
	public String getRequiredPatchVersion() {
		return requiredPatchVersion;
	}

	@Override
	public String toString() {
		return "SqlFunctionImpl{" + "signature=" + signature + '}';
	}

	public static SqlFunctionImplBuilder builder() {
		return new SqlFunctionImplBuilder();
	}

	public static SqlFunctionImplBuilder copyOf(SqlFunction source) {
		return new SqlFunctionImplBuilder()
				.withSignature(source.getSignature())
				.withHash(source.getHash())
				.withFunctionDefinition(source.getFunctionDefinition())
				.withRequiredPatchVersion(source.getRequiredPatchVersion());
	}

	public static class SqlFunctionImplBuilder implements Builder<SqlFunctionImpl, SqlFunctionImplBuilder> {

		private String signature;
		private String hash;
		private String content;
		private String requiredPatchVersion;

		public SqlFunctionImplBuilder withSignature(String signature) {
			this.signature = signature;
			return this;
		}

		public SqlFunctionImplBuilder withHash(String hash) {
			this.hash = hash;
			return this;
		}

		public SqlFunctionImplBuilder withFunctionDefinition(String content) {
			this.content = content;
			return this;
		}

		public SqlFunctionImplBuilder withRequiredPatchVersion(String requiredPatchVersion) {
			this.requiredPatchVersion = requiredPatchVersion;
			return this;
		}

		@Override
		public SqlFunctionImpl build() {
			return new SqlFunctionImpl(this);
		}

	}
}
