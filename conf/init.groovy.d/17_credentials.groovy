import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*
import groovy.json.JsonSlurper

//############### Main ###################//

Jenkins instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.json")

if ( instance.pluginManager.activePlugins.find { it.shortName == "credentials" } != null && file.exists()) {
  def state = new JsonSlurper().parse(file)

  if (state != null && state.credentials != null) {
    domain = Domain.global()
    store  = Jenkins.getInstance().getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

    state.credentials.each {
      usernameAndPassword = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        it.id, //ID
        it.description, //Description
        it.username, //Username
        it.password //Password
      )

      store.addCredentials(domain, usernameAndPassword) //return true or false
    }
  }
}

instance.save()
