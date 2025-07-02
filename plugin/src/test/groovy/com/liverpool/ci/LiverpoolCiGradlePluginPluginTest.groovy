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
}
