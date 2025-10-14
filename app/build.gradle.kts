import org.jetbrains.kotlin.fir.expressions.FirEmptyArgumentList.arguments

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.sapsmostwantedapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sapsmostwantedapp"
        minSdk = 24
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
    kapt {
        arguments {
            arg("kapt.incremental.apt", "false")
        }
        javacOptions {
            option("-Xmaxerrs", 500)
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// GSON converter (for JSON)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// Scalars converter (for plain text or raw JSON strings)
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
// Lifecycle runtime (required for fragments and coroutines)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.fragment:fragment-ktx:1.8.2")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")



    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Room database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Dependency injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}