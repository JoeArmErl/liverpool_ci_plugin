package com.liverpool.ci

import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Binding
import groovy.lang.GroovyShell

class LiverpoolCiGradlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyCorePlugins(project)
        registerExtensions(project)
        applyEmbeddedScripts(project)
    }

    private void applyCorePlugins(Project project) {
        ['java', 'jacoco', 'org.cyclonedx.bom', 'org.sonarqube', 'checkstyle']
            .each { project.pluginManager.apply(it) }
    }

    private void registerExtensions(Project project) {
        project.extensions.create('archUnit',    ArchUnitExtension)
        project.extensions.create('codeCoverage', CodeCoverageExtension)
    }

    private void applyEmbeddedScripts(Project project) {
        def scripts = [
            'core-configuration.gradle',
            'arch-unit.gradle',
            'jacoco.gradle',
            'test-conventions.gradle',
            'checkstyle-conventions.gradle'
        ]

        scripts.each { name ->
            URL url = getClass().classLoader.getResource(name)
            if (!url) {
                project.logger.warn("⚠️ Could not find $name")
                return
            }
            project.logger.lifecycle("▶️ Applying $name")
            shellEvaluateScript(project, url.text)
        }
    }

    private void shellEvaluateScript(Project project, String scriptText) {
        def (imports, body) = splitImportsAndBody(scriptText)
        String wrapped = buildWrappedScript(imports, body)
        def binding = new Binding([ project: project ])
        def shell   = new GroovyShell(getClass().classLoader, binding)
        shell.evaluate(wrapped)
    }

    private List splitImportsAndBody(String text) {
        def imports = []
        def body    = []
        text.readLines().each { line ->
            if (line.trim().startsWith('import ')) imports << line
            else                                     body    << line
        }
        [imports, body]
    }

    private String buildWrappedScript(List<String> imports, List<String> body) {
        def sb = new StringBuilder()
        imports.each { sb.append(it).append('\n') }
        sb.append('project.with {\n')
        body.each   { sb.append(it).append('\n') }
        sb.append('}\n')
        sb.toString()
    }
}
