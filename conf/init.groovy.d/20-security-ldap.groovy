import jenkins.model.*
import hudson.util.*
import hudson.security.*
import groovy.json.JsonSlurper
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher

// Helper Class
class Crypter {
  // Key must be exactly 16 bytes
  def expandKey (def secret) {
    for (def i=0; i<4; i++) {
      secret += secret
    }
    return secret.substring(0, 16)
  }

  // do the magic
  def encrypt (def plainText, def secret) {
    secret = expandKey(secret)
    def cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE")
    SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES")
    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(secret.getBytes("UTF-8")))

    return cipher.doFinal(plainText.getBytes("UTF-8")).encodeBase64().toString()
  }

  // undo the magic
  def decrypt (def cypherText, def secret) {
    byte[] decodedBytes = cypherText.decodeBase64()

    secret = expandKey(secret)
    def cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE")
    SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES")
    cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(secret.getBytes("UTF-8")))

    return new String(cipher.doFinal(decodedBytes), "UTF-8")
  }
}

//############### Main ###################//

Jenkins instance = Jenkins.getInstance()
def crypter = new Crypter()
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
          managerPassword = crypter.decrypt(state.security.ldap.managerPassword, "ThoughtWorks"),
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
