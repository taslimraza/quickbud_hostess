package com.shaddyhollow.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.SuppressLint;

public class DateUtils {

	public static String getTime(String inputDate) {
		return getTime(inputDate, 0);
	}
	
	public static String getTime(String inputDate, int offsetMin) {
		return getTime(inputDate, offsetMin, "hh:mm a");
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getTime(String inputDate, int offsetMin, String format) {
		DateTime date = new DateTime();
		String output = inputDate;
		
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
//		inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			date = dtf.parseDateTime(inputDate);
		} catch (Exception e) {
			dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
			try {
				date = dtf.parseDateTime(inputDate);
			} catch (Exception ee) {
			}
		} 	
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date.toDate());
		cal.add(Calendar.MINUTE, offsetMin);
		
		SimpleDateFormat outputFormat = new SimpleDateFormat(format);
		output = outputFormat.format(cal.getTime());
		return output;
	}

	public static String getTime(Date date) {
		return getTime(date, "HH:mm a");
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getTime(Date date, String format) {
		String output;
		SimpleDateFormat outputFormat = new SimpleDateFormat(format);
		output = outputFormat.format(date);
		return output;
	}
}
