import groovy.transform.BaseScript
import jenkins.model.*
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import core.JenkinsScript

@BaseScript JenkinsScript jenkinsScript

def workspace = new File("Tools")

def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)

// export variables  script
def jobScript = """
def systemFolder = 'Tools'

""" + new File("${Jenkins.get().getRootDir().absolutePath}/init.groovy.d/lib/create_job_update_plugins_versions.groovy").getText('UTF-8').stripIndent().trim()

new DslScriptLoader(jobManagement).runScript(jobScript)s
