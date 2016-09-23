package com.huntloc.handheldpv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements
        HandheldFragment.OnHandheldFragmentInteractionListener, EntranceFragment.OnEntranceFragmentInteractionListener, ExitFragment.OnExitFragmentInteractionListener {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String PREFS_NAME = "HandheldPV2PrefsFile";
    private static long back_pressed;
    TextView textView_lastupdate_date;
    ProgressDialog progress;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);
        progress.setCanceledOnTouchOutside(false);
        textView_lastupdate_date = (TextView) findViewById(R.id.textView_lastupdate_date);
        textView_lastupdate_date.setText("Última actualización: " + getSharedPreferences(PREFS_NAME, 0).getString("lastupdate", "No se ha sincronizado"));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG)
                    .show();
        }
        handleIntent(getIntent());

        SharedPreferences settings = getSharedPreferences(
                PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("door_id", "PV2");
        editor.putString("logEntry_id", "EntryPV2");
        editor.putString("descLogEntry_id", "Entrance");
        editor.putString("logExit_id", "ExitPV2");
        editor.putString("descLogExit_id", "Exit");
        editor.commit();

    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                Parcelable parcelable = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Tag tag = (Tag) parcelable;
                byte[] id = tag.getId();
                String code = getDec(id) + "";
                Log.d("Internal Code", code);
                SQLiteHelper db = new SQLiteHelper(getApplicationContext());
                Personnel personnel = db.getPersonnel(getDec(id));
                if(personnel!=null){
                    HandheldFragment handheldFragment = ((HandheldFragment) mSectionsPagerAdapter.getItem(0));
                    if (handheldFragment != null) {
                        handheldFragment.setCredentialId(personnel.getPrintedCode());
                    }
                }
                else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("PV2 Access Control");
                    alertDialogBuilder.setMessage("Badge no tiene acceso");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialogBuilder.create().show();

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onNavigateUpFromChild(Activity child) {
        HandheldFragment handheldFragment = ((HandheldFragment) mSectionsPagerAdapter.getItem(0));
        if (handheldFragment != null) {
            handheldFragment.setCredentialId("");
        }
        Log.d("MainActivity","I'm back");
        return super.onNavigateUpFromChild(child);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            updatePersonnel();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePersonnel() {
        String serverURL = getResources().getString(R.string.service_url)
                + "/PBIPPersonnelOfflineService/"
                + UUID.randomUUID().toString();
        QueryPersonnelPV2Task personnelTask = new QueryPersonnelPV2Task();
        progress.setMessage(getResources().getString(
                R.string.action_update_message));
        progress.show();
        personnelTask.execute(serverURL);
    }

    private void showLastUpdate() {

        textView_lastupdate_date.setText("Última actualización: "
                + getSharedPreferences(PREFS_NAME, 0).getString("lastupdate",
                "No se ha sincronizado"));
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    public void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(),
                activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters,
                techList);
    }

    public void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public void onHandheldFragmentInteraction() {

    }

    @Override
    public void onEntranceFragmentInteraction() {

    }

    @Override
    public void onExitFragmentInteraction() {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private HandheldFragment handheldFragment;
        private EntranceFragment entranceFragment;
        private ExitFragment exitFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                if (handheldFragment == null) {
                    handheldFragment = new HandheldFragment();
                }
                fragment = handheldFragment;
            } else if (position == 1) {
                if (entranceFragment == null) {
                    entranceFragment = new EntranceFragment();
                }
                fragment = entranceFragment;
            } else if (position == 2) {
                if (exitFragment == null) {
                    exitFragment = new ExitFragment();
                }
                fragment = exitFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HANDHELD";
                case 1:
                    return "ENTRANCE";
                case 2:
                    return "EXIT";
            }
            return null;
        }
    }

    private class QueryPersonnelPV2Task extends
            AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        private QueryPersonnelPV2Task() {
        }

        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);
                Log.d("Query Personnel URL", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray jsonArray = jsonResponse.getJSONArray("values");
                SQLiteHelper db = new SQLiteHelper(
                        MainActivity.this.getApplicationContext());
                db.deletePersonnel();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Personnel personnel = new Personnel(
                            jsonArray.getJSONObject(i)
                                    .optString("InternalCode"),
                            jsonArray.getJSONObject(i).optString("PrintedCode"),
                            jsonArray.getJSONObject(i).optString("Portrait"),
                            jsonArray.getJSONObject(i).optString("Name"),
                            jsonArray.getJSONObject(i).isNull("PBIPIDate") ? null
                                    : jsonArray.getJSONObject(i).optString(
                                    "PBIPIDate"),
                            jsonArray.getJSONObject(i).isNull(
                                    "HazardousGoodsDate") ? null : jsonArray
                                    .getJSONObject(i).optString(
                                            "HazardousGoodsDate"),
                            jsonArray.getJSONObject(i).isNull(
                                    "PortSecurityDate") ? null : jsonArray
                                    .getJSONObject(i).optString(
                                            "PortSecurityDate"),
                            jsonArray.getJSONObject(i).isNull("PBIPColor") ? null
                                    : jsonArray.getJSONObject(i).optString(
                                    "PBIPColor"),
                            jsonArray
                                    .getJSONObject(i).isNull("PBIPCode") ? null
                                    : jsonArray.getJSONObject(i).optString(
                                    "PBIPCode"), jsonArray
                            .getJSONObject(i).optString("Clearance"));

                    db.addPersonnel(personnel);
                }
                progress.dismiss();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                MainActivity.this);
                        alertDialogBuilder.setTitle("PV2 Access Control");
                        alertDialogBuilder.setMessage("Descarga Completa");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialogBuilder.create().show();

                        SimpleDateFormat newDateFormat = new SimpleDateFormat(
                                "EEEE, d MMMM yyyy h:mm a");
                        Calendar today = Calendar.getInstance();
                        SharedPreferences settings = getSharedPreferences(
                                PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("lastupdate",
                                newDateFormat.format(today.getTime()));
                        editor.commit();

                        MainActivity.this.showLastUpdate();
                    }
                });
            } catch (Exception e) {

            }
        }
    }
}
