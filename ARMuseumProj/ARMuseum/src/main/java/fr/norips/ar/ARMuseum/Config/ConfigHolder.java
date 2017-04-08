package fr.norips.ar.ARMuseum.Config;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by norips on 20/10/16.
 */

public class ConfigHolder {
    private static ArrayList<Canvas> targets;
    private static ConfigHolder instance = null;
    private static boolean finish = false;
    private static boolean first = true;
    private static ShaderProgram shaderProgram=null;
    private static ShaderProgram shaderProgramMovie=null;

    /**
     * Init all models and canvas, MUST be called in configureARScene function, during ARToolkit initialisation
     */
    synchronized public void init(){
        for(Canvas c : this.targets) {
            c.init();
        }
        finish = true;
    }

    /**
     * Init all models and canvas, MUST be called in a OpenGL thread
     */
    private void initGL(){
        for (Canvas c : targets){
            c.initGL(shaderProgram,shaderProgramMovie);
        }
    }

    /**
     * Load a config from a List
     * @param targets
     */
    public void load(List<Canvas> targets){
        this.targets = new ArrayList<Canvas>(targets);
    }

    /**
     * Set shader program for standard texture but do not use it
     * @param shaderProgram
     */
    public void setShaderProgram(ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
    }

    /**
     * Set shader program for movie texture but do not use it
     * @param shaderProgram
     */
    public void setShaderProgramMovie(ShaderProgram shaderProgram){
        this.shaderProgramMovie = shaderProgram;
    }

    /**
     * Singleton class
     * @return A unique instance of ConfigHolder
     */
    synchronized public static ConfigHolder getInstance(){
        if (instance == null) instance = new ConfigHolder();
        return instance;
    }

    /**
     * Draw all models and scale them to marker
     * @param projectionMatrix Float projectionMatrix.
     *
     */
    public void draw(float[] projectionMatrix){
        if(finish) {
            if(first){
                initGL();
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

    /**
     * Call nextPage() for all visible canva
     */
    public void nextPage(){
        for(Canvas c : targets){
            if(ARToolKit.getInstance().queryMarkerVisible(c.getMarkerUID())) {
                c.nextPage();
            }
        }
    }
    /**
     * Call previousPage() for all visible canva
     */
    public void previousPage(){
        for(Canvas c : targets){
            if(ARToolKit.getInstance().queryMarkerVisible(c.getMarkerUID())) {
                c.previousPage();
            }
        }
    }
    public void erase() {
        targets.clear();
        finish = false;
        first = true;
    }

}
