
# JFortniteParse

### An asset parser for ue4 assets for the jvm written in kotlin

**Note: This project is compatible and usable with java but you will need to use different methods**
#### Supported Games
I provide active support for the following games
- Fortnite (default)
- Valorant (use Ue4Version.GAME_VALORANT, for details read Usage below)
#### Features
- Load ue4 pak files and provide ways to handle them
- Load ue4 asset files (.uasset, .uexp, .ubulk) and provide ways to convert their exports
- Convert lots of ue4 **texture** formats to BufferedImages and png files
- Save **sound** files
- Load and export **static meshes** with their materials to pskx files
- Load and export **materials** with their textures to mat files
- Convert cosmetics into images providing all of their details with or without variants
- Locres support for asset files
- A file provider for providing comfortable loading of asset files
- **Valorant Support** Load valorant assets and pak files and convert its characters to images with their details and abilities
#### Usage
##### Use the file provider
```kotlin
//Create the file provider by giving it the game file folder
val provider = DefaultFileProvider(File(gameFileFolderPath))
// This contains a list with all encryption key guids needed for loading the pak files  
val requiredKeys = provider.requiredKeys() 
// You can submit a key by passing a valid guid and it's aes key to the submitKey method
provider.submitKey(FGuid(0u, 0u, 0u, 0u), "0x60b40115a36dd9f17cc4a352f03211e3a859ac664fef7e7200930f849fd8a980")  
// Load a package by it's path, it would also be possible with a gamefile object
val pkg = provider.loadGameFile("FortniteGame/Content/Athena/Items/Cosmetics/Characters/CID_144_Athena_Commando_M_SoccerDudeA.uasset") 
// Load a locres by it's language code, it would also be possible by it's path or a gamefile
val locres = provider.loadLocres(FnLanguage.DE)
// Save a file to a byte array by giving it the filepath, same possibilities as with the others
val fileData = provider.saveGameFile("FortniteGame/Config/DefaultGame.ini")
```
##### Use the file provider with Valorant
```kotlin
//Create the file provider by giving it the game file folder and passing valorant as game
val provider = DefaultFileProvider(File(gameFileFolderPath), Ue4Version.GAME_VALORANT)
//from here same procedure as above
```
##### Manually loading a pak file
```kotlin
val reader = PakFileReader(filePath)  
reader.aesKey = "0x..."  
reader.readIndex()
```
Note: Setting the aes key will implicitly check it and throw an exception if its not working with the pak
If you want to check whether a key is valid use:
```kotlin
if(reader.testAesKey("0x...")) {
//Do something
}
```
##### Extract an asset from the pak file
```kotlin
val file = reader.files[0]
val byteArray = reader.extract(file)
```
##### Manually load a package (can be either loaded from files or via byte arrays)
```kotlin
val pkg = Package(uassetFile, uexpFile, ubulkFile)
//or from byte arrays
val pkg2 = Package(uassetData, uexpData, ubulkData, fileName)
```
Note: If there is no ubulk you can pass null for it

##### Handling Packages and export them
Note: We have the variable pkg of the type Package in all of these examples

- Export packages as a json string
```kotlin
val jsonString = pkg.toJson()
``` 
- Export a texture
```kotlin
// This will throw an exception if there is no export of this type
val texture = pkg.getExportOfType<UTexture2D>()
//This is the safe variant which will return null in the case of no texture export
val texture2 = pkg.getExportOfTypeOrNull<UTexture2D>()

// Now lets convert the texture
val img = texture.toBufferedImage()
// Finally we can save this image to a file if we need
ImageIO.write(img, "png", File("test.png"))
```
- Export a sound wave
```kotlin
// This will throw an exception if there is no export of this type
val sound = pkg.getExportOfType<USoundWave>()
//This is the safe variant which will return null in the case of no texture export
val sound2 = pkg.getExportOfTypeOrNull<USoundWave>()

// Now lets export the sound
val soundWave = sound.convert()
// Finally we can save this sound wave to a file if we need
File("test.${soundWave.format}").writeBytes(soundWave.data)
```
- Export a item definition (cosmetic)

Note: This also needs a file provider to be able to load referenced assets like icons
```kotlin
// This will throw an exception if there is no export of this type
val itemDef = pkg.getExportOfType<AthenaItemDefinition>()
//This is the safe variant which will return null in the case of no item definition export
val itemDef2 = pkg.getExportOfTypeOrNull<AthenaItemDefinition>()

// Now create a item definition container, it will contain the item definitions icons and information
// It can be used for creating different types of images
val container = itemDef.createContainer(provider)
// This will create the image with variants if they're available, 
// otherwise a normal image will be generated
val img = itemDef.getImage() //if you don't want the variants image use itemDef.getImageNoVariants()
// This will generate a image for the featured or the daily shop
val shopFeaturedImg = itemDef.getShopFeaturedImage(2000) // 2000 is the price
val shopDailyImg = itemDef.getShopDailyImage(2000) // 2000 is the price

// Finally we can save a image to a file if we need
ImageIO.write(img, "png", File("test.png"))
```
- Export an valorant character

Note: This also needs a file provider to be able to load referenced assets like icons
```kotlin
// This will throw an exception if there is no export of this type
val character = pkg.getExportOfType<CharacterDataAsset>()
//This is the safe variant which will return null in the case of no character export
val character2 = pkg.getExportOfTypeOrNull<CharacterDataAsset>()

// Now create a character container, it will contain the characters icons and information
val container = character.createContainer(provider)

//This will generate the image out of the information stored in the container
val img = container.getImage()

// Finally we can save a image to a file if we need
ImageIO.write(img, "png", File("test.png"))
```
##### Handling Locres files
We will use a variable locres of the type Locres and a pkg of the type Package
```kotlin
// This will apply the locres to all of the packages export,
// which means that all texts will get exported with the strings from the given locres
pkg.applyLocres(locres)
```
#### Dependency
##### Maven
- Add the repository
```xml
<repositories>
	<repository>
		<id>bintray-fungamesleaks-mavenRepo</id>
		<name>bintray</name>
		<url>https://dl.bintray.com/fungamesleaks/mavenRepo</url>
	</repository>
</repositories>
```
- Add the dependency
```xml
<dependency>
	<groupId>me.fungames</groupId>
	<artifactId>JFortniteParse</artifactId>
	<version>3.1.15</version>
</dependency>
```
##### Gradle
- Add the repository
```groovy
repositories {
	maven {
		url  "https://dl.bintray.com/fungamesleaks/mavenRepo"
	}
}
```
- Add the dependency
```groovy
implementation 'me.fungames:JFortniteParse:3.1.15'
```

### Fortnite Text Hotfixes
I'm also providing a plugin for Fortnite's text hotfixes

#### Usage
This example will use a variable locres of the type Locres
You will need an internet connection to use this
```kotlin
locres.applyHotfixes()
```
This method will replace all entries from the locres that are overwritten by the hotfix
Afterwards you can use the locres and apply it to instances of Package etc.
Texts will be exported with the hotfixed ones
- Add the dependency
**Its located in the same repository as the main parser**
##### Maven
```xml
<dependency>
  <groupId>me.fungames</groupId>
  <artifactId>JFortniteParseTextHotfixes</artifactId>
  <version>1.3.9</version>
</dependency>
```
##### Gradle
```groovy
implementation 'me.fungames:JFortniteParseTextHotfixes:1.3.9'
```

### Contributors
- [Asval](https://github.com/iAmAsval)

