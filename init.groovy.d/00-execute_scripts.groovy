import jenkins.model.Jenkins

String groovyScriptsPath = "${Jenkins.getInstanceOrNull().getRootDir().getAbsolutePath()}/init.groovy.d"
ClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader())
classLoader.addURL(new File(groovyScriptsPath).toURI().toURL())
GroovyShell shell = new GroovyShell(classLoader, new Binding())
shell.parse(new File("${groovyScriptsPath}/core/Runner.groovy")).run()