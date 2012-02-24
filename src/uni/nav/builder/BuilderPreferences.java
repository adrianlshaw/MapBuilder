package uni.nav.builder;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BuilderPreferences extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}	
}
