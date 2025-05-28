pipeline {
    agent any

    environment {
        JUNIT_JAR_URL = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.2/junit-platform-console-standalone-1.8.2.jar'
        JUNIT_JAR_FILENAME = 'junit-platform-console-standalone.jar'
        JUNIT_JAR_PATH = "lib/${JUNIT_JAR_FILENAME}" // Relative to workspace root

        // Source root now includes the 'StudentManager' project directory
        PROJECT_DIR = 'StudentManager' // The directory containing your main source folder
        SRC_ROOT = "${PROJECT_DIR}/src"  // Path to the 'src' folder within your project

        CLASS_DIR = 'classes'       // Relative to workspace root for compiled files
        REPORT_DIR = 'test-reports' // Relative to workspace root for test reports
        // BUILD_SUMMARY_FILE = "${REPORT_DIR}/build_summary.txt" // This wasn't used, can be removed or kept if planned for future use
    }

    stages {
        stage('Checkout') {
            steps {
                echo "[*] Checking out code from SCM..."
                checkout scm
            }
        }

        stage('Prepare') {
            steps {
                sh """
                    mkdir -p ${CLASS_DIR}
                    mkdir -p ${REPORT_DIR}
                    mkdir -p lib
                    echo "[*] Downloading JUnit JAR..."
                    # Fixed: Added -L to follow redirects for curl
                    curl -L -o ${JUNIT_JAR_PATH} ${JUNIT_JAR_URL}
                """
            }
        }

        stage('Build') {
            steps {
                sh """
                    echo "[*] Compiling source files..."
                    # Fixed: Use SRC_ROOT to find Java files, not 'sogong_project'
                    find ${SRC_ROOT} -name "*.java" > sources.txt
                    echo "[*] Found Java files to compile:"
                    cat sources.txt
                    echo "[*] Compiling with javac..."
                    javac -encoding UTF-8 -d ${CLASS_DIR} -cp ${JUNIT_JAR_PATH} @sources.txt
                """
            }
        }

        stage('Test') {
            steps {
                sh """
                    echo "[*] Running tests with JUnit..."
                    java -jar ${JUNIT_JAR_PATH} \\
                        --class-path ${CLASS_DIR} \\
                        --scan-class-path \\
                        --details=tree \\
                        --details-theme=ascii \\
                        --reports-dir=${REPORT_DIR} \\
                        --config junit.platform.output.capture.stdout=true \\
                        --config junit.platform.reporting.open.xml.enabled=true \\
                        > ${REPORT_DIR}/test-output.txt
                    echo "[*] Test execution finished. See ${REPORT_DIR}/test-output.txt and XML reports."
                """
            }
        }
    }

    post {
        always {
            echo "[*] Archiving test results..."
            junit allowEmptyResults: true, testResults: "${REPORT_DIR}/**/*.xml"
            archiveArtifacts artifacts: "${REPORT_DIR}/**/*", allowEmptyArchive: true
        }
        failure {
            echo "[!] Build or test failed!"
            emailext (
                subject: "❌ Jenkins Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """<p>Attention!</p>
                         <p>The Jenkins build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> failed.</p>
                         <p>Please check the logs: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                to: "ekdnlt714714@gmail.com", // Ensure this email is correct
                mimeType: 'text/html'
            )
        }
        success {
            echo "[🎉] Build and test succeeded!"
            emailext (
                subject: "✅ Jenkins Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """<p>Good news!</p>
                         <p>The Jenkins build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> succeeded.</p>
                         <p>Check it here: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                to: "ekdnlt714714@gmail.com", // Ensure this email is correct
                mimeType: 'text/html'
            )
        }
    }
}
