/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

@CardMapping("_Temp")
public class TempDataImpl implements TempData {

	private final Long id;
	private final long timeToLiveSeconds;
	private final byte[] data;

	private TempDataImpl(TempDataImplBuilder builder) {
		this.id = builder.id;
		this.timeToLiveSeconds = checkNotNullAndGtZero(builder.timeToLiveSeconds);
		this.data = checkNotNull(builder.data);
		checkArgument(data.length > 0);
	}

	@CardAttr(ATTR_ID)
	@Override
	@Nullable
	public Long getId() {
		return id;
	}

	@CardAttr("TimeToLive")
	@Override
	public long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	@CardAttr("Data")
	@Override
	public byte[] getData() {
		return data;
	}

	public static TempDataImplBuilder builder() {
		return new TempDataImplBuilder();
	}

	public static TempDataImplBuilder copyOf(TempData source) {
		return new TempDataImplBuilder()
				.withId(source.getId())
				.withTimeToLiveSeconds(source.getTimeToLiveSeconds())
				.withData(source.getData());
	}

	public static class TempDataImplBuilder implements Builder<TempDataImpl, TempDataImplBuilder> {

		private Long id, timeToLiveSeconds;
		private byte[] data;

		public TempDataImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public TempDataImplBuilder withTimeToLiveSeconds(Long timeToLiveSeconds) {
			this.timeToLiveSeconds = timeToLiveSeconds;
			return this;
		}

		public TempDataImplBuilder withData(byte[] data) {
			this.data = data;
			return this;
		}

		@Override
		public TempDataImpl build() {
			return new TempDataImpl(this);
		}

	}
}
