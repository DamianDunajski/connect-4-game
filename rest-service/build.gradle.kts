plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.graalvm.native)
}

dependencies {
    api(project(":domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.restclient)
    implementation(libs.spring.boot.starter.actuator)
    testImplementation(libs.spring.boot.starter.test)
    implementation(libs.spring.boot.micrometer.tracing.opentelemetry)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.springdoc.openapi)
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("connect4game-rest-service")
            mainClass.set("com.kainos.connect4game.rest.Application")
            sharedLibrary.set(false)
        }
    }
    metadataRepository {
        enabled.set(true)
    }
}

description = "rest-service"
