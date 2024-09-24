#version 400
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable

#include <common.glsl>

layout (std140, set = 3, binding = 0) uniform MaterialUniform {
  vec4 stereoParams;
  vec4 customParams;

} g_MaterialUniform;

layout (set = 3, binding = 1) uniform sampler2D albedoSampler;

layout(location = 0) in struct {
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

  vec4 pixel = texture(albedoSampler, vertexOut.albedoCoord);

  //direction the transition will start
  vec3 direction = vec3(0.0, 0.0, 1.0);

  //angular distance the vetex is from the direction, from -1 to 1
  float d = dot(vertexOut.worldNormal, direction);
  d = (d+1.0)*0.5; //normalise the dot product to 0 to 1

  float amount = clamp(1.0-g_MaterialUniform.customParams.x, 0.0, 1.0);
  float feather = 0.05;
  float alpha = smoothstep(d-feather, d+feather, amount);

  outColor.rgba = vec4(pixel.rgb, alpha);
}
