package com.saint_peter.halowhip;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputType;


public class SettingsActivity extends PreferenceActivity {

	public static class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
		
		final int TEXT_PASSWORD = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
		final String[] KEYS = {"host", "username", "password", "door"};
		
		@Override
		public void onCreate (final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			for (String key : this.KEYS) {
				Preference pref = findPreference(key);
				pref.setOnPreferenceChangeListener(this);
				if (pref instanceof EditTextPreference) {
					setSummary(pref, ((EditTextPreference) pref).getText());
				}
				else if (pref instanceof ListPreference) {
					setSummary(pref, ((ListPreference) pref).getValue());
				}
			}
		}

		@Override
		public boolean onPreferenceChange (Preference pref, Object value) {
			setSummary(pref, value);
			return true;
		}
		
		public void setSummary (Preference pref, Object value) {
			if (pref instanceof EditTextPreference) {
				String _value = (String) value;
				if (((EditTextPreference) pref).getEditText().getInputType() == TEXT_PASSWORD) {
					_value = new String(new char[_value.length()]).replace("\0", "\u00B7");
				}
				pref.setSummary(_value);
			} else if (pref instanceof ListPreference) {
			}
		}
	}
	
	@Override
	public void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
	}
	
}
