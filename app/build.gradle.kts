import java.math.BigDecimal
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt")
    id("io.gitlab.arturbosch.detekt")
    jacoco
}

android {
    namespace = "kz.taskbar.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "kz.taskbar.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0-rgr3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        create("benchmark") {
            initWith(getByName("release"))
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    sourceSets.getByName("androidTest").assets.srcDir("schemas")
}

kapt {
    correctErrorTypes = true
    arguments { arg("room.schemaLocation", "$projectDir/schemas") }
}

jacoco { toolVersion = "0.8.13" }

detekt {
    buildUponDefaultConfig = false
    allRules = false
    config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
    source.setFrom(files("src/main/java"))
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")
    implementation("androidx.datastore:datastore-preferences:1.1.4")
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.room:room-testing:2.7.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestUtil("androidx.test:orchestrator:1.5.1")
}

val domainClasses = files(
    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include("kz/taskbar/app/domain/**/*.class")
        exclude("**/*\$Companion.class")
    },
    fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        include("kz/taskbar/app/domain/**/*.class")
    },
)
val unitCoverageFiles = fileTree(layout.buildDirectory) {
    include("jacoco/testDebugUnitTest.exec")
    include("outputs/unit_test_code_coverage/**/testDebugUnitTest.exec")
}
val instrumentedCoverageFiles = fileTree(layout.buildDirectory) {
    include("outputs/code_coverage/**/connected/**/*.ec")
    include("outputs/code_coverage/**/connected/**/*.exec")
}

val rgr3CoverageReport by tasks.registering(JacocoReport::class) {
    group = "verification"
    description = "HTML/XML JaCoCo report for domain business logic from JVM unit tests"
    dependsOn("testDebugUnitTest")
    classDirectories.setFrom(domainClasses)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(unitCoverageFiles)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/rgr3"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/rgr3/report.xml"))
    }
}

tasks.register<JacocoCoverageVerification>("rgr3CoverageVerify") {
    group = "verification"
    description = "Fail when business/domain line coverage falls below 70 percent"
    dependsOn(rgr3CoverageReport)
    classDirectories.setFrom(domainClasses)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(unitCoverageFiles)
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal("0.70")
            }
        }
    }
}

tasks.register<JacocoReport>("rgr3CombinedCoverageReport") {
    group = "verification"
    description = "Combined JVM and instrumentation JaCoCo (requires a prior connectedDebugAndroidTest run)"
    dependsOn("testDebugUnitTest")
    classDirectories.setFrom(domainClasses)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(unitCoverageFiles, instrumentedCoverageFiles)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/rgr3-combined"))
    }
}
