package com.tickera.tickeraapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aber on 19/05/15.
 */
public class DetailsListAdapter extends ArrayAdapter<JSONArray> {

    private Context context;

    private TextView key;
    private TextView value;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONArray json = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.details_item, parent, false);
        }

        key = (TextView) convertView.findViewById(R.id.key);
        value = (TextView) convertView.findViewById(R.id.value);
        try {
            key.setText(json.getString(0)+": ");
            value.setText(json.getString(1));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return convertView;
    }

    public DetailsListAdapter(Context context, ArrayList<JSONArray> objects) {
        super(context, R.layout.details_item, objects);
        this.context = context;
    }
}
