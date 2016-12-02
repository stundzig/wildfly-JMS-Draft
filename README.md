# Start each node in a separate shell:

Use always the latest config file. Maven deployments are changing the config files.

cp standalone/configuration/standalone-full-ha-c.xml standalone/configuration/standalone-full-ha-1.xml && bin/standalone.sh --server-config=standalone-full-ha-1.xml -Djboss.node.name=node1 

cp standalone/configuration/standalone-full-ha-c.xml standalone/configuration/standalone-full-ha-2.xml && bin/standalone.sh --server-config=standalone-full-ha-2.xml -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100

cp standalone/configuration/standalone-full-ha-c.xml standalone/configuration/standalone-full-ha-3.xml && bin/standalone.sh --server-config=standalone-full-ha-3.xml -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200

# Monitor the results:

Node1: http://localhost:9990/console/App.html#activemq-metrics;name=%2524%257Bjboss.node.name%257D

Node2: http://localhost:10090/console/App.html#activemq-metrics;name=%2524%257Bjboss.node.name%257D

Node3: http://localhost:10190/console/App.html#activemq-metrics;name=%2524%257Bjboss.node.name%257D

User: admin:admin

# Deployment

## MDB
cd application/mdb

mvn clean install wildfly:deploy -Dwildfly.port=10090

mvn clean install wildfly:deploy -Dwildfly.port=10190

## EJB
cd application/ejb

mvn clean install wildfly:deploy -Dwildfly.port=9090

## Client
cd application/client

mvn clean package exec:exec

# eclipse
cd application

mvn eclipse:clean eclipse:eclipse


