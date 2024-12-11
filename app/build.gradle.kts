plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)


    id("com.google.gms.google-services")

    kotlin("plugin.serialization") version "1.9.0" // Reemplaza con la versión de Kotlin que estés usando

}

android {
    namespace = "com.example.myfitnessapp"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.example.myfitnessapp"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.accessibility.test.framework)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.identity.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.material)

    implementation (libs.ui.tooling)

    implementation(libs.androidx.foundation)

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.analytics)

    implementation (libs.androidx.material.icons.extended)

    implementation (libs.play.services.base)

    implementation (libs.play.services.auth)

    implementation(libs.kotlinx.serialization.json)

    //Navigation
    //implementation(libs.androidx.navigation.compose.v272)
    //implementation(libs.material3)
    //implementation(libs.ui)
    //implementation(libs.ui.tooling.preview)
    //debugImplementation(libs.androidx.ui.tooling.v151)



}