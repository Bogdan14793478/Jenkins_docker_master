import io.jenkins.plugins.casc.ConfigurationAsCode

println("---> Reloading Jenkins Configuration as Code (JCasC)")

Thread.start {
    sleep(10000)

    try {
        ConfigurationAsCode.get().configure()
        println("---> JCasC configuration reloaded successfully.")
    } catch (Exception e) {
        println("---> Failed to reload JCasC configuration:")
        e.printStackTrace()
    }
}