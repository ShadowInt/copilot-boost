import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildkonfig)
}

val projectVersion = "0.0.2"
val projectName = "copilot-boost"

buildkonfig {
    packageName = "ru.copilot.boost"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PROJECT_VERSION",
            projectVersion,
        )
    }
}

kotlin {
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.register("updateVersionInFiles") {
    val dockerComposeFile = File(project.rootDir, "docker-compose.yml")
    val newComposeFileContent = dockerComposeFile
        .readText()
        .replace("""$projectName:\d+\.\d+\.\d+""".toRegex(), "$projectName:$projectVersion")
    dockerComposeFile.writeText(newComposeFileContent)

    val readmeFile = File(project.rootDir, "README.md")
    val newReadmeFileContent = readmeFile
        .readText()
        .replace("""$projectName:\d+\.\d+\.\d+""".toRegex(), "$projectName:$projectVersion")
    readmeFile.writeText(newReadmeFileContent)
}

tasks.named("wasmJsBrowserDistribution") {
    dependsOn("updateVersionInFiles")
}

//tasks.named("jsBrowserDistribution") {
//    dependsOn("updateVersionInFiles")
//}
