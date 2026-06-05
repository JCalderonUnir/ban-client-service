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

        // stage('Quality Gate') {
        //     when {
        //         anyOf {
        //             changeRequest()
        //             branch 'develop'
        //             branch 'qa'
        //             branch 'main'
        //         }
        //     }
        //     steps {
        //         timeout(time: 5, unit: 'MINUTES') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

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
                -t ${IMAGE_NAME}:${IMAGE_TAG} \
                .
                """
            }
        }

        stage('Docker Tag') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }

            steps {
                script {
                    def ENV_TAG = ""

                    if (env.BRANCH_NAME == 'develop') {
                        ENV_TAG = "dev"
                    } else if (env.BRANCH_NAME == 'qa') {
                        ENV_TAG = "qa"
                    } else if (env.BRANCH_NAME == 'main') {
                        ENV_TAG = "latest"
                    }

                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:${ENV_TAG}"
                }
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }

            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    echo $DOCKER_PASS | docker login \
                    -u $DOCKER_USER \
                    --password-stdin
                    '''

                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"

                    script {
                        def ENV_TAG = ""

                        if (env.BRANCH_NAME == 'develop') {
                            ENV_TAG = "dev"
                        } else if (env.BRANCH_NAME == 'qa') {
                            ENV_TAG = "qa"
                        } else if (env.BRANCH_NAME == 'main') {
                            ENV_TAG = "latest"
                        }

                        sh "docker push ${IMAGE_NAME}:${ENV_TAG}"
                    }
                }
            }
        }

        stage('Deploy Kubernetes') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }
        
            steps {
                script {
                    def kubeCredential = ''
                    def overlayPath = ''
        
                    if (env.BRANCH_NAME == 'develop') {
                        kubeCredential = 'kubeconfig-dev'
                        overlayPath = 'k8s/overlays/dev'
                    } else if (env.BRANCH_NAME == 'qa') {
                        kubeCredential = 'kubeconfig-qa'
                        overlayPath = 'k8s/overlays/qa'
                    } else if (env.BRANCH_NAME == 'main') {
                        kubeCredential = 'kubeconfig-prod'
                        overlayPath = 'k8s/overlays/production'
                    }
        
                    withCredentials([file(credentialsId: kubeCredential, variable: 'KUBECONFIG_FILE')]) {
                        sh """
                        export KUBECONFIG=$KUBECONFIG_FILE
                        kubectl apply -k ${overlayPath}
                        kubectl rollout status deployment/client-service -n tfm-${env.BRANCH_NAME == 'main' ? 'prod' : env.BRANCH_NAME}
                        """
                    }
                }
            }
        }
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
