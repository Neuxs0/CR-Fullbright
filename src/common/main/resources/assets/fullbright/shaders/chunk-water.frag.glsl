#version 150
#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec3 cameraPosition;
uniform vec3 skyAmbientColor;
uniform vec3 skyColor;
uniform vec4 tintColor;
uniform vec3 worldAmbientColor;
uniform vec3 u_sunDirection;

#import "base:shaders/common/renderDistance.glsl"

in vec2 v_texCoord0;
in vec4 blocklight;
in float waveStrength;
in vec3 worldPos;
in vec3 toCameraVector;

uniform sampler2D texDiffuse;
uniform sampler2D noiseTex;

out vec4 outColor;

uniform float u_fogDensity;
#import "base:shaders/common/fog.glsl"

void main()
{
    vec2 numTiles = floor(v_texCoord0);
    vec2 tilingTexCoords = v_texCoord0;

    if(numTiles.xy != vec2(0.0, 0.0))
    {
        tilingTexCoords = (v_texCoord0 - numTiles);
        vec2 flooredTexCoords = floor((v_texCoord0 - numTiles) * 16.0) / 16.0;
        numTiles = numTiles + vec2(1.0, 1.0);
        tilingTexCoords = flooredTexCoords +
        mod(((tilingTexCoords - flooredTexCoords) * numTiles) * 16.0, 1.0)
        / 16.0;
    }

    vec4 texColor = texture(texDiffuse, tilingTexCoords);

    float fadeOutDistance = (u_renderDistanceInChunks - 1) * 16.0;
    float fadeOutFactor = clamp((fadeOutDistance - length(worldPos.xz -
    cameraPosition.xz)) / 16.0, 0.0, 1.0);
    float alpha = texColor.a * pow(fadeOutFactor, 0.5);
    if(alpha == 0.0)
    {
        discard;
    }

    vec3 waterColor = texColor.rgb;
    vec3 lightTint = vec3(1.0);

    outColor = tintColor * vec4(waterColor * lightTint, alpha);

    vec3 fogColor = vec3(1.0) - pow(vec3(1.0) - skyAmbientColor, vec3(2.0));
    fogColor = getFogColor(fogColor, vec3(1.0), u_fogDensity, worldPos,
    cameraPosition);
    outColor.rgb = applyFog(fogColor, outColor.rgb, u_fogDensity, worldPos,
    cameraPosition);

    outColor.rgb = max(outColor.rgb, texColor.rgb);

    float gamma = 1.0;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
