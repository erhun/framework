package com.erhun.framework.basic.utils.datetime;

import com.erhun.framework.basic.utils.number.NumberUtils;
import com.erhun.framework.basic.utils.string.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @author weichao <gorilla@aliyun.com>
 */
public class DateUtility {

    public static java.sql.Date toSqlDate(String date) {
        if (isNotDate(date)) {
            return null;
        }

        return java.sql.Date.valueOf(date);

    }

    public static Date toDate(String date) {
        if (isNotDate(date)) {
            return null;
        }
        return java.sql.Date.valueOf(date);

    }

    public static Date now() {
        return new Date();
    }

    public static Timestamp nowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static java.sql.Date convertToSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public static java.sql.Date convertToSqlDate(String date ) {

        if(isNotDate(date)){
            return null;
        }
        return java.sql.Date.valueOf(date);

    }

    public static Timestamp convertToTimestamp(String timestamp) {

        if (isNotDate(timestamp)) {
            return null;
        }

        return Timestamp.valueOf(timestamp);

    }

    public static boolean isDate(String value) {

        if (StringUtils.isEmpty(value)) {
            return false;
        }

        String arra[] = value.split("\\-");

        if (arra.length != 3) {
            return false;
        }

        if (!NumberUtils.isDigits(arra[0]) || !NumberUtils.isDigits(arra[1]) || !NumberUtils.isDigits(arra[2])) {
            return false;
        }

        int y = Integer.valueOf(arra[0]);
        int m = Integer.valueOf(arra[1]) - 1;
        int d = Integer.valueOf(arra[2]);

        Calendar cdr = Calendar.getInstance();

        cdr.set(y, m, d);

        if (y == cdr.get(Calendar.YEAR) && m == cdr.get(Calendar.MONTH)
                && d == cdr.get(Calendar.DATE)) {
            return true;
        }

        return false;

    }

    public static boolean isNotDate(String value) {
        return !isDate(value);
    }

    /**
     * @param d1
     * @param d2
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int compareDate(Date d1, Date d2) {

        int num = compare(d1.getYear(), d2.getYear());

        if (num == 0) {
            num = compare(d1.getMonth(), d2.getMonth());
            if (num == 0) {
                return compare(d1.getDate(), d2.getDate());
            } else {
                return num;
            }
        }

        return num;

    }

    /**
     * @param n1
     * @param n2
     * @return
     */
    private static int compare(int n1, int n2) {

        return n1 > n2 ? 1 : (n1 == n2 ? 0 : -1);

    }

    public static java.sql.Date getSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public static java.sql.Date getSqlDate(String date) {

        if (isNotDate(date)) {
            return null;
        }
        return java.sql.Date.valueOf(date);

    }

    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getTimestamp(String timestamp) {

        if (isNotDate(timestamp)) {
            return null;
        }

        return Timestamp.valueOf(timestamp);

    }


    /**
     * @param d1
     * @param d2
     * @return
     */
    public static int compareDate(LocalDate d1, LocalDate d2) {

        return d1.compareTo(d2);

    }

    /**
     * 间隔天数
     * 1970-01-02 23:59:59 ~ 1970-01-02 00:00:00 = 0
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int calculateDays(Date d1, Date d2) {

        int day = (int) ((d1.getTime() - d2.getTime()) / (24 * 60 * 60 * 1000));

        return Math.abs(day);

    }

    /**
     * 间隔天数
     * 1970-01-02 23:59:59 ~ 1970-01-02 00:00:00 = 1
     * @param startDate
     * @param endDate
     * @return
     */
    public static int intervalDays(Date startDate, Date endDate) {

        int startDays = getDaysInYear(startDate);
        int endDays = getDaysInYear(endDate);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDate);

        int startYear = cal1.get(Calendar.YEAR);
        int endYear = cal2.get(Calendar.YEAR);

        int y = endYear - startYear;

        if (y > 0) {
            for (int i = 0; i < y; i++) {
                endDays += getMaxDaysInYear(startYear + i);
            }
        }

