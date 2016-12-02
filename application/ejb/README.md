# RequestReportMDB

Deploy only to node 1. 

Node1: mvn clean install wildfly:deploy -Dwildfly.port=9090

## Undeploy

Node1: mvn wildfly:undeploy -Dwildfly.port=9090
