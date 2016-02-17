package com.tickera.tickeraapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aber on 17/05/15.
 */
public class Globals {

    public static String logged_user = "trt";

    public static String url = "http://tickera.com/tstt/";
    public static String key = "";
    public static String eventName = "";
    public static String eventDate = "";
    public static String eventLocation = "";
    public static int checked = 0;
    public static int sold = 0;

    public static JSONObject translateJson;

    public static boolean autosave = true;

    private static ProgressDialog _progressDialog=null;
    public static void showProgress(Context context){
        if (_progressDialog != null) return;
        ProgressDialog pd = ProgressDialog.show(context,"","");
        _progressDialog = pd;
        pd.setCancelable(false);
    }
    public static void cancelProgressDialog(){
        if (_progressDialog!=null){
            _progressDialog.hide();
            _progressDialog.dismiss();
            _progressDialog = null;
        }
    }

    public static void showSuccess(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_dialog);

        TextView title = (TextView)dialog.findViewById(R.id.title);
        TextView msg = (TextView)dialog.findViewById(R.id.msg);

        title.setText(Globals.getTranslation("SUCCESS"));
        msg.setText(Globals.getTranslation("SUCCESS_MESSAGE"));

        TextView tv = (TextView)dialog.findViewById(R.id.dialogText);
        tv.setText(Globals.getTranslation("OK"));

        LinearLayout dialogButton = (LinearLayout) dialog.findViewById(R.id.okbtn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUCCESS OK", "OK");
                dialog.hide();
                dialog.dismiss();
                //dialog = null;
            }
        });

        dialog.show();
    }

    public static void showFailCheck(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_dialog);

        ImageView iv = (ImageView)dialog.findViewById(R.id.image);
        iv.setImageDrawable(context.getResources().getDrawable(R.drawable.fail));

        LinearLayout dialogButton = (LinearLayout) dialog.findViewById(R.id.okbtn);
        TextView tv = (TextView)dialog.findViewById(R.id.dialogText);
        tv.setText(Globals.getTranslation("OK"));

        TextView title = (TextView)dialog.findViewById(R.id.title);
        TextView msg = (TextView)dialog.findViewById(R.id.msg);

        title.setText(Globals.getTranslation("FAIL"));
        msg.setText(Globals.getTranslation("ERROR_MESSAGE"));

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FAIL OK", "OK");
                dialog.hide();
                dialog.dismiss();
                //dialog = null;
            }
        });

        dialog.show();
    }

    public static void getPreferences(Context context){
        getPreferences(context, false);
    }

    public static void getPreferences(Context context, boolean firstTime){
        SharedPreferences sp = context.getSharedPreferences("tickera", Context.MODE_PRIVATE);
        key = sp.getString("key", "");
        url = sp.getString("url", "");
        autosave = sp.getBoolean("autosave", true);
        try {
            String trs = sp.getString("translate", defaultTranslate);
// uncomment for reset language after logout
//          if (firstTime) {
//              translateJson = new JSONObject(((autosave) ? trs : defaultTranslate));
//              setPreferences(context, "translate", ((autosave)? trs : defaultTranslate));
//          }
//            else
              translateJson = new JSONObject(trs);
        }catch (JSONException jex){
            jex.printStackTrace();
        }
    }

    public static void setPreferences(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences("tickera", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (key.equals("translate")) {
            JSONObject oldt = translateJson;
            try {
                translateJson = new JSONObject(value);
            }catch (JSONException jex){
                translateJson = oldt;
                jex.printStackTrace();
                return;
            }
        }
        ed.putString(key, value);
        if (key.equals("key")) Globals.key = value;

        ed.commit();
    }

    public static void showClassicDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void setAutosave(boolean b, Context context){
        SharedPreferences sp = context.getSharedPreferences("tickera", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("autosave", b);
        Globals.autosave = b;
        ed.commit();
    }

    public static String getTranslation(String key){
        try {
            String ret = translateJson.getString(key);
            return ret;
        }catch (JSONException jex){}
        return "";
    }

    public static void resetTranslate(Context c){
    // uncomment for reset language after logout
        //setPreferences(c, "translate", defaultTranslate);
        getPreferences(c);
    }

    private static final String defaultTranslate = "{\n" +
            "\"WORDPRESS_INSTALLATION_URL\": \"EVENT URL (WORDPRESS INSTALLATION URL)\",\n" +
            "\"API_KEY\": \"APY KEY\",\n" +
            "\"AUTO_LOGIN\": \"AUTO LOGIN\",\n" +
            "\"SIGN_IN\": \"SIGN IN\",\n" +
            "\"SOLD_TICKETS\": \"SOLD TICKETS\",\n" +
            "\"CHECKED_IN_TICKETS\": \"CHECKED IN TICKETS\",\n" +
            "\"HOME_STATS\": \"HOME - STATS\",\n" +
            "\"LIST\": \"LIST\",\n" +
            "\"SIGN_OUT\": \"SIGN OUT\",\n" +
            "\"CANCEL\": \"CANCEL\",\n" +
            "\"SEARCH\": \"Search\",\n" +
            "\"ID\": \"ID\",\n" +
            "\"PURCHASED\": \"Purchased\",\n" +
            "\"CHECKINS\": \"CHECKINS\",\n" +
            "\"CHECK_IN\": \"CHECK IN\",\n" +
            "\"SUCCESS\": \"SUCCESS\",\n" +
            "\"SUCCESS_MESSAGE\": \"TICKET WITH THIS CODE HAS BEEN CHECKED\",\n" +
            "\"OK\": \"OK\",\n" +
            "\"ERROR\": \"ERROR\",\n" +
            "\"ERROR_MESSAGE\": \"THERE IS NO TICKET WITH THIS CODE\",\n" +
            "\"PASS\": \"Pass\",\n" +
            "\"FAIL\": \"Fail\",\n" +
            "\"ERROR_LOADING_DATA\": \"Error loading data.\",\n" +
            "\"API_KEY_LOGIN_ERROR\": \"An error occurred. Please check your internet connection, URL and the API Key entered.\",\n" +
            "\"APP_TITLE\": \"SIMPLE TICKETING SYSTEM\"\n" +
            "}";

}
