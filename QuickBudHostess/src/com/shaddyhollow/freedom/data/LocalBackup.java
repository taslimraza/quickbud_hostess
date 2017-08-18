package com.shaddyhollow.freedom.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.quicktable.models.SectionPlan;

public class LocalBackup {
	Context context = null;
	File basedir = null;
	
	public LocalBackup(Context context) {
		this.context = context;
		File extStorage = new File("/storage/extSdCard"); //Environment.getExternalStorageDirectory();
		if(!extStorage.exists() || !extStorage.canWrite()) {
			extStorage = Environment.getExternalStorageDirectory();
		}
		basedir = new File(extStorage.getAbsolutePath() + File.separator + "AreaEditor" + File.separator + Config.location.getLocationId() + File.separator + "backup");
		basedir.mkdirs();
	}
	
	public void backup() {
		basedir.delete();
		basedir.mkdirs();
		BackupBundle bundle = new BackupBundle();
		bundle.inititalize();
		
		try {
			File backupFile = new File(basedir, "layout.txt");
			backupFile.getParentFile().mkdirs();
			
			backupFile.createNewFile();
			PrintWriter out = new PrintWriter(new FileWriter(backupFile));
		    out.write(bundle.serialize().hostess_config);
		    out.flush();
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void restore() {
		if(!basedir.exists()) {
			Toast.makeText(context, "Unable to locate backup directory", Toast.LENGTH_SHORT).show();
			return;
		}

		File backupFile = new File(basedir, "layout.txt");
		
		String contents = load(backupFile);
		BackupBundle bundle = new BackupBundle();
		HostessConfig config = new HostessConfig();
		config.hostess_config = contents;
		bundle.deserialize(config);
		
		for(Floorplan floorplan : bundle.floorplans) {
			FloorplanFactory.getInstance().createOrUpdate(floorplan);
		}
		for(SectionPlan sectionplan : bundle.sectionPlans) {
			SectionPlanFactory.getInstance().createOrUpdate(sectionplan);
		}
	}
	
	private String load(File backupFile) {
		String jsonValue = "";

		if(backupFile!=null && backupFile.exists()) {
	        StringBuffer contents = new StringBuffer();
	        BufferedReader reader = null;
	
	        try {
	            reader = new BufferedReader(new FileReader(backupFile));
	            String text = null;
	
	            // repeat until all lines is read
	            while ((text = reader.readLine()) != null) {
	                contents.append(text);
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (reader != null) {
	                    reader.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        // end file read
	        jsonValue = contents.toString();
		}
		return jsonValue;
	}
	

}
