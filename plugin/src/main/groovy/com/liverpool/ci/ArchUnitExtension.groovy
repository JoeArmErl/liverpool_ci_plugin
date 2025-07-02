package com.liverpool.ci

class ArchUnitExtension {
    /** default package path if the user doesn’t override */
    String packagePath       = 'com/example'
    /** will be populated by the script */
    List<?> configurableRules = []
    List<String> excludedPaths      = []
    List<String> preConfiguredRules = []
}
