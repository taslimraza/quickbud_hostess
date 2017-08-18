package com.shaddyhollow.util;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.shaddyhollow.quicktable.models.QueuedVisit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class FileOperations {
	static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	static String date;

	public static void createLogDirectory() {

		try {
			// Globals.timeStamp = Miscellaneous.getDate();
			date = df.format(Calendar.getInstance().getTime());
			File external = Environment.getExternalStorageDirectory();
			String sdcardPath = external.getPath();
			Log.d("PATH", sdcardPath);
			File logDir = new File(sdcardPath + "/Logs");
			if (!logDir.exists() || !logDir.isDirectory()) {
				logDir.mkdir();
				File subDir = new File(sdcardPath + "/Logs/" + date);
				if (!subDir.exists() || !subDir.isDirectory()) {
					subDir.mkdir();
				} else {
					Log.d("Failed", "SUB Directory creation failed");
				}
			} else {
				File subDir = new File(sdcardPath + "/Logs/" + date);
				if (!subDir.exists() || !subDir.isDirectory()) {
					subDir.mkdir();
				} else {
					Log.d("Failed", "SUB Directory creation failed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeToFile(QueuedVisit[] visitList) {

		BufferedWriter out = null;
		FileWriter filewriter = null;

		try {

			String time = df.format(Calendar.getInstance().getTime());
			File external = Environment.getExternalStorageDirectory();
			String sdcardPath = external.getPath();
			File file = new File(sdcardPath + "/Logs/" + date
					+ "/visitList.txt");
			if (!file.isFile()) {
				file.createNewFile();
				filewriter = new FileWriter(file, false);
				out = new BufferedWriter(filewriter);
				out.write(time + "\t" + "Listed Patron" + "\n");
				for (int index = 0; index < visitList.length; index++) {
					out.write(time + "\t" + visitList[index].getName() + "\t"
							+ visitList[index].getCreated_at() + "\t"
							+ visitList[index].getPhone_number() + "\n");
				}
				out.write("---------------------------------------------------------"
						+ "\n");
			} else {
				filewriter = new FileWriter(file, true);
				out = new BufferedWriter(filewriter);
				out.write(time + "\t" + "Listed Patron" + "\n");
				for (int index = 0; index < visitList.length; index++) {
					out.write(time + "\t" + visitList[index].getName() + "\t"
							+ visitList[index].getCreated_at() + "\t"
							+ visitList[index].getPhone_number() + "\n");
				}
				out.write("---------------------------------------------------------"
						+ "\n");
			}
			out.close();
			filewriter.close();
		} catch (Exception e) {
			Log.d("Failed to save file", e.toString());
		} finally {
			try {
				out.close();
				filewriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void writeToFile(QueuedVisit visitList, boolean removed) {

		BufferedWriter out = null;
		FileWriter filewriter = null;

		try {

			String time = df.format(Calendar.getInstance().getTime());
			File external = Environment.getExternalStorageDirectory();
			String sdcardPath = external.getPath();
			File file = new File(sdcardPath + "/Logs/" + date
					+ "/visitList.txt");
			if (!file.isFile()) {
				file.createNewFile();
				filewriter = new FileWriter(file, false);
				out = new BufferedWriter(filewriter);

				if(removed){
					out.write(time + "\t" + "Removed Patron" + "\n");
				}else {
					out.write(time + "\t" + "Added Patron" + "\n");
				}
				
				out.write(time + "\t" + visitList.getName() + "\t"
						+ visitList.getCreated_at() + "\t"
						+ visitList.getPhone_number() + "\n");
				out.write("---------------------------------------------------------"
						+ "\n");
			} else {
				filewriter = new FileWriter(file, true);
				out = new BufferedWriter(filewriter);
				
				if(removed){
					out.write(time + "\t" + "Removed Patron" + "\n");
				}else {
					out.write(time + "\t" + "Added Patron" + "\n");
				}
				
				out.write(time + "\t" + visitList.getName() + "\t"
						+ visitList.getCreated_at() + "\t"
						+ visitList.getPhone_number() + "\n");
				out.write("---------------------------------------------------------"
						+ "\n");
			}
			out.close();
			filewriter.close();
		} catch (Exception e) {
			Log.d("Failed to save file", e.toString());
		} finally {
			try {
				out.close();
				filewriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void saveLogData(String data) {

		BufferedWriter out = null;
		FileWriter filewriter = null;
		String time = df.format(Calendar.getInstance().getTime());
		try {
			File external = Environment.getExternalStorageDirectory();
			String sdcardPath = external.getPath();
			File file = new File(sdcardPath + "/Logs/" + date + "/log.txt");
			if (!file.isFile()) {
				file.createNewFile();
				filewriter = new FileWriter(file, false);
				out = new BufferedWriter(filewriter);
				out.write("\n" + data + "\n");
			} else {
				filewriter = new FileWriter(file, true);
				out = new BufferedWriter(filewriter);
				out.write("\n" + data + "\n");
			}
			out.close();
			filewriter.close();
		} catch (Exception ex) {
			Log.d("failed to save file", ex.toString());
		} finally {
			try {
				out.close();
				filewriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
