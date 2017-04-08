package fr.norips.ar.ARMuseum.Drawable;

import org.artoolkit.ar.base.rendering.gles20.ARDrawableOpenGLES20;
import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

/**
 * Created by norips on 15/12/16.
 */

public interface Drawable extends ARDrawableOpenGLES20{
    void nextTexture();
    void previousTexture();
    void init();
    void setShaderProgram(ShaderProgram shader,ShaderProgram movieShader);
}
