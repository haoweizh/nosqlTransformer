package com.qnvip.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
	private static SimpleDateFormat dateformat = new SimpleDateFormat();

	public static String format(Date date, String pattern) {
		if (date == null)
			return "";
		dateformat.applyPattern(pattern);
		return dateformat.format(date);
	}

	public static String formatDateTime(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String formatDate(Date date) {
		return format(date, "yyyy-MM-dd");
	}

	public static String formatTime(Date date) {
		return format(date, "HH:mm:ss");
	}
	
	/**
	 * 
	 * @param time  到秒
	 * @return
	 */
	public static String formatDateTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time * 1000);
		return format(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 
	 * @param time  到秒
	 * @return
	 */
	public static String formatDate(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time * 1000);
		return format(cal.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 
	 * @param datestring
	 *            ig: 2007-11-08
	 * @return date
	 */
	public static Date convertStringToDate(String datestring) {
		dateformat.applyPattern("yyyy-MM-dd");
		try {
			return dateformat.parse(datestring);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date convertStringToTime(String timestring) {
		dateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
		try {
			return dateformat.parse(timestring);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 日期加减操作
	 * 
	 * @param source
	 *            源日期
	 * @param num
	 *            推迟天数 + 为往后 - 为往前
	 * @return
	 */
	public static Date dateRoler(Date source, int num) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(source);
			c.add(Calendar.DAY_OF_MONTH, num);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 月份加减操作
	 * @param source 源日期
	 * @param num
	 *            推迟天数 + 为往后 - 为往前
	 * @return
	 */
	public static Date monthRoler(Date source, int num){
		try{
			Calendar c = Calendar.getInstance();
			c.setTime(source);
			c.add(Calendar.MONTH, num);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * ���µ�һ������
	 * @param source
	 * @return
	 */
	public static Date beginOfMonth(Date source){
		try{
			Calendar c = Calendar.getInstance();
			c.setTime(source);
			int min = c.getActualMinimum(Calendar.DAY_OF_MONTH);
			c.set(Calendar.DAY_OF_MONTH, min);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * �������һ������
	 * @param source
	 * @return
	 */
	public static Date endOfMonth(Date source){
		try{
			Calendar c = Calendar.getInstance();
			c.setTime(source);
			int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			c.set(Calendar.DAY_OF_MONTH, max);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算两日期之间相差的天数 day1 -day2
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 * @throws ParseException
	 */
	public static int countDays(String day1, String day2) throws ParseException {

		if (day1 != null && day2 != null && day1.length() > 0
				&& day2.length() > 0) {
			// 日期相减算出秒的算法
			Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(day1);
			Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(day2);
			// 日期相减得到相差的日期
			long day = (date1.getTime() - date2.getTime())
					/ (24 * 60 * 60 * 1000);
			return (int) day;
		} else {
			return 0;
		}

	}

	/**
	 * day1-day2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int countDays(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return 0;
		long day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000);
		return (int) day;
	}

	public static Date string2Date(String dateTimeStr) {
		try {
			if (dateTimeStr == null)
				return null;
			else if (dateTimeStr.trim().length() == 10) {
				dateTimeStr = dateTimeStr.trim() + " 00:00:00";
			}
			dateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
			return dateformat.parse(dateTimeStr.trim());
		} catch (Exception e) {
			return null;
		}

	}

	public static String getDatePart(String dateTimeStr) {
		if (dateTimeStr == null)
			return "";
		else if (dateTimeStr.length() <= 10)
			return dateTimeStr;
		else
			return dateTimeStr.substring(0, 10);
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param pTime
	 *            要判断的时间
	 * @return dayForWeek 判断结果
	 */
	public static int dayForWeek(String dateTimeStr) throws Exception {
		dateformat.applyPattern("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(dateformat.parse(dateTimeStr));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	/**
	 * 获取某日期所在周的星期天日期(以周一为一周的第一天)
	 * 
	 * @param dateTimeStr
	 * @return String 返回日期
	 */
	public static String getDataForSunday(String dateTimeStr) throws Exception {
		dateformat.applyPattern("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(dateformat.parse(dateTimeStr));
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		return dateformat.format(c.getTime());
	}
	
	/**
	 * 获取某日期是一年中的第几周
	 * 
	 * @param dateTimeStr
	 * @return int 
	 */
	public static int getWeekOfYear(String dateTimeStr) throws Exception {
		dateformat.applyPattern("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(dateformat.parse(dateTimeStr));
		c.setFirstDayOfWeek(Calendar.MONDAY);
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 时间前推或后推分钟,其中JJ表示分钟. +后推 -前推
	 * 
	 * @see getPreTime(String origin, long seconds)
	 */
	@Deprecated
	public static String getPreTime(String sj1, Integer jj) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + jj * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}
	
	/**
	 * 判断 beforeTime 是否在 afterTime 之前
	 * 
	 * @param beforeTime 2000-01-01 00:00:00 | 2000-01-01
	 * @param afterTime 2000-02-02 00:00:00 | 2000-02-02
	 * @throws Exception 时间格式错误
	 */
	public static boolean isTimeBefore(String beforeTime, String afterTime) throws Exception {
		Date beforeDate = string2Date(beforeTime),
				afterDate = string2Date(afterTime);
		if (beforeDate == null || afterDate == null) {
			throw new Exception("时间格式错误");
		} else {
			return beforeDate.before(afterDate);
		}
	}

	public static String getCurrentDate() {
		return (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date());
	}

	public static String getCurrentTime() {
		return (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
	}

	/**
	 * 获取当前时间戳 unix
	 */
	public static String getUnixTime() {
		return String.valueOf(System.currentTimeMillis() / 1000l);
	}
}
