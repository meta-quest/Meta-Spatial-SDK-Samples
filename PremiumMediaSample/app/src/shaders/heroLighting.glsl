
// ******* Hero Lighting v2 ******* //

#define PI 3.1415926538

// Utility Functions
const float Brightness_VR=1.;
const float Brightness_MR=5.;

float GetLuminance(vec3 color){
	return dot(color,vec3(.2126,.7152,.0722));
}

vec3 GetEyePosition(){
	return getStereoPassId()==0?g_ViewUniform.eyeCenter0.xyz:g_ViewUniform.eyeCenter1.xyz;
}

float PingPong(float val){
	return abs(mod(val,2.)-1.);
}

float clamp01(float val){
	return clamp(val,0.,1.);
}

float SmoothFloat(float value)
{
	return smoothstep(0.,1.,value);
}

float Remap(float value,float inMin,float inMax,float outMin,float outMax)
{
	return(value-inMin)/(inMax-inMin)*(outMax-outMin)+outMin;
}

vec3 RemapVec3(vec3 value,float inMin,float inMax,float outMin,float outMax)
{
	vec3 val=vec3(0.,0.,0.);
	val.r=clamp01(Remap(value.r,inMin,inMax,outMin,outMax));
	val.g=clamp01(Remap(value.g,inMin,inMax,outMin,outMax));
	val.b=clamp01(Remap(value.b,inMin,inMax,outMin,outMax));
	return val;
}

float LightFalloff(float dist,float maxRange,float brightness)
{
	dist=max(dist,.0001);

	float intensity=brightness/(dist*dist);

	return intensity;
}

// Matrix

// implementation of mul for glsl
vec3 mul(mat3 matrix,vec3 vector){
	return matrix*vector;
}

mat3 GetRotationMatrix(vec3 eulerAngles)
{

	eulerAngles=eulerAngles*3.14159265359/180.;

	float pitch=eulerAngles.x;
	float yaw=eulerAngles.y;
	float roll=eulerAngles.z;

	// Compute trigonometric values
	float sinPitch=sin(pitch);
	float cosPitch=cos(pitch);
	float sinYaw=sin(yaw);
	float cosYaw=cos(yaw);
	float sinRoll=sin(roll);
	float cosRoll=cos(roll);

	// Rotation matrix for ZYX order (yaw, pitch, roll)
	mat3 rotationMatrix=mat3(
		cosYaw*cosRoll,cosYaw*sinRoll,-sinYaw,
		sinPitch*sinYaw*cosRoll-cosPitch*sinRoll,sinPitch*sinYaw*sinRoll+cosPitch*cosRoll,sinPitch*cosYaw,
		cosPitch*sinYaw*cosRoll+sinPitch*sinRoll,cosPitch*sinYaw*sinRoll-sinPitch*cosRoll,cosPitch*cosYaw
	);

	return rotationMatrix;
}

// SDF

vec3 boxClosestPoint(vec3 p,vec3 rad)
{
	return clamp(p,-rad,rad);
}

float boxDistance(vec3 p,vec3 rad)
{
	vec3 d=abs(p)-rad;
	return length(max(d,0.))+min(max(max(d.x,d.y),d.z),0.);
}

vec3 PlaneIntersect(vec3 planeP,vec3 planeN,vec3 rayP,vec3 rayD)
{
	float d=dot(planeP,-planeN);
	float t=-(d+dot(rayP,planeN))/dot(rayD,planeN);
	return rayP+t*rayD;
}

//UVs

vec2 StereoUV(vec2 uv){
	return getStereoPassId()*g_MaterialUniform.stereoParams.xy+uv*g_MaterialUniform.stereoParams.zw;// use zero instead of  getStereoPassId() to use first eye only
}

vec2 StereoUV_EyeZero(vec2 uv){
	return 0.*g_MaterialUniform.stereoParams.xy+uv*g_MaterialUniform.stereoParams.zw;
}

