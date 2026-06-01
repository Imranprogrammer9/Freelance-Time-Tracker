import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.about.libraries)
    alias(libs.plugins.detekt)
}

// detekt + ktlint (via detekt-formatting) for the app module.
// Root build.gradle.kts only configures the root project; without this the
// `detekt` task is NO-SOURCE because all source lives in :app.
detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    autoCorrect = true
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
// PostHog project API key + host. Empty key = PostHog disabled (AnalyticsManager no-ops it).
val postHogApiKey: String = localProps.getProperty("posthog.api.key", "")
val postHogHost: String = localProps.getProperty("posthog.host", "https://us.i.posthog.com")
// OpenRouter API key — generic AI access (100+ models behind one key).
// Empty if not set — OpenRouterAiRepository surfaces 401s when called; nothing breaks at build time.
val openRouterApiKey: String = localProps.getProperty("openrouter.api.key", "")
// Sentry DSN — error / breadcrumb reporting. Empty if not set; SentryTree is not planted.
val sentryDsn: String = localProps.getProperty("sentry.dsn", "")

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
        buildConfigField("String", "POSTHOG_API_KEY", "\"$postHogApiKey\"")
        buildConfigField("String", "POSTHOG_HOST", "\"$postHogHost\"")
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterApiKey\"")
        buildConfigField("String", "SENTRY_DSN", "\"$sentryDsn\"")
    }

    // Locales the app ships with. Add a language => add values-XX/strings.xml
    // and append the tag here. Per-app language picker reads this set.
    androidResources {
        @Suppress("UnstableApiUsage")
        localeFilters += setOf("en")
    }

    // Release signing — reads from RELEASE_* env vars (set in CI). When they're absent
    // (local dev, the kit out of the box) no release signingConfig is created, so
    // `bundleRelease` produces an unsigned artifact instead of failing the build.
    val releaseStoreFile: String? = System.getenv("RELEASE_STORE_FILE")
    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (releaseStoreFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.okhttp)

    // Firebase Auth alternative (slot — activate by adding google-services.json + applying google-services plugin).
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Credential Manager — Google sign-in path. Deeplink OAuth (via supabase-kt) is fallback.
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // RevenueCat — subscriptions + prebuilt Paywall composable from purchases-ui
    // (Jetpack Compose for Android; NOT Compose Multiplatform).
    implementation(libs.revenuecat.purchases)
    implementation(libs.revenuecat.purchases.ui)

    // Analytics — PostHog + Firebase Analytics + Crashlytics.
    // Firebase pieces stay inert until google-services.json + plugins are added.
    implementation(libs.posthog.android)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Ops — FCM push, Firebase Remote Config, Play In-App Review.
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.play.review)

    // Logging — Timber. KitApplication plants DebugTree (debug) + CrashlyticsTree (release).
    implementation(libs.timber)

    // Sentry — optional alternative / addition to Firebase Crashlytics. Inert until
    // KitConfig.SENTRY_ENABLED is true AND BuildConfig.SENTRY_DSN is non-empty.
    implementation(libs.sentry.android)

    // AboutLibraries — pairs with the aboutLibraries plugin above. Compose-M3 module ships
    // a `LibrariesContainer` composable that renders the build-time-generated dep list.
    // Settings → About → Open source licenses navigates to a kit-owned screen that uses it.
    implementation(libs.aboutlibraries.compose.m3)

    detektPlugins(libs.detekt.formatting)

    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}

