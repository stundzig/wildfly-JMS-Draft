package de.itemis.wildfly.client;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.itemis.wildfly.ejb.request.ReportService;
import de.itemis.wildfly.ejb.request.RequestedReport;
import de.itemis.wildfly.ejb.report.ReportFactoryBean;
import de.itemis.wildfly.ejb.request.ReportRequest;

public class RemoteEJBClient {
	
	private final static Logger LOGGER = Logger.getLogger(ReportFactoryBean.class.toString());

	public static void main(String[] args) throws Exception {
		// Invoke a stateless bean
		invoke();
	}

	private static void invoke() throws NamingException, InterruptedException {
		final ReportService rrr = lookup();
		LOGGER.info("calling the remote " + rrr.toString());
		RequestedReport requestedReport = rrr.create(new ReportRequest(1));
		LOGGER.info("got response " + requestedReport);

		LOGGER.info("calling the remote " + rrr.toString());
		requestedReport = rrr.create(new ReportRequest(50));
		LOGGER.info("got response " + requestedReport);
		Thread.sleep(15000);
		rrr.lookup(requestedReport);
		LOGGER.info("got lookup " + requestedReport);
		Thread.sleep(15000);
		rrr.lookup(requestedReport);
		LOGGER.info("got lookup " + requestedReport);
		
		
		// rrr.lookup(requestedReport);
		// rrr.cancel(requestedReport);
	}

	private static ReportService lookup() throws NamingException {
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		return (ReportService) context
				.lookup("ejb:/ejb/ReportServiceBean!" + ReportService.class.getName());
	}
}
