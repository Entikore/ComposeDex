import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.dependency.analysis)
}

android {
    namespace = "de.entikore.composedex"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.entikore.composedex"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "de.entikore.composedex.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

detekt {
    config.setFrom("$rootDir/config/detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    autoCorrect = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.compose.ui.google.fonts)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.coroutine.core)
    runtimeOnly(libs.coroutine.android)
    implementation(libs.kotlinx.serialization.core)

    implementation(libs.dagger)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    ksp(libs.androidx.room.compiler)

    implementation(libs.coil.compose)
    runtimeOnly(libs.coil.gif)

    implementation(libs.exo.player)
    implementation(libs.exo.player.common)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.guava)
    implementation(libs.javax.inject)

    implementation(libs.androidx.core.splash)

    implementation(libs.timber)

    testImplementation(libs.jupiter.api)
    testRuntimeOnly(libs.jupiter.engine)
    testImplementation(libs.jupiter.params)
    testImplementation(libs.konsist)
    testImplementation(libs.mock.webserver)
    testImplementation(libs.okhttp)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    testImplementation(project(":sharedTestCode"))
    implementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.androidx.arch.core)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.arch.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.test.core)
    androidTestImplementation(libs.coroutine.test)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.compose.material3)
    androidTestImplementation(libs.androidx.test.runner)
    debugRuntimeOnly(libs.androidx.compose.ui.manifest)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.comp)
    androidTestImplementation(project(":sharedTestCode"))
}

// https://docs.gradle.org/9.0.0-milestone-9/userguide/upgrading_version_8.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}