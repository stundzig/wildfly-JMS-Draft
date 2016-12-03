package de.itemis.wildfly.mdb;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;

import de.itemis.wildfly.ejb.request.RequestedReport;

/**
 * Necessary to send messages with separate trasnactions, if the top spanning
 * transaction from the mdb is running.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ResponseSenderBean {

	private final static Logger LOGGER = Logger.getLogger(ResponseSenderBean.class.toString());

	@Inject
	private JMSContext context;

	public JMSProducer sendResult(Message in, String reportId) {
		LOGGER.info("sendResult: " + reportId);
		try {
			RequestedReport rr = new RequestedReport(in.getJMSCorrelationID());
			rr.setReportId(reportId);
			return send(in, rr);
		} catch (JMSException e) {
			throw new RuntimeException();
		}
	}

	public JMSProducer sendProgress(Message in, int step, String message) {
		try {
			LOGGER.info("sendProgress: " + step + " " + message + " on " + in.getJMSCorrelationID());
			RequestedReport rr = new RequestedReport(in.getJMSCorrelationID());
			rr.setCurrentStep(step);
			rr.setCurrentMessage(message);
			return send(in, rr);
		} catch (JMSException e) {
			throw new RuntimeException();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private JMSProducer send(Message in, RequestedReport rr) throws JMSException {
		try {
			JMSProducer producer = context.createProducer().setJMSCorrelationID(in.getJMSCorrelationID());
			// userTransaction.begin();
			Message message = context.createObjectMessage(rr);
			message.setJMSCorrelationID(in.getJMSCorrelationID());
			message.setStringProperty("foo", in.getJMSCorrelationID());
			producer.send(in.getJMSReplyTo(), message);
			// userTransaction.commit();
			return producer;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setJMSCorrelationID(String jmsCorrelationID) {
		// TODO Auto-generated method stub
		
	}
}
