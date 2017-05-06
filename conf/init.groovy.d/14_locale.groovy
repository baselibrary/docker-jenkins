#!groovy
import jenkins.model.*

Jenkins instance = Jenkins.getInstance()

if ( instance.pluginManager.activePlugins.find { it.shortName == "locale" } != null ) {
  def plugin = instance.pluginManager.getPlugin('locale').getPlugin()
  plugin.setSystemLocale('en')
  plugin.ignoreAcceptLanguage = true
  plugin.save()
}
