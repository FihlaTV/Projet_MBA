package fr.norips.ar.ARMuseum.Drawable;

import android.opengl.GLES20;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by norips on 23/02/17.
 */

public abstract class Texture {
    protected static BlockingDeque<Integer> stack = new LinkedBlockingDeque<Integer>();
    private boolean firstTime = true;
    void paint() {
        if(firstTime){
            int textureUnit[] = new int[1];
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS,textureUnit,0);
            for(int i = 0; i < textureUnit[0];i++){
                stack.addLast(i);
            }
        }
        firstTime = false;
    }
    public abstract void init();
}
