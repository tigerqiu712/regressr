node {
    echo    'Running Regressr Build'

    def app = "regressr"

    checkout scm

    stage 'Load files from GitHub'
    def sbt
    fileLoader.withGit('git@github.corp.ebay.com:N/infra.git', 'master', '10727ea9-7a7b-4309-afd0-a22487fda3aa', '') {
        sbt = fileLoader.load('pipeline/sbt');
    }

    sbt.testWithCoverageStage()
    sbt.scalaStyleStage()
}