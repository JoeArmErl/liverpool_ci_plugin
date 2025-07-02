package com.liverpool.ci

import spock.lang.Specification
import spock.lang.TempDir
import org.gradle.testkit.runner.GradleRunner

class LiverpoolCiGradlePluginFunctionalTest extends Specification {
    @TempDir File projectDir

    File getBuildFile()    { new File(projectDir, 'build.gradle') }
    File getSettingsFile() { new File(projectDir, 'settings.gradle') }

    def "can see archUnit defaults via printConfig task"() {
        given:
        settingsFile << ''
        buildFile << """
      apply plugin: 'com.liverpool.ci'

      task printConfig {
        doLast {
          println project.extensions.archUnit.excludedPaths
        }
      }
    """

        when:
        def runner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments('printConfig', '--stacktrace', '--info')
                .forwardOutput()
        def result = runner.buildAndFail()  // <— note buildAndFail()

        then:
        // Now you will see the full stack in `result.output`, so you can pinpoint the script or plugin line that crashed.
        // Once you know that, you can fix the underlying issue.
        result.output.contains('Your expected error text or stack frame')
    }

}
