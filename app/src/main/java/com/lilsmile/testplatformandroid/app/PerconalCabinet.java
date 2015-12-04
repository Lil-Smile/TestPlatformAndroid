package com.lilsmile.testplatformandroid.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PerconalCabinet extends Activity implements View.OnClickListener, Constants {

    Button buttonCreateTest;
    Button buttonChooseTest;
    Button buttonLogout;
    Button buttonStat;
    SharedPreferences preferences;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perconal_cabinet);

        preferences = getPreferences(MODE_PRIVATE);

        buttonChooseTest = (Button)findViewById(R.id.buttonChooseTest);
        buttonCreateTest = (Button)findViewById(R.id.buttonCreateTest);
        buttonLogout = (Button)findViewById(R.id.buttonLogout);
        buttonStat = (Button)findViewById(R.id.buttonStat);

        buttonLogout.setOnClickListener(this);
        buttonCreateTest.setOnClickListener(this);
        buttonChooseTest.setOnClickListener(this);
        buttonStat.setOnClickListener(this);

        token = getIntent().getStringExtra(TOKEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perconal_cabinet, menu);
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
            case R.id.buttonChooseTest:
            {
                Intent intent = new Intent(PerconalCabinet.this, ChooseTest.class);
                intent.putExtra(TOKEN, token);
                startActivity(intent);
                break;
            }
            case R.id.buttonCreateTest:
            {
                Intent intent = new Intent(PerconalCabinet.this, CreateTest.class);
                intent.putExtra(TOKEN, token);
                startActivity(intent);
                break;
            }
            case R.id.buttonLogout:
            {
                String token = preferences.getString(TOKEN,"");
                if (token.equals(""))
                {
                    Intent intent = new Intent(PerconalCabinet.this, MainActivity.class);
                    startActivity(intent);
                } else
                {
                    String[] param = new String [1];
                    param[0]=token;
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(param);
                }
                break;
            }
            case R.id.buttonStat:
            {
                Intent intent = new Intent(PerconalCabinet.this, Stat.class);
                intent.putExtra(TOKEN, token);
                startActivity(intent);
                break;
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            JSONObject request = new JSONObject();
            try {
                request.put(TOKEN, token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(LOGOUT_STRING);
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

                return response.toString();


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

        @Override
        protected void onPostExecute(String param)
        {
            JSONObject jsonObject=null;
            try {
                jsonObject = new JSONObject(param);

            if (jsonObject.has(RESULT))
            {
                String result = (String) jsonObject.get(RESULT);
                if (RESULT.equals(OK))
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.goodBye), Toast.LENGTH_SHORT).show();
                    preferences.edit().remove(TOKEN).commit();
                    Intent intent = new Intent(PerconalCabinet.this, MainActivity.class);
                    startActivity(intent);
                }
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
