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
//        sh docker.build
        sh 'echo "Hello world"'
    }
}