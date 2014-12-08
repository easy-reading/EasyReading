package nu.info.zeeshan.utility;

import java.util.ArrayList;

import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.rnf.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Utility {
	private static ProgressDialog progress_dialog;
	private static String TAG="nu.info.zeeshan.utility";
	public static void log(String TAG,String msg){
		Log.d(TAG,msg);
	}
	public static void showProgressDialog(Context context){
		try{
			progress_dialog=ProgressDialog.show(context, null, "Please wait..", true, false);
			Utility.log(TAG, "progress is OK");
		}catch(Exception ex){
			log(TAG,"showProgressDialog "+ex.getMessage());
		}
	}
	public static void hideProgressDialog(){
		try{
			progress_dialog.dismiss();
		}catch(Exception ex){
			log(TAG,"hideProgressDialog "+ex.getMessage());
		}
	}
	
	
	
	
}
