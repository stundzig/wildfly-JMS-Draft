package de.itemis.wildfly.ejb.report;

import javax.ejb.Remote;

import de.itemis.wildfly.ejb.request.ReportRequest;


@Remote
public interface ReportFactory {
	public String create(String requestId, ReportRequest rr, ReportCancellationHandler cancellationHandler,
			ReportProgressHandler progress);
}
