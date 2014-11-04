package org.eli;

import android.webkit.WebChromeClient;
import org.renpy.android.PythonActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;
import android.webkit.WebSettings;

import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.view.View;
import android.os.Build;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

//for unzipping
//import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.os.AsyncTask;


import android.content.Context;
import android.view.MotionEvent;
import android.os.Environment;

//for RSA
import android.util.Base64;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;

import android.widget.Toast;
import java.lang.Thread;


public class JavaHandler {
	Activity myActivity = (Activity)PythonActivity.mActivity;
	ProgressBar progressBar;
	MyWebView wv;

	public static void unzipKaLite(){
		String _fileLocation = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test/ka-lite.zip";
	    String _targetLocation = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test/.";
	    movingFile();
	    unzipThreadUI(_fileLocation, _targetLocation);
	}

	public static void movingFile(){
		String copied_content = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test/copied_sdcard_content";
	    String moving = "null";
	    File dir_ainol = new File("/mnt/sd-ext/ka-lite");//this folder has to have unique name
        File dir_nexus7 = new File("/storage/emulated/0/UNICEF");//this folder has to have unique name
        File dir_asus_memo = new File("/removable/microsd/ka-lite");//this folder has to have unique name
		if(dir_ainol.exists()) {
			moving = "/mnt/sd-ext/ka-lite";
		}else if(dir_asus_memo.exists()){
			moving = "/removable/microsd/ka-lite";
		}else if(dir_nexus7.exists()){
			moving = "/storage/emulated/0/UNICEF";
		}
		File sourceFile = new File(moving);
		if(sourceFile.exists()){
			
			Thread myThread = new Thread(new Runnable() {
			    @Override
			    public void run() {
			        try {
			            Thread.sleep(5000);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			    }
			});
			String zipped_location = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test";
			File zipped = new File(zipped_location);
			while(!zipped.exists()){
				myThread.start();
			}
			File targetFolder = new File(copied_content);
			new fileMoving(sourceFile, targetFolder).execute();
	   	}else{
	   // 		Toast.makeText(myActivity, "For the first time installation, please insert a SD card with content folder named <ka-lite>. This app will be closed...",
				// Toast.LENGTH_LONG).show();
			System.exit(0);
	   	}
	}

	public static void dirChecker(String dir) {
		File f = new File(dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

	public static void unzipThreadUI(String _zipFile, String _targetLocation){
		//create target location folder if not exist
			dirChecker(_targetLocation);
			byte[] buffer = new byte[1024];
			try {
				FileInputStream fin = new FileInputStream(_zipFile);
				ZipInputStream zin = new ZipInputStream(fin);
				ZipEntry ze = zin.getNextEntry();
				while (ze != null) {
					System.out.println("elieli while 1: "+ ze.getName());
					//create dir if required while unzipping
					if (ze.isDirectory()) {
						System.out.println("elieli while dir");
						dirChecker(_targetLocation + File.separator + ze.getName());
					} else {
						System.out.println("elieli while file: " + _targetLocation + File.separator + ze.getName());
						File newfile = new File(_targetLocation + File.separator + ze.getName());						
						File parentDir = new File(newfile.getParent());
						System.out.println("elieli parent: " + newfile.getParent());
						if(!parentDir.exists()){
							System.out.println("elieli create dir");
							parentDir.mkdirs();
						}
						
						FileOutputStream fout = new FileOutputStream(newfile);
						System.out.println("elieli pass fout");

						int len_ui;
			            while ((len_ui = zin.read(buffer)) > 0) {
			            	fout.write(buffer, 0, len_ui);
				//       		System.out.println("elieli 2.5");
			            }
						System.out.println("elieli close nentry");
						zin.closeEntry();
						fout.close();
					}
					ze = zin.getNextEntry();
				}
				System.out.println("elieli done");
				zin.close();
				new File(_zipFile).delete(); 
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("elieli error");
			}
	}

//don't use this AsyncTask because environment setup need to wait unzip finished 
	public static class fileMoving extends AsyncTask<Void, Integer, Integer> {
		private File _targetFile;   
		private File _destination;
	//	private int per = 0;
		public fileMoving(File targetFile, File destination) {
			_targetFile = targetFile;     
			_destination = destination;      
      	} 

      	public void copyDir(File sourceLocation, File targetLocation){
      		try{
				if (sourceLocation.isDirectory()) {
				    if (!targetLocation.exists()) {
				        targetLocation.mkdir();
				    }

				    String[] children = sourceLocation.list();
				    for (int i = 0; i < sourceLocation.listFiles().length; i++) {

				        copyDir(new File(sourceLocation, children[i]),
				                new File(targetLocation, children[i]));
				    }
				} else {

				    InputStream in = new FileInputStream(sourceLocation);

				    OutputStream out = new FileOutputStream(targetLocation);

				    // Copy the bits from instream to outstream
				    byte[] buf = new byte[1024];
				    int len;
				    while ((len = in.read(buf)) > 0) {
				        out.write(buf, 0, len);
				    }
				    in.close();
				    out.close();
				}
		    }catch(IOException ex){
		        ex.printStackTrace(); 
		//       System.out.println("elieli error");
		    }
      	}
		
		@Override
		protected Integer doInBackground(Void... params) {
			// File sourceLocation = new File(tobemoved);
			// if(sourceLocation.exists()){
			// 	File targetLocation = new File(outputFolder);
		 //   		copyDir(sourceLocation, targetLocation);
		 //   	}else{
		 //   		Toast.makeText(myActivity, "For the first time installation, 
		 //   			\n please insert a SD card with content folder named <ka-lite>. 
		 //   			\n This app will be closed...",
   // 				Toast.LENGTH_SHORT).show();
   // 				System.exit(0);
		 //   	}
		   	copyDir(_targetFile, _destination);
			return null;
		}
		protected void onProgressUpdate(Integer... progress) {
	//		progressBar.setProgress(per); //Since it's an inner class, Bar should be able to be called directly
	    }
	}

	public static void generateRSA(){
		System.out.println("elieli in"); 
		try { 
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); 
            keyGen.initialize(2048); 
            KeyPair RSA_key = keyGen.generateKeyPair(); 
            Key priavte_key = RSA_key.getPrivate();
            Key public_key = RSA_key.getPublic();
            
            byte[] publicKeyBytes = public_key.getEncoded();
            byte[] privateKeyBytes = priavte_key.getEncoded();
            
          //Convert Public key to String
          //   String pubKeyStr = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);
            
          //   String priKeyStr = "-----BEGIN RSA PRIVATE KEY-----\n" 
        		// + Base64.encodeToString(privateKeyBytes, Base64.DEFAULT)
        		// + "-----END RSA PRIVATE KEY-----";

            // String fileLocation = Environment.getExternalStorageDirectory().getPath() 
            // 	+ "/org.kalite.test/ka-lite/kalite/database/";
            // File myPublicRSA = new File(fileLocation, "myPublicRSA");
            // File myPrivateRSA = new File(fileLocation, "myPrivateRSA");

            // Writer writer1= new BufferedWriter(new FileWriter(myPublicRSA));
            // writer1.write(pubKeyStr);
            // writer1.close();
            
            // Writer writer2= new BufferedWriter(new FileWriter(myPrivateRSA));
            // writer2.write(priKeyStr);
            // writer2.close();
            String content_root = null;
            String content_data = null;
            // File dir_ainol = new File("/mnt/sd-ext");
            // File dir_nexus7 = new File("/storage/emulated/0");
            // File dir_asus_memo = new File("/removable/microsd");

            String copied_sdcard_content = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test/copied_sdcard_content";

            content_root = "\nCONTENT_ROOT = \"" + copied_sdcard_content +"/content/\"";
            content_data = "\nCONTENT_DATA_PATH = \"" + copied_sdcard_content +"/data/\"";

			// if(dir_ainol.exists()) {
			// 	content_root = "\nCONTENT_ROOT = \"/mnt/sd-ext/ka-lite/content/\"";
			// 	content_data = "\nCONTENT_DATA_PATH = \"/mnt/sd-ext/ka-lite/data/\"";
			// }else if(dir_asus_memo.exists()){
			// 	content_root = "\nCONTENT_ROOT = \"/Removable/MicroSD/ka-lite/content/\"";
			// 	content_data = "\nCONTENT_DATA_PATH = \"/Removable/MicroSD/ka-lite/data/\"";
			// }else if(dir_nexus7.exists()){
			// 	content_root = "\nCONTENT_ROOT = \"/storage/emulated/0/UNICEF/content/\"";
			// 	content_data = "\nCONTENT_DATA_PATH = \"/storage/emulated/0/UNICEF/data/\"";
			// }

            String gut ="CHANNEL = \"connectteaching\"" +
            		"\nLOAD_KHAN_RESOURCES = False" +
            		// "\nCONTENT_ROOT = \"/mnt/sd-ext/ka-lite/content/\"" +
            		// "\nCONTENT_DATA_PATH = \"/mnt/sd-ext/ka-lite/data/\"" +
            		content_root +
            		content_data +
            		"\nUSE_I18N = False" +
            		"\nUSE_L10N = False" +
            		"\nEBUG = False" +
            		"\nOWN_DEVICE_PUBLIC_KEY=" + "\"" + Base64.encodeToString(publicKeyBytes, 24, publicKeyBytes.length-24, Base64.DEFAULT).replace("\n", "\\n") + "\""
            		+ "\nOWN_DEVICE_PRIVATE_KEY=" +  "\"" + "-----BEGIN RSA PRIVATE KEY-----" + "\\n" 
            		+ Base64.encodeToString(privateKeyBytes, 26, privateKeyBytes.length-26, Base64.DEFAULT).replace("\n", "\\n")
            		+ "-----END RSA PRIVATE KEY-----" + "\"";
            
            String fileLocation2 = Environment.getExternalStorageDirectory().getPath() + "/org.kalite.test/ka-lite/kalite/";
            File myFile = new File(fileLocation2 , "local_settings.py");
            System.out.println("elieli ok so far 1"); 
            if(myFile.exists())
            {
               try
               {
               		System.out.println("elieli creating key"); 
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(gut);
                    myOutWriter.close();
                    fOut.close();

                    System.out.println("elieli done"); 
                } catch(Exception e)
                {

                }
            }
            else
            {
            	System.out.println("elieli not found"); 
                myFile.createNewFile();
            }
            
        } catch(Exception e) { 
        	System.out.println("elieli error"); 
            System.out.println("RSA generating error"); 
        }
	}

	public void initWebView(){
// 		myActivity = (Activity)PythonActivity.mActivity; 
// //		pd = new ProgressDialog(myActivity);
// //		pb = new ProgressBar(myActivity);
// 		wv = new WebView(myActivity);

// 		myActivity.getWindow().requestFeature(Window.FEATURE_PROGRESS);
// 		myActivity.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
// 		wv.setWebViewClient(new WebViewClient());
// 		wv.setWebChromeClient(new MyWebChromeClient());
// 		WebSettings ws = wv.getSettings();
// 		ws.setJavaScriptEnabled(true);
// 		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
// 		ws.setRenderPriority(WebSettings.RenderPriority.HIGH);

		// pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// pd.setTitle("Please wait");
  //  		pd.setMessage("Page is loading...");
	}

	public void showWebView(){ 
		progressBar = new ProgressBar(myActivity, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10));
		// retrieve the top view of our application
		final FrameLayout decorView = (FrameLayout) myActivity.getWindow().getDecorView();
		decorView.addView(progressBar);

		wv = new MyWebView(myActivity);
		wv.setBackgroundColor(Color.WHITE);
//for nexus 7 only
		// if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		//     WebView.setWebContentsDebuggingEnabled(true);
		// }

		if (Build.VERSION.SDK_INT >= 19) {
		    wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}       
		else {
		    wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

//if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		//wv.setWebContentsDebuggingEnabled(true);
//		}

		wv.setWebViewClient(new WebViewClient());
		wv.setWebChromeClient(new MyWebChromeClient());
		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// ws.setRenderPriority(WebSettings.RenderPriority.HIGH);

        wv.loadUrl("http://0.0.0.0:8008");
 //       wv.loadUrl("http://www.google.com");

        // Setting the RelativeLayout as our content view
    //    myActivity.setContentView(relativeLayout, rlp);
        myActivity.setContentView(wv);
	}

	public void quitApp(){
		System.exit(0);
		//myActivity.finish();
	}

	public boolean backPressed(){
		Toast.makeText(myActivity, wv.getUrl(),
   		Toast.LENGTH_SHORT).show();
   		return true;

		// if(wv.canGoBack()){
		// 	//wv.goBack();
		// 	return true;
		// }else{
		// 	return false;
		// }
	}

	public void goBack(){
		wv.goBack();
	}

	private class MyWebChromeClient extends WebChromeClient{
		@Override
		public void onProgressChanged(WebView view, int progress) {

			progressBar.setVisibility(View.VISIBLE);
        	progressBar.setProgress(progress);

			if(progress == 100){
				progressBar.setVisibility(View.GONE);

            }
		}
	}

	private class MyWebView extends WebView{

	    public MyWebView(Context context) {
	        super(context);
	        // TODO Auto-generated constructor stub
	    }

	    private long lastMoveEventTime = -1;
	    private int eventTimeInterval = 40;

	    @Override
	    public boolean onTouchEvent(MotionEvent ev) {

	        long eventTime = ev.getEventTime();
	        int action = ev.getAction();

	        switch (action){
	            case MotionEvent.ACTION_MOVE: {
	                if ((eventTime - lastMoveEventTime) > eventTimeInterval){
	                    lastMoveEventTime = eventTime;
	                    return super.onTouchEvent(ev);
	                }
	                break;
	            }
	            case MotionEvent.ACTION_DOWN:
	            case MotionEvent.ACTION_UP: {
	                return super.onTouchEvent(ev);
	            }
	        }
	        return true;
	    }
	}


}