vec2 WallUV(vec3 dir)
{
	// Convert view direction to spherical coordinates
	float theta=atan(dir.z,dir.x);// Azimuthal angle (longitude)
	float phi=asin(dir.y);// Polar angle (latitude)

	// Normalize theta to [0, 1] range (longitude)
	float u=(theta/(2.*PI))+.5;// Wrap theta from -pi to pi to [0, 1] //3.14159265359

	// Normalize phi to [0, 1] range (latitude)
	float v=(phi/(PI))+.5;// Wrap phi from -pi/2 to pi/2 to [0, 1]

	return vec2(u,v);
}

vec2 FloorUV(vec3 dir)
{
	// Convert view direction to spherical coordinates
	float theta=atan(-dir.y,dir.x);// Azimuthal angle (longitude)
	float phi=asin(-dir.z);// Polar angle (latitude)

	// Normalize theta to [0, 1] range (longitude)
	float u=(theta/(2.*PI))+.5;// Wrap theta from -pi to pi to [0, 1]

	// Normalize phi to [0, 1] range (latitude)
	float v=(phi/(PI))*2;// Wrap phi from -pi/2 to pi/2 to [0, 1]

	return vec2(u,v);
}

vec2 GetSurfaceUV_Floor(vec3 localPos){

	vec2 surfaceUV=localPos.xz;

	surfaceUV.x=Remap(surfaceUV.x,-1.,1.,0.,1.);
	surfaceUV.y=Remap(surfaceUV.y,-1.,1.,0.,1.)+.5;

	if(localPos.y<=0){
		surfaceUV.y=1-surfaceUV.y;
	}

	surfaceUV.x=PingPong(1-surfaceUV.x);
	surfaceUV.x=Remap(surfaceUV.x,0.,1.,.01,.99);

	surfaceUV.y=PingPong(surfaceUV.y);
	surfaceUV.y=Remap(surfaceUV.y,0.,1.,.01,.99);

	return StereoUV_EyeZero(surfaceUV);

}

vec2 GetSurfaceUV_Wall(vec3 localPos){

	vec2 surfaceUV=localPos.zy;

	surfaceUV.x=Remap(surfaceUV.x,-1.,1.,0.,1.)+.5;
	surfaceUV.y=Remap(surfaceUV.y,-1.,1.,0.,1.);

	if(localPos.x<=0){
		surfaceUV.x=1-surfaceUV.x;
	}

	surfaceUV.x=PingPong(1-surfaceUV.x);
	surfaceUV.x=Remap(surfaceUV.x,0.,1.,.01,.99);

	surfaceUV.y=PingPong(surfaceUV.y);
	surfaceUV.y=Remap(surfaceUV.y,0.,1.,.01,.99);

	return StereoUV_EyeZero(surfaceUV);

}

vec2 GetSurfaceUV_Back(vec3 localPos){

	vec2 surfaceUV=localPos.xy*.5;

	surfaceUV.x=Remap(surfaceUV.x,-1.,1.,0.,1.);// + 0.5;
	surfaceUV.y=Remap(surfaceUV.y,-1.,1.,0.,1.);

	surfaceUV.x=clamp01(surfaceUV.x);
	surfaceUV.y=clamp01(1-surfaceUV.y);

	return StereoUV_EyeZero(surfaceUV);

}

// Texture Sampling

vec4 MipSample(sampler2D tex,vec2 uv,float lod)
{
	return textureLod(tex,uv,lod);//texture(tex, uv);// vec4(0.2);// textureLod(tex, uv, lod);
}

// Local Rect Space Methods

vec3 GetLocalRectNormal(vec3 worldNormal,vec3 rectRot,vec3 rectSize,mat3 rotationMatrix)
{

	return mul(rotationMatrix,worldNormal);

}

vec3 GetLocalRectPos(vec3 worldPos,vec3 rectPos,vec3 rectRot,vec3 rectSize,mat3 rotationMatrix)
{

	vec3 localPos=worldPos-rectPos;

	vec3 localNormal=mul(rotationMatrix,localPos);

	return localNormal;

}

// Reflections

