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

void main()
{
    vec2 numTiles = floor(v_texCoord0);
    vec2 tilingTexCoords = v_texCoord0;

    if(numTiles.xy != vec2(0.0, 0.0))
    {
        tilingTexCoords = (v_texCoord0 - numTiles);
        vec2 flooredTexCoords = floor((v_texCoord0 - numTiles) * 16.0) / 16.0;
        numTiles = numTiles + vec2(1.0, 1.0);
        tilingTexCoords = flooredTexCoords + mod(((tilingTexCoords - flooredTexCoords) * numTiles) * 16.0, 1.0) / 16.0;
    }

    vec4 texColor = texture(texDiffuse, tilingTexCoords);

    vec3 viewVector = normalize(toCameraVector);
    vec3 faceNormal = vec3(0.0, 1.0, 0.0);
    float fresnel = abs(dot(viewVector, faceNormal));

    vec2 noiseUV = 0.2 * vec2(waveStrength - 0.1) + worldPos.xz / 16.0;
    noiseUV += vec2(u_time * 0.02);
    vec2 distortion = fresnel * texture(noiseTex, noiseUV).rg;
    vec3 waterColor = texColor.rgb;

    fresnel = pow(fresnel, mix(3.0, 1.0, 2.0 * (waveStrength - 0.1 + distortion.r / 3.0)));
    fresnel = pow(fresnel, 0.35);
    waterColor = mix(waterColor * 0.5, waterColor, 0.5 + 0.5 * waveStrength * (1.0 - fresnel));
    waterColor = mix(waterColor * 0.75, waterColor, fresnel);
    waterColor = mix(waterColor, skyColor, blocklight.a * (1.0 - fresnel));

    vec3 lightTint = max(blocklight.rgb, blocklight.a * skyAmbientColor);
    float alpha = mix(texColor.a * 2.0, texColor.a * 0.5, fresnel);

    if(alpha == 0.0)
    {
        discard;
    }

    outColor = vec4(waterColor * lightTint, alpha);
    outColor.rgb = mix(outColor.rgb, skyColor, blocklight.a * (1.0 - fresnel));
    outColor *= tintColor;
    outColor.rgb = max(outColor.rgb, texColor.rgb);

    float gamma = 1.0;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
