def call() {
    script{
        withEnv(["GOPATH=${root}"]) {
            sh '''#!/bin/bash
                echo "GOPATH = ${env.root}
            '''
        }
    }
}
