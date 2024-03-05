plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.learn.machinelearningandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.learn.machinelearningandroid"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {
    val cameraxVersion = "1.3.1"
    val retrofit2Version = "2.9.0"
    val okhttpVersion = "4.12.0"
    val mlkitTextRecognitionVersion = "16.0.0"
    val mlkitTranslationVersion = "17.0.2"
    val mlkitBarcodeScanningVersion = "18.3.0"
    val mlkitVisionVersion = "1.4.0-alpha04"
    val tfLiteMetadataVersion = "0.4.4"
    val tfLiteSupportPlayServicesVersion = "16.1.0"
    val tfLiteTaskVisionPlayServicesVersion = "0.4.2"
    val tfLiteGpuPlayServicesVersion = "16.2.0"
    val tfLiteGpuVersion = "2.9.0"
    val tfLiteJavaPlayServicesVersion = "16.1.0"
    val mediaPipeVersion = "0.20230731"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    implementation("com.google.mlkit:text-recognition:$mlkitTextRecognitionVersion")
    implementation("com.google.mlkit:translate:$mlkitTranslationVersion")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:$mlkitBarcodeScanningVersion")
    implementation("androidx.camera:camera-mlkit-vision:$mlkitVisionVersion")

    implementation("org.tensorflow:tensorflow-lite-metadata:$tfLiteMetadataVersion")
    implementation("com.google.android.gms:play-services-tflite-support:$tfLiteSupportPlayServicesVersion")
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:$tfLiteTaskVisionPlayServicesVersion")
    implementation("com.google.android.gms:play-services-tflite-gpu:$tfLiteGpuPlayServicesVersion")
    implementation("org.tensorflow:tensorflow-lite-gpu:$tfLiteGpuVersion")
    implementation("com.google.android.gms:play-services-tflite-java:$tfLiteJavaPlayServicesVersion")
    implementation("com.google.mediapipe:tasks-vision:$mediaPipeVersion")
    implementation("com.google.mediapipe:tasks-audio:$mediaPipeVersion")
    implementation("com.google.mediapipe:tasks-text:$mediaPipeVersion")
}