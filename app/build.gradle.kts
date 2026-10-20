import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

// Lee la clave de Maps desde local.properties (archivo que NO se sube a GitHub)
val propiedadesLocales = Properties()
val archivoLocal = rootProject.file("local.properties")
if (archivoLocal.exists()) {
    propiedadesLocales.load(archivoLocal.inputStream())
}

android {
    namespace = "cl.reymago.georegistro"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.reymago.georegistro"
        minSdk = 27
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Reemplaza ${MAPS_API_KEY} en el AndroidManifest al compilar
        manifestPlaceholders["MAPS_API_KEY"] = propiedadesLocales.getProperty("MAPS_API_KEY", "")
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
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}