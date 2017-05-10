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
    def domain      = Domain.global()
    def store       = instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
    def credentials = CredentialsProvider.lookupCredentials(StandardUsernameCredentials.class, instance)

    state.credentials.each { item ->
      usernameAndPassword = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        item.id,          //ID
        item.description, //Description
        item.username,    //Username
        item.password     //Password
      )

      def credential = credentials.findResult { it.id == item.id ? it : null }
      if(credential) {
        store.updateCredentials(domain, credential, usernameAndPassword)
      }else {
        store.addCredentials(domain, usernameAndPassword) //return true or false
      }
    }
  }
}

instance.save()
