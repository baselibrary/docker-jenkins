#!/bin/bash

#enable job control in script
set -e -m

echo "                                                                                                        "
echo "████████╗██╗  ██╗ ██████╗ ██╗   ██╗ ██████╗ ██╗  ██╗████████╗██╗    ██╗ ██████╗ ██████╗ ██╗  ██╗███████╗"
echo "╚══██╔══╝██║  ██║██╔═══██╗██║   ██║██╔════╝ ██║  ██║╚══██╔══╝██║    ██║██╔═══██╗██╔══██╗██║ ██╔╝██╔════╝"
echo "   ██║   ███████║██║   ██║██║   ██║██║  ███╗███████║   ██║   ██║ █╗ ██║██║   ██║██████╔╝█████╔╝ ███████╗"
echo "   ██║   ██╔══██║██║   ██║██║   ██║██║   ██║██╔══██║   ██║   ██║███╗██║██║   ██║██╔══██╗██╔═██╗ ╚════██║"
echo "   ██║   ██║  ██║╚██████╔╝╚██████╔╝╚██████╔╝██║  ██║   ██║   ╚███╔███╔╝╚██████╔╝██║  ██║██║  ██╗███████║"
echo "   ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝   ╚═╝    ╚══╝╚══╝  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝"
echo "                                                                                                        "

#####   variables           #####
: ${JAVA_OPTS:="-Djava.awt.headless=true"}

#####   check & initialize  #####
echo "$JENKINS_VERSION" > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
echo "$JENKINS_VERSION" > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion
find /usr/share/jenkins/ref/ -type f -exec bash -c '. /usr/local/bin/jenkins-support; for arg; do copy_reference_file "$arg"; done' _ {} +

#run command in background
if [[ "$#" -lt 1 ]] || [[ "$1" == "--"* ]]; then
  ##### pre scripts  #####
  mkdir -p "$JENKINS_HOME"

  ##### run scripts  #####
  exec java "$JAVA_OPTS" -jar /usr/share/jenkins/jenkins.war "$@" &

  ##### post scripts #####

  #bring command to foreground
  fg
else
  exec "$@"
fi
