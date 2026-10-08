plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.graalvm.native)
}

dependencies {
    api(project(":domain"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    testImplementation(libs.spring.boot.starter.test)
    implementation(libs.springdoc.openapi)
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("connect-4-game")
            mainClass.set("com.kainos.connect4game.rest.Application")
            sharedLibrary.set(false)
        }
    }
    metadataRepository {
        enabled.set(true)
    }
}

description = "rest-service"
