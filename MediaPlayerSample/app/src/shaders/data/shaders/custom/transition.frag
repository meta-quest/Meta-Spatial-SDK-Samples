
#version 400
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable

#include <metaSpatialSdkFragmentBase.glsl>
#include <common.glsl>
#include <metaSpatialSdkCommon.glsl>

vec3 map(vec3 value, vec3 inMin, vec3 inMax, vec3 outMin, vec3 outMax) {
  return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

void main() {
  vec4 albedo = texture(albedoSampler, vertexOut.albedoCoord);

  if(albedo.a < 0.0){
    discard;
  }

  vec4 screenMask = texture(occlusion, vertexOut.albedoCoord);

  float feather = 0.1;
  float fade = 0.033;
  vec3 maskCenterPoint = vec3(0, 1.32, 4.85); //position of the screen.

  // calculate the mask
  float maskAmount = g_MaterialUniform.matParams.x;
  float dist = distance(vertexOut.worldPosition, maskCenterPoint);
  float MAX_DIST = 0.2;
  dist = MAX_DIST / dist;
  dist = clamp(dist, 0, 1);
  float alpha = 1.0 - smoothstep(dist, dist+feather, maskAmount);

  // sample average emissive
  vec4 tex0 = texture(emissive, vec2(0.2, 0.2), 10.0);
  vec4 tex1 = texture(emissive, vec2(0.8, 0.2), 10.0);
  vec4 tex2 = texture(emissive, vec2(0.2, 0.8), 10.0);
  vec4 tex3 = texture(emissive, vec2(0.8, 0.8), 10.0);
  vec4 tex4 = texture(emissive, vec2(0.5, 0.5), 10.0);
  vec4 emissive = (tex0 + tex1 + tex2 + tex3 + tex4) / 5.0;

  // remap to dim and ensure the minimum is no less than the faded room
  emissive.rgb = map(emissive.rgb, vec3(0.0), vec3(1.0), vec3(fade), vec3(0.8));

  // combine the emissive screen color with the faded room.
  vec3 fadedRoom = albedo.rgb * fade;
  vec3 screenLitRoom = albedo.rgb * emissive.rgb;

  outColor.rgb = mix(fadedRoom, screenLitRoom, screenMask.x);
  outColor.a = albedo.a * alpha;
}
