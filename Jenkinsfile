pipeline {
    agent any

    environment {
        PROJECT_NAME = "seobom-backend"
        DOCKER_COMPOSE = "${WORKSPACE}/docker-compose.yml"
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'release'}"
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo "Branch: ${BRANCH_NAME}"
				git branch: "${BRANCH_NAME}",
					url: "https://github.com/SWYP-SUBOM/SWYP-SUBOM-BACKEND.git",
					credentialsId: 'github-cred'
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
                echo "Copy required files"
                sh """
                    cp docker/docker-compose.yml ${WORKSPACE}/
                    cp docker/Dockerfile ${WORKSPACE}/
                """
            }
        }

        stage('Docker Down') {
            steps {
                echo "Docker compose down"
                sh "docker compose -f ${DOCKER_COMPOSE} down --rmi all || true"
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image..."
                sh "docker compose -f ${DOCKER_COMPOSE} build --no-cache"
            }
            post {
                failure {
                    echo "Docker build failed, cleaning up unused files..."
                    sh "docker system prune -f"
                    error 'Build aborted'
                }
            }
        }

        stage('Docker Up') {
            steps {
                echo "Starting containers..."
                sh "docker compose -f ${DOCKER_COMPOSE} up -d"
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "Checking health of backend service..."
                    sh '''
                        for i in {1..20}; do
                            if curl -s "http://localhost:8080/actuator/health" | grep -q "UP"; then
                                echo "Service is up !!"
                                exit 0
                            fi
                            echo "Waiting for service to be ready..."
                            sleep 5
                        done
                        echo "Health check failed !!"
                        exit 1
                    '''
                }
            }
            post {
                failure {
                    sh "docker logs ${PROJECT_NAME} | tail -n 50"
                    error 'Pipeline aborted: Service not responding.'
                }
            }
        }

        stage('Docker Clear') {
            steps {
                echo "Cleaning up..."
                sh "docker system prune -f"
            }
        }
    }

    post {
        success {
            echo "Deployment succeeded!"
        }
        failure {
            echo "Deployment failed!"
        }
    }
}
