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

        INFRA_REPO = 'https://github.com/JCalderonUnir/ban-infrastructure.git'
        INFRA_BRANCH = 'main'
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

        stage('Create Image Pull Secret') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }

            steps {

                withCredentials([
                    file(
                        credentialsId: 'kubeconfig-k3s',
                        variable: 'KUBECONFIG_FILE'
                    ),
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    sh '''
                    export KUBECONFIG=$KUBECONFIG_FILE

                    if [ "$BRANCH_NAME" = "develop" ]; then
                        NAMESPACE=tfm-dev
                    elif [ "$BRANCH_NAME" = "qa" ]; then
                        NAMESPACE=tfm-qa
                    else
                        NAMESPACE=tfm-prod
                    fi

                    kubectl create secret docker-registry dockerhub-secret \
                    --docker-username=$DOCKER_USER \
                    --docker-password=$DOCKER_PASS \
                    --docker-email=devops@fincore.local \
                    -n $NAMESPACE \
                    --dry-run=client -o yaml | kubectl apply -f -
                    '''
                }
            }
        }
        stage('Update Infrastructure Repository') {
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
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )
                ]) {
                    sh '''
                    rm -rf infra-temp

                    git clone https://${GIT_USER}:${GIT_TOKEN}@github.com/JCalderonUnir/ban-infrastructure.git infra-temp

                    cd infra-temp

                    if [ "$BRANCH_NAME" = "develop" ]; then
                        PATCH_FILE="k8s/overlays/dev/client-service-patch.yaml"
                    elif [ "$BRANCH_NAME" = "qa" ]; then
                        PATCH_FILE="k8s/overlays/qa/client-service-patch.yaml"
                    else
                        PATCH_FILE="k8s/overlays/production/client-service-patch.yaml"
                    fi

                    sed -i "s|image: jcalderonmunir/ban-client-service:.*|image: ${IMAGE_NAME}:${IMAGE_TAG}|g" $PATCH_FILE

                    git config user.email "jenkins@nexcalder.dev"
                    git config user.name "Jenkins GitOps"

                    git add $PATCH_FILE

                    git commit -m "chore(gitops): update client-service image to ${IMAGE_TAG}" || echo "No changes to commit"

                    git push origin main
                    '''
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
