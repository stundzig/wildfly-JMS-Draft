package de.itemis.wildfly.ejb.report;

public interface ReportResultHandler {

	void handleError(Exception e);

	void handleResult(String reportId);

}
