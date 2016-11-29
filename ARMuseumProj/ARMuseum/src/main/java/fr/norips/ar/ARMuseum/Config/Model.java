package fr.norips.ar.ARMuseum.Config;


import android.content.Context;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;

import fr.norips.ar.ARMuseum.Drawable.RectTex;
import fr.norips.ar.ARMuseum.Drawable.Rectangle;

/**
 * Created by norips on 20/10/16.
 */

public class Model {
    private final static String TAG = "Model";
    private float pos[][] = new float[4][3];
    private Rectangle rect;
    private Context context;
    private ArrayList<String> pathToTextures;

    /**
     *
     * @param name Name of your model, only use to debug
     * @param pos Array of 3D position, the four point of your rectangle
     *            pos[0] = Top Left corner
     *            pos[1] = Top Right corner
     *            pos[2] = Bottom Right corner
     *            pos[3] = Bottom Left corner
     * @param pathToTextures An ArrayList<String> containing paths to your texture
     * @param context Context activity to load from assets folder
     */
    public Model(String name, float pos[][], ArrayList<String> pathToTextures,Context context){
        for(int i = 0; i < 4;i++){
            this.pos[i][0] = pos[i][0];
            this.pos[i][1] = pos[i][1];
            this.pos[i][2] = pos[i][2];
        }
        this.context = context;
        this.pathToTextures = (ArrayList<String>) pathToTextures.clone();

    }

    public Model(String name, Rectangle rect){
        for(int i = 0; i < 4;i++){
            this.pos[i][0] = pos[i][0];
            this.pos[i][1] = pos[i][1];
            this.pos[i][2] = pos[i][2];
        }
        //TODO: Load texture on detection or on startup ?
        this.rect = rect;
    }

    /**
     * Draw all models and scale them to marker
     * @param projectionMatrix Float projectionMatrix.
     * @param modelViewMatrix Float modelViewMatrix.
     *
     */
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        rect.draw(projectionMatrix,modelViewMatrix);
    }

    public void init(){
        if(rect == null)
            rect = new RectTex(pos,pathToTextures,context);
    }

    public void nextPage(){
        rect.nextTexture();
    }

    public void previousPage(){
        rect.previousTexture();
    }

    public void initGL(ShaderProgram shaderProgram){
        rect.setShaderProgram(shaderProgram);
    }
}
