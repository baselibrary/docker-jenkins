FROM baselibrary/java:8
MAINTAINER ShawnMa <qsma@thoughtworks.com>

## Version
ENV JENKINS_MAJOR   2
ENV JENKINS_VERSION 2.46.2

## Repository
RUN \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D50582E6 &&\
  echo "deb http://pkg.jenkins-ci.org/debian-stable binary/" > /etc/apt/sources.list.d/jenkins.list

## Environments
ENV JENKINS_HOME             /var/lib/jenkins
ENV JENKINS_UC               http://updates.jenkins-ci.org
ENV JENKINS_SLAVE_AGENT_PORT 50000

## Packages
RUN \
  apt-get update &&\
  apt-get install -y jenkins=$JENKINS_VERSION zip git cvs subversion mercurial &&\
  mkdir -p /usr/share/jenkins/ref/init.groovy.d &&\
  rm -rf /var/lib/apt/lists/*

## Scripts
COPY jenkins-support /usr/local/bin/jenkins-support
COPY jenkins.sh      /usr/local/bin/jenkins.sh
COPY plugins.sh      /usr/local/bin/plugins.sh

## Plugins
RUN \
  /usr/local/bin/plugins.sh git subversion workflow-aggregator dashboard-view cloudbees-folder token-macro simple-theme docker ldap

## Configurations
COPY scripts/*          /usr/share/jenkins/ref/init.groovy.d/
COPY jenkins.properties $JENKINS_HOME/

EXPOSE 8080 50000

VOLUME ["${JENKINS_HOME}"]

ENTRYPOINT ["/usr/local/bin/jenkins.sh"]
