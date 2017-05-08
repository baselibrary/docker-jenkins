import jenkins.model.*
import hudson.security.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

Jenkins instance = Jenkins.getInstance()
def file = new File("/usr/share/jenkins/rancher/jenkins.groovy")

if ( instance.pluginManager.activePlugins.find { it.shortName == "ldap" } != null && file.exists()){
  def config = new ConfigSlurper().parse(file.toURI().toURL())
  def cipher = Cipher.getInstance("AES")

  cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("1234567812345678".bytes, "AES"))
  if (config && config.security.ldap != null && config.security.ldap.enabled) {
    instance.securityRealm = new LDAPSecurityRealm(
        server                     = config.security.ldap.server,
        rootDN                     = config.security.ldap.rootDN,
        userSearchBase             = config.security.ldap.userSearchBase,
        userSearch                 = config.security.ldap.userSearchFilter,
        groupSearchBase            = config.security.ldap.groupSearchBase,
        groupSearchFilter          = config.security.ldap.groupSearchFilter,
        groupMembershipFilter      = null,
        managerDN                  = config.security.ldap.managerDN,
        managerPassword            = new String(cipher.doFinal(config.security.ldap.managerPassword.bytes)),
        inhibitInferRootDN         = false,
        disableMailAddressResolver = false,
        cache                      = null
    )
    instance.authorizationStrategy = new FullControlOnceLoggedInAuthorizationStrategy()
  }else {
    instance.securityRealm = SecurityRealm.NO_AUTHENTICATION
    instance.authorizationStrategy = AuthorizationStrategy.UNSECURED
  }
} else {
  instance.securityRealm = SecurityRealm.NO_AUTHENTICATION
  instance.authorizationStrategy = AuthorizationStrategy.UNSECURED
}

instance.save()
