pipeline {
    agent { label 'Jenkins-Agent' }

    tools {
        maven 'Maven3'
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(name: 'BUILD_DOCKER', defaultValue: false,
                     description: 'Also build the Docker image (needs Docker on the agent)')
    }

    stages {
        stage('Cleanup Workspace') {
            steps { cleanWs() }
        }

        stage('Checkout from SCM') {
            steps { checkout scm }
        }

        stage('Check Tools') {
            steps {
                sh '''
                    java -version
                    javac -version || { echo "ERROR: agent has a JRE only. Run: sudo apt install -y openjdk-21-jdk"; exit 1; }
                    mvn -version
                '''
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test Application') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/accounting-app.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            when { expression { params.BUILD_DOCKER } }
            steps {
                sh 'docker build -t accounting-app:$BUILD_NUMBER .'
            }
        }
    }

    post {
        success { echo 'Pipeline passed.' }
        failure { echo 'Pipeline failed. Check the first red stage in the log.' }
    }
}
