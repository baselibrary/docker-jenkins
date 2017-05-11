FROM baselibrary/java:8
MAINTAINER ShawnMa <qsma@thoughtworks.com>

## Version
ENV JENKINS_MAJOR   2
ENV JENKINS_VERSION 2.46.2

## Environments
ENV JENKINS_HOME             /var/lib/jenkins
ENV JENKINS_UC               https://updates.jenkins.io
ENV JENKINS_SLAVE_AGENT_PORT 50000

## Arguments
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

## User
RUN \
  groupadd -g ${gid} ${group} &&\
  useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

## Repository
RUN \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D50582E6 &&\
  echo "deb http://pkg.jenkins-ci.org/debian-stable binary/" > /etc/apt/sources.list.d/jenkins.list

## Packages
RUN \
  apt-get update &&\
  apt-get install -y jenkins=$JENKINS_VERSION zip git cvs subversion mercurial &&\
  rm -rf /var/lib/apt/lists/*

## Scripts
COPY bin/* /usr/local/bin/

## Provisions
RUN \
  mkdir -p /usr/share/jenkins/ref &&\
  chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref

## Plugins
RUN \
  /usr/local/bin/plugins.sh git subversion workflow-aggregator dashboard-view cloudbees-folder token-macro simple-theme docker ldap

## Configurations
ADD conf /usr/share/jenkins/ref

USER ${user}

EXPOSE 8080 50000

VOLUME ["${JENKINS_HOME}"]

ENTRYPOINT ["tini", "--", "/usr/local/bin/jenkins.sh"]
