package com.liverpool.ci

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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

        then: "core plugins applied"
        project.plugins.hasPlugin('java')
        project.plugins.hasPlugin('jacoco')
        project.plugins.hasPlugin('org.sonarqube')
        project.plugins.hasPlugin('org.cyclonedx.bom')

        and: "core-configuration settings"
        def jarTask = project.tasks.getByName('jar')
        def manifest = jarTask.manifest.attributes
        manifest['Implementation-Title']   == project.name
        manifest['Implementation-Version'] == project.version
        manifest['Implementation-Vendor-Id']== project.group
        manifest['Implementation-Vendor']   == project.group

        project.tasks.compileJava.options.encoding    == 'UTF-8'
        project.tasks.compileTestJava.options.encoding== 'UTF-8'
        project.tasks.javadoc.options.encoding        == 'UTF-8'
        !project.tasks.javadoc.failOnError

        and: "CycloneDX and SonarQube plugins are applied"
        project.plugins.hasPlugin('org.cyclonedx.bom')
        project.extensions.findByName('cyclonedxBom')

        project.plugins.hasPlugin('org.sonarqube')
        def sonarExt = project.extensions.findByName('sonarqube')
        sonarExt.properties['sonar.qualitygate.wait'] == 'true'
    }

    def "plugin applies test conventions"() {
        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.liverpool.ci')

        then: "test task uses JUnit Platform and UTF-8 encoding"
        def testTask = project.tasks.getByName('test')
        testTask.useJUnitPlatform()
        testTask.systemProperties['file.encoding'] == 'utf-8'

        and: "testLogging is configured for full exceptions"
        testTask.testLogging.exceptionFormat == TestExceptionFormat.FULL

        and: "afterSuite callback is registered"
        testTask.testLogging.afterSuiteListeners.size() > 0
    }

    def "plugin applies checkstyle conventions"() {
        given: "a project with the Checkstyle plugin available"
        def project = ProjectBuilder.builder().build()
        project.pluginManager.apply('checkstyle')

        when: "we apply our LiverpoolCi plugin"
        project.pluginManager.apply('com.liverpool.ci')

        then: "the Checkstyle plugin is applied"
        project.plugins.hasPlugin('checkstyle')

        and: "the Checkstyle extension is configured"
        def csExt = project.extensions.findByType(CheckstyleExtension)
        csExt != null
        csExt.toolVersion    == '10.12.4'
        csExt.ignoreFailures == false
        csExt.maxWarnings    == 0

        and: "it targets only the main source set"
        csExt.sourceSets == [project.sourceSets.main]
    }
}
