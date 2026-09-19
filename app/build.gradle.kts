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
    
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
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
    debug = true
}

dependencies {
    detektPlugins(libs.dev.detekt.detekt.rules.ktlint.wrapper)
    detektPlugins(libs.io.nlopez.compose.rules.detekt)

    val composeBom = platform(libs.androidx.compose.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.activity.activity)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.annotation.annotation)
    implementation(libs.androidx.collection.collection)
    implementation(libs.androidx.compose.animation.animation)
    implementation(libs.androidx.compose.animation.animation.core)
    implementation(libs.androidx.compose.foundation.foundation)
    implementation(libs.androidx.compose.foundation.foundation.layout)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.runtime.runtime)
    implementation(libs.androidx.compose.runtime.runtime.saveable)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.geometry)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.text)
    implementation(libs.androidx.compose.ui.ui.tooling)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.ui.unit)
    implementation(libs.androidx.compose.ui.ui.text.google.fonts)

    implementation(libs.androidx.core.core.ktx)

    implementation(libs.androidx.navigation3.navigation3.ui)
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.savedstate)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    runtimeOnly(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.core)

    implementation(libs.com.google.dagger.dagger)
    implementation(libs.com.google.dagger.hilt.android)
    implementation(libs.com.google.dagger.hilt.core)
    ksp(libs.com.google.dagger.hilt.compiler)
    ksp(libs.org.jetbrains.kotlin.kotlin.metadata.jvm)

    implementation(libs.androidx.datastore.datastore.core)
    implementation(libs.androidx.datastore.datastore.preferences)
    implementation(libs.androidx.datastore.datastore.preferences.core)
    implementation(libs.androidx.fragment.fragment)
    implementation(libs.androidx.hilt.hilt.lifecycle.viewmodel.compose)

    implementation(libs.androidx.room.room.runtime)
    implementation(libs.androidx.room.room.common)
    implementation(libs.androidx.sqlite.sqlite)
    ksp(libs.androidx.room.room.compiler)

    implementation(libs.io.coil.kt.coil3.coil.compose)
    runtimeOnly(libs.io.coil.kt.coil3.coil.gif)

    implementation(libs.androidx.media3.media3.exoplayer)
    implementation(libs.androidx.media3.media3.common)

    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.moshi)
    implementation(libs.com.squareup.moshi.moshi)
    implementation(libs.com.squareup.moshi.moshi.kotlin)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.com.google.guava.guava)
    implementation(libs.javax.inject.javax.inject)

    implementation(libs.androidx.core.core.splashscreen)

    implementation(libs.com.jakewharton.timber.timber)

    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.params)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
    testImplementation(libs.com.lemonappdev.konsist)
    testImplementation(libs.com.squareup.okhttp3.mockwebserver)
    testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    testImplementation(libs.com.google.truth.truth)
    testImplementation(libs.app.cash.turbine.turbine)
    testImplementation(project(":sharedTestCode"))
    implementation(libs.androidx.test.core.ktx)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(libs.androidx.arch.core.core.testing)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.arch.core.core.testing)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.ui.test)
    androidTestImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    androidTestImplementation(libs.app.cash.turbine.turbine)
    androidTestImplementation(libs.com.google.truth.truth)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)
    debugRuntimeOnly(libs.androidx.compose.ui.ui.test.manifest)
    androidTestImplementation(libs.com.google.dagger.hilt.android.testing)
    kspAndroidTest(libs.com.google.dagger.hilt.android.compiler)
    androidTestImplementation(project(":sharedTestCode"))
}

// https://docs.gradle.org/9.0.0-milestone-9/userguide/upgrading_version_8.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}
