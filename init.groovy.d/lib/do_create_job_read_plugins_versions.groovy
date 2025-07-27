import jenkins.model.*

def pluginManager = Jenkins.instance.pluginManager
def plugins = pluginManager.plugins

def output = new StringBuilder()
plugins.each { plugin ->
    def name = plugin.getShortName()
    def version = plugin.getVersion()
    output.append("${name}:${version}\n")
}

println("Installed Jenkins Plugins and Versions:")
println(output)