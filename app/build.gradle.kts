plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.izyinsta"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.izyinsta"
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

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.10")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") //Pour l'affichage des images / Gifs dans le RecyclerView
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") //Idem
    implementation ("com.android.volley:volley:1.2.1") //Pour les requêtes HTTP vers le serveur Web
    implementation ("com.github.bumptech.glide:glide:4.15.1") // Glide pour le chargement de l'image (Media Item) avec l'URI dans l'adapter
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1") // Idem
}