pipeline {
    agent { label 'Jenkins-Agent' }

    tools {
        jdk 'Java21'
        maven 'Maven3'
    }

    environment {
        APP_NAME        = "accounting-app"
        RELEASE         = "1.0.0"
        DOCKER_USER     = "waqaszai"
        DOCKER_CRED_ID  = "dockerhub"
        IMAGE_NAME      = "${DOCKER_USER}/${APP_NAME}"
        IMAGE_TAG       = "${RELEASE}-${BUILD_NUMBER}"
    }

    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout from SCM") {
            steps {
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/waqasalik/Accounting-app'
            }
        }

        stage("Build Application") {
            steps {
                sh "mvn clean package"
            }
        }

        stage("Test Application") {
            steps {
                sh "mvn test"
            }
        }

        stage("SonarQube Analysis") {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: 'Jenkins-SonarQube-token') {
                        sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=accounting-app -Dsonar.projectName='Mini Accounting App'"
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("Build & Push Docker Image") {
            steps {
                script {
                    // Force lowercase to prevent Docker build errors
                    def imageName = "${DOCKER_USER}/${APP_NAME}".toLowerCase()
                    def fullImage = "${imageName}:${IMAGE_TAG}"
                    
                    docker.withRegistry('', DOCKER_CRED_ID) {
                        def dockerImage = docker.build(fullImage)
                        dockerImage.push("${IMAGE_TAG}")
                        dockerImage.push("latest")
                    }
                }
            }
        }

        stage("Trivy Scan") {
            steps {
                script {
                    def imageName = "${DOCKER_USER}/${APP_NAME}".toLowerCase()
                    sh "docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image ${imageName}:latest --no-progress --scanners vuln,secret,misconfig"
                }
            }
        }

        stage("Cleanup Artifacts") {
            steps {
                script {
                    def imageName = "${DOCKER_USER}/${APP_NAME}".toLowerCase()
                    sh "docker rmi ${imageName}:${IMAGE_TAG} || true"
                    sh "docker rmi ${imageName}:latest || true"
                }
            }
        }
    }
}
