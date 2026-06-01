pipeline {
    agent any

    environment {
        SERVICE_NAME = 'ban-client-service'
        IMAGE_NAME = 'jcalderonmunir/ban-client-service'

        IMAGE_TAG = "${env.BRANCH_NAME}-${BUILD_NUMBER}"
            .replaceAll('/', '-')

        SONAR_HOST_URL = 'http://sonarqube:9000'

        DB_URL = 'jdbc:postgresql://postgres:5432/client_db'
        DB_USERNAME = 'usuario'
        DB_PASSWORD = 'contrasena_segura'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Validate Files') {
            steps {
                sh '''
                test -f pom.xml
                test -f Dockerfile
                test -f mvnw
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                chmod +x mvnw
                ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                export TESTCONTAINERS_RYUK_DISABLED=true

                # Ejecutar pruebas
                ./mvnw test \
                -Dspring.profiles.active=test
                '''
            }

            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                    )
                }
            }
        }

        stage('SonarQube') {
            when {
                anyOf {
                    changeRequest()
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }

            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonarqube-token',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
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

        stage('Quality Gate') {
            when {
                anyOf {
                    changeRequest()
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }

            steps {
                sh """
                docker build \
                -t ${IMAGE_NAME}:${IMAGE_TAG} .
                """

                sh """
                docker tag \
                ${IMAGE_NAME}:${IMAGE_TAG} \
                ${IMAGE_NAME}:latest
                """
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
            echo """
            Pipeline ejecutado correctamente
            Rama: ${env.BRANCH_NAME}
            """
        }

        failure {
            echo """
            Error en pipeline
            Rama: ${env.BRANCH_NAME}
            """
        }

        always {
            cleanWs()
        }
    }
}
