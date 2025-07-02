# Liverpool CI Gradle Plugin

A composite Gradle plugin that bundles your organization’s common build conventions—ArchUnit rules, Jacoco coverage, core encoding/manifest settings, test conventions, and Checkstyle—into a single, versioned plugin. Use it in any project with one line.

---

## Features

- **Core configuration** (`core-configuration.gradle`)
  - Applies the `java` plugin
  - Configures JAR manifest attributes from `project.name`, `version`, `group`
  - Forces UTF-8 encoding on `compileJava`, `compileTestJava`, `javadoc`
  - Applies CycloneDX BOM and SonarQube plugins

- **ArchUnit rules** (`arch-unit.gradle`)
  - Registers an `archUnit` extension for configurable and preconfigured rules
  - Populates defaults like `excludedPaths`

- **Jacoco coverage** (`jacoco.gradle`)
  - Applies and configures the `jacoco` plugin
  - Sets up `jacocoTestReport` and `jacocoTestCoverageVerification` with your thresholds

- **Test conventions** (`test-conventions.gradle`)
  - Configures the `test` task to use JUnit Platform
  - Sets `file.encoding=UTF-8`
  - Prints a summary after the test suite finishes

- **Checkstyle** (`checkstyle-conventions.gradle`)
  - Applies and configures the `checkstyle` plugin
  - Loads rules from a local `config/checkstyle-rules.xml` in the plugin JAR
  - Enforces zero warnings, specified `toolVersion`

---

## Getting Started

### 1. Include the plugin via composite build

In your **sample** or consumer project’s `settings.gradle`:

```groovy
pluginManagement {
  includeBuild '../'        // relative path to the liverpool-ci-plugin project
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}
rootProject.name = 'your-project'
```

### 2. Apply the plugin

In your **build.gradle**:

```groovy
plugins {
  id 'com.liverpool.ci'
}
```

That’s it—your project will automatically pick up all embedded conventions.

---

## Sample Project

A working example lives in the `plugin/sample/` folder:

- **`sample/settings.gradle`** declares the composite build
- **`sample/build.gradle`** applies `com.liverpool.ci` and defines probe tasks:
  - `printConfig`        — prints `archUnit.excludedPaths`
  - `printGeneral`       — prints JAR manifest & encoding settings
  - `printJacocoConfig`  — shows the Jacoco HTML report URL

To try it:

```bash
cd plugin/sample
gradle printConfig printGeneral printJacocoConfig --no-daemon
```

---

## Tests

### Unit tests

Location: `plugin/src/test/groovy/com/liverpool/ci/LiverpoolCiGradlePluginTest.groovy`

- **DSL extensions** (ArchUnit + CodeCoverage)
- **Core build settings** (manifest, encoding)
- **Test conventions** (`test` task config)
- **Checkstyle extension** config

Run with:

```bash
./gradlew plugin:test
```

### Functional (composite) smoke test

Use the `sample/` folder as a manual TestKit‐style verification:

```bash
cd plugin/sample
gradle printConfig printGeneral printJacocoConfig --no-daemon
```

---

## Extending

To add new conventions:

1. Drop a `.gradle` script into `src/main/resources/`
2. Add its filename to the `scripts` list in `LiverpoolCiGradlePlugin.apply(...)`

Your plugin will pick it up on the next build.

---

## Publishing

1. Bump the `version` in `plugin/build.gradle`
2. Add a `maven-publish` or `pluginBundle` block
3. Run:

   ```bash
   ./gradlew publish
   ```

Consumers then simply:

```groovy
plugins {
  id 'com.liverpool.ci' version '1.x.y'
}
```

---
