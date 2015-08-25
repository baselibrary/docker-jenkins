#!/bin/bash

#enable job control in script
set -e -m

#run java in background
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]]; then
  #####   variables  #####
  
  
  ##### pre scripts  #####
  echo "========================================================================"
  echo "Prepare: configure the environment:"
  echo "========================================================================"

  ##### run scripts  #####
  exec java $JAVA_OPTS -jar /usr/share/jenkins/jenkins.war $JENKINS_OPTS "$@" &
  until [ "$(curl --silent --head --location --output /dev/null --write-out '%{http_code}' http://localhost:8080/ | grep 200)" ]; do
    echo 'Jenkins init process in progress...'
    sleep 5s
  done
  curl -s -L http://updates.jenkins-ci.org/update-center.json | sed '1d;$d' | curl -X POST -H 'Accept: application/json' -d @- http://localhost:8080/updateCenter/byId/default/postBack

  ##### post scripts #####
  echo "========================================================================"
  echo "Post: install the plugins:"
  echo "========================================================================"
  java -jar $JENKINS_HOME/jenkins-cli.jar -s http://localhost:8080 install-plugin dotci jquery gravatar simple-theme-plugin -restart
 
  echo 'Jenkins init process complete'
  #bring java to foreground
  fg
else
  exec "$@"
fi

