package de.itemis.wildfly.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.itemis.wildfly.ejb.request.RemoteRequestReport;
import de.itemis.wildfly.ejb.request.ReportRequest;

public class RemoteEJBClient {

	public static void main(String[] args) throws Exception {
		// Invoke a stateless bean
		invoke();
	}

	private static void invoke() throws NamingException {
		final RemoteRequestReport rrr = lookup();
		System.out.println("Obtained the remote " + rrr.toString());
		rrr.create(new ReportRequest());
		rrr.lookup(new ReportRequest());
		rrr.close(new ReportRequest());
	}

	private static RemoteRequestReport lookup() throws NamingException {
		final Hashtable<String, String> jndiProperties = new Hashtable<>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		return (RemoteRequestReport) context
				.lookup("ejb:/ejb/RequestReportBean!" + RemoteRequestReport.class.getName());
	}
}
