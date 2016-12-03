/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.itemis.wildfly.mdb;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import de.itemis.wildfly.ejb.report.ReportFactory;
import de.itemis.wildfly.ejb.report.ReportFactoryBean;
import de.itemis.wildfly.ejb.request.ReportRequest;
import de.itemis.wildfly.ejb.request.ReportService;
import de.itemis.wildfly.ejb.request.RequestedReport;

/**
 * <p>
 * A simple Message Driven Bean that asynchronously receives and processes the
 * messages that are sent to the queue.
 * </p>
 */
@MessageDriven(name = "ReportRequestQueueMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:jboss/exported/jms/queue/reportRequest"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ReportRequestQueueMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(ReportRequestQueueMDB.class.toString());

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:global/ejb/ReportFactoryBean!de.itemis.wildfly.ejb.report.ReportFactory")//"ejb:ejb/ReportFactoryBean!" + ReportFactory.class.getName())
	private ReportFactory reportFactory;

	public void onMessage(final Message in) {
		try {
			LOGGER.info("onMessage: " + in);
			ReportRequest rr = (ReportRequest) ((ObjectMessage) in).getObject();
			// send back to the *reply to* destination
			LOGGER.info("factory: " + reportFactory);
			String reportId = reportFactory.create(in.getJMSCorrelationID(), rr, () -> false,
					(step, message) -> sendProgress(in, step, message));
			sendResult(in, reportId);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	private JMSProducer sendResult(Message in, String reportId) {
		LOGGER.info("sendResult: " + reportId);
		try {
			RequestedReport rr = new RequestedReport(in.getJMSCorrelationID());
			rr.setReportId(reportId);
			return send(in, rr);
		} catch (JMSException e) {
			throw new RuntimeException();
		}
	}

	private JMSProducer sendProgress(Message in, int step, String message) {
		LOGGER.info("sendProgress: " + step + " " + message);
		try {
			RequestedReport rr = new RequestedReport(in.getJMSCorrelationID());
			rr.setCurrentStep(step);
			rr.setCurrentMessage(message);
			return send(in, rr);
		} catch (JMSException e) {
			throw new RuntimeException();
		}
	}

	private JMSProducer send(Message in, RequestedReport rr) throws JMSException {
		return context.createProducer().setJMSCorrelationID(in.getJMSCorrelationID()).send(in.getJMSReplyTo(), rr);
	}
}
