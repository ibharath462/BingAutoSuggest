package com.example.bharath.bas;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button bas;
    ListView lv;
    EditText input;
    HashMap<String,String> resMap=new HashMap<String,String>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bas=(Button)findViewById(R.id.button);
        lv=(ListView)findViewById(R.id.lv);
        input=(EditText)findViewById(R.id.input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //Toast.makeText(getApplicationContext(),""+input.getText().toString(),Toast.LENGTH_SHORT).show();
                final AT a=new AT();
                a.execute(input.getText().toString(),"","");

            }
        });

        bas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AT a=new AT();
                a.execute("barbieee","","");


            }
        });
    }

    class AT extends AsyncTask<String,String,String>{

        String data=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try
            {

                HttpClient httpclient = new DefaultHttpClient();
                final Uri.Builder builder=new Uri.Builder();
                builder.scheme("https")
                        .authority("bingapis.azure-api.net")
                        .appendPath("api")
                        .appendPath("v5")
                        .appendPath("suggestions")
                        .appendQueryParameter("q", params[0]);


                final HttpGet httppost=new HttpGet(builder.toString());
                httppost.addHeader("Ocp-Apim-Subscription-Key", "34138fc2e9214ca5aaaa77b5afb79b32");

                Log.d("Status:", "trying....");
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Status:", "got it baby....");
                HttpEntity entity = response.getEntity();


                final int status = response.getStatusLine().getStatusCode();


                if (status == 200)
                {
                    Log.d("Status:", "Success...200....");
                    data = EntityUtils.toString(entity);
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data="Hello";
                            Toast.makeText(getApplicationContext(),"Error baby..."+status,Toast.LENGTH_SHORT).show();
                            Log.d("Status", httppost.toString());
                        }
                    });
                }

            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            return data;

        }

        @Override
        protected void onPostExecute(String s) {


                final JSONObject jsonObject;


                try {

                    jsonObject = new JSONObject(s);
                    final JSONArray suggestions = jsonObject.getJSONArray("suggestionGroups");
                    final JSONObject search = suggestions.getJSONObject(0);
                    final JSONArray result = search.getJSONArray("searchSuggestions");
                    Log.d("Status:","Length:"+result.length());
                    String []StringArray=new String[result.length()];

                    for(int i=0;i<result.length();i++){

                        JSONObject t=result.getJSONObject(i);
                        String x=t.getString("query");
                        String y=t.getString("url");
                        StringArray[i]=x;
                        resMap.put(x,y);
                        adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.activity_listview,StringArray);
                    }
                    lv.setAdapter(adapter);
                } catch (JSONException e) {
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
}
