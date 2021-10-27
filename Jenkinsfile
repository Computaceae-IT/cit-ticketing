node {

  	properties([
    	buildDiscarder(logRotator(numToKeepStr:'5'))
  	])

    /* define variable */
    def app

	try {

		stage('Clone repository') {
			/* Let's make sure we have the repository cloned to our workspace */
			checkout scm
		}

		stage('SED Deployment variable') {
			script {
				env.DEPLOY_COMMIT_HASH = sh(returnStdout: true, script: "git rev-parse HEAD | cut -c1-7").trim()
				env.DEPLOY_BUILD_DATE = sh(returnStdout: true, script: "date -u +'%Y-%m-%dT%H.%M.%SZ'").trim()
			}

			dir ('k8s') {
				sh "sed -i \"s|DATE_DEPLOYMENT|${env.DEPLOY_BUILD_DATE}|g\" 004.deployment.yaml"
			}

			dir ('k8s/dev') {
				sh "sed -i \"s|DATE_DEPLOYMENT|${env.DEPLOY_BUILD_DATE}|g\" 004.deployment.DEV.yaml"
				sh "sed -i \"s|DEPLOY_COMMIT_HASH|${env.DEPLOY_COMMIT_HASH}|g\" 004.deployment.DEV.yaml"
			}
		}

		stage('Build complet') {
			def JAVA_ENV
			withCredentials([
					string(credentialsId: 'app.token.github', variable: 'TOKEN_GITHUB')
				]) {
				JAVA_ENV = "-e app.github.token=${TOKEN_GITHUB} -e app.github.user=Computaceae-IT -e app.github.repository=cit-ticketing -e app.admin.mail=test@mail.com"
			}
			docker.image('maven:3.5.2-jdk-8-alpine').inside("-v maven-repo:/root/.m2 ${JAVA_ENV}") {
				sh 'mvn -Duser.timezone=Europe/Zurich clean install'

			}
		}

		stage('SonarQube analysis') {
			withSonarQubeEnv('Sonar') {
				docker.image('maven:3.5.2-jdk-8-alpine').inside('-v maven-repo:/root/.m2') {
					sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.5.0.1254:sonar'
				}
			}
		}

		stage('Build and Push image') {
			app = docker.build("registry.computaceae-it.tech/cit-ticketing:${env.DEPLOY_COMMIT_HASH}");
			app.push()
		}

		switch (env.BRANCH_NAME) {
			case "master":
			case "main":
				stage('Tag LATEST image') {
					app.push("latest")
				}
				stage('Apply Kubernetes files') {
					withKubeConfig([credentialsId: 'cit-kube-config']) {
						dir ('k8s') {
							sh 'kubectl apply -f 003.service.yaml'
							sh 'kubectl apply -f 004.deployment.yaml'
						}
					}
				}
				break;
			case "dev":
				stage('Apply DEV Kubernetes files') {
					withKubeConfig([credentialsId: 'cit-kube-config']) {
						dir ('k8s/dev') {
							sh 'kubectl apply -f 003.service.DEV.yaml'
							sh 'kubectl apply -f 004.deployment.DEV.yaml'
						}
					}
				}
				break;

		}

	}catch (InterruptedException err) {
		// nothing
	}catch (Exception err) {
		mail (to: 'cyril@botalista.community',
		subject: "[ ERROR ] Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) is build error",
		body: "Please go to ${env.JOB_URL}.\nPlease go to console ${env.BUILD_URL}console.\n\n${err}");
		throw err
	}

}
