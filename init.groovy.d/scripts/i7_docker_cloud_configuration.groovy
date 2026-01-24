// package scripts

// import core.JenkinsScript
// import groovy.transform.BaseScript
// import jenkins.model.*
// import com.nirima.jenkins.plugins.docker.*
// import com.nirima.jenkins.plugins.docker.launcher.*
// import hudson.model.*
// import hudson.slaves.*

// @BaseScript JenkinsScript jenkinsScript

// def instance = getJenkins()

// // Проверяем, не настроен ли уже Docker Cloud
// def existingCloud = instance.clouds.find { it.name == 'Docker' }
// if (existingCloud) {
//     println "[init.groovy.d] Docker Cloud already configured. Skipping..."
//     return
// }

// println "[init.groovy.d] Configuring Docker Cloud..."

// try {
//     // Получаем URI Docker (по умолчанию unix:///var/run/docker.sock)
//     def dockerHost = System.getenv('DOCKER_HOST') ?: 'unix:///var/run/docker.sock'
    
//     // Создаем Docker Template Base (базовый образ для агентов)
//     def templateBase = new DockerTemplateBase('jenkins/inbound-agent:latest')
    
//     // Создаем Docker Launcher (JNLP для подключения агентов)
//     def launcher = new DockerComputerJNLPLauncher()
    
//     // Создаем Docker Template через пустой конструктор и установку свойств
//     // Это более надежный способ, так как конструктор может иметь разные сигнатуры в разных версиях плагина
//     def dockerTemplate = new DockerTemplate()
//     dockerTemplate.setTemplateBase(templateBase)
//     dockerTemplate.setLauncher(launcher)
//     dockerTemplate.setLabelString('docker-agent')
//     dockerTemplate.setRemoteFs('/home/jenkins/agent')
//     dockerTemplate.setInstanceCapStr('10')
    
//     // Создаем список шаблонов
//     List<DockerTemplate> templates = [dockerTemplate]
    
//     // Создаем Docker Cloud
//     def dockerCloud = new DockerCloud(
//         'Docker',           // Cloud name
//         templates,          // Templates
//         dockerHost,         // Docker host URI
//         '10',               // Container cap (String)
//         300,                // Connect timeout (int)
//         60                  // Read timeout (int)
//     )
    
//     // Добавляем Cloud в Jenkins
//     instance.clouds.add(dockerCloud)
//     instance.save()
    
//     println "[init.groovy.d] ✅ Docker Cloud configured successfully with ${templates.size()} template(s)"
//     println "[init.groovy.d] Docker host: ${dockerHost}"
//     println "[init.groovy.d] Template image: jenkins/inbound-agent:latest"
//     println "[init.groovy.d] Template label: docker-agent"
    
// } catch (Exception e) {
//     println "[init.groovy.d] ❌ ERROR: Failed to configure Docker Cloud: ${e.getMessage()}"
//     e.printStackTrace()
// }
