package com.liverpool.ci

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class LiverpoolCiGradlePluginTest extends Specification {
    def "plugin registers both DSL extensions with correct defaults"() {
        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.liverpool.ci')

        then: "archUnit extension defaults"
        def archExt = project.extensions.findByType(ArchUnitExtension)
        archExt.excludedPaths == ['controller.model','application.data']
        archExt.preConfiguredRules.contains('com.societegenerale.commons.plugin.rules.NoInjectedFieldTest')

        and: "codeCoverage extension defaults"
        def covExt = project.extensions.findByType(CodeCoverageExtension)
        covExt.coverageExclusions   == []
        covExt.jacocoInstructionMin == 0.80
        covExt.jacocoBranchMin      == 0.70
        covExt.jacocoLineMin        == 0.75
        covExt.jacocoComplexityMin  == 0.60
        covExt.jacocoMethodMin      == 0.85
        covExt.jacocoClassMin       == 0.80
    }

    def "plugin configures core build settings and applies quality plugins"() {
        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.liverpool.ci')

        then: "the 'jar' task manifest carries project coordinates"
        def jarTask = project.tasks.getByName('jar')
        jarTask.manifest.attributes['Implementation-Title']      == project.name
        jarTask.manifest.attributes['Implementation-Version']    == project.version
        jarTask.manifest.attributes['Implementation-Vendor-Id']  == project.group
        jarTask.manifest.attributes['Implementation-Vendor']     == project.group

        and: "compile & javadoc tasks use UTF-8"
        project.tasks.compileJava.options.encoding    == 'UTF-8'
        project.tasks.compileTestJava.options.encoding== 'UTF-8'
        project.tasks.javadoc.options.encoding        == 'UTF-8'
        !project.tasks.javadoc.failOnError

        and: "CycloneDX and SonarQube plugins are applied and configured"
        project.plugins.hasPlugin('org.cyclonedx.bom')
        project.extensions.findByName('cyclonedxBom') != null

        project.plugins.hasPlugin('org.sonarqube')
        def sonarExt = project.extensions.findByName('sonarqube')
        sonarExt.properties['sonar.qualitygate.wait'] == 'true'
    }
}
