plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.izm.spokoyno"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.izm.spokoyno"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Навигация и иконки
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Lifecycle для Compose
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // DataStore
    implementation(libs.datastore.preferences)

    // Сеть (Retrofit + Gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Тесты
    testImplementation(libs.junit)

    // 👇 ДОБАВЛЕНО: BOM для тестов, чтобы разрешить версию ui-test-junit4 👇
    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}