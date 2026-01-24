 package scripts

 import core.JenkinsScript
 import groovy.transform.BaseScript
 import jenkins.model.*
 import org.jenkinsci.plugins.workflow.libs.*
 import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
 import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
 import jenkins.plugins.git.GitSCMSource

 @BaseScript JenkinsScript jenkinsScript

 def instance = getJenkins()

 // Получаем URL и ветку Shared Library из переменных окружения
 def sharedLibraryUrl = System.getenv('SHARED_LIBRARY_REPO_URL')
 def sharedLibraryBranch = System.getenv('SHARED_LIBRARY_BRANCH') ?: 'main'

 if (!sharedLibraryUrl) {
     println "[init.groovy.d] WARNING: SHARED_LIBRARY_REPO_URL not set. Skipping Shared Library configuration."
     return
 }

 println "[init.groovy.d] Configuring Shared Library..."
 println "[init.groovy.d] Repository URL: ${sharedLibraryUrl}"
 println "[init.groovy.d] Branch: ${sharedLibraryBranch}"

 try {
     // Получаем дескриптор Global Libraries
     def globalLibraries = instance.getDescriptorByType(GlobalLibraries.class)
    
     // Создаем GitSCMSource для Shared Library
     def gitSCMSource = new GitSCMSource(
         'shared-library',      // id
         sharedLibraryUrl        // remote
     )
     gitSCMSource.setCredentialsId(null)  // Можно указать credentialsId если нужны
    
     // Создаем SCMSourceRetriever
     def scmRetriever = new SCMSourceRetriever(gitSCMSource)
    
     // Создаем LibraryConfiguration
     def libraryConfiguration = new LibraryConfiguration(
         'shared-library',      // name
         scmRetriever           // retriever
     )
     libraryConfiguration.setDefaultVersion(sharedLibraryBranch)
     libraryConfiguration.setImplicit(true)  // Автоматически доступна во всех pipelines
    
     // Устанавливаем библиотеки
     globalLibraries.setLibraries([libraryConfiguration])
     instance.save()
    
     println "[init.groovy.d] Shared Library configured successfully!"
     println "[init.groovy.d] Library name: shared-library"
     println "[init.groovy.d] Default version: ${sharedLibraryBranch}"
    
 } catch (Exception e) {
     println "[init.groovy.d] ERROR: Failed to configure Shared Library: ${e.getMessage()}"
     e.printStackTrace()
 }

