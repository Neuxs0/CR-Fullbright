#version 150
#ifdef GL_ES
precision mediump float;
#endif

uniform vec3 cameraPosition;
uniform vec4 tintColor;
uniform vec3 worldAmbientColor;
uniform vec3 skyAmbientColor;
uniform float u_fogDensity;
uniform vec3 u_sunDirection;

#import "base:shaders/common/renderDistance.glsl"
#import "base:shaders/common/fog.glsl"

in vec2 v_texCoord0;
in vec3 worldPos;
in vec4 blocklight;
in vec3 faceNormal;

uniform sampler2D texDiffuse;

out vec4 outColor;

void main()
{
    vec4 texColor = texture(texDiffuse, v_texCoord0);

    float fadeOutDistance = (u_renderDistanceInChunks - 1.0) * 16.0;
    float fadeOutFactor = clamp((fadeOutDistance - length(worldPos - cameraPosition))/16.0, 0.0, 1.0);
    texColor.a = texColor.a * pow(fadeOutFactor, 0.5);

    if(texColor.a == 0.0)
    {
        discard;
    }

    vec3 it =  pow(15.0 * blocklight.rgb / 25.0, vec3(2.0));
    vec3 t = 30.0 / (1.0 + exp(-15.0 * it)) - 15.0;
    vec3 block_ao_factor = t / 15.0;

    vec3 sky_ao_factor = vec3(blocklight.a);

    vec3 lightFactor = max(block_ao_factor, sky_ao_factor);

    outColor = tintColor * vec4(texColor.rgb * lightFactor, texColor.a);

    outColor.rgb = max(outColor.rgb, texColor.rgb * worldAmbientColor);

    vec3 fogColor = skyAmbientColor;
    fogColor = getFogColor(fogColor, blocklight.rgb, u_fogDensity, worldPos, cameraPosition);
    outColor.rgb = applyFog(fogColor, outColor.rgb, u_fogDensity, worldPos, cameraPosition);

    float gamma = 1.1;
    outColor.rgb = pow(outColor.rgb, vec3(1.0 / gamma));
}
