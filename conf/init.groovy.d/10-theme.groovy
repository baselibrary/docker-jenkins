import jenkins.model.*

Jenkins instance = Jenkins.getInstance()

if ( instance.pluginManager.activePlugins.find { it.shortName == "simple-theme-plugin" } != null ){
  def theme = instance.getExtensionList("org.codefirst.SimpleThemeDecorator")[0]
  theme.cssUrl = '/jenkins.css'
  theme.save()
}
