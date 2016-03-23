package com.github.dracute.fullscreendemo.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.github.dracute.fullscreendemo.R;
import com.github.dracute.fullscreendemo.object.IObject;
import com.github.dracute.fullscreendemo.object.Sphere;
import com.github.dracute.fullscreendemo.utils.RawResourceReader;
import com.github.dracute.fullscreendemo.utils.ShaderHelper;
import com.github.dracute.fullscreendemo.utils.TextureHelper;

/**
 * Created by DrAcute on 2016/3/21.
 */
public class FullScreenRender extends TextureSurfaceRender {

    private final float[] projectionMatrix = new float[16];

    private final float rotate_degree = 0;

    private final float[] rotate_matrix = new float[] {
            (float) Math.cos(Math.toRadians(rotate_degree)), 0, (float) Math.sin(Math.toRadians(rotate_degree)), 0,
            0, 1, 0, 0,
            - (float) Math.sin(Math.toRadians(rotate_degree)), 0, (float) Math.cos(Math.toRadians(rotate_degree)), 0,
            0, 0, 0, 1
    };

    private final float[] sight_matrix = new float[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private Context mContext;
    private IObject mObject;

    private int texture;
    private int shaderProgram;

    private boolean isDrawFrame = false;

    public FullScreenRender(Context context, SurfaceTexture surfaceTexture, int width, int height) {
        super(surfaceTexture, width, height);
        this.mContext = context;
        this.mObject = new Sphere();
    }

    @Override
    protected boolean draw() {
        if (isDrawFrame) return false;

        isDrawFrame = true;

        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glViewport(0, 0, width, height);

        GLES20.glUseProgram(shaderProgram);

        final int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        final int matrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMatrix");
        final int rotateHandle = GLES20.glGetUniformLocation(shaderProgram, "uRotate");
        final int sightHandle = GLES20.glGetUniformLocation(shaderProgram, "uSight");
        final int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        final int textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoordinate");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(rotateHandle, 1, false, rotate_matrix, 0);
        GLES20.glUniformMatrix4fv(sightHandle, 1, false, sight_matrix, 0);

        mObject.draw(positionHandle, textureCoordinateHandle);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
        return true;
    }

    @Override
    protected void initGLComponents() {
        mObject.init();
        initShader();
        texture = TextureHelper.loadTexture(mContext, R.drawable.fullscreen);

        final float aspectRatio = width > height ? (float)width / (float)height : (float)height / (float)width;
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
//        Matrix.perspectiveM(sight_matrix, 0, 45, (float)width / (float)height, 1, 2);
    }

    @Override
    protected void deinitGLComponents() {
        GLES20.glDeleteProgram(shaderProgram);
        surfaceTexture.release();
        surfaceTexture.setOnFrameAvailableListener(null);
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    private void initShader() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mContext, R.raw.fullscreen_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mContext, R.raw.fullscreen_fragment_shader);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        shaderProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"texture", "vPosition", "uMatrix", "uRotate", "uSight", "aTexCoordinate"});
    }

}
