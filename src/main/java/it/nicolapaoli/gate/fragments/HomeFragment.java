package it.nicolapaoli.gate.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import it.nicolapaoli.gate.R;
import it.nicolapaoli.gate.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ImageButton phoneButton;
    private TextView phoneNumber;
    private TextView phoneDescription;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        phoneButton = fragment.findViewById(R.id.phone_button);
        phoneNumber = fragment.findViewById(R.id.phone_number);
        phoneDescription = fragment.findViewById(R.id.phone_descritpion);

        loadViews();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    public void loadViews(){
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PHONE_PREFS, Context.MODE_PRIVATE);
        final String phoneNumberPref = preferences.getString(Constants.KEY_PHONE_NUMBER, null);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            phoneButton.setEnabled(false);
            phoneDescription.setText(getActivity().getString(R.string.open_action_error_permission));
        } else if (phoneNumberPref != null) {
            phoneNumber.setText(phoneNumberPref);
            phoneDescription.setText(getActivity().getString(R.string.open_action_description));
            phoneButton.setEnabled(true);
            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number = Uri.parse("tel:"+phoneNumberPref);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, number);

                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(callIntent);
                    }
                }
            });
        } else {
            phoneButton.setEnabled(false);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
