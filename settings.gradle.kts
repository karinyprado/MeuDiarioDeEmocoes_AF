// settings.gradle.kts
import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS // Esta linha é opcional, mas não prejudica

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Adicionado para MPAndroidChart e outras bibliotecas do JitPack
        maven { url = uri("https://jitpack.io") }
    }
}
// Ajustado para corresponder ao nome do projeto do usuário, se for o caso.
// O importante é que rootProject.name corresponda ao nome da pasta raiz do seu projeto.
// Se a pasta se chama "MeuDiarioDeEmocoes" (sem espaços), então use:
// rootProject.name = "MeuDiarioDeEmocoes"
// Se a pasta se chama "Meu Diario de Emocoes" (com espaços), então o seu está correto:
rootProject.name = "Meu Diario de Emocoes"
include(":app")