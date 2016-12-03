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
import de.itemis.wildfly.ejb.report.ReportFactoryBean;

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

	// @EJB(lookup =
	// "java:app/ejb/ReportFactoryBean!de.itemis.wildfly.ejb.report.ReportFactory")
	private ReportFactory reportFactory = new ReportFactoryBean();

	@Override
	public RequestedReport create(ReportRequest rr) {
		LOGGER.info("create: " + rr.toString());
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
		return "steffen" + System.nanoTime();
	}

	@Override
	public RequestedReport lookup(RequestedReport rr) {
		LOGGER.info("lookup:" + rr.requestId());
		/*
		 * InitialContext jndi = new InitialContext(); Queue responseQ = (Queue)
		 * new InitialContext().lookup( "java:jboss/exported/jms/queue/" +
		 * rr.requestId());
		 */
		JMSConsumer consumer = context.createConsumer(responseQ);//, "JMSCorrelationID='" + rr.requestId() + "'" );
		Message currentMessage = null;
		Message lastMessage = null;
		while ((currentMessage = consumer.receiveNoWait()) != null) {
			LOGGER.info("Found message in reponse " + currentMessage);
			lastMessage = currentMessage;
		}
		if (lastMessage != null) {
			// send the last message as response
			try {
				return (RequestedReport) ((ObjectMessage) lastMessage).getObject();
			} catch (JMSException e) {
				throw new RuntimeException();
			}
		}
		return rr;
	}

	@Override
	public void cancel(RequestedReport rr) {
		LOGGER.info(rr.toString());
	}
}
