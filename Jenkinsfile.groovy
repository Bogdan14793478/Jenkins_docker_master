node {
    String TAG = ''
    String docker_img_name = 'bogdan1chernet/jenkins-docker-master'
    stage('Checkout') {
        deleteDir() // Workdir cleanup
        def scmVars = checkout scm

        String REVISION = scmVars.GIT_COMMIT
        TAG = "${REVISION[0..7]}-${UUID.randomUUID().toString()[-12..-1]}"
        echo "Revision: ${REVISION}"
        echo "Tag: ${TAG}"
    }

    stage('Build') {
        sh "docker build -t ${docker_img_name}:${TAG} ."
    }

    stage('Publish') {
        withCredentials(
            [
                usernamePassword(
                    credentialsId: 'docker_hub_log',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )
            ]){
                sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                sh "docker push ${docker_img_name}:${TAG}"
        }
    }
}
