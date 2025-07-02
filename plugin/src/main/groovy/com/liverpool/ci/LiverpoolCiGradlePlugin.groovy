package com.liverpool.ci

import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Binding
import groovy.lang.GroovyShell

class LiverpoolCiGradlePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply('java')    // for jar{}, compileJava{}, javadoc{}
        project.pluginManager.apply('jacoco')
        project.extensions.create('archUnit', ArchUnitExtension)
        project.extensions.create('codeCoverage', CodeCoverageExtension)

        // 2) list all the embedded Gradle scripts you want to apply
        def scripts = [
            'arch-unit.gradle',
            'jacoco.gradle'
            // ← add more filenames here as you add new .gradle files
        ]

        // 3) for each script, GroovyShell-evaluate it so it sees your classes
        scripts.each { name ->
            URL url = getClass().classLoader.getResource(name)
            if (url) {
                project.logger.lifecycle("▶️ Applying embedded script: $name")
                String text    = url.text
                Binding binding = new Binding([ project: project ])
                GroovyShell shell = new GroovyShell(getClass().classLoader, binding)
                shell.evaluate(text)
            } else {
                project.logger.warn("⚠️  Could not find $name in plugin resources")
            }
        }
    }
}
