plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bus_app_go_bus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bus_app_go_bus"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    //implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.preference)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add osmdroid library
    implementation(libs.osmdroid)
    //Material library
    implementation ("com.google.android.material:material:1.9.0")
    // Add Google Play Services Location library
    implementation("com.google.android.gms:play-services-location:18.0.0")
}