package com.lilsmile.testplatformandroid.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class PassTest extends Activity implements  Constants, View.OnClickListener {

    String token;
    int test_id;
    TextView tvTitle;
    TextView tvDesc;

    ScrollView svTest;
    LinearLayout llinsvTest;
    Button buttonSubmit;

    JSONArray questionsJSONArray;

    ArrayList<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_test);


        questionsJSONArray = new JSONArray();
        tags = new ArrayList<String>();

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvDesc = (TextView)findViewById(R.id.tvDescription);
        svTest = (ScrollView)findViewById(R.id.svTest);
        llinsvTest = (LinearLayout)findViewById(R.id.llInsvTest);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);

        token = getIntent().getStringExtra(TOKEN);
        int id = getIntent().getIntExtra(TEST_ID,0);
        test_id=id;
        if (id==0)
        {
            startActivity(new Intent(PassTest.this, ChooseTest.class).putExtra(TOKEN, token));
        }
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        Integer[] param = new Integer[1];
        param[0]=new Integer(id);
        myAsyncTask.execute(param);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_test, menu);
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

        JSONObject request = new JSONObject();
        try {
            request.put(TOKEN, token);
            request.put(TEST_ID, test_id);
            for (String tag : tags)
            {
                EditText et = (EditText)llinsvTest.findViewWithTag(tag);
                JSONObject jsonObject = new JSONObject();
                String[] strings = ((String)et.getTag()).split("!!");
                jsonObject.put(ANSWER, et.getText().toString());
                jsonObject.put(NUMBER, Integer.valueOf(strings[0]));
                jsonObject.put(TITLE, strings[1]);
                questionsJSONArray.put(jsonObject);
            }
            request.put(QUESTIONS, questionsJSONArray);
            Log.wtf("tag", request.toString());
            SendResultsAsyncTask sendResultsAsyncTask = new SendResultsAsyncTask();
            String[] strings = new String[1];
            strings[0]=request.toString();
            sendResultsAsyncTask.execute(strings);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*Intent intent = new Intent(PassTest.this, PerconalCabinet.class);
        intent.putExtra(TOKEN, token);
        startActivity(intent);*/
    }

    private class MyAsyncTask extends AsyncTask<Integer, Void, String>
    {
        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0].intValue();
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(GET_TEST_BY_ID_STRING+id);
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
            //Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show();
            try {
                JSONObject jsonObject = new JSONObject(param);
                tvDesc.setText(jsonObject.getString(DESCRIPTION));
                tvTitle.setText(jsonObject.getString(TITLE));

                JSONArray questionsJSON = jsonObject.getJSONArray(QUESTIONS);
                ArrayList<Question> questions = new ArrayList<Question>();
                for (int i = 0; i<questionsJSON.length(); i++)
                {
                    JSONObject currentQuestion = questionsJSON.getJSONObject(i);
                    int number = currentQuestion.getInt(NUMBER);
                    int type = currentQuestion.getInt(TYPE);
                    String questionTitle = currentQuestion.getString(TITLE);
                    ArrayList<Answer> answers = new ArrayList<Answer>();
                    if (currentQuestion.has(ANSWERS))
                    {
                        JSONArray answersArray = currentQuestion.getJSONArray(ANSWERS);
                        for (int j = 0; j<answersArray.length(); j++)
                        {
                            JSONObject currentAnswer = answersArray.getJSONObject(j);
                            int numberAnswer = currentAnswer.getInt(NUMBER);
                            int weight = currentAnswer.getInt(WEIGHT);
                            String answerTitle = currentAnswer.getString(TITLE);
                            Answer answer = new Answer(answerTitle, numberAnswer, weight);
                            answers.add(answer);
                        }
                    }
                    Collections.sort(answers);
                    Question question = new Question(questionTitle, number, type, answers);
                    questions.add(question);
                }
                Collections.sort(questions);
                for(Question question : questions)
                {
                    View view = question.getViewFromQuestion();
                    //String tag = question.title;
                    //tags.add(tag);
                    //view.setTag(tag);
                    llinsvTest.addView(view);
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

    }

    private class Question implements Comparable
    {
        public Question(String title, int number, int type, ArrayList<Answer> answers) {
            this.title = title;
            this.number = number;
            this.type = type;
            this.answers = answers;
        }




        String title;
        int number;
        int type;
        ArrayList<Answer> answers;

        public View getViewFromQuestion()
        {
            LinearLayout linearLayout = new LinearLayout(PassTest.this);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView titleView = new TextView(PassTest.this);
            titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            titleView.setText(this.title);

            TextView numberView = new TextView(PassTest.this);
            numberView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            numberView.setText("question #"+this.number);


            linearLayout.addView(numberView);
            linearLayout.addView(titleView);

            if (this.type==1)
            {
                RadioGroup radioGroup = new RadioGroup(PassTest.this);
                for (int i = 0; i<this.answers.size(); i++)
                {
                    RadioButton radioButton = new RadioButton(PassTest.this);
                    radioButton.setText(answers.get(i).title);
                    radioButton.setTag(this);
                    radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Question question = (Question)view.getTag();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(ANSWER, ((RadioButton)view).getText().toString());
                                jsonObject.put(NUMBER, question.number);
                                jsonObject.put(TITLE, question.title);
                                boolean flag = false;
                                for (int i = 0; i<questionsJSONArray.length(); i++)
                                {
                                    JSONObject tmp = questionsJSONArray.getJSONObject(i);
                                    if (tmp.get(NUMBER).equals(jsonObject.get(NUMBER)))
                                    {
                                        questionsJSONArray.remove(i);
                                        questionsJSONArray.put(jsonObject);
                                        flag=true;
                                        break;
                                    }
                                }
                                if (!flag)
                                {
                                    questionsJSONArray.put(jsonObject);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    radioGroup.addView(radioButton,i);
                }
                linearLayout.addView(radioGroup);
            } else if (this.type==2)
            {
                LinearLayout linearLayoutBox = new LinearLayout(PassTest.this);
                linearLayoutBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayoutBox.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i<this.answers.size(); i++)
                {
                    CheckBox checkBox = new CheckBox(PassTest.this);
                    checkBox.setText(answers.get(i).title);
                    checkBox.setTag(this);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Question question = (Question) compoundButton.getTag();
                            if (b)
                            {
                                boolean flag = false;
                                for (int i = 0; i<questionsJSONArray.length(); i++)
                                {
                                    try {
                                        JSONObject jsonObject = questionsJSONArray.getJSONObject(i);
                                        if (jsonObject.getInt(NUMBER)==question.number)
                                        {
                                            String answer = jsonObject.getString(ANSWER);
                                            answer = answer+","+compoundButton.getText().toString();
                                            jsonObject.remove(ANSWER);
                                            jsonObject.put(ANSWER, answer);
                                            questionsJSONArray.remove(i);
                                            questionsJSONArray.put(jsonObject);
                                            flag=true;
                                            break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                if (!flag)
                                {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put(TITLE, question.title);
                                        jsonObject.put(NUMBER, question.number);
                                        jsonObject.put(ANSWER, compoundButton.getText().toString());
                                        questionsJSONArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else
                            {
                                for (int i = 0; i<questionsJSONArray.length(); i++)
                                {
                                    try {
                                        JSONObject jsonObject = questionsJSONArray.getJSONObject(i);
                                        if (jsonObject.getString(NUMBER).equals(String.valueOf(question.number)))
                                        {
                                            String answer = jsonObject.getString(ANSWER);
                                            String[] strings = answer.split(",");
                                            StringBuilder sb = new StringBuilder();
                                            for (String string : strings)
                                            {
                                                if (!string.equals(answer))
                                                {
                                                    sb.append(string).append(",");
                                                }
                                            }
                                            jsonObject.put(ANSWER, sb.toString());
                                            questionsJSONArray.put(i, jsonObject);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    });
                    linearLayoutBox.addView(checkBox);
                }

                linearLayout.addView(linearLayoutBox);
            } else
            {
                LinearLayout linearLayoutAnswer = new LinearLayout(PassTest.this);
                linearLayoutAnswer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayoutAnswer.setOrientation(LinearLayout.HORIZONTAL);

                TextView tv = new TextView(PassTest.this);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setText("Answer:");

                EditText et = new EditText(PassTest.this);
                String tag = this.number+"!!"+this.title;
                et.setTag(tag);
                tags.add(tag);
                et.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayoutAnswer.addView(tv);
                linearLayoutAnswer.addView(et);

                linearLayout.addView(linearLayoutAnswer);
            }


            return linearLayout;
        }



        @Override
        public int compareTo(Object obj) {
            Question q = (Question) obj;
            if (this.number>q.number)
            {
                return 1;
            } else
            {
                return -1;
            }
        }
    }

    private class Answer implements Comparable
    {
        String title;
        int number;
        int weight;

        public Answer(String title, int number, int weight) {
            this.title = title;
            this.number = number;
            this.weight = weight;
        }

        @Override
        public int compareTo(Object o) {
            Answer answer = (Answer) o;
            if (this.number>answer.number)
            {
                return 1;
            } else
            {
                return -1;
            }
        }



    }

    private class SendResultsAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection connection = null;
            try {
                JSONObject request = new JSONObject(strings[0]);
                url = new URL(PASSED_TEST_STRING);
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
                return response.toString();
            }  catch (MalformedURLException e) {
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
                JSONObject jsonObject = new JSONObject(param);
                if (jsonObject.has(RESULT))
                {
                    String result = jsonObject.getString(RESULT);
                    if (result.equals(OK))
                    {
                        int userWeight = jsonObject.getInt(USER_WEIGHT);
                        int totalWeight = jsonObject.getInt(TOTAL_WEIGHT);
                        Toast.makeText(getApplicationContext(), "Result="+userWeight/totalWeight,Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(getApplicationContext(), "Something goes wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something goes wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

    }


}

