package de.itemis.wildfly.ejb.request;

public interface RemoteRequestReport {
	
	ReportResponse create(ReportRequest rr);

	ReportResponse lookup(ReportRequest rr);
	
	ReportResponse cancel(ReportRequest rr);
	
	ReportResponse close(ReportRequest rr);

}
