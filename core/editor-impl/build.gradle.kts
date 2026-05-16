plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:editor-api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}
