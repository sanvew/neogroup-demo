plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

java {
	val javaTargetVersion: String by project

	toolchain {
		languageVersion = JavaLanguageVersion.of(javaTargetVersion)
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

dependencies {
	// common
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.springframework:spring-aspects")
	// utils
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.springframework.retry:spring-retry")
	// database
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.liquibase:liquibase-core")
	// testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:toxiproxy")
	testImplementation("org.testcontainers:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
