package com.lilsmile.testplatformandroid.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends Activity implements View.OnClickListener, Constants {

    Button buttonGoLogin;
    EditText etLogin;
    EditText etPassword;

    Button buttonGoAnonymous;

    Button buttonGoSignup;
    EditText etLoginSignup;
    EditText etEmailSignup;
    EditText etPasswordSignup;
    EditText etRepeatPasswordSignup;




    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(MODE_PRIVATE);

        TabHost tabs = (TabHost)findViewById(R.id.tabHost);
        tabs.setup();
        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.login));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.signup));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        buttonGoLogin = (Button)findViewById(R.id.buttonGoLogin);
        etLogin = (EditText)findViewById(R.id.etLogin);
        etPassword = (EditText)findViewById(R.id.etPassword);

        buttonGoAnonymous = (Button)findViewById(R.id.loginAnonymous);

        buttonGoSignup = (Button)findViewById(R.id.buttonGoSignup);
        etLoginSignup = (EditText)findViewById(R.id.etsignupLogin);
        etEmailSignup = (EditText)findViewById(R.id.etsignupEmail);
        etPasswordSignup = (EditText)findViewById(R.id.etsignupPassword);
        etRepeatPasswordSignup = (EditText)findViewById(R.id.etsignupPasswordRepeat);

        buttonGoSignup.setOnClickListener(this);
        buttonGoAnonymous.setOnClickListener(this);
        buttonGoLogin.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonGoLogin: {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    String[] params = new String[3];
                    params[0] = etLogin.getText().toString();
                    params[1] = etLogin.getText().toString();
                    params[2] = LOGIN;
                    myAsyncTask.execute(params);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.buttonGoSignup: {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    String[] params = new String[3];
                    params[0] = etEmailSignup.getText().toString();
                    params[1] = etLoginSignup.getText().toString();
                    params[2] = etPasswordSignup.getText().toString();
                    myAsyncTask.execute(params);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.loginAnonymous:{
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    String[] params = new String[3];
                    params[0] = ANONYMOUS;
                    params[2] = ANONYMOUS;
                    myAsyncTask.execute(params);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String[] strings) {
            if (strings[2]==LOGIN)
            {
                String login = strings[0];
                String password = md5(strings[1]);

                JSONObject request = new JSONObject();
                try {
                    request.put(LOGIN, login);
                    request.put(PASSWORD, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url;
                HttpURLConnection connection = null;
                try {
                    url = new URL(LOGIN_STRING);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(request.toString().getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(request.toString());
                    wr.flush();
                    wr.close();

                    //Get response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    Log.wtf("tag", response.toString());
                    String[] result = new String[3];
                    result[0]=response.toString();
                    result[1]=login;
                    result[2]=password;
                    return result;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }


            } else if (strings[2]==ANONYMOUS)
            {
                JSONObject request = new JSONObject();
                try {
                    request.put(LOGIN, ANONYMOUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url;
                HttpURLConnection connection = null;
                try {
                    url = new URL(ANONYMOUS_STRING);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(request.toString().getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(request.toString());
                    wr.flush();
                    wr.close();

                    //Get response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    Log.wtf("tag", response.toString());
                    String[] result = new String[1];
                    result[0]=response.toString();
                    return result;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            } else
            {
                String email = strings[0];
                String login = strings[1];
                String password = md5(strings[2]);
                JSONObject request = new JSONObject();
                try {
                    request.put(EMAIL, email);
                    request.put(LOGIN, login);
                    request.put(PASSWORD, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                URL url;
                HttpURLConnection connection = null;
                try {
                    url = new URL(SIGNUP_STRING);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(request.toString().getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(request.toString());
                    wr.flush();
                    wr.close();

                    //Get response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    Log.wtf("tag", response.toString());
                    String[] result = new String[3];
                    result[0]=response.toString();
                    result[1]=login;
                    result[2]=password;
                    return result;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String[] param)
        {
            try {
                JSONObject jsonObject = new JSONObject(param[0]);
                String result = null;
                if (jsonObject.has(RESULT))
                {
                    result = (String) jsonObject.get(RESULT);
                }
                if (result!=null)
                {
                    switch (result)
                    {
                        case BAD_EMAIL:
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.badEmail), Toast.LENGTH_LONG).show();
                            break;
                        }
                        case BAD_LOGIN:
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.badLogin), Toast.LENGTH_LONG).show();
                            break;
                        }
                        case WRONG_LOGIN:
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.wrongLogin), Toast.LENGTH_LONG).show();
                            break;
                        }
                        case WRONG_PASSWORD:
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.wrongPassword), Toast.LENGTH_LONG).show();
                            break;
                        }
                        case SMTH_IS_WRONG:
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.smthIsWrong), Toast.LENGTH_LONG).show();
                            break;
                        }
                        case OK:
                        {
                            preferences.edit().putString(LOGIN, param[1]).putString(PASSWORD,param[2]).commit();
                            MyAsyncTask myAsyncTask = new MyAsyncTask();
                            myAsyncTask.execute(param[1], param[2], LOGIN);
                            break;
                        }
                    }
                } else
                {
                    String token = jsonObject.getString(TOKEN);
                    preferences.edit().putString(TOKEN, token).commit();
                    Log.wtf("token1", preferences.getString(TOKEN, ""));
                    Toast.makeText(getApplicationContext(), "Token="+token, Toast.LENGTH_LONG).show();// todo add jump to next screen
                    Intent intent = new Intent(MainActivity.this, PerconalCabinet.class);
                    intent.putExtra(TOKEN, token);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private  final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
