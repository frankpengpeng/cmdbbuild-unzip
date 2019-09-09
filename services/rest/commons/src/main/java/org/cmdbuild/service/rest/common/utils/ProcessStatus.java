package org.cmdbuild.service.rest.common.utils;

public class ProcessStatus {

	public static final String OPEN = "open";
	public static final String SUSPENDED = "suspended";
	public static final String COMPLETED = "completed";
	public static final String ABORTED = "closed";

	private final Long id;
	private final String value;
	private final String desctipion;

	public ProcessStatus(Long id, String value, String desctipion) {
		this.id = id;
		this.value = value;
		this.desctipion = desctipion;
	}

	public Long getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return desctipion;
	}

}
