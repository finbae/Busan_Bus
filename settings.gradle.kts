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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://jitpack.io")
//        maven("https://jitpack.io"){
//            credentials {
//                username = "Songwon"
//                password = "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"
//            }
//
//        }
    }
}

rootProject.name = "MyProject"
include(":app")
