#version 150
#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec3 cameraPosition;

uniform vec3 skyColor;
uniform vec4 tintColor;
uniform vec3 worldAmbientColor;


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



    vec3 block_ao_factor = blocklight.rgb / 15.0;


    vec3 sky_ao_factor = vec3(blocklight.a);


    vec3 lightFactor = max(block_ao_factor, sky_ao_factor);

    float alpha = mix(texColor.a * 2.0, texColor.a * 0.5, fresnel);

    if(alpha == 0.0)
    {
        discard;
    }


    outColor = vec4(waterColor * lightFactor, alpha);


    outColor *= tintColor;


    outColor.rgb = max(outColor.rgb, texColor.rgb * worldAmbientColor);


    float gamma = 1.1;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
