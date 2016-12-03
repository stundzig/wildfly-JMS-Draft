package de.itemis.wildfly.ejb.report;

import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import de.itemis.wildfly.ejb.request.ReportRequest;


/**
 * creates the report
 * 
 * @author stundzig
 */
@Stateless
@Remote(ReportFactory.class)
public class ReportFactoryBean implements ReportFactory {

	private final static Logger LOGGER = Logger.getLogger(ReportFactoryBean.class.toString());

	public String create(String requestId, ReportRequest rr, ReportCancellationHandler cancellationHandler,
			ReportProgressHandler progress) {
		
		try {
			// do some useful calculations
			for (int i = 1; i < rr.expectedEffort(); i++) {
				if (!cancellationHandler.isCancelled()) {
					LOGGER.info("Step " + i + " of " + rr.expectedEffort());
					progress.handleProgress( i, "Step " + i + " of " + rr.expectedEffort());
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return "reportId";
	}
}
