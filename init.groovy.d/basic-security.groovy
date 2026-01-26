#!groovy

import jenkins.model.*
import hudson.security.*

// Get environment variables
// def adminUser = "admin"
// def adminPass = "admin123"
def adminUser = System.getenv("JENKINS_ADMIN_USER")
def adminPass = System.getenv("JENKINS_ADMIN_PASS")

// Check if both variables are present
if (!adminUser || !adminPass) {
    println "[init.groovy.d] ERROR: Missing env variables JENKINS_ADMIN_USER and/or JENKINS_ADMIN_PASS"
    return
}

println "[init.groovy.d] Creating Jenkins admin user: ${adminUser}"

def instance = Jenkins.getInstance()

// Создаем security realm и пользователя
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(adminUser, adminPass)
instance.setSecurityRealm(hudsonRealm)

// def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
// strategy.setAllowAnonymousRead(false)
// instance.setAuthorizationStrategy(strategy)
// НЕ устанавливаем стратегию авторизации здесь - она будет настроена через CASC (jenkins.yaml)
// Это позволяет CASC управлять авторизацией через projectMatrix
// Если CASC не загрузится, можно установить временную стратегию для доступа
// Но лучше полагаться на CASC для консистентности

instance.save()

//println "[init.groovy.d] Admin user created and security configured successfully."
println "[init.groovy.d] Admin user created successfully. Authorization strategy will be configured via CASC (jenkins.yaml)."