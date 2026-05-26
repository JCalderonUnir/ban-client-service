pipeline {
    agent any

    environment {
        SERVICE_NAME = 'ban-client-service'
        SERVICE_DIR = 'client-service'
        IMAGE_NAME = 'jcalderonmunir/ban-client-service'
        IMAGE_TAG = "${BUILD_NUMBER}"
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                    sh 'ls -la'
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                    sh './mvnw test'
            }
            post {
                always {
                    junit "${SERVICE_NAME}/target/surefire-reports/*.xml"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=${SERVICE_NAME} \
                        -Dsonar.projectName=${SERVICE_NAME} \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.token=${SONAR_TOKEN}
                        """
                    }
            }
        }

        stage('Docker Build') {
            steps {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        // stage('Docker Push') {

        //     when {
        //         anyOf {
        //             branch 'qa'
        //             branch 'main'
        //         }
        //     }

        //     steps {

        //         withCredentials([
        //             usernamePassword(
        //                 credentialsId: 'dockerhub-credentials',
        //                 usernameVariable: 'DOCKER_USER',
        //                 passwordVariable: 'DOCKER_PASS'
        //             )
        //         ]) {

        //             sh '''
        //             echo $DOCKER_PASS | docker login \
        //             -u $DOCKER_USER \
        //             --password-stdin
        //             '''

        //             sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
        //             sh "docker push ${IMAGE_NAME}:latest"
        //         }
        //     }
        // }

        // stage('Deploy DEV') {

        //     when {
        //         branch 'develop'
        //     }

        //     steps {
        //         echo 'Deploy ambiente desarrollo'
        //     }
        // }

        // stage('Deploy QA') {

        //     when {
        //         branch 'qa'
        //     }

        //     steps {
        //         echo 'Deploy ambiente QA'
        //     }
        // }

        // stage('Deploy Production') {

        //     when {
        //         branch 'main'
        //     }

        //     steps {

        //         input(
        //             message: '¿Desea desplegar a producción?',
        //             ok: 'Deploy'
        //         )

        //         echo 'Deploy producción'
        //     }
        // }
    }

    post {
        success {
            echo "Pipeline ejecutado correctamente para ${SERVICE_NAME}"
        }
        failure {
            echo "Error en el pipeline de ${SERVICE_NAME}"
        }
        always {
            cleanWs()
        }
    }
}
