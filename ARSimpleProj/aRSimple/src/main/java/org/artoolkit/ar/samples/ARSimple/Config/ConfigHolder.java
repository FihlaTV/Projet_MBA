package org.artoolkit.ar.samples.ARSimple.Config;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class ConfigHolder {
    private static ArrayList<Tableau> targets;
    private static ConfigHolder instance = null;
    public void init(ArrayList<Tableau> targets){
        this.targets = (ArrayList<Tableau>) targets.clone();
    }

    public static ConfigHolder getInstance(){
        if (instance == null) instance = new ConfigHolder();
        return instance;
    }

    public void draw(GL10 gl){
        for(Tableau t : targets){
            if(ARToolKit.getInstance().queryMarkerVisible(t.getMarkerUID())) {
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(t.getMarkerUID()), 0);
                t.draw(gl);
            }
        }
    }


}
