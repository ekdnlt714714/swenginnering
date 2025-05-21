pipeline {
    agent any 

    environment {
        JUNIT_JAR_URL = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.2/junit-platform-console-standalone-1.8.2.jar'
        JUNIT_JAR_FILENAME = 'junit-platform-console-standalone.jar'
        JUNIT_JAR_PATH = "lib/${JUNIT_JAR_FILENAME}" // Relative to workspace root
        
        // Source root now includes the 'StudentManager' project directory
        PROJECT_DIR = 'StudentManager' // The directory created by git clone if repo name is StudentManager
        SRC_ROOT = "${PROJECT_DIR}/src" 
        PACKAGE_PATH = 'student'    

        CLASS_DIR = 'classes'       // Relative to workspace root
        REPORT_DIR = 'test-reports' // Relative to workspace root
        BUILD_SUMMARY_FILE = "${REPORT_DIR}/build_summary.txt" 
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs() 
                checkout scm
                sh 'echo "[+] Current workspace contents after checkout:" && ls -R'
            }
        }

        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "[+] Cleaning up previous build artifacts if any..."
                    # These paths are relative to the workspace root
                    rm -rf ${CLASS_DIR} ${REPORT_DIR} lib 
                    echo "[+] Creating required directories..."
                    mkdir -p ${CLASS_DIR}
                    mkdir -p ${REPORT_DIR}
                    mkdir -p lib
                    
                    echo "[+] Downloading JUnit Platform Console Standalone JAR to ${JUNIT_JAR_PATH}..."
                    curl -fsL -o ${JUNIT_JAR_PATH} ${JUNIT_JAR_URL}
                    if [ ! -f "${JUNIT_JAR_PATH}" ]; then
                        echo "[!!!] CRITICAL: Failed to download JUnit JAR from ${JUNIT_JAR_URL}"
                        exit 1 
                    fi
                    echo "[+] JUnit JAR downloaded successfully."
                '''
            }
        }

        stage('Compile Main and Test Code') {
            steps {
                sh '''
                    echo "[+] Compiling main and test source files..."
                    # Paths are relative to the workspace root
                    MAIN_JAVA_FILE="${SRC_ROOT}/${PACKAGE_PATH}/StudentManager.java"
                    TEST_FILES_PATTERN="${SRC_ROOT}/${PACKAGE_PATH}/StudentManagerTest*.java"

                    if [ ! -f "${MAIN_JAVA_FILE}" ]; then
                        echo "[!!!] ERROR: Main source file not found at ${MAIN_JAVA_FILE}"
                        exit 1
                    fi

                    ls ${TEST_FILES_PATTERN} > /dev/null 2>&1 # Check if any test files exist
                    if [ $? -ne 0 ]; then
                        echo "[!!!] ERROR: No test files found matching pattern ${TEST_FILES_PATTERN}"
                        exit 1
                    fi
                    
                    echo "[i] Compiling main file: ${MAIN_JAVA_FILE}"
                    javac -encoding UTF-8 -d ${CLASS_DIR} ${MAIN_JAVA_FILE}
                    if [ $? -ne 0 ]; then
                        echo "[!!!] ERROR: Main code compilation failed."
                        exit 1
                    fi

                    echo "[i] Compiling test files matching: ${TEST_FILES_PATTERN}"
                    javac -encoding UTF-8 -d ${CLASS_DIR} -cp "${CLASS_DIR}:${JUNIT_JAR_PATH}" ${TEST_FILES_PATTERN}
                    if [ $? -ne 0 ]; then
                        echo "[!!!] ERROR: Test code compilation failed."
                        exit 1
                    fi
                    echo "[+] All source code compiled successfully to ${CLASS_DIR}"
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    echo "[+] Running tests using JUnit Platform Console..."
                    # CLASS_DIR, JUNIT_JAR_PATH, REPORT_DIR are relative to workspace root
                    java -jar ${JUNIT_JAR_PATH} \
                         --class-path ${CLASS_DIR} \
                         --scan-class-path \
                         --details=tree \
                         --details-theme=ascii \
                         --reports-dir=${REPORT_DIR} \
                         --config=junit.platform.output.capture.stdout=true \
                         --config=junit.platform.output.capture.stderr=true \
                         --config=junit.platform.reporting.open.xml.enabled=true > ${REPORT_DIR}/junit-console-output.txt
                    
                    TEST_EXIT_CODE=$?
                    echo "[i] JUnit execution completed with exit code: ${TEST_EXIT_CODE}"

                    if [ ${TEST_EXIT_CODE} -ne 0 ] && [ ${TEST_EXIT_CODE} -ne 1 ]; then
                        echo "[!!!] ERROR: JUnit Platform Console Runner encountered a critical error."
                    fi
                    echo "[+] Tests executed. Raw console output in ${REPORT_DIR}/junit-console-output.txt"
                '''
            }
        }
    }
    post {
        always {
            // ... other archiving ...
            
            script {
                // Using env.GIT_COMMIT as a workaround for scm.GIT_COMMIT script security
                def gitCommit = env.GIT_COMMIT ?: 'N/A (env.GIT_COMMIT not set)'
                def summary = """
                Jenkins Build Summary
                ---------------------
                Project: ${env.JOB_NAME}
                Build Number: ${env.BUILD_NUMBER}
                Build URL: ${env.BUILD_URL}
                Git Commit: ${gitCommit}
                Build Status: ${currentBuild.currentResult ?: 'IN PROGRESS'} 
                Timestamp: ${new Date().format("yyyy-MM-dd HH:mm:ss Z")}

                JUnit Console Output: See archived artifact '${REPORT_DIR}/junit-console-output.txt'
                JUnit XML Reports: See archived artifacts matching '${REPORT_DIR}/TEST-*.xml'
                """
                // This line creates the TXT file:
                writeFile file: env.BUILD_SUMMARY_FILE, text: summary 
                // This line archives it:
                archiveArtifacts artifacts: env.BUILD_SUMMARY_FILE, allowEmptyArchive: true 
            }
            echo "[*] Pipeline finished. Final status: ${currentBuild.currentResult}"
        }
        // ... success, unstable, failure blocks ...
    }
}
