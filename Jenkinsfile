pipeline {
	agent any

   

    stages {
		stage('Flutter Safe Directory Fix') {
			steps {
				sh 'git config --global --add safe.directory /opt/flutter'
    }
}




		stage('Checkout') {
			steps {
				echo 'Kod repodan çekiliyor...'
                checkout scm
            }
        }
	    	stage('Flutter Safe Directory Fix') {
			steps {
				sh 'git config --global --add safe.directory /opt/flutter'
    }
}

        stage('Flutter Sürümü Kontrol') {
			steps {
				sh 'flutter --version'
            }
        }

        stage('Flutter Clean') {
			steps {
				sh 'flutter clean'
            }
        }

        stage('Dependencies (flutter pub get)') {
			steps {
				sh 'flutter pub get'
            }
        }

        stage('Flutter Analyze') {
			steps {
				sh 'flutter analyze'
            }
        }

        stage('Flutter Test') {
			steps {
				sh 'flutter test'
            }
        }
    }

    post {
		success {
			echo '✅ Pipeline başarılı şekilde tamamlandı.'
        }
        failure {
			echo '❌ Hata oluştu.'
        }
    }
}
