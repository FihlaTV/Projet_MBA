package fr.norips.ar.ARMuseum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ConfigurationPicker extends AppCompatActivity {
    public static final String MY_PREFS_ENDPOINT = "PrefEndpoint";
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayListURL;
    private EditText etURL;
    private Button btReload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_picker);
        SharedPreferences sharedPref = this.getSharedPreferences(
                MY_PREFS_ENDPOINT, Context.MODE_PRIVATE);
        etURL = (EditText) findViewById(R.id.etURL);
        String url = sharedPref.getString("endpoint",getResources().getString(R.string.endpointURL));
        etURL.setText(url);

        btReload = (Button) findViewById(R.id.btReload);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                        MY_PREFS_ENDPOINT, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                etURL = (EditText) findViewById(R.id.etURL);
                edit.putString("endpoint",etURL.getText().toString());
                edit.commit();
                new DownloadAndUpdateLv().execute(etURL.getText().toString());
            }
        });

        Button btModify = (Button) findViewById(R.id.btModify);
        btModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etURL.getVisibility() == View.GONE) {
                    etURL.setVisibility(View.VISIBLE);
                } else {
                    etURL.setVisibility(View.GONE);
                }
            }
        });
        list = (ListView) findViewById(R.id.lvSelect);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent myIntent = new Intent(getApplicationContext(), Presentation.class);
                myIntent.putExtra("url",arrayListURL.get(position));
                startActivity(myIntent);
            }
        });
        arrayList = new ArrayList<String>();
        arrayListURL = new ArrayList<String>();
        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(adapter);
        new DownloadAndUpdateLv().execute(url);
    }

    private class DownloadAndUpdateLv extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuffer response = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setFollowRedirects(true);
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                response = new StringBuffer();
                String line = null;
                while ((line = input.readLine()) != null) {
                    response.append(line);
                }
            } catch(SocketException e) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.toastDownloadEndpointFailed), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ConfigPick",result);
            try {
                JSONObject base = new JSONObject(result);
                JSONArray endpoints = base.getJSONArray("endpoint");
                arrayList.clear();
                for(int i = 0; i < endpoints.length();i++) {
                    JSONObject endpoint = endpoints.getJSONObject(i);
                    arrayList.add(endpoint.getString("name"));
                    arrayListURL.add(endpoint.getString("url"));
                }
                adapter.notifyDataSetChanged();
            } catch(JSONException e) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.toastJSONFailed), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
