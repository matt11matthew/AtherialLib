package me.matthewedevelopment.atheriallib.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 7/5/2019 at 5:50 PM for the project atherialapi
 */
public class TimeUtils {
    public static String formatTicks(final int ticks) {
        final int ms = ticks * 50;
        return formatMsTime(ms);
    }

    public static String formatTime(final String format, final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(time));
    }


    public static String formatMsTime(final long ms) {
        try {
            final boolean hour = TimeUnit.MILLISECONDS.toHours(ms) > 0L;
            final boolean minute = TimeUnit.MILLISECONDS.toMinutes(ms) > 0L;
            String format;
            if (hour) {
                format = "hh:mm:ss";
            } else {
                if (!minute) {
                    final long remainingMs = ms % 1000L;
                    final int remainingS = (int) (ms / 1000L);
                    return new DecimalFormat("#0.0s").format(Double.valueOf(remainingS + "." + remainingMs));
                }
                format = "mm:ss";
            }
            return formatTime(format, ms);
        } catch (Exception e) {
            return "0.0s";
        }
    }

    public static String formatTimeToHHMMSS(final long millis) {
        final int seconds = (int) (millis / 1000L) % 60;
        final int minutes = (int) (millis / 60000L % 60L);
        final int hours = (int) (millis / 3600000L % 24L);
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(hours).append("h").append(" ");
        }
        if (minutes > 0) {
            stringBuilder.append(minutes).append("m").append(" ");
        }
        stringBuilder.append(seconds).append("s");
        return stringBuilder.toString().trim();
    }

    @Deprecated
    public static String formatTime(long time) {

        String returnString = "";
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = 0;
        long hours = 0;
        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }
        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }
        if (hours > 0) {
            returnString += hours + "h ";
        }
        if (minutes > 0) {
            returnString += minutes + "m ";
        }
        if (seconds > 0) {
            returnString += seconds + "s";
        }
        if (returnString.length() < 2) {
            returnString = "0s";
        }
        return returnString;


    }

    public static String format(final Number number) {
        return format(number, 5);
    }

    public static String format(final Number number, final int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    public static String format(final Number number, final int decimalPlaces, final RoundingMode roundingMode) {
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }
}
