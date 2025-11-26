package clinicapp.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

// InputValidator provides utility methods for validating and parsing user input.
// Pure validation methods with no I/O operations.
public class InputValidator {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10,15}$|^\\+?[0-9]{10,15}$");
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm");
    
    // Validate phone number format.
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    // Parse and validate date string.
    public static LocalDate parseAndValidateDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    // Parse and validate time string.
    public static LocalTime parseAndValidateTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    // Validate integer is within range.
    public static boolean isValidIntegerRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    // Validate that a string is not null and not empty after trimming.
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    // Normalize gender string.
    public static String normalizeGender(String gender) {
        if (gender == null) return null;
        String normalized = gender.trim().toLowerCase();
        if (normalized.equals("male")) return "Male";
        if (normalized.equals("female")) return "Female";
        if (normalized.equals("other")) return "Other";
        return null;
    }
    
    // Normalize blood type string.
    public static String normalizeBloodType(String bloodType) {
        if (bloodType == null) return null;
        return bloodType.trim().toUpperCase();
    }
}