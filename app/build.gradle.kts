  plugins {
    id("com.android.application") // Apply the Android application plugin
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // Apply the Firebase Google Services plugin
}

android {
    namespace = "com.example.asm2android" // Set the namespace for your app
    compileSdk = 34 // Specify the SDK version used to compile your app

    defaultConfig {
        applicationId = "com.example.asm2android" // Package name of your app
        minSdk = 24 // Minimum SDK version supported by your app
        targetSdk = 34 // Target SDK version
        versionCode = 1 // App version code
        versionName = "1.0" // App version name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Test instrumentation runner
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Disable code minification for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Default ProGuard rules
                "proguard-rules.pro" // Custom ProGuard rules
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Java source compatibility
        targetCompatibility = JavaVersion.VERSION_11 // Java target compatibility
    }

    buildFeatures {
        viewBinding = true // Enable view binding
    }
}

dependencies {
    // AndroidX and Material Design libraries
    implementation("androidx.appcompat:appcompat:1.6.1") // AppCompat for backward compatibility
    implementation("com.google.android.material:material:1.9.0") // Material design components
    implementation("androidx.activity:activity-ktx:1.7.2") // Activity KTX for Jetpack support
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout for layouts

    // Firebase and Google Play Services dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Firebase BOM for consistent versions
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
    implementation("com.google.firebase:firebase-database-ktx") // Firebase Realtime Database
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.activity) // Google Maps SDK



    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2") // JUnit for unit tests

    // Android instrumented testing dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // JUnit for Android testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Espresso for UI testing
}
