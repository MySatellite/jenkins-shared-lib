def call() {
    cleanWs()
    script {
        if (params.serviceName.contains('helper-service')) {
            catchError() {
                deleteDir()
                checkout([$class: 'GitSCM', branches: [[name: '$tag']], extensions: [[$class: 'WipeWorkspace'], [$class: 'GitTagMessageExtension']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab-kumparan-ssh-key', url: 'git@gitlab.kumparan.com:sre/$serviceName.git']]])
                sh '''#!/bin/bash
                    echo $tag
                    echo "skip to step make test"
                '''
            }
        } else {
            catchError() {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'kumparan-gitlab', url: 'git@gitlab.kumparan.com:yowez/$serviceName.git']]])
                goversion = sh( script: "if [ -f goversion ]; then cat goversion | head -n 1 | tr -d '\n' ; else echo -n 'not found'; fi", returnStdout: true )
                echo goversion
                if (goversion == "not found") {
                    goversion = "Go1.14"
                }
                godir = sh( script: "(test -d /home/ubuntu/jenkins/tools/org.jenkinsci.plugins.golang.GolangInstallation/${goversion} && echo '1' || echo '0') |tr -d '\n'", returnStdout: true )
                echo "${godir}"
                if (godir == '1'){
                    env.root = "/home/ubuntu/jenkins/tools/org.jenkinsci.plugins.golang.GolangInstallation/${goversion}"
                    echo "${env.root}"
                } else {
                    env.root = tool name: goversion, type: 'go'
                    echo "${env.root}"
                }
            }
        }
    }
    //return "${env.root}"
}
