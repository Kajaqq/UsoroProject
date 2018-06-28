package com.usoroos.usorosyncprototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.usoroos.usorosyncprototype.TCP.TCPServer;

import java.util.List;

import static com.usoroos.usorosyncprototype.ClipboardService.mRunning;
import static com.usoroos.usorosyncprototype.TCP.TCPServer.mServerRunning;
import static com.usoroos.usorosyncprototype.NotificationService.mListenerRunning;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
@SuppressWarnings("ALL")
public class MainActivity extends AppCompatPreferenceActivity  {
    private static boolean clipboard = false;
    private static boolean linksync = false;
    private static boolean notif = false;
    private static Context context;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String key=preference.getKey();
            if (key.equals("share_switch")) {
                if (value.equals(true)) {
                    ReceiveActivity.EnableSharing();
                    if (!mServerRunning)
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    new TCPServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    preference.setSummary(R.string.disable_share_summary);
                    linksync = true;
                }
                if (value.equals(false))
                    ReceiveActivity.DisableSharing();
                    if(mServerRunning && !clipboard && !notif)
                        TCPServer.stop();
                    preference.setSummary(R.string.share_summary);
                    linksync=false;
            }

            if (key.equals("clipboard_switch")) {
                if (value.equals(true) && !mRunning) {
                    context.startService(new Intent(context, ClipboardService.class));
                    if (!mServerRunning)
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    new TCPServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    preference.setSummary(R.string.disable_clipboard_summary);
                    clipboard = true;
                }
                if (value.equals(false))
                    context.stopService(new Intent(context, ClipboardService.class));
                    if(mServerRunning && !linksync && !notif)
                        TCPServer.stop();
                    preference.setSummary(R.string.enable_clipboard_summary);
                    clipboard = false;

            }

            if (key.equals("notif_switch")) {
                if (value.equals(true) && !mListenerRunning) {
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    context.startActivity(intent);
                    if (!mServerRunning)
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    new TCPServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    preference.setSummary(R.string.disable_notification_summary);
                    notif = true;
                }
                if (value.equals(false))
                if(mServerRunning && !linksync && !clipboard)
                    TCPServer.stop();
                preference.setSummary(R.string.enable_notification_summary);
                notif = false;
            }
            return true;
        }
    };


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,

                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).
                        getBoolean(preference.getKey(),true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        context = getApplicationContext();


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    @SuppressWarnings("EmptyMethod")
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("share_switch"));
            bindPreferenceSummaryToValue(findPreference("clipboard_switch"));
            bindPreferenceSummaryToValue(findPreference("notif_switch"));
        }

        @Override
        public void onResume() {
            super.onResume();
            if (clipboard && !mRunning) {
                PreferenceManager.setDefaultValues(context, "clipboard_switch", Context.MODE_PRIVATE, R.xml.pref_general, true);
                clipboard = false;
            }

            if (linksync && !mServerRunning) {
                PreferenceManager.setDefaultValues(context, "share_switch", Context.MODE_PRIVATE, R.xml.pref_general, true);
                linksync = false;
            }

            if (notif && !mServerRunning || !mListenerRunning) {
                PreferenceManager.setDefaultValues(context, "notif_switch", Context.MODE_PRIVATE, R.xml.pref_general, true);
                notif = false;
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
