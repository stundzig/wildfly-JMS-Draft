package de.itemis.wildfly.ejb.request;

public interface ReportService {

	RequestedReport create(ReportRequest rr);

	RequestedReport lookup(RequestedReport rr);

	void cancel(RequestedReport rr);
}
