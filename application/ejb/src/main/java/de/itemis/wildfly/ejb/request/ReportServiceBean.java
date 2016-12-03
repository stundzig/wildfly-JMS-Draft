package de.itemis.wildfly.ejb.request;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import de.itemis.wildfly.ejb.report.ReportFactory;

@Stateless
@Remote(ReportService.class)
// @Clustered
public class ReportServiceBean implements ReportService {

	private final static Logger LOGGER = Logger.getLogger(ReportServiceBean.class.toString());

	// @Inject
	// private InitialContext jndi;

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:jboss/exported/jms/queue/reportRequest")
	private Queue requestQ;

	@Resource(lookup = "java:jboss/exported/jms/queue/reportResponse")
	private Queue responseQ;

	@EJB(lookup = "java:app/ejb/ReportFactoryBean!de.itemis.wildfly.ejb.report.ReportFactory")
	private ReportFactory reportFactory;

	@Override
	public RequestedReport create(ReportRequest rr) {
		LOGGER.info(rr.toString());
		final RequestedReport result = new RequestedReport(createId(rr));
		if (rr.expectedEffort() > 1) {
			LOGGER.info("creating asynchron");
			// create message
			// TemporaryQueue Behaviour
			// Queue responseQ = context.createTemporaryQueue();
			// try {
			// LOGGER.info(responseQ.getQueueName());
			// InitialContext jndi = new InitialContext();
			// jndi.bind(/*"java:jboss/exported/jms/queue/" +
			// */result.requestId(), responseQ);
			// LOGGER.warning("jndi: " + new
			// InitialContext().lookup(/*"java:jboss/exported/jms/queue/" +*/
			// result.requestId()));
			// } catch (Exception e) {
			// throw new RuntimeException(e);
			// }
			context.createProducer().setJMSCorrelationID(result.requestId()).setJMSReplyTo(responseQ).send(requestQ,
					rr);
		} else {
			// return synchron
			LOGGER.info("creating synchron");
			result.setReportId(reportFactory.create(result.requestId(), rr, () -> false, (i, m) -> LOGGER.info(m)));
		}
		return result;
	}

	private String createId(ReportRequest rr) {
		// user username from request and a unique id
		return "steffen_" + System.nanoTime();
	}

	@Override
	public RequestedReport lookup(RequestedReport rr) {
		LOGGER.info(rr.toString());/*
									 * InitialContext jndi = new
									 * InitialContext(); Queue responseQ =
									 * (Queue) new InitialContext().lookup(
									 * "java:jboss/exported/jms/queue/" +
									 * rr.requestId());
									 */
		JMSConsumer consumer = context.createConsumer(responseQ, "JMSCorrelationID = '" + rr.requestId() + "'");
		Message message;
		while ((message = consumer.receive()) != null) {
			LOGGER.info("Found message in reponse " + message);
		}
		if (message != null) {
			try {
				return (RequestedReport) ((ObjectMessage) message).getObject();
			} catch (JMSException e) {
				throw new RuntimeException();
			}
		}
		return null;
	}

	@Override
	public void cancel(RequestedReport rr) {
		LOGGER.info(rr.toString());
	}
}
