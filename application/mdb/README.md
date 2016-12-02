# RequestReportMDB

Deploy only to node 2 and 3. 
```bash
mvn clean install wildfly:deploy -Dwildfly.port=10090
mvn clean install wildfly:deploy -Dwildfly.port=10190
```

## Undeploy

```bash
mvn wildfly:undeploy -Dwildfly.port=10090
mvn wildfly:undeploy -Dwildfly.port=10190
```

# Servlet
The servlets send messages automatically on reload.

http://localhost:8180/mdb/JMSClientServlet
http://localhost:8280/mdb/JMSClientServlet