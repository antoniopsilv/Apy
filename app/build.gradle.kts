plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "br.edu.ifsp.apy"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.edu.ifsp.apy"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"   // ajuste para a versão do Compose usada
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

    viewBinding {
        enable = true
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.core)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")



    // ImagePicker para galeria e câmera
    //implementation("com.github.dhaval2404:imagepicker:2.1")

    // Caso use o CameraX (opcional)
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // Lib Ucrop para ajustar imagem
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("com.github.CanHub:Android-Image-Cropper:4.3.2")

    // Compose BOM (recomendado para manter versões em sincronia)
    implementation(platform("androidx.compose:compose-bom:2024.09.00")) // verifique a versão mais recente


    // Módulos principais do Compose
    implementation("androidx.activity:activity-compose")          // ComponentActivity, setContent
    implementation("androidx.compose.ui:ui")                      // UI básica, Modifier etc.
    implementation("androidx.compose.foundation:foundation")       // Layouts, background, shapes…
    implementation("androidx.compose.material3:material3")         // Material 3 (Buttons, Text, Theme)
//    implementation("androidx.compose.material3:material3:1.3.0") // ou mais recente
    implementation("androidx.compose.material:material-icons-core")// Core de ícones
    implementation("androidx.compose.material:material-icons-extended") // Icons.Default.Search, FolderOpen

    // Opcional: Preview no Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")

    // Tema padrão gerado pelo template
    implementation("androidx.compose.runtime:runtime")             // @Composable, remember, etc.
}