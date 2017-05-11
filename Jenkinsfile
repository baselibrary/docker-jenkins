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

    if (!infra.isTrusted()) {
        /* Outside of the trusted.ci environment, we're building and testing
         * the Dockerful in this repository, but not publishing to docker hub
         */
        stage('Build') {
            docker.build('jenkins')
        }

        stage('Test') {
            sh """
            git submodule update --init --recursive
            git clone https://github.com/sstephenson/bats.git
            bats/bin/bats tests
            """
        }
    }
}
