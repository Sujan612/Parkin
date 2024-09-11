plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}


android {
    namespace = "com.example.parkin1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.parkin1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding=true
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


}

dependencies {
    implementation(libs.google.services)
    implementation(libs.firebase.database)
    implementation(libs.play.services)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-params:5.8.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.material)
    implementation(libs.play.services.tflite.acceleration.service)
    implementation("org.jetbrains:annotations:15.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)



}

