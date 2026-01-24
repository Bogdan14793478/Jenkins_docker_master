#!groovy

import jenkins.model.*

println "[init.groovy.d] Disabling CSRF protection for Terraform provider compatibility..."

def instance = Jenkins.getInstance()

// Отключаем CSRF protection
instance.setCrumbIssuer(null)
instance.save()

println "[init.groovy.d] CSRF protection disabled successfully."

