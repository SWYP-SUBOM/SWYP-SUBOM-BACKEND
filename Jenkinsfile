pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        durabilityHint('PERFORMANCE_OPTIMIZED')
        preserveStashes(buildCount: 5)
    }

    environment {
        PATH = "/usr/libexec/docker/cli-plugins:/usr/bin:/usr/local/bin:/bin"
        PROJECT_NAME = "seobom-backend"
        WORKSPACE_DIR = "${WORKSPACE}"
        DOCKER_COMPOSE = "${WORKSPACE}/docker/docker-compose.yml"
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'release'}"
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo "Branch: ${BRANCH_NAME}"
                // deleteDir()
                dir("${WORKSPACE}") {   // 명시적으로 workspace 지정
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${BRANCH_NAME}"]],
                        userRemoteConfigs: [[
                            url: 'https://github.com/SWYP-SUBOM/SWYP-SUBOM-BACKEND.git',
                            credentialsId: 'github-cred'
                        ]]
                    ])
                    sh 'ls -al' // clone 결과를 콘솔에 출력
                }
            }
        }

        stage('Create application.properties') {
            steps {
                withCredentials([file(credentialsId: 'application-properties', variable: 'APP_PROPS')]) {
                    echo "Writing application.properties file"
                    sh '''
                        mkdir -p ./src/main/resources
                        cp "$APP_PROPS" ./src/main/resources/application.properties
                    '''
                }
            }
        }

        stage('Prepare Environment') {
            steps {
                echo "Using docker-compose and Dockerfile in docker/ directory"
            }
        }

        stage('Docker Down') {
            steps {
                echo "Docker compose down"
                sh """
                    docker compose -p ${PROJECT_NAME} -f ${DOCKER_COMPOSE} down --rmi all || true
                    docker rm -f seobom-backend nginx 2>/dev/null || true
                """
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image..."
                sh "docker compose -p ${PROJECT_NAME} -f ${DOCKER_COMPOSE} build --no-cache"
            }
            post {
                failure {
                    echo "Docker build failed, cleaning up unused files..."
                    sh "docker system prune -f || true"
                    error 'Build aborted'
                }
            }
        }

        stage('Docker Up') {
            steps {
                echo "Starting containers..."
                sh "docker compose -p ${PROJECT_NAME} -f ${DOCKER_COMPOSE} up -d --remove-orphans"
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "Checking health of backend service..."
                    sh '''
                        for i in $(seq 1 20); do
                            echo "Checking service health... Attempt $i"
                            result=$(curl -s -w "%{http_code}" -o /tmp/health.json http://seobom-backend:8080/actuator/health || true)
                            cat /tmp/health.json || true

                            if grep -q "UP" /tmp/health.json; then
                                echo "Service is UP!"
                                exit 0
                            fi

                            echo "Waiting for service to be ready... ($i/20)"
                            sleep 5
                        done

                        echo "Health check failed after 20 attempts!"
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
            steps {
                echo "Cleaning up..."
                sh "docker system prune -f --filter 'label!=jenkins' --volumes=false || true"
            }
        }
    }

    post {
        always {
           echo "Preserving workspace..."
           sh "ls -al ${WORKSPACE} || true"
        }
        success {
            echo "Deployment succeeded!"
        }
        failure {
            echo "Deployment failed!"
        }
    }
}
