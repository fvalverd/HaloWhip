package com.saint_peter.halowhip;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class SettingsActivity extends PreferenceActivity {

	public static class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
		
		final String[] KEYS = {"host", "username", "password", "door"};
		
		@Override
		public void onCreate (final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			for (String key : this.KEYS) {
				Preference pref = findPreference(key);
				pref.setOnPreferenceChangeListener(this);
				this.setSummary(pref, ((EditTextPreference) pref).getText());
			}
		}
		
		@Override
		public boolean onPreferenceChange (Preference pref, Object value) {
			this.setSummary(pref, value);
			return true;
		}
		
		public void setSummary (Preference pref, Object value) {
			if (!pref.equals(findPreference("whip_sensibility"))) {
				String _value = (String)value; 
				if (pref.equals(findPreference("password"))) {
					_value  = new String(new char[_value .length()]).replace("\0", "*");
				}
				pref.setSummary(_value );
			}
		}
		
	}
	
	@Override
	public void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
	}
	
}
