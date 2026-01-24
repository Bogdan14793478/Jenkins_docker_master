// package scripts

// import core.JenkinsScript
// import groovy.transform.BaseScript
// import jenkins.model.*
// import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
// import jenkins.branch.*
// import jenkins.plugins.git.GitSCMSource
// import org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory

// @BaseScript JenkinsScript jenkinsScript

// def instance = getJenkins()

// // Список репозиторіїв для створення multibranch jobs
// def repositories = [
//     [
//         name: "monitor-slaves",
//         url: "https://github.com/Bogdan14793478/monitor-slaves.git",
//         description: "Моніторинг Jenkins агентів"
//     ],
//     [
//         name: "monitor-master",
//         url: "https://github.com/Bogdan14793478/monitor-master.git",
//         description: "Healthcheck Jenkins Master"
//     ],
//     [
//         name: "stuck-jobs-detector",
//         url: "https://github.com/Bogdan14793478/stuck-jobs-detector.git",
//         description: "Виявлення завислих джобів"
//     ],
//     [
//         name: "terraform-deploy",
//         url: "https://github.com/Bogdan14793478/terraform-deploy.git",
//         description: "Запуск Terraform з параметрами"
//     ],
//     [
//         name: "jenkins-backup",
//         url: "https://github.com/Bogdan14793478/jenkins-backup.git",
//         description: "Бекап Jenkins Home"
//     ],
//     [
//         name: "agent-load-test",
//         url: "https://github.com/Bogdan14793478/agent-load-test.git",
//         description: "Навантажувальний тест Jenkins"
//     ],
//     [
//         name: "terraform-lint",
//         url: "https://github.com/Bogdan14793478/terraform-lint.git",
//         description: "Лінтер Terraform-кодів"
//     ]
// ]

// println "[init.groovy.d] Creating Multibranch Pipeline Jobs..."

// repositories.each { repo ->
//     try {
//         // Перевіряємо, чи job вже існує
//         def existingJob = instance.getItem(repo.name)
//         if (existingJob != null) {
//             println "[init.groovy.d] Job '${repo.name}' already exists. Skipping..."
//             return
//         }

//         println "[init.groovy.d] Creating multibranch pipeline job: ${repo.name}"

//         // Створюємо GitSCMSource
//         def gitSCMSource = new GitSCMSource(
//             repo.name,      // id
//             repo.url        // remote
//         )
//         gitSCMSource.setCredentialsId(null)  // Без credentials для публічних репозиторіїв
        
//         // Додаємо traits для discovery гілок та тегів
//         def branchDiscoveryTrait = new jenkins.plugins.git.traits.BranchDiscoveryTrait()
//         branchDiscoveryTrait.setStrategyId(1)  // Exclude branches that are also filed as PRs
//         gitSCMSource.setTraits([
//             branchDiscoveryTrait,
//             new jenkins.plugins.git.traits.TagDiscoveryTrait()
//         ])

//         // Створюємо BranchSource
//         def branchSource = new BranchSource(gitSCMSource)
//         branchSource.setStrategy(new DefaultBranchPropertyStrategy([]))

//         // Створюємо WorkflowMultiBranchProject через правильний конструктор
//         def multibranchProject = instance.createProject(
//             WorkflowMultiBranchProject.class,
//             repo.name
//         )
        
//         if (multibranchProject == null) {
//             throw new Exception("Failed to create WorkflowMultiBranchProject")
//         }
        
//         multibranchProject.setDescription(repo.description)
        
//         // Налаштовуємо сканування кожні 5 хвилин
//         def trigger = new com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger(
//             "H/5 * * * *",  // Cron: кожні 5 хвилин
//             300000          // 300 секунд = 5 хвилин
//         )
//         multibranchProject.addTrigger(trigger)

//         // Додаємо BranchSource
//         multibranchProject.getSourcesList().add(branchSource)

//         // Налаштовуємо factory для Jenkinsfile
//         def factory = new WorkflowBranchProjectFactory(multibranchProject, "Jenkinsfile")
//         multibranchProject.setProjectFactory(factory)
        
//         // Зберігаємо зміни
//         multibranchProject.save()
        
//         println "[init.groovy.d] ✅ Successfully created multibranch pipeline job: ${repo.name}"
        
//     } catch (Exception e) {
//         println "[init.groovy.d] ❌ ERROR: Failed to create job '${repo.name}': ${e.getMessage()}"
//         e.printStackTrace()
//     }
// }

// instance.save()
// println "[init.groovy.d] ✅ All multibranch pipeline jobs created successfully!"

