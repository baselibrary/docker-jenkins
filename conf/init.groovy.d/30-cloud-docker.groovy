import jenkins.model.*
import hudson.util.*
import hudson.security.*
import groovy.json.JsonSlurper
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import hudson.plugins.sshslaves.SSHConnector
import hudson.plugins.sshslaves.SSHLauncher
import hudson.model.Hudson
import hudson.slaves.Cloud
import java.net.*

import com.nirima.jenkins.plugins.docker.*
import com.nirima.jenkins.plugins.docker.launcher.*
import com.nirima.jenkins.plugins.docker.strategy.*

//############### Main ###################//

Jenkins instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.json")
def state = new JsonSlurper().parse(file)

if (state != null && state.cloud != null && state.cloud.docker != null) {
  def dockerClouds = []

  state.cloud.docker.each { docker ->
    def templates = []
    docker.templates.each { template ->
      def dockerTemplateBase = new DockerTemplateBase(
             template.image,
             template.dnsString,
             null,
             template.dockerCommand,
             template.volumesString,
             template.volumesFromString,
             template.environmentsString,
             template.lxcConfString,
             template.hostname,
             template.memoryLimit,
             template.memorySwap,
             template.cpuShares,
             template.bindPorts,
             template.bindAllPorts,
             template.privileged,
             template.tty,
             template.macAddress
      )

      def dockerTemplate = new DockerTemplate(
          dockerTemplateBase,
          template.labelString,
          template.remoteFs,
          template.remoteFsMapping,
          template.instanceCapStr,
          Node.Mode.EXCLUSIVE,
          1,
          new DockerComputerSSHLauncher(new SSHConnector(22, docker.credentialsId, "", "", "", "", null, 0, 0)),
          new DockerOnceRetentionStrategy(10),
          false,
          DockerImagePullStrategy.PULL_LATEST
        )

      templates.add(dockerTemplate)
    }


    if(instance.clouds.getByName(docker.name)) {
      instance.clouds.remove(Jenkins.instance.clouds.getByName(docker.name));
    }
    instance.clouds.add(new DockerCloud(
                       docker.name,
                       templates,
                       docker.serverUrl ?: "unix:///var/run/docker.sock",
                       docker.containerCapStr ?: 50,
                       docker.connectTimeout ?: 15, // Well, it's one for the money...
                       docker.readTimeout ?: 15,    // Two for the show
                       docker.credentialsId ?: null,
                       null))


  }
}

instance.save()