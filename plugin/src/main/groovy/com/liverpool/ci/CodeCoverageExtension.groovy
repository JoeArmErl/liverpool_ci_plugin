// src/main/groovy/com/liverpool/ci/CodeCoverageExtension.groovy
package com.liverpool.ci

class CodeCoverageExtension {
    /** Allow users to override the excludes */
    List<String> coverageExclusions       = []
    /** Minimum coverages [0.0–1.0] */
    double       jacocoInstructionMin     = 0.80
    double       jacocoBranchMin          = 0.70
    double       jacocoLineMin            = 0.75
    double       jacocoComplexityMin      = 0.60
    double       jacocoMethodMin          = 0.85
    double       jacocoClassMin           = 0.80
}
