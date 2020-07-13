node {

  	properties([
    	buildDiscarder(logRotator(numToKeepStr:'5'))
  	])

    /* define variable */
    def app

    /* define constant */
	def ID_BUILD = "cit-ticketing-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
    def STACK_NAME = "MSM_CIT_TICKETING_${env.BRANCH_NAME.toUpperCase()}"
    
    withCredentials([					
                    string(credentialsId: 'registry.username', variable: 'REGISTRY_USERNAME'),
					string(credentialsId: 'registry.password', variable: 'REGISTRY_PASSWORD'),
					string(credentialsId: 'app.token.github', variable: 'TOKEN_GITHUB'),
				]) {

	    try {
	        
		  stage('Clone repository') {
		        /* Let's make sure we have the repository cloned to our workspace */
		        checkout scm      
			}
				
			stage('Login registry') {
		        sh "docker login registry.botalista-dev.ch --username ${REGISTRY_USERNAME} --password ${REGISTRY_PASSWORD}"
			}

			stage('Build complet') {
				def JAVA_ENV = "-e app.token.github=CI"
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
		        /* This builds the actual image; synonymous to
		         * docker build on the command line */
		        app = docker.build("registry.botalista-dev.ch/cit-ticketing:${env.BRANCH_NAME}");
		        app.push()
			}
			
			switch (env.BRANCH_NAME) {
				case "master":
					stage('Master branch question') {
						
						mail (to: 'cyril.boillat@ville-ge.ch', 
						subject: "[ WAITING ] Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) is waiting for input", 
						body: "Please go to ${env.JOB_URL}.");
						
		        		input "Does the staging environment look ok?"
		   			}
		   			
		   			stage('Tag image') {
		        		app.push("latest")
					}
										
					stage("Deploy CIT-Ticketing On Docker PRODUCTION environment") {
						sh """\
						STACK_NAME=MSM_CIT-TICKETING_PRD \
					    DB_PASSWORD=${DB_PASSWORD} \
						DOCKER_HOST=tcp://172.16.0.1:3272 \
						docker stack deploy MSM_CIT_TICKETING_PRD --compose-file docker/api/docker-compose-production.yml --resolve-image always --prune"""
					}
					break;
					stage('Tag image PRD') {
		        		app.push("latest")
					}
				break;
				case "dev":
					stage('Deploy CIT-Ticketing On Docker dev bridge') {
						sh """\
						STACK_NAME=${STACK_NAME} \
						BRANCH=${env.BRANCH_NAME} \
					    DB_PASSWORD=${DB_PASSWORD} \
						DOCKER_HOST=tcp://172.16.0.1:3272 \
						docker stack deploy ${STACK_NAME} --compose-file docker/api/docker-compose-dev.yml --resolve-image always --prune"""
					}
				break;
				default:
					if(env.BRANCH_NAME.startsWith('rc-')) {
						stage('Tag image RC') {
		        		app.push("rc")
					}
				}
			}
			
      
			
		}catch (InterruptedException err) {
			// nothing
		}catch (Exception err) {
	        mail (to: 'cyril.boillat@ville-ge.ch, gael.boquet@ville-ge.ch',
			subject: "[ ERROR ] Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) is build error", 
			body: "Please go to ${env.JOB_URL}.\nPlease go to console ${env.BUILD_URL}console.\n\n${err}");
	        throw err
		}
	}
   
}
