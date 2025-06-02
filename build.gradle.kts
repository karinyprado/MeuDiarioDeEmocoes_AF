plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // Verifique a versão mais recente no site do Firebase
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
