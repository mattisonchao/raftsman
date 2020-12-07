plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70"
}

subprojects {
    apply(plugin = "kotlin")
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
    }
}



