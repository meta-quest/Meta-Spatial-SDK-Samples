#version 430
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable


#include <common.glsl>
#include <app2vertex.glsl>

layout(location = 0) out struct {
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

layout (std140, set = 3, binding = 0) uniform MaterialUniform {
  vec4 stereoParams;
  vec4 customParams;

} g_MaterialUniform;

vec2 stereo(vec2 uv) {
  return getStereoPassId() * g_MaterialUniform.stereoParams.xy + uv * g_MaterialUniform.stereoParams.zw;
}

void main() {
  App2VertexUnpacked app = getApp2VertexUnpacked();

  vec4 wPos4 = g_PrimitiveUniform.worldFromObject * vec4(app.position, 1.0f);
  vertexOut.albedoCoord = stereo(app.uv);
  vertexOut.lighting = app.incomingLighting;
  vertexOut.worldPosition = wPos4.xyz;
  vertexOut.worldNormal = normalize((transpose(g_PrimitiveUniform.objectFromWorld) * vec4(app.normal, 0.0f) ).xyz);

  gl_Position = getClipFromWorld() * wPos4;

  postprocessPosition(gl_Position);
}
