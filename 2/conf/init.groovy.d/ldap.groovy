import jenkins.model.*;
import hudson.security.*;
import jenkins.security.plugins.ldap.*;
import hudson.util.Secret

def home_dir = System.getenv("JENKINS_HOME")
def instance = Jenkins.getInstance()
def properties = new ConfigSlurper().parse(new File("$home_dir/security.properties").toURI().toURL())

if (properties.ldap.enabled) {
  println "--> Configuring LDAP"
  def realm = new LDAPSecurityRealm(
      server                     = properties.ldap.server,
      rootDN                     = properties.ldap.rootDN,
      userSearchBase             = properties.ldap.userSearchBase,
      userSearch                 = properties.ldap.userSearchFilter,
      groupSearchBase            = properties.ldap.groupSearchBase,
      groupSearchFilter          = properties.ldap.groupSearchFilter,
      groupMembershipStrategy    = new FromUserRecordLDAPGroupMembershipStrategy(properties.ldap.groupSearchAttr),
      managerDN                  = properties.ldap.managerDN,
      managerPasswordSecret      = Secret.fromString(properties.ldap.managerPassword),
      inhibitInferRootDN         = false,
      disableMailAddressResolver = false,
      cache                      = null,
      EnvironmentProperty        = null,
      displayNameAttributeName   = "displayname",
      mailAddressAttributeName   = "mail",
      userIdStrategy             = IdStrategy.CASE_INSENSITIVE,
      groupIdStrategy            = IdStrategy.CASE_INSENSITIVE,
  )
  instance.setSecurityRealm(realm)

  def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
  instance.setAuthorizationStrategy(strategy)

  instance.save()
}
