package core

import groovy.io.FileType
import groovy.transform.BaseScript

@BaseScript JenkinsScript jenkinsScript

try {
    // Get all filenames from scripts directory
    List<String> scripts = []
    new File("${getJenkinsHomePath()}/init.groovy.d/scripts").eachFile (FileType.FILES) {
        if (!it.getName().startsWith('i')) {
            return
        }
        scripts << it.getPath()
    }
    scripts.sort()

    // Execute scripts
    scripts.each { this.runScript(it) }
} catch (Exception exception) {
    // Stop Jenkins in case an exception occurs in one of the scripts
    println("An exception occurred during initialization")
    exception.printStackTrace()
    println("Init script and Jenkins exit now...")
}
