package de.itemis.wildfly.ejb.request;

import java.io.Serializable;
import java.util.Optional;

public class RequestedReport implements Serializable {
	private static final long serialVersionUID = 1L;
	private String reportId;
	private final String requestId;
	private int currentStep;
	private String currentMessage;

	public RequestedReport(final String requestID) {
		this.requestId = requestID;
	}

	public Optional<String> reportId() {
		return Optional.ofNullable(reportId);
	}

	public void setReportId(final String reportId) {
		this.reportId = reportId;
	}

	public String requestId() {
		return requestId;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public int currentStep() {
		return currentStep;
	}

	public String currentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}
}