// ─────────────────────────────────────────────────────────────────────────────
// refactorPackage — rename the kit's package + applicationId + app name in one shot.
// Adapted from the ShipKaro KMM starter for this single-module native project.
//
// Usage (run once, early — before you add your own code):
//   ./gradlew refactorPackage -PnewAppId=com.acme.myapp -PnewAppName=MyApp
//   ./gradlew refactorPackage -PnewAppId=com.acme.myapp -PnewAppName=MyApp -PshouldUpdatePackageName=false
// ─────────────────────────────────────────────────────────────────────────────
tasks.register("refactorPackage") {
    group = "shipkaro"
    description = "Renames applicationId, app name, package declarations and directory structure"
    notCompatibleWithConfigurationCache("Uses Task.project and file I/O at execution time")

    doLast {
        val newAppId = project.findProperty("newAppId")?.toString()
            ?: throw GradleException("Missing parameter. Usage: -PnewAppId=com.example.app")
        val newAppName = project.findProperty("newAppName")?.toString()
            ?: throw GradleException("Missing parameter. Usage: -PnewAppName=MyApp")
        val updatePackage = project.findProperty("shouldUpdatePackageName")
            ?.toString()?.toBoolean() ?: true

        val packagePattern = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*){1,}$")
        if (!packagePattern.matches(newAppId)) {
            throw GradleException(
                "Invalid package name: '$newAppId'. Must be lowercase dot-separated, e.g. com.example.app",
            )
        }

        // Detect current package from the `namespace` in this build file.
        val buildGradle = file("build.gradle.kts")
        val oldAppId = buildGradle.readLines()
            .firstOrNull { it.trim().startsWith("namespace") }
            ?.substringAfter("\"")?.substringBefore("\"")
            ?: throw GradleException("Could not detect namespace in app/build.gradle.kts")

        if (oldAppId == newAppId) {
            println("\n⚠️  New app ID equals the current one ($oldAppId). Nothing to do.\n")
            return@doLast
        }

        val oldPath = oldAppId.replace('.', '/')
        val newPath = newAppId.replace('.', '/')
        val changed = mutableListOf<String>()

        fun File.replaceText(old: String, new: String): Boolean {
            if (!exists() || !isFile) return false
            val original = readText()
            val updated = original.replace(old, new)
            return if (updated != original) { writeText(updated); true } else false
        }

        // 1. build.gradle.kts — namespace + applicationId
        if (buildGradle.replaceText("\"$oldAppId\"", "\"$newAppId\"")) {
            changed += "app/build.gradle.kts"
        }

        // 2. strings.xml — app_name (every locale)
        fileTree("src").matching { include("**/res/values*/strings.xml") }.forEach { f ->
            val original = f.readText()
            val updated = original.replace(
                Regex("<string name=\"app_name\">[^<]*</string>"),
                "<string name=\"app_name\">$newAppName</string>",
            )
            if (updated != original) { f.writeText(updated); changed += f.relativeTo(rootDir).path }
        }

        // 3. Kotlin sources — package + import declarations, plus any other
        // fully-qualified references (KDoc [links], qualified type names).
        if (updatePackage) {
            fileTree("src").matching { include("**/*.kt") }.forEach { f ->
                val original = f.readText()
                val updated = original.replace(oldAppId, newAppId)
                if (updated != original) { f.writeText(updated); changed += f.relativeTo(rootDir).path }
            }

            // 4. Rename source directories
            listOf("src/main/java", "src/test/java", "src/androidTest/java").forEach { base ->
                val oldDir = file("$base/$oldPath")
                val newDir = file("$base/$newPath")
                if (oldDir.exists()) {
                    newDir.parentFile.mkdirs()
                    oldDir.copyRecursively(newDir, overwrite = true)
                    oldDir.deleteRecursively()
                    var parent = oldDir.parentFile
                    while (parent != null &&
                        parent.listFiles()?.isEmpty() == true &&
                        parent.canonicalPath != file(base).canonicalPath
                    ) {
                        parent.delete()
                        parent = parent.parentFile
                    }
                    changed += "  $base/$oldPath  →  $base/$newPath"
                }
            }
        }

        val line = "─".repeat(64)
        println("\n$line\n  Refactor complete!\n$line")
        println("  Old ID   : $oldAppId")
        println("  New ID   : $newAppId")
        println("  App Name : $newAppName")
        println("  Package  : ${if (updatePackage) "updated" else "skipped"}")
        println(line)
        changed.forEach { println("  ✓ $it") }
        println(line)
        println("  Next steps:")
        println("  1. Sync Gradle (File → Sync Project with Gradle Files)")
        if (updatePackage) println("  2. Invalidate Caches / Restart in Android Studio")
        println("  3. Update google-services.json if Firebase is configured")
        println("  4. Update the OAuth deeplink scheme/host in AndroidManifest.xml + KitConfig")
        println("$line\n")
    }
}
