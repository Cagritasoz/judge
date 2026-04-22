package com.cagritasoz.validation;

public record ValidationResult(boolean valid, String message) {

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }
    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

}
