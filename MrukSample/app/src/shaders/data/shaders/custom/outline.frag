#version 400
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable

#include <metaSpatialSdkFragmentBase.glsl>
#include <Uniforms.glsl>

float getCoordFromPosition(float worldPos, float offset) {
    float coordValue = clamp(fract(abs(worldPos)), 0.0, 1.0);
    coordValue = abs((coordValue * 2.0) - 1.0);
    return coordValue;
}

void main() {
    float PI = 3.14159265;
    float backFace = gl_FrontFacing ? 1.0 : 0.1;
    float angle = 60.0;

    vec4 color = vec4(37.0 / 255.0, 176 / 255.0, 221.0 / 255.0, 1.0);
    vec2 uv = vertexOut.albedoCoord;
    float edgeGradient = max(getCoordFromPosition(uv.x, 0.0), getCoordFromPosition(uv.y, 0.0));
    float stroke = step(0.99, edgeGradient);
    float glow = clamp((edgeGradient - 0.75) * 4.0, 0.0, 1.0);
    vec4 edgeEffect = color * stroke + color * pow(glow, 4);

    float uGrid = getCoordFromPosition(vertexOut.worldPosition.x, 0);
    float vGrid = getCoordFromPosition(vertexOut.worldPosition.z, 0);
    float groundGradient = max(uGrid, vGrid);

    float uOffset = getCoordFromPosition(vertexOut.worldPosition.x, 0.5);
    float vOffset = getCoordFromPosition(vertexOut.worldPosition.z, 0.5);
    float gridOffset = min(uOffset, vOffset);

    float groundGrid = step(0.99, groundGradient) * step(0.8, gridOffset);
    float groundGlow = smoothstep(0.8, 0.99, groundGradient) * smoothstep(0.5, 1, gridOffset);

    vec4 floorEffect = edgeEffect + color * groundGrid + color * (groundGlow * 0.25 + 0.2);

    float groundMask = acos(abs(dot(vertexOut.worldNormal.xyz, vec3(0,1,0))));
    groundMask = step((angle/90) * PI * 0.5,groundMask);

    vec4 finalEffect = mix(floorEffect, edgeEffect, groundMask) * backFace;

    outColor = finalEffect;
}
