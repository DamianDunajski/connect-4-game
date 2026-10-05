plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.com.fasterxml.jackson.core.jackson.annotations)
    api(libs.io.swagger.swagger.annotations)
    api(libs.javax.validation.validation.api)
    api(libs.com.google.guava.guava)
}

description = "domain"
