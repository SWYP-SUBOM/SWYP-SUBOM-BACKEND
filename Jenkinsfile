pipeline {
    agent any

    environment {
        PATH = "/usr/libexec/docker/cli-plugins:/usr/bin:/usr/local/bin:/bin"
        BASE_PROJECT_NAME = "seobom-backend"
        WORKSPACE_DIR = "${WORKSPACE}"
        // Git 브랜치 이름 정리 (origin/ 제거)
        BRANCH_NAME = "${env.GIT_BRANCH?.replaceAll('origin/', '') ?: 'release'}"
    }

    stages {
        stage('Set Environment') {
            steps {
                script {
                    echo "=== 환경 설정 시작 ==="
                    echo "Git Branch: ${env.GIT_BRANCH}"
                    echo "Branch Name: ${BRANCH_NAME}"

                    // 브랜치에 따라 환경 결정
                    if (BRANCH_NAME == 'main') {
                        env.DEPLOY_ENV = 'PRODUCTION'
                        env.PROJECT_NAME = "${BASE_PROJECT_NAME}-prod"
                        env.DOCKER_COMPOSE = "${WORKSPACE_DIR}/docker/docker-compose-prod.yml"
                        env.APP_PROPS_ID = 'application-prod-properties'
                        env.SPRING_PROFILE = 'prod'
                        env.CONTAINER_NAME = 'seobom-backend-prod'
                        echo '🏭 PRODUCTION 환경으로 설정'
                    } else if (BRANCH_NAME == 'release') {
                        env.DEPLOY_ENV = 'TEST'
                        env.PROJECT_NAME = "${BASE_PROJECT_NAME}-test"
                        env.DOCKER_COMPOSE = "${WORKSPACE_DIR}/docker/docker-compose-test.yml"
                        env.APP_PROPS_ID = 'application-test-properties'
                        env.SPRING_PROFILE = 'test'
                        env.CONTAINER_NAME = 'seobom-backend-test'
                        echo '🧪 TEST 환경으로 설정'
                    } else {
                        error "❌ 지원하지 않는 브랜치입니다: ${BRANCH_NAME}"
                    }

                    echo "=== 환경 설정 완료 ==="
                    echo "Environment: ${env.DEPLOY_ENV}"
                    echo "Project Name: ${env.PROJECT_NAME}"
                    echo "Docker Compose: ${env.DOCKER_COMPOSE}"
                    echo "Spring Profile: ${env.SPRING_PROFILE}"
                    echo "Container Name: ${env.CONTAINER_NAME}"
                }
            }
        }

        stage('Clone Repository') {
            steps {
                echo "📥 코드 가져오기: ${BRANCH_NAME} 브랜치"
                git branch: "${BRANCH_NAME}",
                    url: "https://github.com/SWYP-SUBOM/SWYP-SUBOM-BACKEND.git",
                    credentialsId: 'github-cred'
            }
        }

        stage('Prepare Environment') {
            steps {
                script {
                    echo "⚙️ 환경 준비 및 찌꺼기 제거 시작"
                    sh """
                        # 1. 이전 빌드에서 Docker가 잘못 만든 '디렉토리' 삭제 (파일이어야 하는 경로)
                        if [ -d "${WORKSPACE_DIR}/nginx/conf.d/default-test.conf" ]; then
                            echo "⚠️ 파일 경로에 디렉토리가 발견되었습니다. 삭제합니다."
                            rm -rf "${WORKSPACE_DIR}/nginx/conf.d/default-test.conf"
                        fi

                        # 2. 진짜 파일이 들어왔는지 최종 확인
                        if [ ! -f "${WORKSPACE_DIR}/nginx/conf.d/default-test.conf" ]; then
                            echo "❌ 에러: Git에서 파일을 가져오지 못했거나 경로가 틀렸습니다."
                            find ${WORKSPACE_DIR} -name "default-test.conf"
                            exit 1
                        fi

                        echo "✅ 설정 파일 검증 완료"
                    """
                }
            }
        }

        stage('Create application.properties') {
            steps {
                script {
                    echo "📝 application-${env.SPRING_PROFILE}.properties 생성 중..."
                    withCredentials([file(credentialsId: "${env.APP_PROPS_ID}", variable: 'APP_PROPS')]) {
                        sh """
                            mkdir -p ./src/main/resources
                            cp "\$APP_PROPS" ./src/main/resources/application-${env.SPRING_PROFILE}.properties
                            echo "✅ application-${env.SPRING_PROFILE}.properties 생성 완료"
                        """
                    }
                }
            }
        }

        stage('Docker Down') {
            steps {
                echo "🛑 기존 컨테이너 중지: ${env.PROJECT_NAME}"
                sh """
                    docker compose -p ${env.PROJECT_NAME} -f ${env.DOCKER_COMPOSE} down --rmi all || true
                    echo "✅ 기존 컨테이너 정리 완료"
                """
            }
        }

        stage('Docker Build') {
            steps {
                echo "🐳 Docker 이미지 빌드 중: ${env.DEPLOY_ENV}"
                sh """
                    docker compose -p ${env.PROJECT_NAME} -f ${env.DOCKER_COMPOSE} build --no-cache
                    echo "✅ Docker 이미지 빌드 완료"
                """
            }
            post {
                failure {
                    echo "❌ Docker 빌드 실패, 정리 중..."
                    sh "docker system prune -f || true"
                    error 'Build aborted'
                }
            }
        }

        stage('Docker Up') {
            steps {
                echo "▶️ 컨테이너 시작: ${env.PROJECT_NAME}"
                sh """
                    docker compose -p ${env.PROJECT_NAME} -f ${env.DOCKER_COMPOSE} up -d
                """
                echo "✅ 컨테이너 시작 완료"
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🏥 헬스 체크 시작: ${env.DEPLOY_ENV}"
                    sh """
                        for i in \$(seq 1 20); do
                            echo "헬스 체크 시도 \$i/20..."
                            result=\$(curl -s -w "%{http_code}" -o /tmp/health-${env.DEPLOY_ENV}.json http://${env.CONTAINER_NAME}:8080/actuator/health || true)
                            cat /tmp/health-${env.DEPLOY_ENV}.json || true

                            if grep -q "UP" /tmp/health-${env.DEPLOY_ENV}.json; then
                                echo "✅ ${env.DEPLOY_ENV} 서비스가 정상 작동 중입니다!"
                                exit 0
                            fi

                            echo "대기 중... (\$i/20)"
                            sleep 5
                        done

                        echo "❌ 헬스 체크 실패: 20회 시도 후에도 응답 없음"
                        exit 1
                    """
                }
            }
            post {
                failure {
                    sh """
                        echo "❌ 헬스 체크 실패, 컨테이너 로그 출력:"
                        docker logs ${env.CONTAINER_NAME} | tail -n 50 || true
                    """
                    error 'Pipeline aborted: Service not responding.'
                }
            }
        }

        stage('Docker Clear') {
            steps {
                echo "🧹 불필요한 이미지 정리..."
                sh """
                    docker image prune -f || true
                    echo "✅ 정리 완료"
                """
            }
        }
    }

    post {
        success {
            echo "🎉 ${env.DEPLOY_ENV} 배포 성공!"
        }
        failure {
            echo "❌ ${env.DEPLOY_ENV} 배포 실패!"
        }
        always {
            echo "📊 배포 완료: ${env.DEPLOY_ENV} (${env.PROJECT_NAME})"
        }
    }
}