#!groovy

import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage

println "[init.groovy.d] Starting script approval for JavaMelody monitoring..."

// Проверяем доступность Jenkins instance
def instance = Jenkins.getInstanceOrNull()
if (instance == null) {
    println "[init.groovy.d] WARNING: Jenkins instance not available yet, retrying..."
    // Небольшая задержка и повторная попытка
    Thread.sleep(2000)
    instance = Jenkins.getInstanceOrNull()
    if (instance == null) {
        println "[init.groovy.d] ERROR: Jenkins instance still not available. Script approval will be skipped."
        return
    }
}

// Проверяем доступность ScriptApproval
def approval = null
try {
    approval = ScriptApproval.get()
    if (approval == null) {
        println "[init.groovy.d] WARNING: ScriptApproval not available yet, retrying..."
        Thread.sleep(1000)
        approval = ScriptApproval.get()
    }
} catch (Exception e) {
    println "[init.groovy.d] ERROR: Cannot access ScriptApproval: ${e.message}"
    println "[init.groovy.d] This might happen if script-security plugin is not loaded yet."
    return
}

println "[init.groovy.d] ScriptApproval is available. Approving signatures..."

def signatures = [
    'new net.bull.javamelody.RemoteCallHelper java.lang.String',
    'method net.bull.javamelody.RemoteCallHelper collectJavaInformationsListByName',
    'method net.bull.javamelody.internal.model.JavaInformations getThreadInformationsList',
    'method net.bull.javamelody.internal.model.JavaInformations getHost',
    'method net.bull.javamelody.internal.model.JavaInformations getOs',
    'method net.bull.javamelody.internal.model.JavaInformations getJavaVersion',
    'method net.bull.javamelody.internal.model.JavaInformations getJvmVersion',
    'method net.bull.javamelody.internal.model.JavaInformations getPid',
    'method net.bull.javamelody.internal.model.JavaInformations getServerInfo',
    'method net.bull.javamelody.internal.model.JavaInformations getContextPath',
    'method net.bull.javamelody.internal.model.JavaInformations getStartDate',
    'method net.bull.javamelody.internal.model.JavaInformations getAvailableProcessors',
    'method net.bull.javamelody.internal.model.JavaInformations getSessionCount',
    'method net.bull.javamelody.internal.model.JavaInformations getActiveThreadCount',
    'method net.bull.javamelody.internal.model.JavaInformations getThreadCount',
    'method net.bull.javamelody.internal.model.JavaInformations getSystemLoadAverage',
    'method net.bull.javamelody.internal.model.JavaInformations getSystemCpuLoad',
    'method net.bull.javamelody.internal.model.JavaInformations getMemoryInformations',
    'method net.bull.javamelody.internal.model.MemoryInformations getUsedMemory',
    'method net.bull.javamelody.internal.model.MemoryInformations getMaxMemory',
    'method net.bull.javamelody.internal.model.MemoryInformations getUsedPermGen',
    'method net.bull.javamelody.internal.model.MemoryInformations getMaxPermGen',
    'method net.bull.javamelody.internal.model.MemoryInformations getUsedNonHeapMemory',
    'method net.bull.javamelody.internal.model.MemoryInformations getUsedPhysicalMemorySize',
    'method net.bull.javamelody.internal.model.MemoryInformations getUsedSwapSpaceSize',
    'method net.bull.javamelody.internal.model.ThreadInformations isDeadlocked',
    'method net.bull.javamelody.internal.model.ThreadInformations getStackTrace'
]

def approvedCount = 0
def failedCount = 0

signatures.each { sig ->
    try {
        // Просто вызываем approveSignature - если уже одобрено, метод не выбросит ошибку
        approval.approveSignature(sig)
        println "[init.groovy.d] ✓ Approved: ${sig}"
        approvedCount++
    } catch (Exception e) {
        // Если ошибка, выводим её, но продолжаем
        println "[init.groovy.d] ✗ Failed to approve ${sig}: ${e.message}"
        failedCount++
    }
}

println "[init.groovy.d] Done! Approved: ${approvedCount}, Failed: ${failedCount}"

