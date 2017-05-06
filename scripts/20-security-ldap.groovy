import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.properties")

if ( instance.pluginManager.activePlugins.find { it.shortName == "ldap" } != null && file.exists()){
  def properties = new ConfigSlurper().parse(file.toURI().toURL())

  if (properties && properties.containsKey("security.ldap")) {
    instance.securityRealm = new LDAPSecurityRealm(
        server                     = properties.get("security.ldap.server"),
        rootDN                     = properties.get("security.ldap.rootDN"),
        userSearchBase             = properties.get("security.ldap.userSearchBase"),
        userSearch                 = properties.get("security.ldap.userSearchFilter"),
        groupSearchBase            = properties.get("security.ldap.groupSearchBase"),
        groupSearchFilter          = properties.get("security.ldap.groupSearchFilter"),
        groupMembershipStrategy    = null,
        managerDN                  = properties.get("security.ldap.managerDN"),
        managerPasswordSecret      = properties.get("security.ldap.managerPassword"),
        inhibitInferRootDN         = false,
        disableMailAddressResolver = false,
        cache                      = null
    )
    instance.save()
  }
}
