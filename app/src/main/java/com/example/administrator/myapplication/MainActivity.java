package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends ActionBarActivity {


    public static final String USER_PROFILE1 = "http://graph.facebook.com/";// 1420370681588319+
    public static final String USER_PROFILE2 = "/picture?type=large";

    LoginButton loginButton;

    String TAG = "tag";
    UiLifecycleHelper uiHelper;
    Context mContext;
    Activity mActivity;

    String APP_ID;
    Facebook fb;
    boolean flag = false;
    public static boolean isFbDataReceived = false, isServiceCalled = false;
    String accessToken;
    Date accessExpire;
    public static boolean isFbclicked = false;
    ArrayList<String> friends_list;



    private Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState state, Exception exception) {

            onSessionStateChange(session, state, exception);

        }

    };


    @Override
    public void onStart() {
        super.onStart();
        flag = false;
        isServiceCalled = false;
        isFbDataReceived = false;
        Session session = Session.getActiveSession();
    }

    @SuppressWarnings("deprecation")
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        flag = true;

        if (state.isOpened()) {
            accessToken = session.getAccessToken();
            accessExpire = session.getExpirationDate();

            // showProgress();

            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                public void onCompleted(GraphUser user, Response response) {
                    Log.v("come here", " " + session);
                    session.close();
                }
            });
        } else if (state.isClosed()) {
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this;
        mActivity = this;
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        loginButton = (LoginButton) findViewById(R.id.authButton);

        // loginButton.setBackgroundResource(R.drawable.facebook_icon);

        // loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
        // null);

		/*
		 * loginButton.setReadPermissions(Arrays.asList("user_likes",
		 * "user_status","public_profile","email"));
		 */
        loginButton.setReadPermissions(Arrays.asList("user_likes", "user_friends", "user_status", "public_profile", "email"/*,
                "read_friendlists"*/));
		/*
		 * user_checkins friends_checkins read_friendlists manage_friendlists
		 * publish_checkins
		 */

        APP_ID = getString(R.string.facebook_app_id);
        fb = new Facebook(APP_ID);
		/*
		 * mAsyncRunner = new AsyncFacebookRunner(fb); friends_list = new
		 * ArrayList<String>();
		 */


        loginButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
				/*
				 * try {
				 * Session.getActiveSession().closeAndClearTokenInformation(); }
				 * catch (Throwable e) { e.printStackTrace(); }
				 */
                // TODO Auto-generated method stub
                boolean b = session.isOpened();
                if (session.isOpened()) {

                    fetchUserDetails(session);
                    // requestMyAppFacebookFriends(session);
                } else {
                    Log.v("session is", "closed");
                }
            }
        });


        getHashCode();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        final Session session = Session.getActiveSession();
        if (fb.isSessionValid()) {
            accessToken = session.getAccessToken();
            accessExpire = session.getExpirationDate();

            if (Session.getActiveSession().isOpened()) {

//                showProgress();
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    public void onCompleted(GraphUser user, Response response) {
                        Log.v("come here", " " + session);
						/* session.close(); */

                        getUserFacebookData(user);
                    }
                });
            }
        }

        else if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        } else {
            if (flag) {
                Session s = new Session(MainActivity.this);
                Session.setActiveSession(s);
                s.openForRead(new Session.OpenRequest(this).setCallback(callback).setPermissions(Arrays.asList("public_profile", "email")));
            }

        }
        uiHelper.onResume();
    }


    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                // clear your preferences if saved

            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
            // clear your preferences if saved
        }
        // Session.setActiveSession(null);
    }



    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void fetchUserDetails(final Session session) {
        Log.i("Session is", "Access Token" + session.getAccessToken());

        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // some code
                getUserFacebookData(user);

                requestMyAppFacebookFriends(session);

            }
        }).executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }




    @SuppressWarnings("deprecation")
    private void getUserFacebookData(GraphUser user) {

        if (user != null) {

            try {


                String userName = (String) user.getProperty("name");
				/*
				 * String profilePic = "http://graph.facebook.com/" + userName +
				 * "/picture?type=large";
				 */
//                String firstName = user.getFirstName();
//                String lastName = user.getLastName();
//                String gender = user.getProperty("gender").toString();
                String email = user.getProperty("email").toString();
                String fb_id = user.getId();


                uiHelper.onDestroy();


            } catch (Exception e) {

                e.printStackTrace();

            }
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestMyAppFacebookFriends(Session session) {
        Request friendsRequest = createRequest(session);
        friendsRequest.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) { }
        });
        friendsRequest.executeAsync();
    }

    private Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "me/friends", null);

        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[] { "id", "name", "picture" };
        fields.addAll(Arrays.asList(requiredFields));

        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);

        return request;
    }

    private List<GraphUser> getResults(Response response) {
        Log.e("Response = ", "Fb Response = " + response);
        GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
        GraphObjectList<GraphObject> data = multiResult.getData();
        return data.castToListOf(GraphUser.class);
    }


    // This code used for generating SHA1 key which is used in facebook app
    // project.
    void getHashCode() {

        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.example.administrator.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashval = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                Log.i("SHA1 - Hashkey ===   ",hashval);

            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

}
