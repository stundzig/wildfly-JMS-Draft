package de.itemis.wildfly.ejb.request;

import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(RemoteRequestReport.class)
// @Clustered
public class RequestReportBean implements RemoteRequestReport {

	private final static Logger LOGGER = Logger.getLogger(RequestReportBean.class.toString());

	@Override
	public ReportResponse create(ReportRequest rr) {
		LOGGER.info(rr.toString());
		return null;
	}

	@Override
	public ReportResponse lookup(ReportRequest rr) {
		LOGGER.info(rr.toString());
		return null;
	}

	@Override
	public ReportResponse close(ReportRequest rr) {
		LOGGER.info(rr.toString());
		return null;
	}

	@Override
	public ReportResponse cancel(ReportRequest rr) {
		LOGGER.info(rr.toString());
		return null;
	}

}
