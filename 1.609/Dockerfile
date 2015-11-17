FROM baselibrary/java:8
MAINTAINER ShawnMa <qsma@thoughtworks.com>

## Version
ENV JENKINS_MAJOR   1.609
ENV JENKINS_VERSION 1.625.2

## Repository
RUN \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D50582E6 &&\
  echo "deb http://pkg.jenkins-ci.org/debian-stable binary/" > /etc/apt/sources.list.d/jenkins.list

## Packages
RUN \
  apt-get update &&\
  apt-get install -y jenkins=$JENKINS_VERSION git cvs subversion mercurial &&\
  rm -rf /var/lib/apt/lists/*

## Configurations
ADD config/config.xml          /var/lib/jenkins/config.xml
ADD config/download.xml        /var/lib/jenkins/jenkins.model.DownloadSettings.xml
ADD config/slave-agent.groovy  /var/lib/jenkins/init.groovy.d/slave-agent.groovy

## Environments
ENV JENKINS_HOME /var/lib/jenkins

## Scripts
ADD docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod a+x            /docker-entrypoint.sh

EXPOSE 8080 50000

VOLUME ["${JENKINS_HOME}"]

ENTRYPOINT ["tini", "--", "/docker-entrypoint.sh"]

CMD ["java", "-jar", "/usr/share/jenkins/jenkins.war"]


