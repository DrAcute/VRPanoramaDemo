uniform mat4 uMatrix;
uniform mat4 uRotate;
uniform mat4 uSight;
attribute vec4 aPosition;
attribute vec4 aTexCoordinate;
varying vec2 v_TexCoordinate;

void main () {
    v_TexCoordinate = aTexCoordinate.xy;
    gl_Position = uMatrix * uRotate * aPosition;
    gl_Position = gl_Position.xyzz;
    gl_Position = uSight * gl_Position;
}