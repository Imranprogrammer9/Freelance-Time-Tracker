import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// Read Supabase URL / anon key from local.properties (git-ignored). Empty if not set —
// kit ships with KitConfig.AUTH_PROVIDER = STUB by default so empty creds are fine.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val supabaseUrl: String = localProps.getProperty("supabase.url", "")
val supabaseKey: String = localProps.getProperty("supabase.key", "")
// RevenueCat Android API key (from RevenueCat dashboard > Project > Android app).
// Empty if not set — PurchaseManager no-ops gracefully so the kit still builds & runs.
val revenueCatApiKey: String = localProps.getProperty("revenuecat.android.api.key", "")

android {
    namespace = "dev.shipkaro.kit"
    // compileSdk 36 — required by androidx.browser 1.10.x (pulled by supabase-auth-kt-android 3.6.0).
    // targetSdk stays at 35 (stable; Play Store still accepts 35 in 2026).
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.shipkaro.kit"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatApiKey\"")
    }

    // Locales the app ships with. Add a language => add values-XX/strings.xml
    // and append the tag here. Per-app language picker reads this set.
    androidResources {
        @Suppress("UnstableApiUsage")
        localeFilters += setOf("en", "ur")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat) // per-app locale switching (AppCompatDelegate)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    // Optional icon packs (see libs.versions.toml for full list). R8 strips unused.
    implementation(libs.composeicons.feather)
    implementation(libs.composeicons.tabler)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.datastore.preferences)
    implementation(libs.coil.compose)

    // Supabase Auth (KMP, but configured with ktor-client-okhttp so HTTP stack is shared with Retrofit).
    // compose-auth/compose-auth-ui omitted — they pull Compose Multiplatform material3 + activity 1.12
    // which conflict with our AGP 8.5.2. Google sign-in done via Credential Manager + signInWith(IDToken).
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.okhttp)

    // Firebase Auth alternative (slot — activate by adding google-services.json + applying google-services plugin).
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Credential Manager — Google sign-in path. Deeplink OAuth (via supabase-kt) is fallback.
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // RevenueCat — subscriptions / paywall. Custom paywall UI, purchases-ui omitted (see catalog note).
    implementation(libs.revenuecat.purchases)

    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
