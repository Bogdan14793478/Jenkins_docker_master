package core

class InfraConfig {
    private String identifier
    private Object javamelody
    private Object location
    private Object ec2Slaves
    private Object vSphereSlaves
    private Object credentials
    private Object infraRepo
    private String backupCronExpression
    private String cascConfigUrl
    private int maxLogSize = 0

    String getIdentifier() {
        return identifier
    }

    Object getJavamelody() {
        return javamelody
    }

    Object getLocation() {
        return location
    }

    Object getEc2Slaves() {
        return ec2Slaves
    }

    Object getvSphereSlaves() {
        return vSphereSlaves
    }

    Object getCredentials() {
        return credentials
    }

    Object getInfraRepo() {
        return infraRepo
    }

    String getBackupCronExpression() {
        return backupCronExpression
    }

    String getCascConfigUrl() {
        return cascConfigUrl
    }

    int getMaxLogSize() {
        return maxLogSize
    }
}