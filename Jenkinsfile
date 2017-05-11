#!/usr/bin/env groovy

properties([
    buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5')),
    pipelineTriggers([cron('@daily')]),
])

node('docker') {
    deleteDir()

    stage('checkout') {
      checkout scm
    }


    stage('build') {
      docker.build('jenkins')
    }

    stage('test') {
      sh """
        git submodule update --init --recursive
        git clone https://github.com/sstephenson/bats.git
        bats/bin/bats tests
        """
    }
}
