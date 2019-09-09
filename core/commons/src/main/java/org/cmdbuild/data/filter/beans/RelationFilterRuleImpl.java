/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import java.util.List;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.RelationFilterRule;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.utils.lang.Builder;

public class RelationFilterRuleImpl implements RelationFilterRule {

	private final String domain, source, destination;
	private final RelationFilterRuleType type;
	private final RelationFilterDirection direction;
	private final List<RelationFilterCardInfo> cardInfos;

	private RelationFilterRuleImpl(RelationFilterRuleBuilder builder) {
		this.domain = checkNotNull(builder.domain);
		this.source = checkNotNull(builder.source);
		this.destination = checkNotNull(builder.destination);
		this.type = checkNotNull(builder.type);
		this.direction = builder.direction;
		if (RelationFilterRuleType.ONEOF.equals(type)) {
			this.cardInfos = ImmutableList.copyOf(checkNotNull(builder.cardInfos));
		} else {
			this.cardInfos = null;
		}
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getDestination() {
		return destination;
	}

	@Override
	public RelationFilterRuleType getType() {
		return type;
	}

	@Override
	public RelationFilterDirection getDirection() {
		return checkNotNull(direction);
	}

	@Override
	public List<RelationFilterCardInfo> getCardInfos() {
		return checkNotNull(cardInfos);
	}

	public static RelationFilterRuleBuilder builder() {
		return new RelationFilterRuleBuilder();
	}

//	public static RelationFilterRuleImplBuilder copyOf(RelationFilterRuleImpl source) {
//		return new RelationFilterRuleImplBuilder()
//				.withDomain(source.getDomain())
//				.withSource(source.getSource())
//				.withDestination(source.getDestination())
//				.withType(source.getType())
//				.withDirection(source.getDirection())
//				.withCardInfos(source.getCardInfos());
//	}
	public static class RelationFilterRuleBuilder implements Builder<RelationFilterRuleImpl, RelationFilterRuleBuilder> {

		private String domain;
		private String source;
		private String destination;
		private RelationFilterRuleType type;
		private RelationFilterDirection direction;
		private List<RelationFilterCardInfo> cardInfos;

		public RelationFilterRuleBuilder withDomain(String domain) {
			this.domain = domain;
			return this;
		}

		public RelationFilterRuleBuilder withSource(String source) {
			this.source = source;
			return this;
		}

		public RelationFilterRuleBuilder withDestination(String destination) {
			this.destination = destination;
			return this;
		}

		public RelationFilterRuleBuilder withType(RelationFilterRuleType type) {
			this.type = type;
			return this;
		}

		public RelationFilterRuleBuilder withDirection(RelationFilterDirection direction) {
			this.direction = direction;
			return this;
		}

		public RelationFilterRuleBuilder withCardInfos(List<RelationFilterCardInfo> cardInfos) {
			this.cardInfos = cardInfos;
			return this;
		}

		@Override
		public RelationFilterRuleImpl build() {
			return new RelationFilterRuleImpl(this);
		}

	}
}
