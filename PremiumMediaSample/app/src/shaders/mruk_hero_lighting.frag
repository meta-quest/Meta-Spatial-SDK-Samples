#version 400
#extension GL_ARB_separate_shader_objects:enable
#extension GL_ARB_shading_language_420pack:enable

#include <Uniforms.glsl>
#include <customBindingFrag.glsl>

#include <heroLighting_frag.glsl>

void main(){

    vec4 lightingParams=g_MaterialUniform.matParams;

    if(lightingParams.x<=.01){
        discard;
    }

    vec4 heroLighting=HeroLightingVertex(emissive,heroLightingVertexOut.rectSpacePos,heroLightingVertexOut.rectSpaceNormal,heroLightingVertexOut.rectSpaceCamPos,heroLightingVertexOut.screenSize);

    if(heroLighting.a<=.01){
        discard;
    }

    outColor.rgb=heroLighting.rgb;

    outColor.a=heroLighting.a;

}
