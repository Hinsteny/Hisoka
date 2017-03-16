package org.hisoka.common.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Hinsteny
 * @date 2016/8/15
 * @copyright: 2016 All rights reserved.
 */
public class DateUtil {

    public static final String SHORT_DATE_FORMAT_STR = "yyyy-MM-dd";
    public static final String LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String MAX_LONG_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String EARLY_TIME = "00:00:00 000";
    public static final String LATE_TIME = "23:59:59";
    public static final String EARER_IN_THE_DAY = "yyyy-MM-dd 00:00:00.000";
    public static final String LATE_IN_THE_DAY = "yyyy-MM-dd 23:59:59.999";
    public static final long DAY_LONG = 24 * 60 * 60 * 1000;
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * @param date
     * @return Date
     * @throws ParseException
     */
    public static Date getEarlyInTheDay(Date date) throws ParseException {
        String dateString = new SimpleDateFormat(SHORT_DATE_FORMAT_STR).format(date) + " " + EARLY_TIME;
        return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getFirstOfDay(Date date) {
        String dateString = new SimpleDateFormat(EARER_IN_THE_DAY).format(date);
        try {
            return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param date
     * @return Date
     * @throws ParseException
     */
    public static Date getLateInTheDay(Date date) throws ParseException {
        String dateString = new SimpleDateFormat(SHORT_DATE_FORMAT_STR).format(date) + " " + LATE_TIME;
        return new SimpleDateFormat(LONG_DATE_FORMAT_STR).parse(dateString);
    }

    /**
     * @param date
     * @param month
     * @return Date
     */
    public static Date addMonth(Date date, int month){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    /**
     * @param date
     * @param day
     * @return Date
     */
    public static Date addDay(Date date, int day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    /**
     * @param date
     * @param hour
     * @return Date
     */
    public static Date addHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    /**
     * @param date
     * @param minute
     * @return Date
     */
    public static Date addMinute(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    /**
     * @param date
     * @param second
     * @return Date
     */
    public static Date addSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * @param date
     * @return Date
     */
    public static long subtractNowDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        long dateTimeInMillis = calendar.getTimeInMillis();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        long nowTimeInMillis = nowCalendar.getTimeInMillis();
        return (nowTimeInMillis - dateTimeInMillis) / (24 * 60 * 60 * 1000);
    }

    /**
     * @param startDate
     * @param endDate
     * @return Date
     */
    public static long subtractSecond(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        long startTimeInMillis = startCalendar.getTimeInMillis();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long endTimeInMillis = endCalendar.getTimeInMillis();
        return (endTimeInMillis - startTimeInMillis) / 1000;

    }

    /**
     * @param dateString
     * @param format
     * @return Date
     * @throws ParseException
     */
    public static Date parserStringToDate(String dateString, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(dateString);
    }

    /**
     * @param dateString
     * @param format
     * @return Date
     */
    public static Date parse(String dateString, String format){
        Date date = null;
        try{
            date = parserStringToDate(dateString, format);
        }catch(Exception e){}
        return date;
    }

    /**
     * @param date
     * @param dateInterval
     * @return Date
     */
    public static Date dateInterval(Date date, int dateInterval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, dateInterval);
        return cal.getTime();
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getBeginOfDay(Date date) {
        String beginDay = new SimpleDateFormat(EARER_IN_THE_DAY).format(date);
        try {
            return parserStringToDate(beginDay, LONG_DATE_FORMAT_STR);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getEndOfDay(Date date) {
        String endDay = new SimpleDateFormat(LATE_IN_THE_DAY).format(date);
        try {
            return parserStringToDate(endDay, MAX_LONG_DATE_FORMAT_STR);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * @param date
     * @param format
     * @return String
     */
    public static String formatDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * @param millisecond
     * @param format
     * @return String
     */
    public static String formatDate(long millisecond, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(millisecond);
        return dateFormat.format(date);
    }

    /**
     * @param date
     * @return Calendar
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param beginDate
     * @param endDate
     * @return long
     */
    public static long truncateDate(Date beginDate, Date endDate) {
        if (endDate != null && beginDate != null) {
            GregorianCalendar end = new GregorianCalendar();
            end.setTime(endDate);

            GregorianCalendar begin = new GregorianCalendar();
            begin.setTime(beginDate);

            return (end.getTimeInMillis() - begin.getTimeInMillis()) / DAY_LONG;
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        Date date1 = parserStringToDate("2014-07-02", SHORT_DATE_FORMAT_STR);
        Date date2 = parserStringToDate("2014-07-01", SHORT_DATE_FORMAT_STR);
        System.out.println(truncateDate(date1, date2));

        System.out.println(formatDate(getFirstOfDay(new Date()),"yyyy-MM-dd HH:mm:ss SSS"));
        System.out.println(formatDate(getEndOfDay(new Date()),"yyyy-MM-dd HH:mm:ss SSS"));
    }
}
