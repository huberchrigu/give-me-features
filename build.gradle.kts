import kotlin.io.path.Path

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("gg.jte.gradle") version "3.2.1"
}

group = "ch.chrigu.gmf"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "2.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("gg.jte:jte-spring-boot-starter-3:3.2.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("tools.jackson.module:jackson-module-kotlin")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
    runtimeOnly("org.springframework.modulith:spring-modulith-observability")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mongodb")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Spring custom
    testImplementation("org.springframework.boot:spring-boot-devtools") // because we use the Test... run config
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.awaitility:awaitility-kotlin")
    runtimeOnly("io.projectreactor:reactor-tools")

    // KTE
    implementation("gg.jte:jte-kotlin:3.2.1")

    // Webjars
    runtimeOnly("org.webjars.npm:htmx.org:2.0.4")
    runtimeOnly("org.webjars.npm:htmx-ext-sse:2.2.3")
    runtimeOnly("org.webjars.npm:htmx-ext-response-targets:2.0.3")
    runtimeOnly("org.webjars.npm:coreui__coreui:5.4.3")
    runtimeOnly("org.webjars.npm:coreui__icons:3.0.1")
    runtimeOnly("org.webjars:webjars-locator-lite")

    // HTML sanitizer
    implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20240325.1")

    // Markdown
    implementation("org.jetbrains:markdown:0.7.3")
    implementation("io.github.java-diff-utils:java-diff-utils:4.15")

    // Testing
    testImplementation("com.microsoft.playwright:playwright:1.45.1")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.jsoup:jsoup:1.18.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

jte {
    generate()
    binaryStaticContent = true
    sourceDirectory = Path("src/main/kte")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Playwright
tasks.register<JavaExec>("playwrightInstallDeps") {
    mainClass = "com.microsoft.playwright.CLI"
    classpath = sourceSets.test.get().runtimeClasspath
    args = listOf("install-deps")
}