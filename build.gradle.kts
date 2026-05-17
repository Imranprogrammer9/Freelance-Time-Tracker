plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

// detekt + ktlint(via detekt-formatting) run across the whole project.
// Tooling discipline carried over from cortinico/kotlin-android-template.
detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    autoCorrect = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
