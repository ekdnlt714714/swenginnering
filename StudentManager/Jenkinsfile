// Jenkinsfile (Declarative Pipeline)

pipeline {
    agent any // Run on any available agent (suitable for macOS if agent is configured)

    environment {
        // JUnit 5 Platform Console Standalone JAR for running tests
        JUNIT_JAR_URL = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.2/junit-platform-console-standalone-1.8.2.jar'
        JUNIT_JAR_FILENAME = 'junit-platform-console-standalone.jar'
        JUNIT_JAR_PATH = "lib/${JUNIT_JAR_FILENAME}"
        
        // Source and class directories based on your structure
        SRC_ROOT = 'src' 
        PACKAGE_PATH = 'student'    // Your package is 'student'

        CLASS_DIR = 'classes'       // Directory for compiled .class files
        REPORT_DIR = 'test-reports' // Directory for test reports and outputs
        BUILD_SUMMARY_FILE = "${REPORT_DIR}/build_summary.txt" // Summary file for success
    }

    stages {
        stage('Checkout') {
            steps {
                // Clean workspace before checkout to ensure a fresh environment
                cleanWs() 
                // Checkout the source code from the SCM configured in the Jenkins job
                // (https://github.com/ekdnlt714714/swenginnering.git, branch master)
                checkout scm
                sh 'echo "[+] Current workspace contents after checkout:" && ls -R'
            }
        }

        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "[+] Cleaning up previous build artifacts if any..."
                    rm -rf ${CLASS_DIR} ${REPORT_DIR} lib
                    echo "[+] Creating required directories..."
                    mkdir -p ${CLASS_DIR}
                    mkdir -p ${REPORT_DIR}
                    mkdir -p lib
                    
                    echo "[+] Downloading JUnit Platform Console Standalone JAR..."
                    # Using curl with -f to fail fast if download error, -s for silent, -L to follow redirects
                    curl -fsL -o ${JUNIT_JAR_PATH} ${JUNIT_JAR_URL}
                    if [ ! -f "${JUNIT_JAR_PATH}" ]; then
                        echo "[!!!] CRITICAL: Failed to download JUnit JAR from ${JUNIT_JAR_URL}"
                        exit 1 // Fail the build if JAR not downloaded
                    fi
                    echo "[+] JUnit JAR downloaded successfully to ${JUNIT_JAR_PATH}"
                '''
            }
        }

        stage('Compile Main and Test Code') {
            steps {
                sh '''
                    echo "[+] Compiling main and test source files from ${SRC_ROOT}/${PACKAGE_PATH}/"
                    # Define main and test file patterns
                    MAIN_JAVA_FILE="${SRC_ROOT}/${PACKAGE_PATH}/StudentManager.java"
                    TEST_FILES_PATTERN="${SRC_ROOT}/${PACKAGE_PATH}/StudentManagerTest*.java"

                    # Check if main source file exists
                    if [ ! -f "${MAIN_JAVA_FILE}" ]; then
                        echo "[!!!] ERROR: Main source file not found at ${MAIN_JAVA_FILE}"
                        exit 1
                    fi

                    # Check if test files exist
                    ls ${TEST_FILES_PATTERN} > /dev/null 2>&1
                    if [ $? -ne 0 ]; then
                        echo "[!!!] ERROR: No test files found matching pattern ${TEST_FILES_PATTERN}"
                        exit 1
                    fi
                    
                    echo "[i] Compiling main file: ${MAIN_JAVA_FILE}"
                    echo "[i] Compiling test files matching: ${TEST_FILES_PATTERN}"

                    # Compile main code first
                    javac -encoding UTF-8 -d ${CLASS_DIR} ${MAIN_JAVA_FILE}
                    if [ $? -ne 0 ]; then
                        echo "[!!!] ERROR: Main code compilation failed."
                        # Capture and print compiler errors (optional, basic example)
                        # find . -name "*.java" -print0 | xargs -0 javac -d ${CLASS_DIR} 2> compiler_errors.txt || true 
                        # if [ -s compiler_errors.txt ]; then cat compiler_errors.txt; fi
                        exit 1
                    fi

                    # Then compile test code, linking against main compiled code and JUnit
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
                    # The classpath for JUnit should include all compiled classes
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

                    # JUnit Platform Console Runner exit codes:
                    # 0: All tests successful
                    # 1: Test failures occurred
                    # Other non-zero: Invalid arguments or other runtime error
                    if [ ${TEST_EXIT_CODE} -ne 0 ] && [ ${TEST_EXIT_CODE} -ne 1 ]; then
                        echo "[!!!] ERROR: JUnit Platform Console Runner encountered a critical error (not just test failures)."
                        # This will cause the stage to fail if not already handled by JUnit plugin
                    fi
                    echo "[+] Tests executed. Raw console output in ${REPORT_DIR}/junit-console-output.txt"
                '''
            }
        }
    }

    post {
        always {
            echo "[*] Archiving test reports and build summary..."
            // JUnit plugin processes XML reports and sets build status (SUCCESS, UNSTABLE for test failures, FAILURE)
            junit pattern: "${REPORT_DIR}/TEST-*.xml", // Pattern for JUnit XML reports
                   allowEmptyResults: true,          // Don't fail if no XML reports are found (e.g., compilation error before tests)
                   healthScaleFactor: 1.0            // Test health reporting scale

            // Archive all contents of the report directory
            archiveArtifacts artifacts: "${REPORT_DIR}/**/*", allowEmptyArchive: true
            
            script {
                // Create a build summary text file (Goal 3: TXT file on success/always)
                def summary = """
                Jenkins Build Summary
                ---------------------
                Project: ${env.JOB_NAME}
                Build Number: ${env.BUILD_NUMBER}
                Build URL: ${env.BUILD_URL}
                Git Commit: ${scm.GIT_COMMIT ?: 'N/A'} 
                Build Status: ${currentBuild.currentResult ?: 'IN PROGRESS'}
                Timestamp: ${new Date().format("yyyy-MM-dd HH:mm:ss Z")}

                JUnit Console Output: See archived artifact '${REPORT_DIR}/junit-console-output.txt'
                JUnit XML Reports: See archived artifacts matching '${REPORT_DIR}/TEST-*.xml'
                """
                writeFile file: env.BUILD_SUMMARY_FILE, text: summary
                archiveArtifacts artifacts: env.BUILD_SUMMARY_FILE, allowEmptyArchive: true // Archive the summary file
            }
            echo "[*] Pipeline finished. Final status: ${currentBuild.currentResult}"
        }

        success { // Goal 3: Email on success
            echo "[***] PIPELINE SUCCEEDED [***]"
            script {
                if (Hudson.instance.getPlugin('email-ext')) { // Check if Email Extension Plugin is available
                    emailext (
                        subject: "SUCCESS: Jenkins Build #${env.BUILD_NUMBER} for ${env.JOB_NAME}",
                        body: """<p>Build SUCCEEDED for project <b>${env.JOB_NAME}</b>, build number <b>#${env.BUILD_NUMBER}</b>.</p>
                               <p>Access the build at: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                               <p>Build summary and test reports are available as archived artifacts.</p>
                               <p>Summary file: ${env.BUILD_SUMMARY_FILE}</p>""",
                        to: 'ekdnlt714714@gmail.com, leek0729@naver.com, cba7215@g.hongik.ac.kr', // <<< --- !!! CHANGE THIS EMAIL ADDRESS !!!
                        mimeType: 'text/html'
                    )
                } else {
                    echo "[w] Email Extension plugin not found. Skipping success email."
                }
            }
        }
        
        unstable { // Typically means tests failed
            echo "[!!!] PIPELINE UNSTABLE (Likely Test Failures) [!!!]"
            script {
                if (Hudson.instance.getPlugin('email-ext')) {
                    emailext (
                        subject: "UNSTABLE: Jenkins Build #${env.BUILD_NUMBER} for ${env.JOB_NAME} (Test Failures?)",
                        body: """<p>Build is UNSTABLE for project <b>${env.JOB_NAME}</b>, build number <b>#${env.BUILD_NUMBER}</b>. This often indicates test failures.</p>
                               <p>Please review the test results: <a href="${env.BUILD_URL}testReport">${env.BUILD_URL}testReport</a></p>
                               <p>Full build log: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                               <p>Summary file: ${env.BUILD_SUMMARY_FILE}</p>""",
                        to: 'ekdnlt714714@gmail.com, leek0729@naver.com, cba7215@g.hongik.ac.kr', // <<< --- !!! CHANGE THIS EMAIL ADDRESS !!!
                        mimeType: 'text/html'
                    )
                } else {
                    echo "[w] Email Extension plugin not found. Skipping unstable build email."
                }
            }
        }

        failure { // Goal 2: Email on failure
            echo "[!!!] PIPELINE FAILED [!!!]"
            script {
                if (Hudson.instance.getPlugin('email-ext')) {
                    emailext (
                        subject: "FAILURE: Jenkins Build #${env.BUILD_NUMBER} for ${env.JOB_NAME}",
                        body: """<p>Build FAILED for project <b>${env.JOB_NAME}</b>, build number <b>#${env.BUILD_NUMBER}</b>.</p>
                               <p>Please check the console output immediately for failure reasons: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                               <p>The failure could be in compilation, JUnit JAR download, or test execution setup.</p>
                               <p>Summary file (if generated before failure): ${env.BUILD_SUMMARY_FILE}</p>""",
                        to: 'ekdnlt714714@gmail.com, leek0729@naver.com, cba7215@g.hongik.ac.kr', // <<< --- !!! CHANGE THIS EMAIL ADDRESS !!!
                        mimeType: 'text/html'
                    )
                } else {
                    echo "[w] Email Extension plugin not found. Skipping failure email."
                }
            }
        }
    }
}
