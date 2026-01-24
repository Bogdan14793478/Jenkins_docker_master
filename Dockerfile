FROM jenkins/jenkins:2.504.3

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
USER root

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /var/jenkins_home/init.groovy.d

COPY plugins.txt /opt/jenkins/plugins.txt
COPY init.groovy.d $REF/init.groovy.d/


RUN jenkins-plugin-cli --plugin-file /opt/jenkins/plugins.txt