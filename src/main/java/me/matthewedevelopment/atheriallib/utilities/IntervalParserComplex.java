package me.matthewedevelopment.atheriallib.utilities;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntervalParserComplex {
    public static  long getDuration(String input) {
        try {
            if (input.endsWith("m")){
                return TimeUnit.MINUTES.toMillis(Long.parseLong(input.substring(0, input.length()-1)));
            } else if (input.endsWith("d")){
                return TimeUnit.DAYS.toMillis(Long.parseLong(input.substring(0, input.length()-1)));
            } else if (input.endsWith("s")){
                return TimeUnit.SECONDS.toMillis(Long.parseLong(input.substring(0, input.length()-1)));
            }
        }catch ( Exception e) {
            return -1;
        }
        return -1;

    }

    /**
     * Parses a complex interval string with multiple units (e.g., "1h 30m") into seconds.
     * @param intervalStr The complex interval string.
     * @return The total interval in seconds, or a special value for permanent.
     */
    public static long parseComplexInterval(String intervalStr) {
        if (intervalStr == null || intervalStr.trim().isEmpty()) {
            return -2;//INVALID
        }

        // Pattern to match the parts of the interval string (e.g., "1h", "30m")
        Pattern pattern = Pattern.compile("(\\d+)([SMHDWP])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(intervalStr.replaceAll("\\s+", "").toUpperCase());

        long totalSeconds = 0;
        while (matcher.find()) {
            long number = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    totalSeconds += number;
                    break;
                case "M":
                    totalSeconds += number * 60;
                    break;
                case "H":
                    totalSeconds += number * 3600;
                    break;
                case "D":
                    totalSeconds += number * 86400;
                    break;
                case "W":
                    totalSeconds += number * 604800;
                    break;
                case "P":
                    return -1; // Permanent
                default:
                    throw new IllegalArgumentException("Invalid interval unit: " + unit);
            }
        }

        if (totalSeconds == 0 && !intervalStr.toUpperCase().contains("P")) {
            throw new IllegalArgumentException("Invalid interval format or units");
        }

        return totalSeconds;
    }


}
