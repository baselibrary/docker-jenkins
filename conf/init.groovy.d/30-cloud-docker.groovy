import jenkins.model.*
import hudson.util.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import com.nirima.jenkins.plugins.docker.*
import com.nirima.jenkins.plugins.docker.launcher.*
import com.nirima.jenkins.plugins.docker.strategy.*
import groovy.json.JsonSlurper

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
             template.dns,
             null,
             template.command,
             template.volumes,
             template.volumesFrom,
             template.environments,
             template.lxcConf,
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
          template.label,
          template.remoteFs,
          template.remoteFsMapping,
          template.instanceCap,
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
                       docker.containerCap ?: 50,
                       docker.connectTimeout ?: 15, // Well, it's one for the money...
                       docker.readTimeout ?: 15,    // Two for the show
                       docker.credentialsId ?: null,
                       null))


  }
}

instance.save()
