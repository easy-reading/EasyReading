package nu.info.zeeshan.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	public interface Filter{
		int UNREAD=1;
		int READ=2;
		int ALL=3;
	}
	
	public static class ViewHolder {
		public TextView title;
		public TextView desc;
		public TextView time;
		public ImageView image;
		public ImageButton check;
		public int id;
		public int state;
		public int type;
	}
}
