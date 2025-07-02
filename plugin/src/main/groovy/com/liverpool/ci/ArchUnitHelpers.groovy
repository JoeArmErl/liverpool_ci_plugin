package com.liverpool.ci

class ArchUnitHelpers {
  static Object applyOn(String pkg, String scope) {
    // return whatever the ArchUnit DSL wants here
    [package: pkg, on: scope]
  }

  static Object configurableRule(String ruleClass, Object target) {
    [rule: ruleClass, target: target]
  }
}
