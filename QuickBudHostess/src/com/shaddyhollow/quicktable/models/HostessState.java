package com.shaddyhollow.quicktable.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shaddyhollow.quickbud.Config;

public class HostessState {
	public Integer activePlan;
	public List<Table> tables = new ArrayList<Table>();
	public List<Section> sections = new ArrayList<Section>();
	public String dataDir = Config.getStorageDir("state");
	
	public void save() {
		
		for(Section section : sections) {
			try {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String jsonvalue = gson.toJson(section);
				File backupFile = new File(dataDir, "section_" + section.id + ".json");
				backupFile.getParentFile().mkdirs();
				
				backupFile.createNewFile();
				PrintWriter out = new PrintWriter(new FileWriter(backupFile));
			    out.write(jsonvalue);
			    out.flush();
			    out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Table table : tables) {
			try {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String jsonvalue = gson.toJson(table);
				File backupFile = new File(dataDir, "table_" + table.id + ".json");
				backupFile.getParentFile().mkdirs();
				
				backupFile.createNewFile();
				PrintWriter out = new PrintWriter(new FileWriter(backupFile));
			    out.write(jsonvalue);
			    out.flush();
			    out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonvalue = gson.toJson(activePlan);
			File backupFile = new File(dataDir, "activeplan.json");
			backupFile.getParentFile().mkdirs();
			
			backupFile.createNewFile();
			PrintWriter out = new PrintWriter(new FileWriter(backupFile));
		    out.write(jsonvalue);
		    out.flush();
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String load(Gson gson, File backupFile) {
		String jsonValue = "";

		if(backupFile!=null && backupFile.exists()) {
	        StringBuffer contents = new StringBuffer();
	        BufferedReader reader = null;
	
	        try {
	            reader = new BufferedReader(new FileReader(backupFile));
	            String text = null;
	
	            // repeat until all lines is read
	            while ((text = reader.readLine()) != null) {
	                contents.append(text).append(System.getProperty("line.separator"));
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
	
	public void load() {
		String jsonString;
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		File backupDir = new File(dataDir);

		jsonString = load(gson, new File(backupDir, "activeplan.json"));
		activePlan = gson.fromJson(jsonString, Integer.class);
		
		File[] tableFiles = backupDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName();
				return filename.startsWith("table_") && filename.endsWith(".json");
			}
		});
		
		tables = new ArrayList<Table>();
		for(File tableFile : tableFiles) {
			jsonString = load(gson, tableFile);
			tables.add(gson.fromJson(jsonString, Table.class));
		}
		
		File[] sectionFiles = backupDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName();
				return filename.startsWith("section_") && filename.endsWith(".json");
			}
		});
		
		sections = new ArrayList<Section>();
		for(File sectionFile : sectionFiles) {
			jsonString = load(gson, sectionFile);
			sections.add(gson.fromJson(jsonString, Section.class));
		}
	}
	
}
