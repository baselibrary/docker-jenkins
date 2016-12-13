FROM baselibrary/java:8
MAINTAINER ShawnMa <qsma@thoughtworks.com>

## Version
ENV JENKINS_MAJOR   2
ENV JENKINS_VERSION 2.19.4

## Repository
RUN \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D50582E6 &&\
  echo "deb http://pkg.jenkins-ci.org/debian-stable binary/" > /etc/apt/sources.list.d/jenkins.list

## Packages
RUN \
  apt-get update &&\
  apt-get install -y jenkins=$JENKINS_VERSION zip git cvs subversion mercurial &&\
  rm -rf /var/lib/apt/lists/*

## Configurations
ADD bin/*    /usr/local/bin/
ADD config/* /var/lib/jenkins/

## Environments
ENV JENKINS_HOME             /var/lib/jenkins
ENV JENKINS_UC               http://updates.jenkins.io
ENV JENKINS_SLAVE_AGENT_PORT 50000

RUN \
  mkdir -p /usr/share/jenkins/ref &&\
  /usr/local/bin/install-plugins.sh git subversion workflow-aggregator dashboard-view cloudbees-folder simple-theme docker ldap

## Scripts
ADD docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod a+x            /docker-entrypoint.sh

EXPOSE 8080 50000

VOLUME ["${JENKINS_HOME}"]

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["java", "-jar", "/usr/share/jenkins/jenkins.war"]
