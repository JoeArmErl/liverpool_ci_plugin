package com.liverpool.ci

import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Binding
import groovy.lang.GroovyShell

class LiverpoolCiGradlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply('java')
        project.pluginManager.apply('jacoco')
        project.pluginManager.apply('org.cyclonedx.bom')
        project.pluginManager.apply('org.sonarqube')
        project.pluginManager.apply('checkstyle')

        project.extensions.create('archUnit', ArchUnitExtension)
        project.extensions.create('codeCoverage', CodeCoverageExtension)

        // 3) List your embedded scripts
        def scripts = [
            'core-configuration.gradle',
            'arch-unit.gradle',
            'jacoco.gradle',
            'test-conventions.gradle',
            'checkstyle-conventions.gradle'
        ]

        // 4) Load, split imports & body, then delegate the body to project
        scripts.each { name ->
            URL url = getClass().classLoader.getResource(name)
            if (!url) {
                project.logger.warn("⚠️ Could not find $name in plugin resources")
                return
            }
            project.logger.lifecycle("▶️ Applying embedded script: $name")
            String text = url.text

            // Split top‐level imports vs. the rest
            List<String> imports = []
            List<String> body    = []
            text.readLines().each { line ->
                if (line.trim().startsWith('import ')) {
                    imports << line
                } else {
                    body << line
                }
            }

            // Reassemble: imports remain at top, body runs inside project.with { … }
            StringBuilder sb = new StringBuilder()
            imports.each { sb.append(it).append('\n') }
            sb.append('project.with {\n')
            body.each   { sb.append(it).append('\n') }
            sb.append('}\n')

            // Evaluate with GroovyShell, binding 'project' in scope
            Binding binding = new Binding([ project: project ])
            GroovyShell shell = new GroovyShell(getClass().classLoader, binding)
            shell.evaluate(sb.toString())
        }
    }
}
