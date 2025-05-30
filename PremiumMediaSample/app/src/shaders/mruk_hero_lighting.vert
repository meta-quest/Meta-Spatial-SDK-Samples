#version 430
#extension GL_ARB_separate_shader_objects:enable
#extension GL_ARB_shading_language_420pack:enable

#include <common.glsl>
#include <app2vertex.glsl>

#include <customBindingVert.glsl>
#include <heroLighting_vert.glsl>

void main(){
  App2VertexUnpacked app=getApp2VertexUnpacked();

  vec4 wPos4=g_PrimitiveUniform.worldFromObject*vec4(app.position,1.f);
  vec3 worldPos=wPos4.xyz;
  vec3 worldNormal=normalize((transpose(g_PrimitiveUniform.objectFromWorld)*vec4(app.normal,0.f)).xyz);

  GenerateHeroLightVertexData(worldPos,worldNormal,heroLightingVertexOut.rectSpacePos,heroLightingVertexOut.rectSpaceNormal,heroLightingVertexOut.rectSpaceCamPos,heroLightingVertexOut.screenSize);

  gl_Position=getClipFromWorld()*wPos4;

  postprocessPosition(gl_Position);
}
