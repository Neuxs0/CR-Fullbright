#version 150
#ifdef GL_ES
precision mediump float;
#endif

uniform vec3 cameraPosition;
uniform vec3 skyAmbientColor;
uniform vec4 tintColor;
uniform vec3 worldAmbientColor;

#import "base:shaders/common/renderDistance.glsl"

in vec2 v_texCoord0;
in vec3 worldPos;
in vec4 blocklight;
in vec3 faceNormal;

uniform sampler2D texDiffuse;
uniform vec3 u_sunDirection;

out vec4 outColor;

void main()
{
    vec2 tilingTexCoords = v_texCoord0;
    vec4 texColor = texture(texDiffuse, v_texCoord0);

    if(texColor.a == 0.0)
    {
        discard;
    }

    vec3 blockAmbientColor = skyAmbientColor * max(dot(u_sunDirection, faceNormal), 0.5);
    vec3 it =  pow(15.0 * blocklight.rgb / 25.0, vec3(2.0));
    vec3 t = 30.0 / (1.0 + exp(-15.0 * it)) - 15.0;
    vec3 lightTint = max(t / 15.0, blocklight.a * blockAmbientColor);

    outColor = tintColor * vec4(texColor.rgb * lightTint, texColor.a);

    outColor.rgb = max(outColor.rgb, texColor.rgb);

    float gamma = 1.0;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
