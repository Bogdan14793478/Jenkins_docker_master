package scripts

import core.JenkinsScript

import jenkins.model.*

import hudson.model.*
import hudson.plugins.ec2.*
import hudson.plugins.ec2.util.MinimumNumberOfInstancesTimeRangeConfig

import groovy.transform.Field
import groovy.transform.BaseScript

import com.amazonaws.util.*
import com.amazonaws.regions.*
import com.amazonaws.services.ec2.model.*
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey

@BaseScript JenkinsScript jenkinsScript

@Field AmazonEC2 amazonEC2Client

amazonEC2Client = AmazonEC2ClientBuilder.defaultClient()
def instance = getJenkins()
def CLOUD_NAME = 'AWS';

def getRegion() {
    return Regions.getCurrentRegion().toString();
}

def getPrivateKey() {
    def path = "${getJenkinsHomePath()}/secrets/ec2_slaves.key"
    File file = new File(path)
    if (!file.exists()) {
        String keyPairName = "jenkins/keyPairName"
        println "Trying to deleted key pair named ${keyPairName}..."
        DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest(keyPairName)
        amazonEC2Client.deleteKeyPair(deleteKeyPairRequest)

        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest(keyPairName)
        CreateKeyPairResult createKeyPairResult = amazonEC2Client.createKeyPair(createKeyPairRequest)
        KeyPair keyPair = createKeyPairResult.getKeyPair()
        println "Successfully created key pair named ${keyPairName} with fingerprint: ${keyPair.getKeyFingerprint()}"


        String privateKey = keyPair.getKeyMaterial()
        file.write(privateKey, 'UTF-8')

        return privateKey
    }

    return file.getText('UTF-8')
}

def getPrivateKeyCredId() {
    def domain = Domain.global()
    def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
    def privateKeyString = new File("${getJenkinsHomePath()}/secrets/ec2_slaves.key").text
    def keySource = new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(privateKeyString)
    String credentialId = "jenkins_slave_private_key_cred_id"
    String credentialUserName = "ec2-user"
    String credentialDescription = "EC2 Cloud private key for Jenkins Slave instance"


    def privateKeyCredential = new BasicSSHUserPrivateKey(
            CredentialsScope.GLOBAL,
            credentialId,                // id
            credentialUserName,          // username
            keySource,                   // private key
            "",                          // passphrase
            credentialDescription        // description
    )

    store.addCredentials(domain, privateKeyCredential)

    return credentialId
}


EC2Cloud cloud = new EC2Cloud(
        // String cloudName
        CLOUD_NAME,
        // boolean useInstanceProfileForCredentials
        true,
        // String credentialsId
        null,
        // String region
        getRegion(),
        // String privateKey
        getPrivateKey(),
        // String PrivateKeyCredId
        getPrivateKeyCredId(),
        // String instanceCapStr
        null,
        // List<? extends SlaveTemplate> templates
        //TODO: need add shablon for create agent!!
        null,
        // String roleArn
        null,
        // String roleSessionName
        ''
);
instance.clouds.add(cloud);

instance.save();