plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.learn.machinelearningandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.learn.machinelearningandroid"
        minSdk = 23
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
    val retrofit2 = "2.9.0"
    val okhttp = "4.12.0"
    val mlkitTextRecognition = "16.0.0"
    val mlkitTranslation = "17.0.2"
    val mlkitBarcodeScanning = "18.3.0"
    val mlkitVision = "1.4.0-alpha04"
    val tfLiteMetadata = "0.4.4"
    val tfLiteSupportPlayServices = "16.1.0"
    val tfLiteTaskVisionPlayServices = "0.4.2"
    val tfLiteGpuPlayServices = "16.2.0"
    val tfLiteGpu = "2.9.0"
    val tfLiteJavaPlayServices = "16.1.0"

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

    implementation("com.squareup.retrofit2:retrofit:$retrofit2")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")

    implementation("com.google.mlkit:text-recognition:$mlkitTextRecognition")
    implementation("com.google.mlkit:translate:$mlkitTranslation")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:$mlkitBarcodeScanning")
    implementation("androidx.camera:camera-mlkit-vision:$mlkitVision")

    implementation("org.tensorflow:tensorflow-lite-metadata:$tfLiteMetadata")
    implementation("com.google.android.gms:play-services-tflite-support:$tfLiteSupportPlayServices")
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:$tfLiteTaskVisionPlayServices")
    implementation("com.google.android.gms:play-services-tflite-gpu:$tfLiteGpuPlayServices")
    implementation("org.tensorflow:tensorflow-lite-gpu:$tfLiteGpu")
    implementation("com.google.android.gms:play-services-tflite-java:$tfLiteJavaPlayServices")


}