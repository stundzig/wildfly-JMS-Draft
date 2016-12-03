package de.itemis.wildfly.ejb.report;

public interface ReportProgressHandler {

	void handleProgress(int progress, String message);

}
