#!/bin/bash

#enable job control in script
set -e -m

#####   variables  #####
: ${VAULT_ADDR:="http://vault.thoughtworks.io:8200"}
: ${VAULT_TOKEN:="5d5ae0f7-8449-0c2f-12df-a6da82fb9869"}
: ${JAVA_OPTS:="-Djava.awt.headless=true"}
: ${JENKINS_OPTS:="-Djenkins.install.runSetupWizard=false"}

#run command in background
if [[ "$#" -lt 1 ]] || [[ "$1" == "--"* ]]; then
  ##### pre scripts  #####
  echo "========================================================================"
  echo "initialize:"
  echo "========================================================================"
  mkdir -p "$JENKINS_HOME"

  echo "update templates"
  confd -onetime -backend vault -node "$VAULT_ADDR" -auth-type token -auth-token "$VAULT_TOKEN"
  echo "update configurations"
  find /usr/share/jenkins/ref/ -type f -exec bash -c '. /usr/local/bin/jenkins-support; for arg; do copy_reference_file "$arg"; done' _ {} +

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
