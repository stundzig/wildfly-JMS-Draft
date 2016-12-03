package de.itemis.wildfly.ejb.report;

import javax.ejb.Remote;
import javax.ejb.Stateless;


/**
 * creates the report
 * 
 * @author stundzig
 */
@Stateless
@Remote(ReportFactory.class)
public class ReportFactoryBean extends ReportFactoryImpl {
}
