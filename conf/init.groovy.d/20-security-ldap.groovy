import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.properties")

if ( instance.pluginManager.activePlugins.find { it.shortName == "ldap" } != null && file.exists()){
  def config = new ConfigSlurper().parse(file.toURI().toURL())

  if (config && config.containsKey("security") ) {
    instance.securityRealm = new LDAPSecurityRealm(
        server                     = config.security.ldap.server,
        rootDN                     = config.security.ldap.rootDN,
        userSearchBase             = config.security.ldap.userSearchBase,
        userSearch                 = config.security.ldap.userSearchFilter,
        groupSearchBase            = config.security.ldap.groupSearchBase,
        groupSearchFilter          = config.security.ldap.groupSearchFilter,
        groupMembershipFilter      = null,
        managerDN                  = config.security.ldap.managerDN,
        managerPassword            = config.security.ldap.managerPassword,
        inhibitInferRootDN         = false,
        disableMailAddressResolver = false,
        cache                      = null
    )
    instance.save()
  }
}
