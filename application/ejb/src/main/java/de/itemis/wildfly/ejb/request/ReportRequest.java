package de.itemis.wildfly.ejb.request;

import java.io.Serializable;

public class ReportRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int expectedEffort;

	public ReportRequest() {
		this(1);
	}

	public ReportRequest(int expectedEffort) {
		this.expectedEffort = expectedEffort;
	}

	public int expectedEffort() {
		return expectedEffort;
	}
}
