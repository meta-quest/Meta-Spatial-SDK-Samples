/**
 * Displays a texture on a surface, implementing 9-slice scaling
 * (https://en.wikipedia.org/wiki/9-slice_scaling).
 *
 * Learn more about using custom shader with Meta's Spatial SDK here:
 * https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders
 */

#version 400
#extension GL_ARB_separate_shader_objects: enable
#extension GL_ARB_shading_language_420pack: enable

#include <common.glsl>

layout (std140, set = 3, binding = 0) uniform MaterialUniform {
    // .xy = quad width and height, .zw = texture width and height
    vec4 sliceParams;
    // slice size in order: left, right, top, bottom
    vec4 sliceSize;
    // .rgb = tint color, .w = pixels-per-unit multiplier
    vec4 tintColor;
    vec4 stereoParams;
} g_MaterialUniform;

layout (set = 3, binding = 1) uniform sampler2D sliceTex;

layout (location = 0) in struct {
    vec4 color;
    vec2 albedoCoord;
    vec2 roughnessMetallicCoord;
    vec2 emissiveCoord;
    vec2 occlusionCoord;
    vec2 normalCoord;
    vec3 lighting;
    vec3 worldNormal;
    vec3 worldPosition;
} vertexOut;

layout (location = 0) out vec4 outColor;

void main() {
    // default panel resolution (PanelConfigOptions.EYEBUFFER_WIDTH * 0.5f)
    float pixelsPerUnit = 1032.0 * g_MaterialUniform.tintColor.a;

    vec2 uTextureSize = g_MaterialUniform.sliceParams.zw;
    vec2 uOutputSize = g_MaterialUniform.sliceParams.xy * pixelsPerUnit;
    vec4 uSliceSize = g_MaterialUniform.sliceSize.xyzw;

    // convert slice sizes to UVs
    vec2 texelSize = 1.0 / uTextureSize;
    float left = uSliceSize.x * texelSize.x;
    float right = uSliceSize.y * texelSize.x;
    float top = uSliceSize.z * texelSize.y;
    float bottom = uSliceSize.w * texelSize.y;

    // thresholds for dividing into 3x3 regions in UV space
    float leftLimit = uSliceSize.x;
    float rightLimit = uOutputSize.x - uSliceSize.y;
    float topLimit = uSliceSize.z;
    float bottomLimit = uOutputSize.y - uSliceSize.w;

    // normalized position in output space
    vec2 uv = vertexOut.albedoCoord;
    vec2 finalUV;

    // pixel position
    float x = uv.x * uOutputSize.x;
    float y = uv.y * uOutputSize.y;

    // horizontal slicing

    if (x < leftLimit) {
        finalUV.x = x / leftLimit * left;
    }
    else if (x > rightLimit) {
        finalUV.x = 1.0 - (uOutputSize.x - x) / (uOutputSize.x - rightLimit) * right;
    }
    else {
        float stretchWidth = uOutputSize.x - uSliceSize.x - uSliceSize.y;
        float innerX = (x - uSliceSize.x) / stretchWidth;
        float texWidth = 1.0 - left - right;
        finalUV.x = left + innerX * texWidth;
    }

    // vertical slicing

    if (y < topLimit) {
        finalUV.y = y / topLimit * top;
    }
    else if (y > bottomLimit) {
        finalUV.y = 1.0 - (uOutputSize.y - y) / (uOutputSize.y - bottomLimit) * bottom;
    }
    else {
        float stretchHeight = uOutputSize.y - uSliceSize.z - uSliceSize.w;
        float innerY = (y - uSliceSize.z) / stretchHeight;
        float texHeight = 1.0 - top - bottom;
        finalUV.y = top + innerY * texHeight;
    }

    // our final sampling and tinting

    vec4 pixel = texture(sliceTex, finalUV);
    vec3 linearTint = srgb_to_linear(g_MaterialUniform.tintColor.rgb); // convert to linear color
    vec3 color = pixel.rgb * linearTint;

    outColor = vec4(color, pixel.a);
}
