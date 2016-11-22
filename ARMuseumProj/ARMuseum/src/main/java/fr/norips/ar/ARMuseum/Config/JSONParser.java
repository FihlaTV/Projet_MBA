package fr.norips.ar.ARMuseum.Config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Button;
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
    private Button button = null;
    public JSONParser(Context context, Button b) {
        this.context = context;
        button = b;
    }
    //TODO Add warning when createConfig take too long time
    public boolean createConfig(String... urls) {
        new AsyncTask<String,Integer,ArrayList<Canvas>>() {
            private boolean checkAndDownload(File currFile,DownloadConfig dc,boolean connected,String filePath,String fileName,String fileMD5){
                if (!currFile.exists()) {
                    if(connected)
                        return dc.downloadURL(filePath,fileName);
                    else
                        return false;
                } else {
                    if (!MD5.checkMD5(fileMD5, currFile))
                        if(connected)
                            return dc.downloadURL(filePath,fileName);
                    return true;
                }
            }
            @Override
            protected void onPostExecute(ArrayList<Canvas> result){
                super.onPostExecute(result);
                if(result != null) {
                    ConfigHolder.getInstance().load(result);
                    button.setEnabled(true);
                    Log.d(TAG,"Button enabled");
                } else {
                    Toast.makeText(context,"Error while downloading file or loading cache",Toast.LENGTH_LONG).show();
                }
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
                    if (!connected) { //Go offline
                        Log.d(TAG,"f");
                    }
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
                    } catch (IOException e) { //Can't open file
                        //log the exception
                        e.printStackTrace();
                        return null; //Abort
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
                        }
                        JSONArray files = feature.getJSONArray("files");
                        boolean error = false;
                        for (int indFile = 0; indFile < files.length(); indFile++) {
                            JSONObject file = files.getJSONObject(indFile);
                            String filePath = file.getString("path");
                            String fileMD5 = file.getString("MD5");
                            String fileName = file.getString("name");
                            File currFile = new File(context.getExternalFilesDir(null) + "/" + featureName + "/" + fileName);
                            if( ! checkAndDownload(currFile,dc,connected,filePath,featureName + "/" + fileName,fileMD5))
                                error = true;
                        }
                        if(error) //Go to next canvas, there is one or more missing files
                            continue;

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

                                if( ! checkAndDownload(currFile,dc,connected,texturePath,featureName + "/" + textureName,textureMD5)){
                                    continue;
                                    //TODO check for return
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

