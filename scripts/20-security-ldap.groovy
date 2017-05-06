import jenkins.model.*
import hudson.security.*

def home_dir  = System.getenv("JENKINS_HOME")

def instance = Jenkins.getInstance()
def properties = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())

if ( instance.pluginManager.activePlugins.find { it.shortName == "ldap" } != null ){
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
