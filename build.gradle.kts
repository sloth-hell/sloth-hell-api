import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.9.20"

	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.epages.restdocs-api-spec") version "0.19.2"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("kapt") version kotlinVersion
}

group = "com.slothhell"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
}

val copyOasToSwaggerTaskName = "copyOasToSwagger"
val swaggerDir = "src/main/resources/static/swagger-ui/"
val openapi3OutputDir = "build/resources/main/static/swagger-ui/"
val oasFileName = "openapi3.yml"
val jarName = "sloth-hell.jar"
val mysqlVersion = "8.0.28"
val jjwtVersion = "0.12.3"
val jdslVersion = "3.3.1"
val jdslStarterVersion = "2.2.1.RELEASE"
val restdocsApiSpecVersion = "0.19.2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
	implementation("com.linecorp.kotlin-jdsl:jpql-dsl:$jdslVersion")
	implementation("com.linecorp.kotlin-jdsl:jpql-render:$jdslVersion")
	implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:$jdslVersion")
	implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:$jdslStarterVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
	runtimeOnly("mysql:mysql-connector-java:$mysqlVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("com.epages:restdocs-api-spec-mockmvc:$restdocsApiSpecVersion")
	testImplementation("org.springframework.security:spring-security-test")
	modules {
		module("org.springframework.boot:spring-boot-starter-logging") {
			replacedBy("org.springframework.boot:spring-boot-starter-log4j2")
		}
	}
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

openapi3 {
	setServer("http://sloth-hell.com")
	title = "나태지옥 API Docs"
	description = "Sloth-Hell API Description"
	version = "1.0.0"
	format = "yml"
	outputDirectory = openapi3OutputDir
}

val copyOasToBuildSwaggerDir = tasks.register<Copy>(copyOasToSwaggerTaskName) {
	dependsOn("openapi3")
	from(openapi3OutputDir)
	into(swaggerDir)
}

tasks.jar {
	enabled = false
}

tasks.resolveMainClassName {
	dependsOn(copyOasToBuildSwaggerDir)
}

tasks.bootJar {
	archiveFileName.set(jarName)
}
