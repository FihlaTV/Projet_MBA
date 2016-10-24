package org.artoolkit.ar.samples.ARSimple.Config;


import android.content.Context;

import org.artoolkit.ar.samples.ARSimple.Model.RectTex;
import org.artoolkit.ar.samples.ARSimple.Model.Rectangle;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class Model {
    private String name;
    private float pos[][] = new float[4][3];
    private ArrayList<String> pathToTextures;
    private ArrayList<Integer> texture_id;
    private Rectangle rect;

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
        this.name = name;
        for(int i = 0; i < 4;i++){
            this.pos[i][0] = pos[i][0];
            this.pos[i][1] = pos[i][1];
            this.pos[i][2] = pos[i][2];
        }
        //TODO: Load texture on detection or on startup ?
        rect = new RectTex(pos,pathToTextures,context);
    }

    /**
     *
     * @param gl GL10 Context
     */
    public void draw(GL10 gl){
        rect.draw(gl);
    }
}
