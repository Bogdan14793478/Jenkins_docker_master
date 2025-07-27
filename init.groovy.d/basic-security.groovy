#!groovy

import jenkins.model.*
import hudson.security.*

// Get environment variables
def adminUser = System.getenv('JENKINS_ADMIN_USER')
def adminPass = System.getenv('JENKINS_ADMIN_PASS')

// Check if both variables are present
if (!adminUser || !adminPass) {
    println "[init.groovy.d] ERROR: Missing env variables JENKINS_ADMIN_USER and/or JENKINS_ADMIN_PASS"
    return
}

println "[init.groovy.d] Creating Jenkins admin user: ${adminUser}"

def instance = Jenkins.getInstance()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(adminUser, adminPass)
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

instance.save()

println "[init.groovy.d] Admin user created and security configured successfully."