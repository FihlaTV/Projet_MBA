package fr.norips.ar.ARMuseum.Config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by norips on 01/11/16.
 */

public class JSONParser {
    private String json;
    private Context context;
    public JSONParser(String json, Context context){
        this.context = context;
        this.json = json;
    }

    public void createConfig(){
        JSONObject jObject;
        try {
            jObject = new JSONObject(json);
            JSONArray canvas = jObject.getJSONArray("canvas");
            ArrayList<Canvas> ALcanvas = new ArrayList<Canvas>();
            for(int i = 0; i < canvas.length(); i++){
                JSONObject jO = canvas.getJSONObject(i);
                String name = jO.getString("name");
                String feature = jO.getString("feature");
                //create new canvas
                Canvas c = new Canvas(name,feature);
                JSONArray models = jO.getJSONArray("models");
                for(int j=0; j < models.length(); j++){
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
                    for(int k = 0; k < 3; k++)
                        pos[0][k] = Float.parseFloat(tlcs[k]);
                    for(int k = 0; k < 3; k++)
                        pos[1][k] = Float.parseFloat(trcs[k]);
                    for(int k = 0; k < 3; k++)
                        pos[2][k] = Float.parseFloat(brcs[k]);
                    for(int k = 0; k < 3; k++)
                        pos[3][k] = Float.parseFloat(blcs[k]);
                    JSONArray textures = model.getJSONArray("textures");
                    ArrayList<String> pathToTextures = new ArrayList<>();
                    for(int k = 0; k < textures.length();k++)
                        pathToTextures.add(textures.getJSONObject(k).getString("path"));
                    //Add model to canvas
                    Model m = new Model(modelName,pos,pathToTextures,context);
                    c.addModel(m);
                }
                ALcanvas.add(c);
            }
            ConfigHolder.getInstance().init(ALcanvas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

