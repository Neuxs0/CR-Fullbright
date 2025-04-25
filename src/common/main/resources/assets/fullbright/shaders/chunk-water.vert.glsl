#version 150

in vec4 a_lighting;
uniform vec3 u_batchPosition;
uniform mat4 u_projViewTrans;
uniform mat4 u_modelMat;

uniform float u_time;
uniform vec3 cameraPosition;

uniform bool u_isItem = false;

out vec2 v_texCoord0;
out vec4 blocklight;
out float waveStrength;
out vec3 worldPos;
out vec3 toCameraVector;

#import "base:shaders/common/bitUnpacker.glsl"

void main()
{
    worldPos = GET_BLOCK_VERT_POSITION + u_batchPosition;

    vec3 trueWorldPos = worldPos + cameraPosition;
    if(u_isItem)
    {
        trueWorldPos = vec3(0.0);
    }

    blocklight = vec4(1.0);

    v_texCoord0 = GET_TEX_COORDS;

    toCameraVector = cameraPosition - trueWorldPos;
    vec3 normal = GET_VERT_NORMAL;

    float xOff = 0.0;
    float zOff = 0.0;
    if(normal.y < 0.0)
    {
        xOff = normal.x * -0.001;
        zOff = normal.z * -0.001;
    }
    float waveSpeed = 0.25;
    float waveTime = waveSpeed * u_time;
    float scale = 5.0;
    float wavePositionA = 10.0 * (cos(trueWorldPos.x * scale) +
    sin(trueWorldPos.z * scale));
    float wavePositionB = 10.0 * (sin(trueWorldPos.x * scale) +
    cos(trueWorldPos.z * scale));

    waveStrength = 0.1 * (sin(waveTime + wavePositionA) *
    cos(waveTime + wavePositionB));

    vec4 modelPosition = u_modelMat * vec4(worldPos.x + xOff,
    worldPos.y + waveStrength - 0.2,
    worldPos.z + zOff, 1.0);
    gl_Position = u_projViewTrans * modelPosition;

    modelPosition = u_modelMat * vec4(cameraPosition, 1.0);
    worldPos = trueWorldPos;
}
