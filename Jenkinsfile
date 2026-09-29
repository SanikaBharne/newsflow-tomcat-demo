pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'Maven-3'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Downloading source code from GitHub'
                git branch: 'main',
                    url: 'https://github.com/SanikaBharne/newsflow-tomcat-demo.git'
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests'
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating WAR'
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying to Tomcat'
                bat '''
                copy /Y target\\newsflow-demo.war ^
                "C:\\apache-tomcat-11.0.26\\webapps\\newsflow-demo.war"
                '''
            }
        }

        stage('Verify') {
            steps {
                echo 'Checking deployed application'
                bat 'curl.exe --fail http://localhost:8081/newsflow-demo/'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
        }

        success {
            echo 'CI/CD PIPELINE SUCCESSFUL'
        }

        failure {
            echo 'CI/CD PIPELINE FAILED'
        }
    }
}