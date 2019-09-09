/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import java.util.List;
import org.cmdbuild.workflow.FlowAdvanceResponse;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;

public class SimpleFlowAdvanceResponse implements FlowAdvanceResponse {

	final Flow flowCard;
	final List<Task> tasklist;
	final AdvancedFlowStatus advancedFlowStatus;

	private SimpleFlowAdvanceResponse(SimpleFlowAdvanceResponseBuilder builder) {
		this.flowCard = checkNotNull(builder.flowCard);
		this.tasklist = (List) checkNotNull(builder.tasklist);
		this.advancedFlowStatus = checkNotNull(builder.advancedFlowStatus);
	}

	@Override
	public Flow getFlowCard() {
		return flowCard;
	}

	@Override
	public List<Task> getTasklist() {
		return tasklist;
	}

	@Override
	public AdvancedFlowStatus getAdvancedFlowStatus() {
		return advancedFlowStatus;
	}

	public static SimpleFlowAdvanceResponseBuilder builder() {
		return new SimpleFlowAdvanceResponseBuilder();
	}

	public static SimpleFlowAdvanceResponseBuilder copyOf(SimpleFlowAdvanceResponse source) {
		return new SimpleFlowAdvanceResponseBuilder()
				.withFlowCard(source.getFlowCard())
				.withTasklist(source.getTasklist())
				.withAdvancedFlowStatus(source.getAdvancedFlowStatus());
	}

	public static class SimpleFlowAdvanceResponseBuilder implements Builder<SimpleFlowAdvanceResponse, SimpleFlowAdvanceResponseBuilder> {

		private Flow flowCard;
		private List<Task> tasklist;
		private AdvancedFlowStatus advancedFlowStatus;

		public Flow getFlowCard() {
			return checkNotNull(flowCard);
		}

		public SimpleFlowAdvanceResponseBuilder withFlowCard(Flow flowCard) {
			this.flowCard = flowCard;
			return this;
		}

		public SimpleFlowAdvanceResponseBuilder withTasklist(List<Task> tasklist) {
			this.tasklist = tasklist;
			return this;
		}

		public SimpleFlowAdvanceResponseBuilder withAdvancedFlowStatus(AdvancedFlowStatus advancedFlowStatus) {
			this.advancedFlowStatus = advancedFlowStatus;
			return this;
		}

		@Override
		public SimpleFlowAdvanceResponse build() {
			return new SimpleFlowAdvanceResponse(this);
		}

	}
}
