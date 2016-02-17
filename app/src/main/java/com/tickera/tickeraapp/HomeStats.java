package com.tickera.tickeraapp;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class HomeStats extends Fragment implements ApiCallListener, Activity2FragmentShit{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout view;
    private TextView soldOnes;
    private TextView checkedOnes;
    private ImageView camera;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeStats.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeStats newInstance(String param1, String param2) {
        HomeStats fragment = new HomeStats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeStats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HOME", "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (!mParam1.equals("")){
            String[] resArr = mParam1.split("\\|");
            if (resArr.length > 0) {
                Globals.showProgress(this.getActivity());
                ApiCall check = new ApiCall(Globals.url + "/tc-api/" + Globals.key + "/check_in/"+resArr[resArr.length-1], this, 2);
            }
        }

        translate();

        ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/event_essentials", this, 1);
    }

    private void translate(){
        if (view == null) return;
        VerticalTextView sold = (VerticalTextView)view.findViewById(R.id.sold_text);
        VerticalTextView checked = (VerticalTextView)view.findViewById(R.id.checkedin_text);
        sold.setText(Globals.getTranslation("SOLD_TICKETS"));
        checked.setText(Globals.getTranslation("CHECKED_IN_TICKETS"));
    }

    @Override
    public void call(String result, boolean success, int id) { // id 1 stats    id 2 check in res
        Globals.cancelProgressDialog();
        if (success){
            try {
                JSONObject job = new JSONObject(result);
                if (job.has("checked_tickets") && job.has("sold_tickets")) {
                    checkedOnes.setText("0");
                    soldOnes.setText("0");
                    int checked = job.getInt("checked_tickets");
                    int sold = job.getInt("sold_tickets");
                    Globals.eventName = job.getString("event_name");
                    Globals.eventDate = job.getString("event_date_time");
                    Globals.eventLocation = job.getString("event_location");
                    Globals.checked = checked;
                    Globals.sold = sold;
                    checkedOnes.setText(Integer.toString(checked));
                    soldOnes.setText(Integer.toString(sold));
                }else if (job.has("checksum") && job.has("status") && job.has("pass")){
                    if (job.getBoolean("status") && job.getBoolean("pass")){
                        Globals.showSuccess(this.getActivity());
                    }else{
                        Globals.showFailCheck(this.getActivity());
                    }
                }
            }catch (JSONException jex){
                if (result.lastIndexOf("Ticket does not exist")!=-1){
                    Globals.showFailCheck(this.getActivity());
                }
                jex.printStackTrace();
            }
        }else{
            Globals.cancelProgressDialog();
        }
    }

    @Override
    public void update() {
        ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/event_essentials", this, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (FrameLayout)inflater.inflate(R.layout.fragment_home_stats, container, false);

        soldOnes = (TextView)view.findViewById(R.id.tickets_sold);
        checkedOnes = (TextView)view.findViewById(R.id.checked_in_tickets);
        camera = (ImageView)view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CAMERA", "rrrrrr");
                ((MainActivity2Activity)getActivity()).doScanner();
            }
        });

        checkedOnes.setText(Integer.toString(Globals.checked));
        soldOnes.setText(Integer.toString(Globals.sold));

        translate();

        return view;
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
            translate();
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

}
