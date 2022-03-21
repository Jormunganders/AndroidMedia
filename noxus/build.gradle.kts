plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "me.juhezi.sub.noxus"
        minSdk = 21
        targetSdk = 32
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
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
    viewBinding {
        isEnabled = true
    }
}

dependencies {
    Libs.baseLibs.forEach {
        implementation(it)
    }
    Libs.rxLibs.forEach {
        implementation(it)
    }
    Libs.roomLibs.forEach {
        implementation(it)
    }
    Libs.lifeCycleLibs.forEach {
        implementation(it)
    }
    Libs.ktxLibs.forEach {
        implementation(it)
    }
    implementation(Libs.hilt)
    implementation(Libs.gson)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.apache.ftpserver:ftpserver-core:1.2.0")

    kapt(AnnotationProcessors.room)
    kapt(AnnotationProcessors.hilt)
}