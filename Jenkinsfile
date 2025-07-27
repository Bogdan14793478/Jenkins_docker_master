node {
    stage('Checkout') {
        git 'https://github.com/your-org/your-repo.git'
    }

    stage('Install dependencies') {
        sh 'npm install'
    }

    stage('Run tests') {
        sh 'npm test'
    }

    stage('Finish') {
        echo 'Pipeline finished.'
    }
}