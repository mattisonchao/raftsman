plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70"
}
repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.alipay.sofa:bolt:1.6.3")
    implementation("org.slf4j:slf4j-api:1.7.30")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
