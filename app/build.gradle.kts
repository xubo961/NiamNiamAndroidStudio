plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.xubop961.niamniamapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xubop961.niamniamapp"
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
}

dependencies {
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.6")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.6")
    implementation ("com.google.android.material:material:1.9.0")
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.v2110)
    implementation (libs.converter.gson.v2110)
    implementation ("com.google.android.material:material:1.6.0")
    implementation(libs.glide)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}