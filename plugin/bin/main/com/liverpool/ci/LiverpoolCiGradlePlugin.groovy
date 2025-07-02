package com.liverpool.ci

import org.gradle.api.Plugin
import org.gradle.api.Project

class LiverpoolCiGradlePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('archUnit', ArchUnitExtension)

        URL scriptUrl = getClass().classLoader.getResource('arch-unit.gradle')
        if (scriptUrl) {
            project.apply(from: scriptUrl)
            project.logger.lifecycle("✅ Applied arch-unit.gradle")
        } else {
            project.logger.warn("⚠️ arch-unit.gradle not found in plugin JAR")
        }
    }
}
