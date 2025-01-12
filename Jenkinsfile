pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'parkheejun/gta'
        NETWORK_NAME = 'jenkins'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/LetsGTA/server',
                credentialsId: 'github'
            }
        }

        stage('Setup Properties') {
            steps {
                withCredentials([
                file(credentialsId: 'application-yml', variable: 'APP_YML'),
                file(credentialsId: 'application-prod-yml', variable: 'APP_PROD_YML'),
                file(credentialsId: 'application-test-yml', variable: 'APP_TEST_YML')
                ]) {
                    sh '''
                    mkdir -p /var/jenkins_home/workspace/sonarqube_main/src/main/resources
                    chmod 755 /var/jenkins_home/workspace/sonarqube_main/src/main/resources
                    cp ${APP_YML} /var/jenkins_home/workspace/sonarqube_main/src/main/resources/application.yml
                    cp ${APP_PROD_YML} /var/jenkins_home/workspace/sonarqube_main/src/main/resources/application-prod.yml
                    cp ${APP_TEST_YML} /var/jenkins_home/workspace/sonarqube_main/src/main/resources/application-test.yml
                    '''
                }
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([
                    string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN'),
                    string(credentialsId: 'sonar-host-url', variable: 'SONAR_URL'),
                    string(credentialsId: 'sonar-projectKey', variable: 'SONAR_PROJECT_KEY'),
                    string(credentialsId: 'sonar-projectName', variable: 'SONAR_PROJECT_NAME')
                    ]) {
                    sh '''
                    ./gradlew sonar \
                    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                    -Dsonar.projectName=$SONAR_PROJECT_NAME \
                    -Dsonar.host.url=$SONAR_URL \
                    -Dsonar.token=$SONAR_TOKEN \
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('Deploy Spring Boot Container') {
            steps {
                script {
                    try {
                    sh '''
                    docker stop back-end-container || true
                    docker rm back-end-container || true
                    docker run -d --name back-end-container \
                    --network ${NETWORK_NAME} \
                    -p 8081:8081 \
                    ${DOCKER_IMAGE}:latest
                    '''
                    } catch (Exception e) {
                    sh '''
                    docker start back-end-container || true
                    '''
                    }
                }
            }
        }
    }

    post {
        success {
            withCredentials([
                string(credentialsId: 'discord-webhook', variable: 'DISCORD')
            ]) {
                discordSend description:"""
                    결과: ${currentBuild.currentResult}
                    실행 시간 : ${currentBuild.duration / 1000}s
                    Jenkins 에서 확인: ${env.BUILD_URL}
                """,
                footer: "Jenkins CI/CD 시스템",
                link: env.BUILD_URL,
                title: "[성공] ${env.JOB_NAME} - #${env.BUILD_NUMBER}",
                webhookURL: "$DISCORD"
            }
        }

        failure {
            withCredentials([
                string(credentialsId: 'discord-webhook', variable: 'DISCORD')
            ]) {
                discordSend description:"""
                    결과: ${currentBuild.currentResult}
                    원인: ${currentBuild.getDescription() ?: "원인을 확인하려면 Jenkins 를 참조하세요."}
                    실행 시간 : ${currentBuild.duration / 1000}s
                    Jenkins 에서 확인: ${env.BUILD_URL}
                    """,
                footer: "Jenkins CI/CD 시스템",
                link: env.BUILD_URL,
                title: "[실패] ${env.JOB_NAME} - #${env.BUILD_NUMBER}",
                webhookURL: "$DISCORD"
            }
        }
    }
}
