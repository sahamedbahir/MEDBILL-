plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.projectmedbill"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectmedbill"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    // Add the packagingOptions block here to exclude the conflicting NOTICE.md files
    packagingOptions {
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE")
    }
}

dependencies {
    implementation(libs.material.v190) // Material Components
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation(libs.itext7.core)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    implementation (libs.appcompat ) // Use the latest version
    implementation (libs.android.mail.v161)
    implementation (libs.android.activation.v161)



    implementation(libs.appcompat.v170) // AndroidX AppCompat library
    implementation(libs.constraintlayout) // Constraint Layout
    implementation(libs.firebase.auth.v2230) // Firebase Authentication
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation(libs.navigation.fragment) // Navigation component for fragments
    implementation(libs.navigation.ui) // Navigation component UI
    implementation(libs.activity) // AndroidX Activity KTX
    implementation("com.github.bumptech.glide:glide:4.15.0") // Latest Glide version
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0") // Glide compiler

    // Testing dependencies
    testImplementation(libs.junit) // JUnit for local unit testing
    androidTestImplementation(libs.ext.junit) // JUnit extensions for Android testing
    androidTestImplementation(libs.espresso.core) // Espresso for UI testing
}