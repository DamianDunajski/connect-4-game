plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("io.swagger.core.v3:swagger-annotations-jakarta:2.2.54")
    implementation("com.google.guava:guava")
}

description = "domain"
