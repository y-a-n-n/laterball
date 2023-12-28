val logback_version: String by project
//val 1.6.8: String by project
//val 1.6.21: String by project
val koin_version: String by project

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
//    id("io.ktor.plugin") version "2.2.2"
}

group = "com.laterball.server"
version = "3.3.5"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$1.9.0")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core-jvm:2.3.7")
    implementation("io.ktor:ktor-client-core-jvm:2.3.7")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.7")
    implementation("io.ktor:ktor-client-auth-jvm:2.3.7")
    implementation("io.ktor:ktor-client-json-jvm:2.3.7")
    implementation("io.ktor:ktor-client-gson-jvm:2.3.7")
    implementation("io.ktor:ktor-client-logging-jvm:2.3.7")
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-servlet-jvm:2.3.7")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.7")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")
    implementation("io.ktor:ktor-server-locations-jvm:2.3.7")
    implementation("io.ktor:ktor-server-metrics-jvm:2.3.7")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("io.insert-koin:koin-test:$koin_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:2.3.7")
    implementation("org.litote.kmongo:kmongo-coroutine:4.2.8")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.7")


    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.7")
    testImplementation("io.ktor:ktor-client-mock-jvm:2.3.7")
    testImplementation("io.ktor:ktor-client-mock-jvm:2.3.7")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

