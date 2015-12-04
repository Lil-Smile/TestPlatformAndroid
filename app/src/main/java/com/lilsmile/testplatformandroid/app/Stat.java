package com.lilsmile.testplatformandroid.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Stat extends Activity implements Constants, View.OnClickListener{

    String token;
    GraphView graphViewPassed, graphViewCreated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        token = getIntent().getStringExtra(TOKEN);

        graphViewPassed = (GraphView) findViewById(R.id.graphPassed);
        graphViewCreated = (GraphView) findViewById(R.id.graphCreated);

        GetDataForGraphAsyncTask getDataForGraphAsyncTask = new GetDataForGraphAsyncTask();
        String[] params = new String[1];
        params[0]=token;
        getDataForGraphAsyncTask.execute(params);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stat, menu);
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

    }

    private class GetDataForGraphAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection connection = null;
            try {
                JSONObject request = new JSONObject();
                request.put(TOKEN, strings[0]);
                url = new URL(PASSED_CREATED_STRING);
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
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response)
        {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Object passed = jsonObject.get(PASSED);
                Object created = jsonObject.get(CREATED);
                if (passed instanceof JSONArray)
                {
                    JSONArray passedArray = (JSONArray)passed;
                    ArrayList<CountAndDate> data = new ArrayList<CountAndDate>(passedArray.length());
                    for (int i = 0; i<passedArray.length(); i++)
                    {
                        JSONObject object = passedArray.getJSONObject(i);
                        String label = object.getString(DATE);
                        int count = object.getInt(COUNT);
                        data.add(new CountAndDate(label,count,i));
                    }
                    Collections.sort(data);
                    String[] labels = new String[data.size()>1 ? data.size() : 2];
                    DataPoint[] dataPoints = new DataPoint[data.size()];
                    int i = 0;
                    for (CountAndDate count : data)
                    {
                        labels[i] = count.label;
                        dataPoints[i] = new DataPoint(i,count.number);
                    }
                    if (data.size()==1)
                    {
                        labels[1]="tomorrow";
                    }
                    graphViewPassed.getGridLabelRenderer().setLabelFormatter(new StaticLabelsFormatter(graphViewPassed, labels,null));
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                    graphViewPassed.addSeries(series);
                }
                if (created instanceof JSONArray)
                {
                    JSONArray createdArray = (JSONArray)created;
                    ArrayList<CountAndDate> data = new ArrayList<CountAndDate>(createdArray.length());
                    for (int i = 0; i<createdArray.length(); i++)
                    {
                        JSONObject object = createdArray.getJSONObject(i);
                        String label = object.getString(DATE);
                        int count = object.getInt(COUNT);
                        data.add(new CountAndDate(label,count,i));
                    }
                    Collections.sort(data);
                    String[] labels = new String[data.size()>1 ? data.size() : 2];
                    DataPoint[] dataPoints = new DataPoint[data.size()];
                    int i = 0;
                    for (CountAndDate count : data)
                    {
                        labels[i] = count.label;
                        dataPoints[i] = new DataPoint(i,count.number);
                    }
                    if (data.size()==1)
                    {
                        labels[1]="tomorrow";
                    }
                    graphViewCreated.getGridLabelRenderer().setLabelFormatter(new StaticLabelsFormatter(graphViewCreated, labels,null));
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                    graphViewCreated.addSeries(series);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private class CountAndDate implements Comparable
    {
        String label;
        Date date;
        int count;
        int number;

        public CountAndDate(String label, int count, int number) {
            this.label = label;
            this.count = count;
            this.number = number;
        }

        @Override
        public int compareTo(Object o) {
            CountAndDate obj = (CountAndDate)o; //todo fix it
            if (this.number<obj.number)
            {
                return -1;
            } else
            {
                return 1;
            }
        }
    }

    private class GetInfoAboutPassedTestsAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection connection = null;
            try {
                JSONObject request = new JSONObject();
                request.put(TOKEN, strings[0]);
                url = new URL(PASSED_TESTS_STRING);
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
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String param)
        {
            try {
                JSONObject response = new JSONObject(param);
                JSONArray testsJSON = response.getJSONArray(TESTS);
                for (int i = 0; i<testsJSON.length(); i++)
                {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

}
