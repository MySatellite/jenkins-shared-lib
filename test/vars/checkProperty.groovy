def call() {
    script{
        withEnv(["GOPATH=${root}"]) {
            sh '''#!/bin/bash
                echo "GOPATH = ${root}
            '''
        }
    }
}
