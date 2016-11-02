package fr.norips.ar.ARMuseum.Config;

import android.opengl.Matrix;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;

/**
 * Created by norips on 20/10/16.
 */

public class Canvas {
    private String name;
    private String pathToFeature;
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
        this.pathToFeature = pathToFeature;
        this.models = (ArrayList<Model>) models.clone();
    }

    public void init(){
        this.markerID = ARToolKit.getInstance().addMarker("nft;" + pathToFeature);
        for(Model m : models)
            m.init();
    }

    /**
     *
     * @param name Only use to debug
     * @param pathToFeature Path to feature folder (folder containing iset,fset and fset3 file
     */
    public Canvas(String name, String pathToFeature){
        this.name = name;
        this.pathToFeature = pathToFeature;
        models = new ArrayList<Model>();
    }


    public void initGL(ShaderProgram shaderProgram){
        for(Model m : models){
            m.initGL(shaderProgram);
        }
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
     * @param projectionMatrix Float projectionMatrix.
     * @param modelViewMatrix Float modelViewMatrix.
     *
     */
    public void draw(float[] projectionMatrix, float[] modelViewMatrix){
        if(ARToolKit.getInstance().getMarkerPatternCount(markerID)>0){
            float width[] = new float[1];
            float height[] = new float[1];
            ARToolKit.getInstance().getMarkerPatternConfig(markerID,0,null,width,height,null,null);
            Matrix.scaleM(modelViewMatrix,0,width[0]/100.0f,height[0]/100,1.0f);
        }
        for(Model model : models){
            model.draw(projectionMatrix,modelViewMatrix);
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
