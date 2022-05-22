import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.iplease"
version = "1.0.0-RELEASE"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation(files("libs/proto-lib-0.0.1-RELEASE.jar"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-bus-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.7")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")

    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("dev.miku:r2dbc-mysql")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    implementation(platform("com.linecorp.armeria:armeria-bom:1.16.0"))
    implementation("com.linecorp.armeria:armeria")
    implementation("com.linecorp.armeria:armeria-grpc")
    testImplementation("com.linecorp.armeria:armeria-junit5")
    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")

    implementation("io.grpc:grpc-protobuf:1.45.1")
    implementation("io.grpc:grpc-stub:1.45.1")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.3")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.0.0")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.11")

    implementation("io.projectreactor:reactor-core")
    testImplementation("io.projectreactor:reactor-test")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
