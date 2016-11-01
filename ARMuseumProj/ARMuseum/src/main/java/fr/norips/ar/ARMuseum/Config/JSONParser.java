package fr.norips.ar.ARMuseum.Config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import fr.norips.ar.ARMuseum.DownloadConfig;


/**
 * Created by norips on 01/11/16.
 */

public class JSONParser {
    private String jsonPath;
    private Context context;
    private static final String TAG = "JSONParser";
    public JSONParser(String jsonPath, Context context) {
        this.context = context;
        this.jsonPath = jsonPath;
    }

    public void createConfig() {
        JSONObject jObject;
        try {
            new DownloadConfig(context).execute(jsonPath,"format.json").get();
            BufferedReader reader = null;
            StringBuilder result = new StringBuilder();
            try {
                reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(new File(context.getExternalFilesDir(null),"format.json"))));

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
                    }
                }
            }
            jObject = new JSONObject(result.toString());
            JSONArray canvas = jObject.getJSONArray("canvas");
            ArrayList<Canvas> ALcanvas = new ArrayList<Canvas>();
            for (int i = 0; i < canvas.length(); i++) {
                JSONObject jO = canvas.getJSONObject(i);
                String name = jO.getString("name");
                String feature = jO.getString("feature");
                String featureName = jO.getString("featureName");
                File folder = new File(context.getExternalFilesDir(null) + "/" + featureName);
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    // Do something on success
                    Log.d(TAG,"Successfully created folder");
                } else {
                    Log.d(TAG,"Can't create folder");
                    // Do something else on failure
                }
                new DownloadConfig(context).execute(feature + ".iset", featureName + "/" + featureName + ".iset").get();
                new DownloadConfig(context).execute(feature + ".fset", featureName + "/" + featureName + ".fset").get();
                new DownloadConfig(context).execute(feature + ".fset3", featureName + "/" + featureName + ".fset3").get();
                feature = context.getExternalFilesDir(null).getAbsolutePath() + "/" + featureName + "/" + featureName;
                //create new canvas
                Canvas c = new Canvas(name, feature);
                JSONArray models = jO.getJSONArray("models");
                for (int j = 0; j < models.length(); j++) {
                    JSONObject model = models.getJSONObject(j);
                    String modelName = model.getString("name");
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
                        new DownloadConfig(context).execute(texturePath, featureName + "/" + textureName).get();
                        pathToTextures.add(context.getExternalFilesDir(null).getAbsolutePath() + "/" + featureName + "/" + textureName);
                    }
                    //Add model to canvas
                    Model m = new Model(modelName, pos, pathToTextures, context);
                    c.addModel(m);
                }
                ALcanvas.add(c);
            }
            ConfigHolder.getInstance().init(ALcanvas);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

