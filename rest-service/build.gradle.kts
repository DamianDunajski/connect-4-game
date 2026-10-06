plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":domain"))
    api("io.dropwizard:dropwizard-core")
    testImplementation("io.dropwizard:dropwizard-testing")
    api("com.smoketurner:dropwizard-swagger:4.0.5-1")
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
