#### Step 1. Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
		maven { url 'https://www.jitpack.io' }
	}
}
```
#### Step 2. Add the dependency
```groovy
   dependencies {
       implementation 'com.github.andnux.ZRouter:api:0.0.2'
       annotationProcessor 'com.github.andnux.ZRouter:compiler:0.0.2'
   }
```