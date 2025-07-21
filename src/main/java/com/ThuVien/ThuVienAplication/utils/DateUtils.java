package com.ThuVien.ThuVienAplication.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    public static Date convertToDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String convertToString (Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return  dateFormat.format(date);
    }

    public static String dateTimeConvertToString (Date date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return  dateTimeFormatter.format(((Timestamp)date).toLocalDateTime());
    }


    public static String dateTimeConvertToStringTimestamp (Timestamp timestamp){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return  dateTimeFormatter.format(timestamp.toLocalDateTime());
    }
}