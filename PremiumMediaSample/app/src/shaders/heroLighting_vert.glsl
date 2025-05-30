layout(location = 10) out struct {
  vec3 rectSpacePos;
  vec3 rectSpaceNormal;
  vec3 rectSpaceCamPos;
  vec3 screenSize;
} heroLightingVertexOut;

#include <heroLighting.glsl>
