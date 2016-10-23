package org.artoolkit.ar.samples.ARSimple.Config;


import android.content.Context;

import org.artoolkit.ar.samples.ARSimple.CubeTex;
import org.artoolkit.ar.samples.ARSimple.RectTex;

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
    private RectTex rect;
    private Context context;
    //TODO: Constructor take context ?
    public Model(String name, float pos[][], ArrayList<String> pathToTextures,Context context){
        this.name = name;
        for(int i = 0; i < 4;i++){
            this.pos[i][0] = pos[i][0];
            this.pos[i][1] = pos[i][1];
            this.pos[i][2] = pos[i][2];
        }
        //TODO: Load texture on detection or on startup ?
        rect = new RectTex(this.pos,pathToTextures);
        this.context = context;
    }

    public void draw(GL10 gl){
        rect.draw(gl,context);
    }
}
