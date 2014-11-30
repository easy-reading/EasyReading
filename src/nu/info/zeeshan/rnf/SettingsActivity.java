package nu.info.zeeshan.rnf;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_settings);
	       getFragmentManager().beginTransaction().replace(R.id.setting_fragment_holder,new FragmentSettings()).commit();
	    }
}
