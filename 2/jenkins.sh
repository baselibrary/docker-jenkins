#!/bin/bash

#enable job control in script
set -e -m

#####   variables  #####
: ${JAVA_OPTS:="-Djava.awt.headless=true"}
: ${JENKINS_OPTS:="-Djenkins.install.runSetupWizard=false"}

#####     checks   #####
find /usr/share/jenkins/ref/ -type f -exec bash -c '. /usr/local/bin/jenkins-support; for arg; do copy_reference_file "$arg"; done' _ {} +

#run command in background
if [[ "$#" -lt 1 ]] || [[ "$1" == "--"* ]]; then
  ##### pre scripts  #####
  echo "========================================================================"
  echo "initialize:"
  echo "========================================================================"
  mkdir -p "$JENKINS_HOME"

  ##### run scripts  #####
  echo "========================================================================"
  echo "startup:"
  echo "========================================================================"
  exec java "$JAVA_OPTS" -jar /usr/share/jenkins/jenkins.war "$JENKINS_OPTS" "$@" &

  ##### post scripts #####
  echo "========================================================================"
  echo "configure:"
  echo "========================================================================"

  #bring command to foreground
  fg
else
  exec "$@"
fi
