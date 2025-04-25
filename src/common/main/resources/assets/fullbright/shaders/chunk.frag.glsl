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
uniform float u_fogDensity;

#import "base:shaders/common/fog.glsl"

void main()
{
    vec2 tilingTexCoords = v_texCoord0;
    vec4 texColor = texture(texDiffuse, v_texCoord0);

    float fadeOutDistance = (u_renderDistanceInChunks - 1) * 16.0;
    float fadeOutFactor = clamp((fadeOutDistance - length(worldPos.xz -
    cameraPosition.xz)) / 16.0, 0.0, 1.0);
    texColor.a *= pow(fadeOutFactor, 0.5);

    if(texColor.a == 0.0)
    {
        discard;
    }

    vec3 lightTint = vec3(1.0);

    outColor = tintColor * vec4(texColor.rgb * lightTint, texColor.a);

    vec3 fogColor = skyAmbientColor;
    fogColor = getFogColor(fogColor, vec3(1.0), u_fogDensity, worldPos,
    cameraPosition);
    outColor.rgb = applyFog(fogColor, outColor.rgb, u_fogDensity, worldPos,
    cameraPosition);

    outColor.rgb = max(outColor.rgb, texColor.rgb);

    float gamma = 1.0;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
