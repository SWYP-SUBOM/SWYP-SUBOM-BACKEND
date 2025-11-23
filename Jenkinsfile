pipeline {
    agent any

    environment {
        PATH = "/usr/libexec/docker/cli-plugins:/usr/bin:/usr/local/bin:/bin"

        PROJECT_NAME = "seobom-backend"
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'release'}"

        TEST_COMPOSE_PATH = "${WORKSPACE}/docker/docker-compose-test.yml"
        PROD_COMPOSE_PATH = "${WORKSPACE}/docker/docker-compose-prod.yml"

        PROD_SSH_USER = credentials('prod-ssh-user')
        PROD_SSH_HOST = credentials('prod-ssh-host')
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo "Current Branch: ${BRANCH_NAME}"
                git branch: "${BRANCH_NAME}",
                    url: "https://github.com/SWYP-SUBOM/SWYP-SUBOM-BACKEND.git",
                    credentialsId: 'github-cred'
            }
        }

        stage('Create Properties') {
            steps{
                script {
                    if (BRANCH_NAME == "release") {
                        echo "TEST 환경 application-test.properties 복사"
                        withCredentials([file(credentialsId: 'app-props-test', variable: 'APP_PROPS')]) {
                            sh '''
                                mkdir -p ./src/main/resources
                                cp "$APP_PROPS" ./src/main/resources/application-test.properties
                            '''
                        }
                    } else if (BRANCH_NAME == "main") {
                        echo "PROD 환경 application-prod.properties 복사"
                        withCredentials([file(credentialsId: 'app-props-prod', variable: 'APP_PROPS')]) {
                            sh '''
                                mkdir -p ./src/main/resources
                                cp "$APP_PROPS" ./src/main/resources/application-prod.properties
                            '''
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps{
                script {
                    if (BRANCH_NAME == "release") {
                        sh """
                            docker build -t seobom-backend-test:latest -f docker/Dockerfile .
                        """
                    } else if (BRANCH_NAME == "main") {
                        sh """
                            docker build -t seobom-backend-prod:latest -f docker/Dockerfile .
                        """
                    }
                }
            }
        }

        stage('Deploy TEST') {
            when { branch 'release' }
            steps {
                sh """
                    docker compose -p ${PROJECT_NAME} -f ${TEST_COMPOSE_PATH} stop || true
                    docker compose -p ${PROJECT_NAME} -f ${TEST_COMPOSE_PATH} up -d --force-recreate --remove-orphans
                """
            }
        }

        stage('Deploy PROD') {
            when { branch 'main' }
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'prod-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                        ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no \
                        ${PROD_SSH_USER}@${PROD_SSH_HOST} \
                        "docker compose -f ${PROD_COMPOSE_PATH} stop || true &&
                         docker compose -f ${PROD_COMPOSE_PATH} up -d --force-recreate --remove-orphans"
                    """
                }
            }
        }

        stage('Health Check') {
            when { branch 'release' }
            steps {
                script {
                    echo "Checking health of backend service..."
                    sh '''
                        for i in $(seq 1 20); do
                            echo "Checking service health... Attempt $i"
                            result=$(curl -s -o /tmp/health.json http://seobom-backend-test:8080/actuator/health || true)
                            cat /tmp/health.json || true

                            if grep -q "UP" /tmp/health.json; then
                                echo "Service is UP!"
                                exit 0
                            fi

                            echo "Waiting for service to be ready... ($i/20)"
                            sleep 5
                        done

                        echo "Health check failed!"
                        exit 1
                    '''
                }
            }
            post {
                failure {
                    sh "docker logs ${PROJECT_NAME} | tail -n 50 || true"
                    error 'Pipeline aborted: Service not responding.'
                }
            }
        }

        stage('Docker Clear') {
            when {
                anyOf {
                    branch 'release'
                    branch 'main'
                }
            }
            steps {
                sh "docker image prune -f || true"
            }
        }
    }

    post {
        success { echo "Deployment succeeded!" }
        failure { echo "Deployment failed!" }
    }
}
