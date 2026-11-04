/**
 * Learn more about using custom shader with Meta's Spatial SDK here:
 * https://developers.meta.com/horizon/documentation/spatial-sdk/spatial-sdk-custom-shaders
 */

#version 430
#extension GL_ARB_separate_shader_objects: enable
#extension GL_ARB_shading_language_420pack: enable

layout (location = 0) out vec4 outColor;
layout (location = 0) in vec2 otc;

layout (binding = 0) uniform texture2D tex;
layout (binding = 1) uniform sampler samp;

void main() {
    vec4 pixel = texture(sampler2D(tex, samp), otc);

    // Add any additional processing to the panel texture here

    outColor = pixel;
}
