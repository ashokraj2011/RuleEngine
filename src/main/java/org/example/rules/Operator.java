package org.example.rules;

public enum Operator {
    // Comparison
    eq, ne, lt, lte, gt, gte,
    // Collection/String
    contains, in, has_all_of,
    // Pattern
    regex,
    // Range
    between,
    // Existence
    exists, not_exists, isNull, isNotNull
}
