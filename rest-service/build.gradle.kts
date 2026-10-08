plugins {
    id("buildlogic.java-conventions")
    id("org.springframework.boot") version "4.1.1"
}

dependencies {
    api(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
}

description = "rest-service"
