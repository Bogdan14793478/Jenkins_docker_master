import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage



def script = new File("${Jenkins.get().getRootDir().absolutePath}/init.groovy.d/lib/do_create_job_read_plugins_versions.groovy").getText('UTF-8').stripIndent().trim()

freeStyleJob('Tools/get_versions_plugins') {
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

