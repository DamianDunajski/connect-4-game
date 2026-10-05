plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":domain"))
    api(libs.io.dropwizard.dropwizard.core)
    api(libs.com.smoketurner.dropwizard.swagger)
    api(libs.javax.xml.bind.jaxb.api)
    runtimeOnly(libs.org.glassfish.jaxb.jaxb.runtime)
    testImplementation(libs.io.dropwizard.dropwizard.testing)
}

tasks.named<Test>("test") {
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
}

tasks.named<Jar>("jar") {
    dependsOn(copyDependencies)
    manifest {
        attributes(
            "Main-Class" to "com.kainos.connect4game.rest.Application",
            "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") {
                "dependencies/${it.name}"
            }
        )
    }
}

val copyDependencies = tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs/dependencies"))
}

description = "rest-service"
