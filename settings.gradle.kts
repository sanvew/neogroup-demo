pluginManagement {
    val springBootVersion: String by settings
    val dependencyManagementPluginVersion: String by settings

    plugins {
        java
        id("org.springframework.boot").version(springBootVersion)
        id("io.spring.dependency-management").version(dependencyManagementPluginVersion)
    }
}

rootProject.name = "neogroup-demo"
