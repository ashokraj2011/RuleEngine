package org.example.rules;

public enum Operator {
    // Comparison
    // tan: compares Math.tan(fieldValue) (field value interpreted as radians)
    // against value within a fixed tolerance.
    eq, ne, lt, lte, gt, gte, tan,
    // Collection/String
    contains, in,
    // Pattern
    regex,
    // Range
    between,
    // Existence
    exists, not_exists, isNull, isNotNull
}
