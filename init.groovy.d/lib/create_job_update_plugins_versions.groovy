import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage

def jobScriptFile = new File("/opt/jenkins/init.groovy.d/lib/job_update_plugins.groovy")
if (jobScriptFile.exists()) {
    def script = jobScriptFile.text.stripIndent().trim()
    def hash = ScriptApproval.get().hash(script)
    if (!ScriptApproval.get().isApproved(hash)) {
        println "Approving plugin update script..."
        ScriptApproval.get().approveScript(hash)
    }
}


def script = new File("${Jenkins.get().getRootDir().absolutePath}/init.groovy.d/lib/job_update_plugins.groovy").getText('UTF-8').stripIndent().trim()

freeStyleJob('Tools/update_versions_plugins') {
    label('imperator')
    disabled(false)

    steps {
        systemGroovyCommand(script) {
            sandbox(false)
        }
    }
}

// script approval
ScriptApproval.PendingScript s = new ScriptApproval.PendingScript(script, GroovyLanguage.get(), ApprovalContext.create())
ScriptApproval.get().approveScript(s.getHash())

