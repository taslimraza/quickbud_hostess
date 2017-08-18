package com.shaddyhollow.quickbud.datastore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.freedom.data.Base64Coder;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddAttachmentRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetAttachmentRequest;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Attachment;
import com.shaddyhollow.robospice.BaseListener;

public class TabletStateHelper {
	private final static String BASEKEY = "tablet_state";
    private static String liveDBPath = "//data//" + Config.packageName + "//databases//" + "dinendash.db";
    private static String backupPath = "//" + Config.packageName + "//backup.db"; 
    private static String backupEncodedPath = "//" + Config.packageName + "//backup64.db";
    
    private static String getKey() {
    	return BASEKEY + "_" + DatabaseHelper.CURRENT_DATABASE_VERSION;
    }
    
    public static void importDB(final Context context, SpiceManager contentManager) {
    	if(contentManager!=null) {
    		GetAttachmentRequest request = new GetAttachmentRequest(Config.getTenantID(), Config.getLocationID(), getKey());
    		request.execute(contentManager, new BaseListener<Attachment>() {

				@Override
				public void onRequestSuccess(Attachment attachment) {
			        File sd = null;
		        	File backupFile  = null;
					PrintWriter out = null;
		        	try {
				        sd = Environment.getExternalStorageDirectory();
			        	backupFile  = new File(sd, backupEncodedPath);
			        	backupFile.getParentFile().mkdirs();
			        	
						out = new PrintWriter(backupFile);
						
						out.print(attachment.getValue());
						out.flush();
						out.close();
						importDBFile(context);
					} catch (Exception e) {
						Toast.makeText(context, "Unable to load table state from server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					} finally {
						out.close();
					}
		        	
				}
				
				@Override
				public void onFailure(SpiceException ex) {
					super.onFailure(ex);
					Toast.makeText(context, "Unable to load table state from server: " + ex.getCause().getMessage(), Toast.LENGTH_SHORT).show();
				}

    		});
    	}
    }
    
    public static void importDBFile(Context context) {
    	
        File sd = Environment.getExternalStorageDirectory();
        File data  = Environment.getDataDirectory();

        FileInputStream fis = null;
    	FileOutputStream fos = null;
    	
        try {
        	File inputFile = new File(sd, backupEncodedPath);
        	File outputFile = new File(sd, backupPath);
        	
        	Base64Coder.decodeFile(inputFile, outputFile);
        	inputFile.delete();
        } catch (Exception e) {
			Toast.makeText(context, "Unable to load table state from server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        	e.printStackTrace();
        	return;
        } finally {
        }

        try {
            File  backupDB = new File(data, liveDBPath);
            File currentDB = new File(sd, backupPath);

            currentDB.getParentFile().mkdirs();
            if (backupDB.canWrite()) {

                fis = new FileInputStream(currentDB);
                fos = new FileOutputStream(backupDB);
                FileChannel src = fis.getChannel();
                FileChannel dst = fos.getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Current state restored", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } finally {
    		try { fis.close(); } catch (Exception e) { }
    		try { fos.close(); } catch (Exception e) { }
        }
    }

    public static void exportDB(final Context context, SpiceManager contentManager) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

    	FileInputStream is = null;
    	FileOutputStream os = null;

    	// backup database
        try {
            if (sd.canWrite()) {
                File currentDB = new File(data, liveDBPath);
                File backupDB = new File(sd, backupPath);

                backupDB.getParentFile().mkdirs();

                is = new FileInputStream(currentDB);
                os = new FileOutputStream(backupDB);

                FileChannel src = is.getChannel();
                FileChannel dst = os.getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
	    } finally {
			try { is.close(); } catch (Exception e) { }
			try { os.close(); } catch (Exception e) { }
	    }
        
        // convert backup to base64
        try {
        	File inputFile = new File(sd, backupPath);
        	File outputFile = new File(sd, backupEncodedPath);
        	
        	Base64Coder.encodeFile(inputFile, outputFile);
        	inputFile.delete();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        }
      
        // send base64 file to server
    	if(contentManager!=null) {
    		Scanner scanner = null;
        	try {
        		File inputFile = new File(sd, backupEncodedPath);
        		String content = getStringFromFile(inputFile);

        		AddAttachmentRequest request = new AddAttachmentRequest(Config.getTenantID(), Config.getLocationID(), getKey(), content);
        		request.execute(contentManager, new BaseListener<Void>() {

					@Override
					public void onRequestSuccess(Void obj) {
						Toast.makeText(context, "Table state saved", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(SpiceException ex) {
						super.onFailure(ex);
						Toast.makeText(context, "Unable to save table state: " + ex.getCause().getMessage(), Toast.LENGTH_SHORT).show();
					}
        			
        		});
        	} catch (Exception e) {
        	} finally {
        		if(scanner!=null) {
        			scanner.close();
        		}
        	}
    	}
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile (File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();        
        return ret;
    }
}
