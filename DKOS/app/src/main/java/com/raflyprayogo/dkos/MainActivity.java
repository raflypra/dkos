package com.raflyprayogo.dkos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.raflyprayogo.dkos.Handler.AppConfig;
import com.raflyprayogo.dkos.Handler.ConnectionHelper;
import com.raflyprayogo.dkos.Handler.SQLiteHandler;
import com.raflyprayogo.dkos.Handler.SessionManager;
import com.raflyprayogo.dkos.ImageHandler.CircleTransform;
import com.raflyprayogo.dkos.Login.LoginActivity;
import com.raflyprayogo.dkos.gcm.QuickstartPreferences;
import com.raflyprayogo.dkos.gcm.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;
    private SQLiteHandler db;
    CoordinatorLayout coordinatorLayout;
    private SessionManager session;
    private String member_id;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private FloatingActionButton fab,fab1,fab2,fab3,fab4,fab5;
    private TextView txtName, txtPosition, fab1_tv,fab2_tv,fab3_tv,fab4_tv, fab5_tv;
    private View fade_white;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    boolean firstBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //================= Start get user login =====================
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        member_id = user.get("uid");
        //================= End get user login =======================

        //================= Start drawer config ======================

        String name = user.get("name");
        String picture = user.get("picture");
        String number = user.get("username");

        // Displaying the user details on the screen
        TextView txtName = (TextView)findViewById(R.id.textUser);
        txtName.setText(name);

        TextView txtPosition = (TextView)findViewById(R.id.textUserNumber);
        txtPosition.setText(number);

        ImageView imgUser = (ImageView) findViewById(R.id.imgUser);
        ImageView drawer_bg = (ImageView) findViewById(R.id.drawer_bg);
        Glide.with(getApplicationContext())
                .load(AppConfig.URL_GetImageUser + picture)
                .transform(new CircleTransform(getApplicationContext()))
                .into(imgUser);

        Glide.with(getApplicationContext())
                .load(R.drawable.drawer_bg)
                .into(drawer_bg);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //================ End drawer config ===========================

        //================ Start main frame config ================

        fade_white      = (View)findViewById(R.id.fade_white);
        fab             = (FloatingActionButton)findViewById(R.id.fab);
        fab1            = (FloatingActionButton)findViewById(R.id.fab1);
        fab2            = (FloatingActionButton)findViewById(R.id.fab2);
        fab3            = (FloatingActionButton)findViewById(R.id.fab3);
        fab4            = (FloatingActionButton)findViewById(R.id.fab4);
        fab2_tv         = (TextView)findViewById(R.id.fab2_tv);
        fab1_tv         = (TextView)findViewById(R.id.fab1_tv);
        fab3_tv         = (TextView)findViewById(R.id.fab3_tv);
        fab4_tv         = (TextView)findViewById(R.id.fab4_tv);
        fab_open        = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close       = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward  = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fade_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Thread settingTitle = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            setTitle("Dashboard");
                        }
                    });
                }
            }
        };
        settingTitle.start();

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        Fragment fragment = new com.raflyprayogo.dkos.fragmentExpense.ItemFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        //================ End main frame config ==================


        //================ Other Action ========================
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra("from", "main");
            startService(intent);
        }

        //================== Handle no connection =======================
        IntentFilter filter = new IntentFilter(CONNECTIVITY_CHANGE_ACTION);
        this.registerReceiver(mChangeConnectionReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChangeConnectionReceiver != null) {
            this.unregisterReceiver(mChangeConnectionReceiver);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(id == R.id.nav_dashboard){
            setTitle("Dashboard");
        } else if (id == R.id.nav_expense) {
            fragment = new com.raflyprayogo.dkos.fragmentExpense.ItemFragment();
            setTitle("Pengeluaran");
        }else if (id == R.id.nav_income) {
            setTitle("Pemasukan");
        }else if (id == R.id.nav_wishlist) {
            setTitle("Wishlist Belanja");
        }else if (id == R.id.nav_forum) {
            setTitle("Forum");
        }else if (id == R.id.nav_savings) {
            setTitle("Tabungan");
        }else if (id == R.id.nav_logmonth) {
            setTitle("Log Bulanan");
        }else if (id == R.id.nav_logout){
            logoutUser();
        } else if(id == R.id.nav_connection){
            if(!ConnectionHelper.isConnectedOrConnecting(getApplicationContext())) {
                Toast.makeText(getApplication(), "Tidak ada koneksi", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplication(), "Terkoneksi", Toast.LENGTH_LONG).show();
            }
        }

        if (fragment != null)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!firstBackPressed) {
                firstBackPressed = true;
                Toast.makeText(MainActivity.this, R.string.exit_app, Toast.LENGTH_SHORT).show();
                Thread timerThread = new Thread(){
                    public void run(){
                        try{
                            sleep(2500);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            firstBackPressed = false;
                        }
                    }
                };
                timerThread.start();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void logoutUser() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.logout)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        session.setLogin(false);

                        db.deleteUsers();

                        // Launching the login activity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void checkConnectivity(){
        if(!ConnectionHelper.isConnectedOrConnecting(getApplicationContext())) {
            Snackbar snackbar_errcon = Snackbar.make(coordinatorLayout, R.string.no_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkConnectivity();
                }
            });
            snackbar_errcon.setActionTextColor(Color.WHITE);
            View sbView = snackbar_errcon.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar_errcon.show();
        }else {
        }
    }

    private final BroadcastReceiver mChangeConnectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (CONNECTIVITY_CHANGE_ACTION.equals(action))
            {
                //check internet connection
                if (!ConnectionHelper.isConnectedOrConnecting(getApplicationContext())) {
                    if (context != null) {
                        boolean show = false;
                        if(ConnectionHelper.lastNoConnectionTs == -1) {//first time
                            show = true;
                            ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                        }else {
                            if(System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            }
                        }

                        if(show && ConnectionHelper.isOnline) {
                            Snackbar snackbar_errcon = Snackbar.make(coordinatorLayout, R.string.no_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    checkConnectivity();
                                }
                            });
                            snackbar_errcon.setActionTextColor(Color.WHITE);
                            View sbView = snackbar_errcon.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar_errcon.show();snackbar_errcon.show();
                            ConnectionHelper.isOnline = false;
                        }
                    }
                }else {
                    ConnectionHelper.isOnline = true;
                }
            }
        }
    };

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fade_white.setVisibility(View.INVISIBLE);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab2_tv.startAnimation(fab_close);
            fab1_tv.startAnimation(fab_close);
            fab3_tv.startAnimation(fab_close);
            fab4_tv.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fade_white.setVisibility(View.VISIBLE);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab2_tv.startAnimation(fab_open);
            fab1_tv.startAnimation(fab_open);
            fab3_tv.startAnimation(fab_open);
            fab4_tv.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }
}
