def getRelease
def getEnvironment

pipeline {
   agent any
   environment {
        HRS_WORKSPACE = "${JENKINS_HOME}/workspace/${hrs}"
    }

   stages {
      stage('Repositories update') {
         steps {
            build('hrs')
         }
      }

      stage('Select release') {
          steps {
            dir(path: "${HRS_WORKSPACE}/shamu-hrs-company-service") {
                 // get all releases from git tag
                script { RELEASES = sh(returnStdout: true, script: 'git tag') }

                // select release you want to deploy
                timeout(60) {                // timeout waiting for input after 60 minutes
                    script {
                        getRelease = input id: 'release', message: 'Please select your release.', ok: 'Proceed?', parameters: [choice(choices: RELEASES, description: 'Select a release to deploy.', name: 'RELEASE')], submitter: 'jenkins', submitterParameter: 'jenkins'
                    }
                }
            }
          }
      }
      stage('Select environment') {
          steps {
            dir(path: "${HRS_WORKSPACE}/shamu-hrs/auto-deploy/ansible/include") {
                 // get all environments
                script { ENVIRONMENTS = sh(returnStdout: true, script: '''ls | sed 's/\\.[^.]*$//' ''') }

                // select environment you want to deploy
                timeout(60) {                // timeout waiting for input after 60 minutes
                    script {
                        getEnvironment = input id: 'environment', message: 'Please select your environment.', ok: 'Proceed?', parameters: [choice(choices: ENVIRONMENTS, description: 'Select a environment to deploy.', name: 'ENVIRONMENT')], submitter: 'jenkins', submitterParameter: 'jenkins'
                    }
                }
            }
          }
      }

       stage('parallel') {
            parallel {
            stage('Shamu-hrs-company-deploy') {
                steps {
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-company-service") {
                        sh "git checkout ${getRelease['RELEASE']} && sudo bin/deploy-run ${getEnvironment['ENVIRONMENT']}"
                    }
                }
            }
            stage('Shamu-hrs-gateway-deploy') {
                steps {
                    script { env = sh(returnStdout: true, script: "echo ${getEnvironment['ENVIRONMENT']} | sed 's/^.*-//'") }
                    script { stack_prefix = sh(returnStdout: true, script: """ echo ${getEnvironment['ENVIRONMENT']} | sed 's/-[^-]*\$//' """)}
                    dir(path: "${HRS_WORKSPACE}") {
                        sh "sudo ansible-playbook --vault-password-file password -e 'stack_prefix=${stack_prefix} env=${env} workspace=${HRS_WORKSPACE} deploy_branch=heads/${getRelease['RELEASE']}' shamu-hrs/auto-deploy/ansible/refresh-eb-config.yml"
                    }
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-gateway") {
                        sh "git checkout master"
                        // delete all local branches except master
                        sh "git branch | grep -v 'master' | xargs git branch -D"
                        sh "git checkout -b ${getRelease['RELEASE']} ${getRelease['RELEASE']}"
                        sh "sudo bin/deploy ${env}"
                    }
                }
            }
            stage('Shamu-hrs-document-deploy') {
                steps {
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-document-service") {
                        sh "git checkout ${getRelease['RELEASE']} && sudo bin/deploy-run ${getEnvironment['ENVIRONMENT']}"
                    }
                }
            }
            stage('Shamu-hrs-logging-deploy') {
                steps {
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-logging-service") {
                        sh "git checkout ${getRelease['RELEASE']} && sudo bin/deploy-run ${getEnvironment['ENVIRONMENT']}"
                    }
                }
            }
            stage('Shamu-hrs-search-deploy') {
                steps {
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-search-service") {
                        sh "git checkout ${getRelease['RELEASE']} && sudo bin/deploy-run ${getEnvironment['ENVIRONMENT']}"
                    }
                }
            }
            stage('Shamu-hrs-web-deploy') {
                steps {
                    dir(path: "${HRS_WORKSPACE}/shamu-hrs-web") {
                        sh "git checkout ${getRelease['RELEASE']} && sudo bin/deploy-run ${getEnvironment['ENVIRONMENT']}"
                    }
                }
            }
           }
       }

   }
}