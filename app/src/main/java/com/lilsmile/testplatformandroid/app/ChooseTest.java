package com.lilsmile.testplatformandroid.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ChooseTest extends Activity implements Constants{


    TableLayout tableLayout;
    SharedPreferences preferences;

    ArrayList<Integer> ids;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_test);

        ids = new ArrayList<Integer>();

        tableLayout = (TableLayout)findViewById(R.id.tableTests);
        preferences = getPreferences(MODE_PRIVATE);
        token = getIntent().getStringExtra(TOKEN);
        Log.wtf("token",token);
        if (token.equals(""))
        {
            startActivity(new Intent(ChooseTest.this, MainActivity.class));
        } else
        {
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            String[] params = new String[1];
            params[0] = token;
            myAsyncTask.execute(params);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_test, menu);
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
                url = new URL(GET_TESTS_STRING);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");



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

            try {
                JSONArray response = new JSONArray(param);
                for (int i = 0; i<response.length(); i++)
                {
                    JSONObject currentTest = response.getJSONObject(i);
                    String title = currentTest.getString(TITLE);
                    String author = currentTest.getString(AUTHOR);
                    String category = currentTest.getString(TEST_CATEGORY);
                    Integer id = currentTest.getInt(TEST_ID);
                    ids.add(id);
                    TableRow tableRow = new TableRow(ChooseTest.this);

                    TextView titleView = new TextView(ChooseTest.this); //todo add style
                    titleView.setLayoutParams(findViewById(R.id.testTitleHeader).getLayoutParams());
                    titleView.setTextSize(getResources().getDimension(R.dimen.textSize));
                    titleView.setText(title);
                    titleView.setId(id);
                    titleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();
                            Intent intent = new Intent(ChooseTest.this, PassTest.class);
                            intent.putExtra(TOKEN,token);
                            intent.putExtra(TEST_ID, id);
                            startActivity(intent);
                        }
                    });

                    TextView authorView = new TextView(ChooseTest.this);
                    authorView.setTextSize(getResources().getDimension(R.dimen.textSize));
                    authorView.setLayoutParams(findViewById(R.id.authorHeader).getLayoutParams());
                    authorView.setText(author);

                    TextView categoryView = new TextView(ChooseTest.this);
                    categoryView.setLayoutParams(findViewById(R.id.categoryHeader).getLayoutParams());
                    categoryView.setText(category);

                    tableRow.addView(titleView);
                    tableRow.addView(authorView);
                    tableRow.addView(categoryView);

                    View breaker = new View(ChooseTest.this);
                    breaker.setBackgroundColor(getResources().getColor(R.color.breakerColor));
                    breaker.setLayoutParams((findViewById(R.id.breaker0)).getLayoutParams());

                    tableLayout.addView(tableRow);
                    tableLayout.addView(breaker);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
