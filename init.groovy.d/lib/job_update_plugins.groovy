import jenkins.model.*
import hudson.PluginWrapper
import java.util.logging.*

def jenkins = Jenkins.instance
def updateCenter = jenkins.getUpdateCenter()

def pluginsToUpdate = []

updateCenter.updateAllSites()

jenkins.pluginManager.plugins.each { PluginWrapper plugin ->
    def availableUpdate = plugin.getUpdateInfo()
    if (availableUpdate) {
        pluginsToUpdate << plugin.shortName
        availableUpdate.deploy()
    }
}

println(pluginsToUpdate)
