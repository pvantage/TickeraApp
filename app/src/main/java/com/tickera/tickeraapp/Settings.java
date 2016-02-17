package com.tickera.tickeraapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;




public class Settings extends Fragment implements ApiCallListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView saveDetails;
    private EditText url;
    private EditText apikey;
    private ToggleButton toggleButton;

    private FrameLayout view;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (FrameLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        url = (EditText) view.findViewById(R.id.url);
        apikey = (EditText) view.findViewById(R.id.apikey);
        toggleButton = (ToggleButton) view.findViewById(R.id.autologin);
        saveDetails = (ImageView) view.findViewById(R.id.savedetails);

        translate();


        toggleButton.setChecked(Globals.autosave);
        if (!Globals.autosave){
            //toggleButton.setTrackDrawable(getResources().getDrawable(R.drawable.track_no));
        }
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Globals.setAutosave(b, Settings.this.getActivity());
                if (b){
                    //toggleButton.setTrackResource(R.drawable.track);
                    //toggleButton.setTrackDrawable(getResources().getDrawable(R.drawable.track));
                }else{
                    //toggleButton.setTrackDrawable(getResources().getDrawable(R.drawable.track_no));
                }
            }
        });

        url.setText(Globals.url);
        apikey.setText(Globals.key);

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!url.getText().toString().equals("") && !apikey.getText().toString().equals("")) {
                    Globals.showProgress(Settings.this.getActivity());
                    ApiCall ac = new ApiCall(url.getText().toString() + "/tc-api/" + apikey.getText().toString() + "/check_credentials", Settings.this, 1);
                }
            }
        });

        url.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v==url && !hasFocus) {
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        apikey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v==apikey && !hasFocus) {
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    private void translate(){
        TextView event_url = (TextView)view.findViewById(R.id.event_url);
        event_url.setText(Globals.getTranslation("WORDPRESS_INSTALLATION_URL"));
        TextView apKey = (TextView)view.findViewById(R.id.api_key);
        apKey.setText(Globals.getTranslation("API_KEY"));
        TextView al = (TextView)view.findViewById(R.id.auto_login);
        al.setText(Globals.getTranslation("AUTO_LOGIN"));
        TextView sd = (TextView)view.findViewById(R.id.save_details);
        sd.setText(Globals.getTranslation("SIGN_IN"));

    }

    @Override
    public void call(String result, boolean success, int id) {
        Globals.cancelProgressDialog();
        if (success && id==1){
            try {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("pass")) {
                    Globals.url = url.getText().toString();
                    Globals.key = apikey.getText().toString();
                    Globals.setPreferences(this.getActivity(), "key", Globals.key);
                    Globals.setPreferences(this.getActivity(), "url", Globals.url);
                    //call it after translate is loaded
                    //((MainActivity2Activity) this.getActivity()).onNavigationDrawerItemSelected(0);
                    ApiCall ac = new ApiCall(Globals.url+"/tc-api/" + Globals.key + "/translation/", this, 17);
                }else{
                    upErrorDialog();
                }
            }catch (JSONException jex){
                upErrorDialog();
                jex.printStackTrace();
            }
        }else if (id == 1){
            upErrorDialog();
        }else if (id == 17){
            if (success){
                Globals.setPreferences(this.getActivity(), "translate", result);
            }
            ((MainActivity2Activity) this.getActivity()).onNavigationDrawerItemSelected(0);
        }
    }

    private void upErrorDialog(){
        Globals.showClassicDialog(this.getActivity(), Globals.getTranslation("ERROR"), Globals.getTranslation("API_KEY_LOGIN_ERROR"));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
