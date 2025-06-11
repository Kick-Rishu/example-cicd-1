pipeline {
        agent any

        options {
            timestamps()
        }

        environment {
            SONARQUBE_SERVER = 'SonarQube'
            AWS_REGION = 'ap-south-1'
            S3_BUCKET = credentials('eb-s3-bucket')
            EB_APP_NAME = credentials('eb-app-name')
            EB_ENV_NAME = 'rishu-devops-kdu-cicd-01-default-ebs-env'
        }

        tools {
            maven 'Maven 3'
        }

        stages {
            stage('Checkout') {
                steps {
                    echo "\033[1;36m📥 Checking out source code from GitHub...\033[0m"
                    checkout([$class: 'GitSCM',
                    branches: [[name: 'main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Kick-Rishu/example-cicd-1/',
                        credentialsId: 'Github-CICD-Example'
                    ]]
                ])
                }
            }

            stage('Build') {
                steps {
                    echo "\033[1;34m🏗️ Building the Spring Boot project...\033[0m"
                    dir('./') {
                        sh 'mvn clean compile'
                    }
                }
            }

            stage('Test') {
                steps {
                    echo "\033[1;35m🧪 Running unit tests...\033[0m"
                    dir('./') {
                        sh 'mvn test'
                    }
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    echo "\033[1;33m🔎 Performing static code analysis with SonarQube...\033[0m"
                    dir('./') {
                        withSonarQubeEnv("${SONARQUBE_SERVER}") {
                            sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=prinshu-springboot-project \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                        }
                    }
                }
            }

            stage('Package') {
                steps {
                    echo "\033[1;34m📦 Packaging Spring Boot JAR...\033[0m"
                    dir('./') {
                        sh 'mvn clean package -DskipTests'
                    }
                }
            }

            stage('Deploy to Elastic Beanstalk') {
                steps {
                    script {
                        def version = "v-${env.BUILD_NUMBER}"
                        def zipName = "rishu-app-${env.BUILD_NUMBER}.zip"

                        echo "\033[1;36m📂 Preparing deployment ZIP: ${zipName}\033[0m"
                        dir('./target') {
                            echo "\033[1;36m☁️ Uploading ${zipName} to S3 bucket: ${S3_BUCKET}\033[0m"
                            sh """
                            cp *.jar application.jar
                            zip -r ${zipName} application.jar
                            aws s3 cp ${zipName} s3://${S3_BUCKET}/${zipName} --region ${AWS_REGION}
                            """

                            echo "\033[1;34m📡 Registering new Elastic Beanstalk version: ${version}\033[0m"
                            // Register new application version
                            sh """
                            aws elasticbeanstalk create-application-version \
                              --application-name ${EB_APP_NAME} \
                              --version-label ${version} \
                              --source-bundle S3Bucket=${S3_BUCKET},S3Key=${zipName} \
                              --region ${AWS_REGION}
                            """

                            echo "\033[1;32m🚀 Updating EB environment: ${EB_ENV_NAME} to version: ${version}\033[0m"
                            // Update EB environment
                            sh """
                            aws elasticbeanstalk update-environment \
                              --environment-name ${EB_ENV_NAME} \
                              --version-label ${version} \
                              --region ${AWS_REGION}
                            """
                        }
                    }
                }
            }
        }

        post {
            failure {
                echo "\033[1;31m❌ Pipeline failed at stage: ${env.STAGE_NAME}\033[0m"
            }
            success {
                echo "\033[1;32m✅ CI/CD pipeline completed successfully and deployed to EB!\033[0m"
            }
        }
}

