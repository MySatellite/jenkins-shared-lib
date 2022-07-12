def call() {
    script{
        withEnv(["GOPATH=${env.root}"]) {
            sh '''#!/bin/bash
                echo "GOPATH = ${env.root}"
            '''
        }
    }
}
