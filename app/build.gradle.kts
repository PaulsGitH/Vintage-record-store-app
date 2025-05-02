plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("org.jetbrains.dokka") version "1.8.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "ie.setu.album"
    compileSdk = 35

    defaultConfig {
        applicationId = "ie.setu.album"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("dokka"))
    }

    ktlint {
        debug.set(true)
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true  // ← use the “is…” prefix in Kotlin
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation(libs.youtube.player)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.timberkt)
    implementation(libs.picasso)
    implementation(libs.firebase.firestore.ktx)

    // JUnit4 + Robolectric for JVM unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.10")
    testImplementation("androidx.test:core:1.5.0")

    // Mockito (optional)
    testImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.0")
}
