pipeline {
    agent any

    environment {
        SERVICE_NAME = 'ban-client-service'
        IMAGE_NAME = 'jcalderonmunir/ban-client-service'
        IMAGE_TAG = "${env.BRANCH_NAME}-${BUILD_NUMBER}".replaceAll('/', '-')
        SONAR_HOST_URL = 'http://sonarqube:9000'

        DB_URL = "jdbc:postgresql://postgres:5432/client_db"
        DB_USERNAME = "usuario"
        DB_PASSWORD = "contrasena_segura"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'find . -maxdepth 3 -name mvnw'
            }
        }

        stage('Validate Structure') {
            steps {
                sh 'test -f pom.xml'
                sh 'test -f mvnw'
                sh 'test -d src'
                sh 'test -f Dockerfile'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh '''
                export TESTCONTAINERS_RYUK_DISABLED=true
                export TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal
                ./mvnw test -Dspring.profiles.active=test
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: "target/surefire-reports/*.xml"
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'qa'
                    branch 'main'
                }
            }
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

        stage('Docker Hub Push') {
            when {
                anyOf {
                    branch 'qa'
                    branch 'main'
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy DEV') {
            when {
                branch 'develop'
            }
            steps {
                echo "Despliegue DEV para ${SERVICE_NAME}"
                // Aquí luego puedes agregar kubectl o docker compose para DEV
            }
        }

        stage('Deploy QA') {
            when {
                branch 'qa'
            }
            steps {
                echo "Despliegue QA para ${SERVICE_NAME}"
                // Aquí luego agregas kubeconfig-qa + kubectl/helm
            }
        }

        stage('Deploy PROD') {
            when {
                branch 'main'
            }
            steps {
                input message: '¿Confirmas despliegue a producción?'
                echo "Despliegue PROD para ${SERVICE_NAME}"
                // Aquí luego agregas kubeconfig-prod + kubectl/helm
            }
        }
    }

    post {
        success {
            echo "Pipeline ejecutado correctamente para ${SERVICE_NAME} en rama ${env.BRANCH_NAME}"
        }

        failure {
            echo "Error en pipeline para ${SERVICE_NAME} en rama ${env.BRANCH_NAME}"
        }

        always {
            cleanWs()
        }
    }
}