        return endDays - startDays;
    }

    /**
     * 某年最大天数
     *
     * @param year
     * @return days
     */
    public static int getMaxDaysInYear(int year) {
        int days = 0;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            days = 366;
        } else {
            days = 365;
        }
        return days;
    }

    /**
     * @param d1
     * @param d2
     * @return
     */
    public static int calculateHours(Date d1, Date d2) {

        int hours = (int) ((d1.getTime() - d2.getTime()) / (60 * 60 * 1000));

        return Math.abs(hours);

    }

    /**
     * @param d1
     * @param d2
     * @return
     */
    public static int calculateMinutes(Date d1, Date d2) {

        int minutes = (int) ((d1.getTime() - d2.getTime()) / (60 * 1000));

        return Math.abs(minutes);

    }

    /**
     * @param d1
     * @param d2
     * @return
     */
    public static long calculateSeconds(Date d1, Date d2) {

        long day = ((d1.getTime() - d2.getTime()) / 1000);

        return Math.abs(day);

    }

    /*public static void main(String[] args) {

        Date date = new Date();

        date = DateUtils.truncateTime(date);

        Date endDate = DateUtils.plusDays(date, 10);

        System.out.println("sxxf:" + calculateDays(date, endDate));

        LocalDateTime localDate = toLocalDateTime(new Date());


        Date s = toDate(localDate);

        System.out.println(s);
        System.out.println(localDate);
        System.out.println(localDate.withDayOfMonth(1));
        System.out.println(localDate.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println(localDate);
        //System.out.println(caculateDays(Timestamp.valueOf("2014-07-09 14:30:00"), Timestamp.valueOf("2014-07-10 14:30:00")));
    }*/

    /**
     * @param date
     * @return
     */
    public static int getMaximumDayOfMonth(Date date) {

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_YEAR);
        }

        return 0;

    }

    /**
     * 当月第一天日期
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {

        if (date != null) {
            LocalDate localDate = toLocalDate(date);
            localDate.withDayOfMonth(1);
            return toDate(localDate);
        }

        return null;

    }

    /**
     * 下一月第一天日期
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfNextMonth(Date date) {

        if (date != null) {
            LocalDate localDate = toLocalDate(date);
            localDate.with(TemporalAdjusters.firstDayOfNextMonth());
            return toDate(localDate);
        }

        return null;

    }

    /**
     * 前一天日期
     *
     * @param date
     * @return
     */
    public static Date getPreviousDayOfDate(Date date) {

        if (date != null) {
            LocalDate localDate = toLocalDate(date);
            localDate.minusDays(1);
            return toDate(localDate);
        }

        return null;

    }

    /**
     * 当月最后一天日期
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {

        if (date != null) {
            LocalDate localDate = toLocalDate(date);
            localDate.with(TemporalAdjusters.lastDayOfMonth());
            return toDate(localDate);
        }

        return null;

    }

    /**
     * 截断时间
     *
     * @param date
     * @return
     */
    public static Date truncateTime(Date date) {

        if (date != null) {
            LocalDate localDate = toLocalDate(date);
            return toDate(localDate);
        }

        return null;

    }

    /**
     * @param date
     * @return
     */
    public static Date plusDays(Date date, int days) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.plusDays(days);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param hours
     * @return
     */
    public static Date plusHours(Date date, int hours) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.plusHours(hours);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param minutes
     * @return
     */
    public static Date plusMinutes(Date date, long minutes) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.plusMinutes(minutes);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param seconds
     * @return
     */
    public static Date plusSeconds(Date date, long seconds) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.plusSeconds(seconds);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param months
     * @return
     */
    public static Date plusMonths(Date date, int months) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.plusMonths(months);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param days
     * @return
     */
    public static Date minusDays(Date date, int days) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.minusDays(days);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param hours
     * @return
     */
    public static Date minusHours(Date date, int hours) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.minusHours(hours);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param minutes
     * @return
     */
    public static Date minusMinutes(Date date, int minutes) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.minusMinutes(minutes);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param seconds
     * @return
     */
    public static Date minusSeconds(Date date, int seconds) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.minusSeconds(seconds);
            return toDate(localTimeDate);
        }

        return null;

    }

    /**
     * @param date
     * @param months
     * @return
     */
    public static Date minusMonths(Date date, int months) {

        if (date != null) {
            LocalDateTime localTimeDate = toLocalDateTime(date);
            localTimeDate = localTimeDate.minusMonths(months);
            return toDate(localTimeDate);
        }

        return null;

    }

    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static Date toDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取指定日期在当年中的第几天
     * @param date
     * @return
     */
    public static int getDaysInYear(Date date) {

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_YEAR);
        }

        return 0;

    }

    /**
     * 将秒转成时间字符串
     *
     * @param seconds
     * @return
     */
    public static String convertToChinese(long seconds) {
        int d = (int) (seconds / 3600 / 24);
        int h = (int) (seconds / 3600 - d * 24);
        int m = (int) (Math.ceil((seconds - d * 24 * 3600 - h * 3600) / 60));
        String stayTime = "";
        if (d > 0) {
            stayTime += d + "天";
        }
        if (h > 0) {
            stayTime += h + "小时";
        }
        stayTime += m + "分钟";
        return stayTime;
    }


}
