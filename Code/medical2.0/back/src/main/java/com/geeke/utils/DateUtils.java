package com.geeke.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private final static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/MILLIS_PER_DAY;
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/MILLIS_PER_HOUR;
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/MILLIS_PER_MINUTE;
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis / MILLIS_PER_DAY;
		long hour = (timeMillis / MILLIS_PER_HOUR - day * 24);
		long min = ((timeMillis / MILLIS_PER_MINUTE) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / MILLIS_PER_SECOND - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = timeMillis % MILLIS_PER_SECOND;
		return (day > 0 ? day + ",":"") + hour + ":" + min + ":" + s + "." + sss;
    }

	/**
	 * 判断两个日期是否是同年
	 * @param before
	 * @param after
	 * @return
	 */
	public static boolean isSameYear(Date before, Date after) {
		if (null != before && null != after) {
			Calendar beforeCal = Calendar.getInstance();
			beforeCal.setTime(before);
			Calendar afterCal = Calendar.getInstance();
			afterCal.setTime(after);
			return beforeCal.get(Calendar.YEAR) == afterCal.get(Calendar.YEAR);
		}
		return false;
	}

	/**
	 * 判断两个日期是否是同年
	 * @param before
	 * @param after
	 * @return
	 */
	public static boolean isSameYear(Calendar before, Calendar after) {
		return isSameYear(before.getTime(), after.getTime());
	}

	/**
	 * 判断两个日期是否是同年同月
	 * @param before
	 * @param after
	 * @return
	 */
	public static boolean isSameMonth(Date before, Date after) {
		if (null != before && null != after) {
			Calendar beforeCal = Calendar.getInstance();
			beforeCal.setTime(before);
			Calendar afterCal = Calendar.getInstance();
			afterCal.setTime(after);
			return beforeCal.get(Calendar.YEAR) == afterCal.get(Calendar.YEAR)
					&& beforeCal.get(Calendar.MONTH) == afterCal.get(Calendar.MONTH);
		}
		return false;
	}

	/**
	 * 判断两个日期是否是同月
	 * @param before
	 * @param after
	 * @return
	 */
	public static boolean isSameMonth(Calendar before, Calendar after) {
		return isSameMonth(before.getTime(), after.getTime());
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceDays(Date before, Date after) {
		return ((double)getDistanceMillis(before, after)) / MILLIS_PER_DAY;
	}

	/**
	 * 获取两个日期之间的天数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceDays(Calendar before, Calendar after) {
		return ((double)getDistanceMillis(before.getTime(), after.getTime())) / MILLIS_PER_DAY;
	}

	/**
	 * 获取两个日期间的毫秒数
	 * @param before
	 * @param after
	 * @return
	 */
	public static long getDistanceMillis(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return afterTime - beforeTime;
	}

	/**
	 * 获取两个日期之间的月数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceMonths(Date before, Date after) {
		// TODO
        double month = 0;
		// 获取开始时间当月的最后一天
		Date endDay = getEndDayOfMonth(before);
		// 获取结束时间当月的第一天
		Date startDay = getStartDayOfMonth(after);
		// 开始日期和结束日期在同一月内
		if (isSameMonth(before, after)) {
            month = getDistanceSameMonth(before, after);
			return month;
		} else {
			// 获取开始时间----开始时间最后一天之间的月数
			double startMonth = getDistanceSameMonth(before, endDay);

			// 获取结束时间----结束时间第一天之间的月数
			double endMonth = getDistanceSameMonth(startDay, after);

			// 获取开始时间当月第一天,月数 + 1（整月计算，开始时间）
			Date startDayOfMonth = getStartDayOfMonth(before);
			Calendar startDayMonth = Calendar.getInstance();
			startDayMonth.setTime(startDayOfMonth);
			startDayMonth.add(Calendar.MONTH, 1);

			// 获取结束时间当月最后一天，月数 - 1（整月计算，结束时间）
			Date endDayOfMonth = getEndDayOfMonth(after);
			Calendar endDayMonth = Calendar.getInstance();
			endDayMonth.setTime(endDayOfMonth);
			endDayMonth.add(Calendar.MONTH, -1);

			// 获取两个日期间的月数（取整）
			int distanceMonth = getDistance(startDayMonth.getTime(), endDayMonth.getTime(), Calendar.MONTH);
			month = startMonth + endMonth + distanceMonth;
			return month;
		}
	}
	/**
	 * 获取两个日期之间的月数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceMonths(Calendar before, Calendar after) {
		// TODO
		return getDistanceMonths(before.getTime(), after.getTime());
	}

	/**
	 * 获取两个日期之间的年数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceYears(Date before, Date after) {
		// TODO
		double year = 0;
		// 获取开始时间当年的最后一天
		Date endDay = getEndDayOfYear(before);
		// 获取结束时间当年的第一天
		Date startDay = getStartDayOfYear(after);
		// 开始日期和结束日期在同一年内
		if (isSameYear(before, after)) {
			year = getDistanceSameYear(before, after);
			return year;
		} else { // 跨年计算
			// 获取开始时间----开始时间最后一天之间的年数
			double startYear = getDistanceSameYear(before, endDay);

			// 获取结束时间----结束时间第一天之间的年数
			double endYear = getDistanceSameYear(startDay, after);

			// 获取开始时间第一天,年数 + 1（整年计算，开始时间）
			Date startDayOfYear = getStartDayOfYear(before);
			Calendar startDayYear = Calendar.getInstance();
			startDayYear.setTime(startDayOfYear);

			// 获取结束时间最后一天，年数 - 1（整年计算，结束时间）
			Date endDayOfYear = getEndDayOfYear(after);
			Calendar endDayYear = Calendar.getInstance();
			endDayYear.setTime(endDayOfYear);

			int distanceYear = 0;
			// 两个日期为相邻年，不需要中间跨年计算
			if (endDayYear.get(Calendar.YEAR) - startDayYear.get(Calendar.YEAR) > 1) {
				startDayYear.add(Calendar.YEAR, 1);
				endDayYear.add(Calendar.YEAR, -1);
				// 获取两个日期间的年数数（取整）
				distanceYear = getDistance(startDayYear.getTime(), endDayYear.getTime(), Calendar.YEAR);
			}
			year = startYear + endYear + distanceYear;
			return year;
		}
	}

	/**
	 * 获取两个日期之间的年数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceYears(Calendar before, Calendar after) {
		// TODO
		return getDistanceYears(before.getTime(), after.getTime());
	}

	/**
	 * 获取同年中两个日期间的年数
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceSameYear(Date before, Date after) {
		// 获取两个日期间的毫秒数
		long millis = getDistanceMillis(before, after);
		// 获取当年的年天数
		double daysYear = getDaysOfTheYear(before);
		// 结束时间---》结束时间第一天年数
		return (double)millis / MILLIS_PER_DAY / daysYear;
	}

	/**
	 * 获取同月中两个日期间的月数
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceSameMonth(Date before, Date after) {
		// 获取两个日期间的毫秒数
		long millis = getDistanceMillis(before, after);
		// 获取当月的月天数
		double daysMonth = getDaysOfTheMonth(before);
		// 结束时间---》结束时间当月第一天月数
		return (double)millis / MILLIS_PER_DAY / daysMonth;
	}

	/**
	 * 获取日期的凌晨日期和时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getZeroTimeOfDate(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setMinTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期的凌晨日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getZeroTimeOfDate(Calendar calendar) {
		// TODO
		return getZeroTimeOfDate(calendar.getTime());
	}
	
	/**
	 * 获取日期当天的午夜日期和时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getEndTimeOfDate(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setMaxTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期当天的午夜日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getEndTimeOfDate(Calendar calendar) {
		// TODO
		return getEndTimeOfDate(calendar.getTime());
	}
	
	/**
	 * 获取日期当月的起始日期和时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfMonth(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1);
		setMinTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期当月的起始日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getStartDayOfMonth(Calendar calendar) {
		// TODO
		return getStartDayOfMonth(calendar.getTime());
	}

	/**
	 * 获取日期当月的结束日期和时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndDayOfMonth(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int maxMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),maxMonthDay);
		setMaxTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期当月的结束日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getEndDayOfMonth(Calendar calendar) {
		// TODO
		return getEndDayOfMonth(calendar.getTime());
	}

	/**
	 * 获取日期下个月的开始日期和时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfNextMonth(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.add(Calendar.MONTH, 1);
		setMinTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期下个月的开始日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getStartDayOfNextMonth(Calendar calendar) {
		// TODO
		return getStartDayOfNextMonth(calendar.getTime());
	}

	/**
	 * 获取日期上个月的结束日期和时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getEndDayOfPreviousMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		int maxMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),maxMonthDay);
		setMaxTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期上个月的结束日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getEndDayOfPreviousMonth(Calendar calendar) {
		// TODO
		return getEndDayOfPreviousMonth(calendar.getTime());
	}

	/**
	 * 判断当前日期是否为当月第一天
	 *
	 * @param date
	 * @return
	 */
	public static boolean isFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH) == 1;
	}

	/**
	 * 判断当前日期是否为当月第一天
	 *
	 * @param calendar
	 * @return
	 */
	public static boolean isFirstDayOfMonth(Calendar calendar) {
		return isFirstDayOfMonth(calendar.getTime());
	}

	/**
	 * 判断当前日期是否为当月最后一天
	 *
	 * @param date
	 * @return
	 */
	public static boolean isLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 判断当前日期是否为当月最后一天
	 *
	 * @param calendar
	 * @return
	 */
	public static boolean isLastDayOfMonth(Calendar calendar) {
		return isLastDayOfMonth(calendar.getTime());
	}

	/**
	 * 获取日期当年的起始日期和时间
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfYear(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 注意月份要减1
		calendar.set(calendar.get(Calendar.YEAR),0,1);
		setMinTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期当年的起始日期和时间
	 * @param calendar
	 * @return
	 */
	public static Date getStartDayOfYear(Calendar calendar) {
		// TODO
		return getStartDayOfYear(calendar.getTime());
	}
	
	/**
	 * 获取日期当年的结束日期和时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndDayOfYear(Date date) {
		// TODO
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 注意月份要减1
		calendar.set(calendar.get(Calendar.YEAR),11,31);
		setMaxTimeOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 获取日期当年的结束日期和时间
	 *
	 * @param calendar
	 * @return
	 */
	public static Date getEndDayOfYear(Calendar calendar) {
		// TODO
		return getEndDayOfYear(calendar.getTime());
	}
	
	/**
	 * 获取日期所在全年的天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getDaysOfTheYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取日期所在全年的天数
	 *
	 * @param calendar
	 * @return
	 */
	public static int getDaysOfTheYear(Calendar calendar) {
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * 获取日期所在全月的天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getDaysOfTheMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期所在全月的天数
	 *
	 * @param calendar
	 * @return
	 */
	public static int getDaysOfTheMonth(Calendar calendar) {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取两个日期间的年数、月数（取整）
	 * @param before
	 * @param after
	 * @param dateField (YEAR：1，MONTH：2)
	 * @return
	 */
	public static int getDistance(Date before, Date after, int dateField) {
		Calendar calBefore = Calendar.getInstance();
		calBefore.setTime(before);
		Calendar calAfter = Calendar.getInstance();
		calAfter.setTime(after);

		// dateField (1：（计算两日期间的年数），2：（计算两日期间的月数）)
		if (dateField == Calendar.YEAR) {
			// 结束日期：当年最后一天，需+1进行计算
			return calAfter.get(Calendar.YEAR) - calBefore.get(Calendar.YEAR) + 1;
		} else if (dateField == Calendar.MONTH) {
			int beforeMonth = calBefore.get(Calendar.YEAR) * 12 + calBefore.get(Calendar.MONTH);
			// 结束日期：当月最后一天，需+1进行计算
			int afterMonth = calAfter.get(Calendar.YEAR) * 12 + calAfter.get(Calendar.MONTH) + 1;

			return afterMonth - beforeMonth;
		}

		return 0;
	}
	/**
	 * 返回小日期
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date min(Date date1, Date date2) {
		return date1 != null && date1.compareTo(date2) == 1 ? date2 : date1;
	}

	/**
	 * 返回大日期
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date max(Date date1, Date date2) {
		return date1 != null && date1.compareTo(date2) == 1 ? date1 : date2;
	}
	
	
	/**
	 * 设置日期的开始时间
	 * @param calendar
	 */
	private static void setMinTimeOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.MILLISECOND, 000);
	}

	/**
	 * 设置日期的结束时间
	 * @param calendar
	 */
	private static void setMaxTimeOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.MILLISECOND, 999);
	}
		
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String time1 = "2012-12-28 00:00:00.000";
		String time2 = "2013-01-01 23:59:59.999";
		Date date = format.parse(time1);
		Date date2 = format.parse(time2);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		// double distanceDays = getDistanceDays(date, date2);
		//getDistanceMonths(date, date2);
		//getDistanceYears(cal1, cal2);
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
	}
}
