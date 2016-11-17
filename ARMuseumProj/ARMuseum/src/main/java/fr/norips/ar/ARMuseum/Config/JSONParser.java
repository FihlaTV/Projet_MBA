package fr.norips.ar.ARMuseum.Config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fr.norips.ar.ARMuseum.R;
import fr.norips.ar.ARMuseum.ARMuseumActivity;
import fr.norips.ar.ARMuseum.Drawable.RectMovie;
import fr.norips.ar.ARMuseum.Util.DownloadConfig;
import fr.norips.ar.ARMuseum.Util.MD5;


/**
 * Created by norips on 01/11/16.
 */

public class JSONParser {
    private Context context;
    private static final String TAG = "JSONParser";
    private ProgressDialog pDialog;
    public JSONParser(Context context, ProgressDialog p) {
        this.context = context;
        pDialog = p;
    }

    public boolean createConfig(String... urls) {
        new AsyncTask<String,Integer,ArrayList<Canvas>>() {
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                try {
                    pDialog.setIndeterminate(false);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setTitle(context.getResources().getString(R.string.loading_title));
                    pDialog.setMessage(context.getResources().getString(R.string.loading_text));
                    pDialog.setCancelable(false);
                    pDialog.setMax(100);
                    pDialog.show();
                } catch (Exception e ){
                    e.printStackTrace();
                }
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values.length == 2) {
                    pDialog.setProgress(values[0]);
                    pDialog.setMax(values[1]);
                }
            }
            @Override
            protected void onPostExecute(ArrayList<Canvas> result){
                super.onPostExecute(result);
                if(result != null) {
                    ConfigHolder.getInstance().init(result);
                } else {
                    Toast.makeText(context,"Error while downloading file",Toast.LENGTH_LONG);
                }
                ARMuseumActivity.dismisspDialog = true;
            }
            @Override
            protected ArrayList<Canvas> doInBackground(String... urls) {
                float currentProgress = 0;
                if(Debug.isDebuggerConnected())
                    Debug.waitForDebugger();
                JSONObject jObject;
                DownloadConfig dc = new DownloadConfig(context);
                try {
                    boolean connected = false;
                    for (int indURL = 0; indURL < urls.length; indURL++) {
                        if (dc.downloadURL(urls[indURL], "format.json") == true) {
                            connected = true;
                            break;
                        }
                    }
                    if (!connected)
                        return null;

                    BufferedReader reader = null;
                    StringBuilder result = new StringBuilder();
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(new File(context.getExternalFilesDir(null), "format.json"))));

                        // do reading, usually loop until end of file reading
                        String mLine;
                        while ((mLine = reader.readLine()) != null) {
                            result.append(mLine);
                        }
                    } catch (IOException e) {
                        //log the exception
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                //log the exception
                                e.printStackTrace();
                                return null;
                            }
                        }
                    }
                    jObject = new JSONObject(result.toString());
                    JSONArray canvas = jObject.getJSONArray("canvas");
                    ArrayList<Canvas> ALcanvas = new ArrayList<Canvas>();
                    for (int i = 0; i < canvas.length(); i++) {
                        JSONObject jO = canvas.getJSONObject(i);
                        String name = jO.getString("name");
                        JSONObject feature = jO.getJSONObject("feature");
                        String featureName = feature.getString("name");
                        File folder = new File(context.getExternalFilesDir(null) + "/" + featureName);
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }
                        if (success) {
                            // Do something on success
                            Log.d(TAG, "Successfully created folder");
                        } else {
                            Log.d(TAG, "Can't create folder");
                            return null;
                            // Do something else on failure
                        }
                        JSONArray files = feature.getJSONArray("files");
                        for (int indFile = 0; indFile < files.length(); indFile++) {
                            JSONObject file = files.getJSONObject(indFile);
                            String filePath = file.getString("path");
                            String fileMD5 = file.getString("MD5");
                            String fileName = file.getString("name");
                            File currFile = new File(context.getExternalFilesDir(null) + "/" + featureName + "/" + fileName);
                            if (!currFile.exists()) {
                                dc.downloadURL(filePath, featureName + "/" + fileName);
                            } else {
                                if (!MD5.checkMD5(fileMD5, currFile)) {
                                    dc.downloadURL(filePath, featureName + "/" + fileName);
                                }
                            }
                        }

                        String localFeaturePath = context.getExternalFilesDir(null).getAbsolutePath() + "/" + featureName + "/" + featureName;
                        //create new canvas
                        Canvas c = new Canvas(name, localFeaturePath);
                        JSONArray models = jO.getJSONArray("models");

                        for (int j = 0; j < models.length(); j++) {
                            JSONObject model = models.getJSONObject(j);
                            String modelName = model.getString("name");
                            String modelType = model.getString("type");
                            float pos[][] = new float[4][3];
                            String tlc = model.getString("tlc");
                            String trc = model.getString("trc");
                            String brc = model.getString("brc");
                            String blc = model.getString("blc");
                            String[] tlcs = tlc.split(",");
                            String[] trcs = trc.split(",");
                            String[] brcs = brc.split(",");
                            String[] blcs = blc.split(",");
                            for (int k = 0; k < 3; k++)
                                pos[0][k] = Float.parseFloat(tlcs[k]);
                            for (int k = 0; k < 3; k++)
                                pos[1][k] = Float.parseFloat(trcs[k]);
                            for (int k = 0; k < 3; k++)
                                pos[2][k] = Float.parseFloat(brcs[k]);
                            for (int k = 0; k < 3; k++)
                                pos[3][k] = Float.parseFloat(blcs[k]);
                            JSONArray textures = model.getJSONArray("textures");
                            ArrayList<String> pathToTextures = new ArrayList<>();
                            for (int k = 0; k < textures.length(); k++) {
                                String textureName = textures.getJSONObject(k).getString("name");
                                String texturePath = textures.getJSONObject(k).getString("path");
                                String textureMD5 = textures.getJSONObject(k).getString("MD5");
                                File currFile = new File(context.getExternalFilesDir(null) + "/" + featureName + "/" + textureName);
                                if (!currFile.exists()) {
                                    dc.downloadURL(texturePath, featureName + "/" + textureName);
                                } else {
                                    if (!MD5.checkMD5(textureMD5, currFile)) {
                                        dc.downloadURL(texturePath, featureName + "/" + textureName);
                                    }
                                }
                                pathToTextures.add(context.getExternalFilesDir(null).getAbsolutePath() + "/" + featureName + "/" + textureName);
                                publishProgress((k+1)/textures.length() * 1/models.length() * (1/canvas.length()) * 100 , 100);
                                float perCanva = 1.0f / canvas.length();
                                float perModel = 1.0f / models.length();
                                float perTexture = 1.0f/textures.length()* perCanva * perModel*100;
                                currentProgress += perTexture;
                                publishProgress((int)currentProgress,100);
                            }
                            //Add model to canvas
                            Model m;
                            if(modelType.equalsIgnoreCase("video")) {
                                m = new Model(modelName, new RectMovie(pos, pathToTextures, context));
                                c.addModelMovie(m);
                            } else {
                                m = new Model(modelName, pos, pathToTextures, context);
                                c.addModel(m);
                            }

                        }
                        ALcanvas.add(c);
                    }
                    return ALcanvas;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute(urls);
    return true;
    }
}

