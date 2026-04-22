package org.example.rules;

public enum Operator {
    // Comparison
    eq, ne, lt, lte, gt, gte,
    // Collection/String
    contains, notContains, startsWith, notStartsWith, endsWith, in,
    // Pattern
    regex,
    // Range
    between,
    // Existence
    exists, not_exists, isNull, isNotNull
}
