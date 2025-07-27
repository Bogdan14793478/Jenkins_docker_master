package core

import groovy.json.JsonSlurper
import jenkins.model.Jenkins

abstract class JenkinsScript extends Script {
    private static InfraConfig config

    static InfraConfig getConfig() {
        if (!config) {
            Map<String, Object> infraConfigMap = [:]
            File infraConfFile = new File("${getJenkinsHomePath()}/infrastructure_config.json")
            if (infraConfFile.exists()) {
                infraConfigMap = new JsonSlurper().parse(infraConfFile) as Map
            }

            config = new InfraConfig(infraConfigMap)
        }

        return config
    }

    static Jenkins getJenkins() {
        return Jenkins.getInstanceOrNull()
    }

    static String getJenkinsHomePath() {
        return getJenkins().getRootDir().getAbsolutePath()
    }

    void runScript(String path, Map<String, Object> args){
        Binding bindings = new Binding(args)
        ClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader())
        classLoader.addURL(new File("${getJenkinsHomePath()}/init.groovy.d").toURI().toURL())
        GroovyShell shell = new GroovyShell(classLoader, bindings)
        File file = new File(path)
        try {
            if(file.exists()){
                println("Running script : ${file.getName()}")
                shell.parse(file).run()
            }
        } catch(Exception e) {
            println("Error running script : ${e.getMessage()}")

            throw e
        }
    }

    void runScript(String path) {
        runScript(path, [:])
    }
}