vec4 GetReflectionUV(vec3 rectSpacePos,vec3 rectSpaceNormal,vec3 rectSpaceCamPos,vec2 screenSize)
{

	// REFLECTIONS
	vec3 dir=rectSpacePos-rectSpaceCamPos;
	vec3 reflectedPos=reflect(dir,-normalize(rectSpaceNormal));

	vec3 planeIntersect=PlaneIntersect(vec3(0,0,0),vec3(0,0,1),rectSpacePos,reflectedPos);

	vec2 reflectionUV=planeIntersect.xy+(screenSize*.5);

	reflectionUV*=1/screenSize;

	return vec4(reflectionUV.x,reflectionUV.y,planeIntersect.x,planeIntersect.y);
}

// Shading

float GetShading(vec3 rectSpacePos,vec3 rectSpaceNormal){

	vec3 screenDir=normalize(rectSpacePos);

	float dotProd=dot(-screenDir,rectSpaceNormal);

	return clamp01(dotProd);

}

// Lighting

// ****** Vertex Based Hero Lighting ****** //

vec4 HeroLightingVertex(sampler2D tex,vec3 rectSpacePos,vec3 rectSpaceNormal,vec3 rectSpaceCamPos,vec3 screenSize){

	vec4 col=vec4(0.);

	bool isfloor=dot(rectSpaceNormal,vec3(0,1,0))>=.99;
	bool iswall=dot(rectSpaceNormal,vec3(1,0,0))>=.99;
	bool isback=dot(rectSpaceNormal,vec3(0,0,1))>=.99;

	bool isFrontFace=rectSpacePos.z<0.;

	vec4 lightingParams=g_MaterialUniform.matParams;

	float lightingStrength=lightingParams.x;

	if(lightingStrength<=0.){
		return vec4(0.);
	}

	vec3 screenHalfSize=screenSize*.5;
	float screenArea=screenSize.x*screenSize.y;
	float screenAreaAlpha=screenSize.x*screenSize.y*lightingStrength;
	float screenRatio=screenSize.x/screenSize.y;
	float screenScale=screenSize.x*screenRatio*lightingStrength;
	float reflectionScale=screenSize.x*screenRatio;

	float _MipLevel=8.;

	// Light and Shadow

	float falloff=8.*screenArea*lightingParams.x;
	float brightness=2.*lightingParams.x;

	vec3 lightScreenSpacePos=rectSpacePos;
	float lightScreenDistance=boxDistance(lightScreenSpacePos,screenHalfSize)+.8;
	float light=LightFalloff(lightScreenDistance,falloff,brightness);

	vec3 shadowSpacePos=rectSpacePos*vec3(1.,1.,2.);
	float shadowScreenDistance=boxDistance(shadowSpacePos,screenHalfSize);

	shadowScreenDistance=Remap(shadowScreenDistance,0.,.5,0.,1.);
	float shadow=pow(shadowScreenDistance,.3);
	shadow=clamp01(shadow);

	float lightShadow=light*shadow;

	vec3 floorDotLocalPos=normalize(rectSpacePos);

	float floorDot=dot(floorDotLocalPos,vec3(0,1,0));
	floorDot=1-clamp01(abs(floorDot));

	// Sample Mips

	// **** Optional surface UUs for walls and floors **** //

	//vec2 surfaceUV_wall = GetSurfaceUV_Wall(rectSpacePos / screenSize.x);
	//vec2 surfaceUV_back = GetSurfaceUV_Back(rectSpacePos / screenSize.x );
	//vec2 surfaceUV_floor = GetSurfaceUV_Floor(rectSpacePos  / screenSize.x);

	vec2 surfaceUV=GetSurfaceUV_Back(rectSpacePos/screenSize.x);

	vec4 mipA=MipSample(tex,surfaceUV,_MipLevel);
	vec4 mipB=MipSample(tex,surfaceUV,_MipLevel+1);

	col=mix(mipA,mipB,.6);

	// Multiply by light and shadow
	col=col*lightShadow;

	// Reflections

	float reflectionMask=0.;
	vec4 reflectionColor=vec4(0.);

	if(isfloor){

		reflectionMask=1.;

		vec4 reflectionData=GetReflectionUV(rectSpacePos,rectSpaceNormal,rectSpaceCamPos,screenSize.xy);

		vec2 reflectionUV=reflectionData.xy;

		reflectionUV.x=clamp(reflectionUV.x,0.,1.);
		reflectionUV.y=clamp(reflectionUV.y,0.,1.);

		reflectionUV.y=1-reflectionUV.y;// flipped y in glsl

		float edgeBleed=3.;
		reflectionMask*=clamp01(sin(reflectionUV.y*3.14159)*edgeBleed);
		reflectionMask*=clamp01(sin(reflectionUV.x*3.14159)*edgeBleed);// multiplied by 2 for stereo

		reflectionMask*=rectSpacePos.z>0.?0.:1.;// remove reflection from backface

		float reflectionFade=abs(rectSpacePos.y-reflectionData.w);
		reflectionFade=LightFalloff(reflectionFade,screenHalfSize.y,1.);
		reflectionFade*=reflectionUV.y;

		reflectionMask*=clamp01(reflectionFade);
		reflectionMask=clamp01(reflectionMask);

		vec2 reflectionStereoUV=StereoUV(reflectionData.xy);
		reflectionStereoUV.y=1-reflectionStereoUV.y;// flipped y in gl
		reflectionStereoUV.y=clamp01(reflectionStereoUV.y);

		reflectionColor=texture(tex,reflectionStereoUV);
		reflectionColor*=reflectionMask;

	}

	col.a=lightShadow*GetLuminance(col.rgb)+(reflectionMask*lightingParams.x);

	col.rgb=col.rgb+(reflectionColor.rgb*.25);

	return col;

}

