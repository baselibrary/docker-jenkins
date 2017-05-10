import jenkins.model.*
import hudson.util.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import hudson.plugins.sshslaves.verifiers.*
import com.nirima.jenkins.plugins.docker.*
import com.nirima.jenkins.plugins.docker.launcher.*
import com.nirima.jenkins.plugins.docker.strategy.*
import groovy.json.JsonSlurper

//############### Main ###################//
Jenkins instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.json")
def state = new JsonSlurper().parse(file)

if ( instance.pluginManager.activePlugins.find { it.shortName == "docker-plugin" } != null && file.exists()) {
  if (state != null && state.cloud != null && state.cloud.docker != null) {
    def dockerClouds = []

    state.cloud.docker.each { docker ->
      def templates = []
      docker.templates.each { template ->
        def dockerTemplateBase = new DockerTemplateBase(
             image              = template.image,
             dnsString          = template.dns,
             network            = template.network,
             dockerCommand      = template.command,
             volumesString      = template.volumes,
             volumesFromString  = template.volumesFrom,
             environmentsString = template.environments,
             lxcConfString      = template.lxcConf,
             hostname           = template.hostname,
             memoryLimit        = template.memoryLimit,
             memorySwap         = template.memorySwap,
             cpuShares          = template.cpuShares,
             bindPorts          = template.bindPorts,
             bindAllPorts       = template.bindAllPorts,
             privileged         = template.privileged,
             tty                = template.tty,
             macAddress         = template.macAddress
        )

        def dockerTemplate = new DockerTemplate(
          dockerTemplateBase = dockerTemplateBase,
          labelString        = template.label,
          remoteFs           = template.remoteFs,
          remoteFsMapping    = template.remoteFsMapping,
          instanceCapStr     = template.instanceCap,
          mode               = hudson.model.Node.Mode.NORMAL,
          numExecutors       = 1,
          launcher           = new DockerComputerSSHLauncher(new SSHConnector(22, docker.credentialsId, "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8", "", "", "", null, 0, 0, new NonVerifyingKeyVerificationStrategy())),
          retentionStrategy  = new DockerOnceRetentionStrategy(10),
          removeVolumes      = false,
          pullStrategy       = DockerImagePullStrategy.PULL_LATEST
        )

        templates.add(dockerTemplate)
      }


      if(instance.clouds.getByName(docker.name)) {
        instance.clouds.remove(Jenkins.instance.clouds.getByName(docker.name));
      }
      instance.clouds.add(new DockerCloud(
                       name            = docker.name,
                       templates       = templates,
                       serverUrl       = docker.serverUrl ?: "unix:///var/run/docker.sock",
                       containerCap    = docker.containerCap ?: 50,
                       connectTimeout  = docker.connectTimeout ?: 15, // Well, it's one for the money...
                       readTimeout     = docker.readTimeout ?: 15,    // Two for the show
                       credentialsId   = docker.credentialsId ?: null,
                       version         = null))


    }
  }
}

instance.save()
