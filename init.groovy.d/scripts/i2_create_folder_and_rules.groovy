import core.JenkinsScript
import groovy.transform.BaseScript
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

@BaseScript JenkinsScript jenkinsScript
String jenkins_system_folder_name = 'Tools'

// Creating folder "_Jenkins_system" for all Jenkins system jobs
def workspace = new File(jenkins_system_folder_name)
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)

def create_folder = new File("${getJenkinsHomePath()}/init.groovy.d/lib/create_folder_tools.groovy").getText('UTF-8').trim()

println("Creating folder '${jenkins_system_folder_name}' for all Jenkins system jobs...")
new DslScriptLoader(jobManagement).runScript(create_folder)