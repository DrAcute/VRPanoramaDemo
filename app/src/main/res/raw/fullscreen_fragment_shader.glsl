precision mediump float;
uniform sampler2D texture;
varying vec2 v_TexCoordinate;

void main () {
    vec4 color = texture2D(texture, v_TexCoordinate);
    gl_FragColor = color;
}