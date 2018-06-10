package it.nicolapaoli.gate.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import it.nicolapaoli.gate.MainActivity;
import it.nicolapaoli.gate.R;
import it.nicolapaoli.gate.utils.Constants;

public class DialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, 0);

            }
        } else {
            dial();
        }

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "PReDial " , Toast.LENGTH_SHORT).show();
                    dial();
                } else {
                    Toast.makeText(this, getString(R.string.open_action_error_permission), Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void dial() {
        SharedPreferences preferences = getSharedPreferences(Constants.PHONE_PREFS, Context.MODE_PRIVATE);
        final String phoneNumberPref = preferences.getString(Constants.KEY_PHONE_NUMBER, null);

        Toast.makeText(this, "Dial " + phoneNumberPref, Toast.LENGTH_SHORT).show();
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    && phoneNumberPref != null) {

                    Uri number = Uri.parse("tel:" + phoneNumberPref);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, number);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

            Toast.makeText(this, "Calling " + phoneNumberPref, Toast.LENGTH_SHORT).show();
                    if (isIntentSafe) {
                        startActivity(callIntent);
                    }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "nee intent", Toast.LENGTH_SHORT).show();
        performTagOperations(intent);
    }

    private void performTagOperations(Intent intent){
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Toast.makeText(this, intent.getData().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
