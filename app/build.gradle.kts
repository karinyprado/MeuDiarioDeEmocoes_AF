plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // Plugin Kotlin para Android
    // Adicione o plugin Gradle dos Serviços do Google
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.meudiariodeemocoes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.meudiariodeemocoes"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0") // Verifique a versão mais recente
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Importe o Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-analytics") // Adicionado conforme instrução
    implementation("com.google.firebase:firebase-firestore") // Mantido para o Firestore
    implementation("com.google.firebase:firebase-auth")      // Mantido para Autenticação

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
