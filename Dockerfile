FROM jenkins/jenkins:2.504.3

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG=/opt/jenkins/jenkins.yaml

RUN mkdir -p /var/jenkins_home/init.groovy.d

COPY plugins.txt /opt/jenkins/plugins.txt
COPY init.groovy.d /var/jenkins_home/init.groovy.d/
COPY jenkins.yaml /opt/jenkins/jenkins.yaml

RUN jenkins-plugin-cli --plugin-file /opt/jenkins/plugins.txt
