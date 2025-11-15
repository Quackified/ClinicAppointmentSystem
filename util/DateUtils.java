package clinicapp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy"); // e.g. November 15, 2025

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a"); // e.g. 09:30 AM

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a"); // e.g. November 15, 2025 10:23 PM

    private static final DateTimeFormatter SQL_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // e.g. 2025-11-15 22:23:00

    // Format LocalDate
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    // Format LocalTime
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "N/A";
    }

    // Format LocalDateTime (Normal Format)
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "N/A";
    }

    // Format LocalDateTime (SQL? Format)
    public static String formatLogDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(SQL_FORMATTER) : "N/A";
    }
}
