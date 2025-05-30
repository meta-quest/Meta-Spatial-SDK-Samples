# TweenEngine

Uses Universal Tween Engine library by Aurelien Ribon (more docs about timelines, repeats, delays etc):
https://github.com/AurelienRibon/universal-tween-engine

# How to install
In your main activity, install the engine system which keeps track of frame timings
```kotlin
systemManager.registerSystem(TweenEngineSystem())
```

## Examples for Meta Spatial SDK

Tween Position for 1 second
```kotlin
Tween.to(TweenTransform(entity),TweenTransform.POSE_XYZ_XYZ,1f)
    .target(Vector3(1f,0f,0f))
    .ease(TweenEquations.easeOutBack)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Rotation Quaternion (Invalid 'poses' from the framework sometimes when rebuilding the quaternion...)
```kotlin
Tween.to(TweenTransform(entity),TweenTransform.ROTATION_WXYZ,1f)
    .target(targetQuaternion)
    .ease(TweenEquations.easeOutBack)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Rotation Euler
```kotlin
Tween.to(TweenTransform(entity),TweenTransform.ROTATION_XYZ,1f)
    .target(Vector3(90f,0f,0f))
    .ease(TweenEquations.easeOutBack)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Pose
```kotlin
Tween.to(TweenTransform(entity),TweenTransform.POSE_XYZ_XYZ,1f)
    .target(Pose(Vector3(0f,1f,0.1f),Quaternion(1f,0f,0f,0f)))
    .ease(TweenEquations.easeOutBack)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Scale Value
```kotlin
Tween.to(TweenScale(entity),TweenScale.SCALE_VALUE,1f)
    .target(2f)
    .ease(TweenEquations.easeInOutQuad)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Scale XYZ
```kotlin
Tween.to(TweenScale(entity),TweenScale.SCALE_XYZ,1f)
    .target(Vector3(2f,1f,0.5f))
    .ease(TweenEquations.easeInOutQuad)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween SceneMaterial attribute "baseColor" from 0,0,0,0 to 1,0.5,1,0.5
```kotlin
Tween.to(TweenSceneMaterial(sceneMaterial, "baseColor", Vector4(0f,0f,0f,0f)),TweenSceneMaterial.SET_ATTRIBUTE_VECTOR4,1f)
    .target(1f, 0.5f, 1f, 0.5f) // To value (can also put in vector4)
    .ease(TweenEquations.easeNone)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```
Tween Material base color to 1f,0f,0f,1f (red full alpha)
```kotlin
Tween.to(TweenMaterial(material),TweenTransform.BASE_COLOR_COLOR4_RGBA,1f)
    .target(Vector4(1f,0f,0f,1f)) // To value, (can also put 4 floats)
    .ease(TweenEquations.easeNone)
    .start(systemManager.findSystem<TweenEngineSystem>().tweenManager)
```

# Ease Equations:
- Linear easeNone = Linear.INOUT;
- Quad easeInQuad = Quad.IN;
- Quad easeOutQuad = Quad.OUT;
- Quad easeInOutQuad = Quad.INOUT;
- Cubic easeInCubic = Cubic.IN;
- Cubic easeOutCubic = Cubic.OUT;
- Cubic easeInOutCubic = Cubic.INOUT;
- Quart easeInQuart = Quart.IN;
- Quart easeOutQuart = Quart.OUT;
- Quart easeInOutQuart = Quart.INOUT;
- Quint easeInQuint = Quint.IN;
- Quint easeOutQuint = Quint.OUT;
- Quint easeInOutQuint = Quint.INOUT;
- Circ easeInCirc = Circ.IN;
- Circ easeOutCirc = Circ.OUT;
- Circ easeInOutCirc = Circ.INOUT;
- Sine easeInSine = Sine.IN;
- Sine easeOutSine = Sine.OUT;
- Sine easeInOutSine = Sine.INOUT;
- Expo easeInExpo = Expo.IN;
- Expo easeOutExpo = Expo.OUT;
- Expo easeInOutExpo = Expo.INOUT;
- Back easeInBack = Back.IN;
- Back easeOutBack = Back.OUT;
- Back easeInOutBack = Back.INOUT;
- Bounce easeInBounce = Bounce.IN;
- Bounce easeOutBounce = Bounce.OUT;
- Bounce easeInOutBounce = Bounce.INOUT;
- Elastic easeInElastic = Elastic.IN;
- Elastic easeOutElastic = Elastic.OUT;
- Elastic easeInOutElastic = Elastic.INOUT;
