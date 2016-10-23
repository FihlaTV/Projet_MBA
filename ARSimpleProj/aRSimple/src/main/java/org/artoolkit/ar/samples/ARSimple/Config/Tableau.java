package org.artoolkit.ar.samples.ARSimple.Config;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class Tableau {
    private String name;
    private int markerID;
    private ArrayList<Model> models;

    public Tableau(String name, String pathToFeature,ArrayList<Model> models){
        this.name = name;
        this.markerID = ARToolKit.getInstance().addMarker("nft;" + pathToFeature);
        this.models = (ArrayList<Model>) models.clone();
    }

    public Tableau(String name, String pathToFeature){
        this.name = name;
        this.markerID = ARToolKit.getInstance().addMarker("nft;" + pathToFeature);
        models = new ArrayList<Model>();
    }

    public int getMarkerUID(){
        return markerID;
    }

    public void addModel(Model model){
        models.add(model);
    }

    public void draw(GL10 gl){
        if(ARToolKit.getInstance().getMarkerPatternCount(markerID)>0){
            float width[] = new float[1];
            float height[] = new float[1];
            ARToolKit.getInstance().getMarkerPatternConfig(markerID,0,null,width,height,null,null);
            gl.glScalef(width[0]/100.0f,height[0]/100.0f,1.0f);
        }
        for(Model model : models){
            model.draw(gl);
        }
    }

}
