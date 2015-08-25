FROM quay.io/baselibrary/java:8
MAINTAINER ShawnMa <qsma@thoughtworks.com>

ENV JENKINS_HOME /var/lib/jenkins

## Add repository
RUN \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D50582E6 &&\
  echo "deb http://pkg.jenkins-ci.org/debian binary/" > /etc/apt/sources.list.d/jenkins.list

## Install app
RUN \
  apt-get update &&\
  apt-get install -y git unzip jenkins=1.626 &&\
  unzip -qqc /usr/share/jenkins/jenkins.war WEB-INF/jenkins-cli.jar > /var/lib/jenkins/jenkins-cli.jar &&\
  rm -rf /var/lib/apt/lists/*

## Config apps
ADD files/config.xml   /var/lib/jenkins/config.xml
ADD files/download.xml /var/lib/jenkins/jenkins.model.DownloadSettings.xml
ADD files/theme.xml    /var/lib/jenkins/org.codefirst.SimpleThemeDecorator.xml

## Install scripts
ADD docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod a+x            /docker-entrypoint.sh

EXPOSE 8080 50000

VOLUME ["${JENKINS_HOME}"]

ENTRYPOINT ["tini", "--", "/docker-entrypoint.sh"]


