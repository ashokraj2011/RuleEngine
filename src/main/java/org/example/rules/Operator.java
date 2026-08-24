package org.example.rules;

public enum Operator {
    // Comparison
    eq, ne, lt, lte, gt, gte,
    // Collection/String
    contains, contains_all, in,
    // Pattern
    regex,
    // Range
    between,
    // Existence
    exists, not_exists, isNull, isNotNull,
    // Value-producing
    circle_area
}
