package com.raflyprayogo.dkos.gcm;

/**
 * Created by Cendana on 1/25/2016.
 */
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.raflyprayogo.dkos.Handler.AppConfig;
import com.raflyprayogo.dkos.R;
import com.raflyprayogo.dkos.Handler.RequestHandler;
import com.raflyprayogo.dkos.Handler.SQLiteHandler;
import com.raflyprayogo.dkos.Handler.SessionManager;
import com.raflyprayogo.dkos.Login.LoginActivity;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    SQLiteHandler db;
    String member_id;
    private SessionManager session;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            //get user logged in
            db = new SQLiteHandler(getApplicationContext());
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();
            member_id = user.get("uid");

            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.

            String extras = intent.getStringExtra("from");
            if(extras.equals("login")){
                sendRegistrationToServer(token);
            }else if(extras.equals("main")){
                checkLoginToken(token);
            }else if(extras.equals("instance")){

            }

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void checkLoginToken(final String token) {
        // Add custom implementation, as needed.
        class SendToken extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("1")) {
                    Toast.makeText(getApplicationContext(), R.string.double_login, Toast.LENGTH_LONG).show();
                    session.setLogin(false);
                    db.deleteUsers();

                    Thread timerThread = new Thread(){
                        public void run(){
                            try{
                                sleep(2000);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }finally{
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                System.exit(0);
                            }
                        }
                    };
                    timerThread.start();
//                    // Launching the login activity
//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }else if(s.equals("2")){
                    sendRegistrationToServer(token);
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("member_id", member_id);
                param.put("token", token);
                String result = rh.sendPostRequest(AppConfig.URL_CHECK_TOKEN, param);
                return result;
            }
        }
        SendToken u = new SendToken();
        u.execute();
    }

    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        class SendToken extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("member_id", member_id);
                param.put("token", token);
                String result = rh.sendPostRequest(AppConfig.URL_SENT_TOKEN, param);
                return result;
            }
        }
        SendToken u = new SendToken();
        u.execute();
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
