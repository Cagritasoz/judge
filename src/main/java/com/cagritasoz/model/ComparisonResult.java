package com.cagritasoz.model;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class ComparisonResult {
    private boolean passed;
    private ComparisonMode comparisonMode;
    private int expectedLength;
    private int actualLength;
}
