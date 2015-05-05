package nu.info.zeeshan.rnf;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		if(getIntent().getStringExtra("name").equalsIgnoreCase("about")){
			getFragmentManager().beginTransaction()
			.replace(R.id.setting_fragment_holder, new FragmentAbout())
			.commit();
		}
		else{
		getFragmentManager().beginTransaction()
				.replace(R.id.setting_fragment_holder, new SettingFragment())
				.commit();
		}
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

}
