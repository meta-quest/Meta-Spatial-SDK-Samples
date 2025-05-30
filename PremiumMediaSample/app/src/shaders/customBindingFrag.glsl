layout (std140, set = 3, binding = 0) uniform MaterialUniform {
  vec4 emissiveFactor;
  vec4 albedoFactor;
  vec4 matParams;
  vec4 stereoParams;

} g_MaterialUniform;

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
#if VERTEX_FORMAT_TWOUV_TANGENT == 1
  vec4 tangent;
#endif
} vertexOut;

layout (location = 0) out vec4 outColor;

layout (set = 3, binding = 1) uniform sampler2D albedoSampler;
layout (set = 3, binding = 2) uniform sampler2D roughnessMetallicTexture;
layout (set = 3, binding = 3) uniform sampler2D emissive;
layout (set = 3, binding = 4) uniform sampler2D occlusion;
