package it.nicolapaoli.gate.fragments;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import it.nicolapaoli.gate.MainActivity;
import it.nicolapaoli.gate.R;
import it.nicolapaoli.gate.utils.Constants;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private EditText phoneNumber;
    private ImageButton pickContact;
    private ImageButton savePhoneNumber;
    private ImageButton nfcWrite;

    public SettingsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_settings, container, false);

        phoneNumber = fragment.findViewById(R.id.phone_num_edit);
        pickContact = fragment.findViewById(R.id.phone_num_pick);
        savePhoneNumber = fragment.findViewById(R.id.phone_num_save);
        nfcWrite = fragment.findViewById(R.id.nfcWrite);

        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PHONE_PREFS, Context.MODE_PRIVATE);
        final String phoneNumberPref = preferences.getString(Constants.KEY_PHONE_NUMBER, null);

        phoneNumber.setText(phoneNumberPref);
        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    savePhoneNumber.setImageDrawable(getActivity().getDrawable(R.drawable.ic_save_black_24dp));
                    savePhoneNumber.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                    savePhoneNumber.setImageTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.white)));
                }
            }
        });

        savePhoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String phoneNumberTest = phoneNumber.getText().toString();
                SharedPreferences.Editor preferences = getActivity().getSharedPreferences(Constants.PHONE_PREFS, Context.MODE_PRIVATE).edit();
                preferences.putString(Constants.KEY_PHONE_NUMBER, phoneNumberTest);
                preferences.commit();
                Toast.makeText(getActivity(), getActivity().getString(R.string.message_new_phone_number) + phoneNumberTest, Toast.LENGTH_LONG).show();
                ((MainActivity) getActivity()).loadViewPager(1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    savePhoneNumber.setImageDrawable(getActivity().getDrawable(R.drawable.ic_done_black_24dp));
                    savePhoneNumber.setBackgroundColor(Color.TRANSPARENT);
                    savePhoneNumber.setImageTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorAccent)));
                }
            }
        });

        nfcWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).enableTagWriteMode();

                new AlertDialog.Builder(getActivity()).setTitle("Touch tag to write")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                ((MainActivity) getActivity()).disableTagWriteMode();
                            }

                        }).create().show();
            }
        });

        return fragment;
    }



    public static final int RESULT_PICK_CONTACT = 1;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }


    public void contactPicked(Intent data){
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);
            // Set the value to the textviews
            phoneNumber.setText(phoneNo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                savePhoneNumber.setImageDrawable(getActivity().getDrawable(R.drawable.ic_save_black_24dp));
                savePhoneNumber.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                savePhoneNumber.setImageTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.white)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
