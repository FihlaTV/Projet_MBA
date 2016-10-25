package fr.norips.ar.ARMuseum.Config;

import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class ConfigHolder {
    private static ArrayList<Canvas> targets;
    private static ConfigHolder instance = null;
    private static boolean finish = false;
    private static boolean first = true;
    private static ShaderProgram shaderProgram=null;
    public void init(ArrayList<Canvas> targets){
        this.targets = (ArrayList<Canvas>) targets.clone();
        finish = true;
    }
    private void initGL(){
        for (Canvas c : targets){
            c.initGL(shaderProgram);
        }
    }
    public void setShaderProgram(ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
    }

    public static ConfigHolder getInstance(){
        if (instance == null) instance = new ConfigHolder();
        return instance;
    }

    /**
     * Draw all VISIBLE canvas
     */
    public void draw(float[] projectionMatrix){
        if(finish) {
            if(first){
                //initGL();
                first = false;
            } else {
                for (Canvas c : targets) {
                    if (ARToolKit.getInstance().queryMarkerVisible(c.getMarkerUID())) {
                        c.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(c.getMarkerUID()));
                    }
                }
            }
        }
    }

    public void nextPage(){
        for(Canvas c : targets){
            if(ARToolKit.getInstance().queryMarkerVisible(c.getMarkerUID())) {
                c.nextPage();
            }
        }
    }

    public void previousPage(){
        for(Canvas c : targets){
            if(ARToolKit.getInstance().queryMarkerVisible(c.getMarkerUID())) {
                c.previousPage();
            }
        }
    }

}
