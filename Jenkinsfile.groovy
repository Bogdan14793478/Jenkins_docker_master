node {
    stage('Checkout') {
        deleteDir() // Workdir cleanup
        def scmVars = checkout scm

        REVISION = scmVars.GIT_COMMIT
        TAG = "${REVISION[0..7]}-${UUID.randomUUID().toString()[-12..-1]}"
        echo "Revision: ${REVISION}"
        echo "Tag: ${TAG}"
    }
    stage('Build') {
        withCredentials(
                [
                        usernamePassword(
                                credentialsId: 'jenkins_docker_master_secret_docker_hub',
                                usernameVariable: 'DOCKER_USER',
                                passwordVariable: 'DOCKER_PASS'
                        )
                ]
        ) {
            sh """
        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

        docker build \
          -t bogdan1chernet/jenkins-docker-master:v${TAG} \
          --push .
        """
        }
    }
}

