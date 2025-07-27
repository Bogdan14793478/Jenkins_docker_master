import org.jenkinsci.plugins.scriptsecurity.scripts.*


folder('Tools') {
    description('Folder (Todo) for Jenkins system jobs')
    
    authorization {
        permissionAll('bogdan')
    }
}
