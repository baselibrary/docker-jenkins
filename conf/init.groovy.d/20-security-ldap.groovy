import jenkins.model.*
import hudson.util.*
import hudson.security.*
import groovy.json.JsonSlurper

Jenkins instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.json")

if ( instance.pluginManager.activePlugins.find { it.shortName == "ldap" } != null && file.exists()) {
  def state = new JsonSlurper().parse(file)

  if (state != null) {
    // ldap
    if (state.security.ldap != null) {
      instance.securityRealm = new LDAPSecurityRealm(server = state.security.ldap.server,
          rootDN = state.security.ldap.rootDN,
          userSearchBase = state.security.ldap.userSearchBase,
          userSearch = state.security.ldap.userSearchFilter,
          groupSearchBase = state.security.ldap.groupSearchBase,
          groupSearchFilter = state.security.ldap.groupSearchFilter,
          groupMembershipFilter = null,
          managerDN = state.security.ldap.managerDN,
          managerPassword = state.security.ldap.managerPassword,
          inhibitInferRootDN = false,
          disableMailAddressResolver = false,
          cache = null)
      instance.authorizationStrategy = new FullControlOnceLoggedInAuthorizationStrategy()
    } else {
      instance.securityRealm = SecurityRealm.NO_AUTHENTICATION
      instance.authorizationStrategy = AuthorizationStrategy.UNSECURED
    }
  } else {
    instance.securityRealm = SecurityRealm.NO_AUTHENTICATION
    instance.authorizationStrategy = AuthorizationStrategy.UNSECURED
  }
}

instance.save()
