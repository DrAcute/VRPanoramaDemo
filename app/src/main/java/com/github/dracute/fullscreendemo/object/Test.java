package com.github.dracute.fullscreendemo.object;

import android.opengl.GLES20;

import com.github.dracute.fullscreendemo.utils.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by DrAcute on 2016/3/21.
 */
public class Test implements IObject {

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;
    ShortBuffer drawOrderBuffer;
    float[] vertex = new float[] {
            -1.0f, 1f, 0.0f,   // top left
            -1.0f, -1f, 0.0f,   // bottom left
            1f, -1f, 0.0f,   // bottom right
            1f, 1f, 0.0f
    };
    private float textureCoords[] = {
            0.25f, 1.0f, 0.0f, 1.0f,
            0.25f, 0.0f, 0.0f, 1.0f,
            0.75f, 0.0f, 0.0f, 1.0f,
            0.75f, 1.0f, 0.0f, 1.0f
    };
    short[] drawOrder = new short[] {0, 1, 2, 0, 2, 3};

    @Override
    public void init() {
        ByteBuffer vBb = ByteBuffer.allocateDirect(vertex.length * Constant.BYTE_PER_FLOAT);
        vBb.order(ByteOrder.nativeOrder());

        vertexBuffer = vBb.asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        ByteBuffer textureBb = ByteBuffer.allocateDirect(textureCoords.length * Constant.BYTE_PER_FLOAT);
        textureBb.order(ByteOrder.nativeOrder());

        textureBuffer = textureBb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        ByteBuffer tBb = ByteBuffer.allocateDirect(drawOrder.length * Constant.BYTE_PER_SHORT);
        tBb.order(ByteOrder.nativeOrder());

        drawOrderBuffer = tBb.asShortBuffer();
        drawOrderBuffer.put(drawOrder);
        drawOrderBuffer.position(0);
    }

    @Override
    public void draw(int positionHandle, int textureCoordinateHandle) {
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
    }
}
