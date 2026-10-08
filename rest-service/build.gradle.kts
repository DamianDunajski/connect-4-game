plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    api(project(":domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    testImplementation(libs.spring.boot.starter.test)
    implementation(libs.springdoc.openapi)
}

description = "rest-service"
