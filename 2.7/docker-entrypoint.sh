#!/bin/bash

#enable job control in script
set -e -m

#####   variables  #####

# add command if needed
if [ "${1:0:1}" = '-' ]; then
  set -- java "$@"
fi

#run command in background
if [ "$1" = 'java' ]; then
  ##### pre scripts  #####
  echo "========================================================================"
  echo "initialize:"
  echo "========================================================================"
  mkdir -p "$JENKINS_HOME"
  find /usr/share/jenkins/ref/ -type f -exec bash -c ". /usr/local/bin/jenkins-support; copy_reference_file '{}'" \;
  
  ##### run scripts  #####
  echo "========================================================================"
  echo "startup:"
  echo "========================================================================"
  exec "$@" &

  ##### post scripts #####
  echo "========================================================================"
  echo "configure:"
  echo "========================================================================"
  
  #bring command to foreground
  fg
else
  exec "$@"
fi
