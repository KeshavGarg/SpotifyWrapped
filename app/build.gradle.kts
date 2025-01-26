plugins {
    id("com.android.application")
}

android {
    namespace = "com.broyojo.spotifywrappedclone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.broyojo.spotifywrappedclone"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["redirectSchemeName"] = "spotify-sdk"
        manifestPlaceholders["redirectHostName"] = "auth"

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
}

dependencies {
    implementation("com.spotify.android:auth:1.2.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}