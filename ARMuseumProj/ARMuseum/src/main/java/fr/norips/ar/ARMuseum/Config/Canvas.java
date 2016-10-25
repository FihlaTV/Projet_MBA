package fr.norips.ar.ARMuseum.Config;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class Canvas {
    private String name;
    private int markerID;
    private ArrayList<Model> models;

    /**
     *
     * @param name Only use to debug
     * @param pathToFeature Path to feature folder (folder containing iset,fset and fset3 file
     * @param models An ArrayList of your models
     */
    public Canvas(String name, String pathToFeature, ArrayList<Model> models){
        this.name = name;
        this.markerID = ARToolKit.getInstance().addMarker("nft;" + pathToFeature);
        this.models = (ArrayList<Model>) models.clone();
    }

    /**
     *
     * @param name Only use to debug
     * @param pathToFeature Path to feature folder (folder containing iset,fset and fset3 file
     */
    public Canvas(String name, String pathToFeature){
        this.name = name;
        this.markerID = ARToolKit.getInstance().addMarker("nft;" + pathToFeature);
        models = new ArrayList<Model>();
    }

    /**
     *
     * @return markerUID of the Canvas
     */
    public int getMarkerUID(){
        return markerID;
    }

    /**
     *
     * @param model The model you want to add to this canvas
     */
    public void addModel(Model model){
        models.add(model);
    }

    /**
     * Draw all models and scale them to marker
     * @param gl GL10 context
     */
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

    public void nextPage(){
        for(Model model : models){
            model.nextPage();
        }
    }

    public void previousPage(){
        for(Model model : models){
            model.previousPage();
        }
    }

}