vec4 HeroLightingPixel(sampler2D tex,vec3 worldPos,vec3 worldNormal){

	vec4 screenPositionData=g_MaterialUniform.emissiveFactor;
	vec4 screenRotationData=g_MaterialUniform.albedoFactor;

	vec3 screenPosition=screenPositionData.xyz;

	vec3 screenAngles=-screenRotationData.xyz;
	mat3 rotationMatrix=GetRotationMatrix(screenAngles);

	vec3 screenSize=vec3(screenPositionData.w,screenRotationData.w,0.);

	vec3 worldSpaceCamPos=GetEyePosition();
	vec3 rectSpaceCamPos=GetLocalRectPos(worldSpaceCamPos,screenPosition,screenAngles,screenSize,rotationMatrix);

	vec3 rectSpacePos=GetLocalRectPos(worldPos,screenPosition,screenAngles,screenSize,rotationMatrix);
	vec3 rectSpaceNormal=GetLocalRectNormal(worldNormal,screenAngles,screenSize,rotationMatrix);

	vec3 sphereDir=normalize(rectSpacePos);

	vec2 sphereUV=WallUV(sphereDir);
	sphereUV.x=sphereUV.x*2;
	if(sphereUV.x>1)
	sphereUV.x=1-fract(sphereUV.x);

	sphereUV.y=1-sphereUV.y;
	sphereUV=StereoUV_EyeZero(sphereUV);

	vec2 sphereUV_Floor=FloorUV(sphereDir);
	sphereUV_Floor.y=abs(sphereUV_Floor.y);
	sphereUV_Floor.x=1-sphereUV_Floor.x;

	return HeroLightingVertex(tex,rectSpacePos,rectSpaceNormal,rectSpaceCamPos,screenSize);

}

void GenerateHeroLightVertexData(vec3 worldPos,vec3 worldNormal,out vec3 rectSpacePos,out vec3 rectSpaceNormal,out vec3 rectSpaceCamPos,out vec3 screenSize)
{

	vec4 screenPositionData=g_MaterialUniform.emissiveFactor;
	vec4 screenRotationData=g_MaterialUniform.albedoFactor;

	vec3 screenPosition=screenPositionData.xyz;
	vec3 screenAngles=-screenRotationData.xyz;
	mat3 rotationMatrix=GetRotationMatrix(screenAngles);

	screenSize=vec3(screenPositionData.w,screenRotationData.w,0.);

	rectSpacePos=GetLocalRectPos(worldPos,screenPosition,screenAngles,screenSize,rotationMatrix);
	rectSpaceNormal=GetLocalRectNormal(worldNormal,screenAngles,screenSize,rotationMatrix);
	rectSpaceCamPos=GetLocalRectPos(GetEyePosition(),screenPosition,screenAngles,screenSize,rotationMatrix);

}
