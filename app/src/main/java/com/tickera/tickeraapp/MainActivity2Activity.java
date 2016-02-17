package com.tickera.tickeraapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;


public class MainActivity2Activity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //private DrawerLayout drawer;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    //TextView scanText;
    // scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Globals.getPreferences(this, true);
        if (!Globals.autosave){
            Globals.setPreferences(this, "key", "");
            Globals.key = "";
        }

        Log.d("MAIN", "onCreate");

        setContentView(R.layout.activity_main_activity2);

        //drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //FrameLayout preview = (FrameLayout)findViewById(R.id.container);
        //preview.addView(mPreview);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = Globals.getTranslation("APP_TITLE");

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ActionBar ab = getSupportActionBar();
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff2525));
        this.getSupportActionBar().setIcon(new ColorDrawable(android.R.color.transparent));
        this.getSupportActionBar().setTitle(Globals.getTranslation("APP_TITLE"));

        //drawer.openDrawer(Gravity.START);
    }

    public void doScanner(){

        if (mCamera == null)
            mCamera = getCameraInstance();

        if (autoFocusHandler == null)
            autoFocusHandler = new Handler();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        //if (mPreview == null)
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);

        FrameLayout preview = (FrameLayout)findViewById(R.id.container);
        preview.removeAllViews();
        preview.addView(mPreview);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private int currentPosition=0;
    private String scannedData = "";

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Globals.getPreferences(this);

        if (Globals.key.equals("")) currentPosition = 2;
        else currentPosition = position;

        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = HomeStats.newInstance(scannedData,"");
                break;
            case 1:
                fragment = List.newInstance(scannedData,"");
                break;
            case 2:
                fragment = Settings.newInstance("","");
                break;
            case 3:
                fragment = Info.newInstance();
                break;
            case 4:
                signOut();
                fragment = Settings.newInstance("","");
                //return;
                break;
        }
        scannedData = "";

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        restoreActionBar();
    }

    private void signOut(){
        //Globals.setPreferences(this, "url", "");
        Globals.setPreferences(this, "key", "");
        Globals.resetTranslate(this);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString(R.string.title_section1);
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                break;
            case 3:
                //mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0x252525));
        actionBar.setTitle(Globals.getTranslation("APP_TITLE"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main_activity2, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            //Log.d("SCAN RESULT", Integer.toString(result));

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mPreview.getHolder().removeCallback(mPreview);
                mCamera.stopPreview();
                //mCamera.release();

                SymbolSet syms = scanner.getResults();
                boolean noMore = false;
                for (Symbol sym : syms) {
                    //scanText.setText("barcode result " + sym.getData());
                    if (!noMore) {
                        noMore = true;
                        Log.d("SCANNED", sym.getData());
                        //barcodeScanned = true;
                        scannedData = sym.getData();
                        onNavigationDrawerItemSelected(currentPosition);
                    }
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Info extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Info newInstance() {
            Info fragment = new Info();
            //Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        public Info() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_activity2, container, false);
            return rootView;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static boolean canGoBack = true;
    public static BackerCallback backerCallback = null;
    @Override
    public void onBackPressed() {
        if (canGoBack)
            super.onBackPressed();
        else if (backerCallback!=null){
            backerCallback.call();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return true;
    }

}
