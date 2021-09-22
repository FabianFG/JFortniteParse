# JFortniteParse

### An asset parser for UE4 assets for the JVM written in Kotlin

**Note: This project is compatible and usable with Java but you will need to use different methods**
#### Supported Games
I provide active support for the following games:
- Fortnite (default)
- Valorant (use Ue4Version.GAME_VALORANT, for details read Usage below)
#### Features
- Load UE4 pak files and provide ways to handle them
- Load UE4 asset files (.uasset, .umap, .uexp, .ubulk) and provide ways to convert their exports
- Convert lots of UE4 **texture** formats to BufferedImages and png files
- Save **sound** files
- Load and export **static meshes** with their materials to .pskx files
- Load and export **materials** with their textures to .mat files
- Convert cosmetics into images providing all of their details with or without variants
- Locres (Translation files) support for asset files
- AssetRegistry.bin support (used to find export classes without needing to serialize the asset)
- A file provider for providing comfortable loading of asset files
- **Valorant Support:** Load Valorant assets and pak files and convert its characters to images with their details and abilities
#### Usage
##### Use the file provider
```kotlin
// Create the file provider by giving it the game file folder
val provider = DefaultFileProvider(File(gameFileFolderPath))
// This contains a list with all encryption key guids needed for loading the pak files  
val requiredKeys = provider.requiredKeys() 
// You can submit a key by passing a valid guid and its AES key to the submitKey method
provider.submitKey(FGuid.mainGuid, "0x60b40115a36dd9f17cc4a352f03211e3a859ac664fef7e7200930f849fd8a980")  
// Load a package by its path or a GameFile object
val pkg = provider.loadGameFile("FortniteGame/Content/Athena/Items/Cosmetics/Characters/CID_144_Athena_Commando_M_SoccerDudeA.uasset") 
// Load a locres by its language code, its path, or a GameFile object
val locres = provider.loadLocres(FnLanguage.DE)
// Save a file to a byte array by giving its path or its GameFile object
val fileData = provider.saveGameFile("FortniteGame/Config/DefaultGame.ini")
```
##### Use the file provider with Valorant
```kotlin
// Create the file provider by giving it the game file folder and passing Valorant as game
val provider = DefaultFileProvider(File(gameFileFolderPath), Ue4Version.GAME_VALORANT)
// from here same procedure as above
```
##### Manually loading a pak file
```kotlin
val reader = PakFileReader(filePath)  
reader.aesKey = "0x..."  
reader.readIndex()
```
Note: Setting the AES key will implicitly check it and throw an exception if its not working with the pak
If you want to check whether a key is valid use:
```kotlin
if (reader.testAesKey("0x...")) {
    // Do something
}
```
##### Extract an asset from the pak file
```kotlin
val file = reader.files[0]
val byteArray = reader.extract(file)
```
##### Manually load a package (can be either loaded from files or via byte arrays)
```kotlin
val pkg = Package(uassetOrUmapFile, uexpFile, ubulkFile)
// or from byte arrays
val pkg2 = Package(uassetOrUmapData, uexpData, ubulkData, fileName)
```
Note: If there is no .ubulk you can pass null for it

##### Handling Packages and export them
Note: We have the variable pkg of the type Package in all of these examples

- Export a package as JSON string
```kotlin
val jsonString = pkg.toJson()
``` 
- Export a texture
```kotlin
// This will throw an exception if there is no export of this type
val texture = pkg.getExportOfType<UTexture2D>()
// This is the safe variant which will return null in the case of no texture export
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
// This is the safe variant which will return null in the case of no texture export
val sound2 = pkg.getExportOfTypeOrNull<USoundWave>()

// Now lets export the sound
val soundWave = sound.convert()
// Finally we can save this sound wave to a file if we need
File("${soundWave.name}.${soundWave.format}").writeBytes(soundWave.data)
```
- Export a Fortnite item definition (cosmetic)
```kotlin
// This will throw an exception if there is no export of this type
val itemDef = pkg.getExportOfType<AthenaItemDefinition>()
// This is the safe variant which will return null in the case of no item definition export
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
- Export a Valorant character
```kotlin
// This will throw an exception if there is no export of this type
val character = pkg.getExportOfType<CharacterDataAsset>()
// This is the safe variant which will return null in the case of no character export
val character2 = pkg.getExportOfTypeOrNull<CharacterDataAsset>()

// Now create a character container, it will contain the characters icons and information
val container = character.createContainer()

// This will generate the image out of the information stored in the container
val img = container.getImage()

// Finally we can save a image to a file if we need
ImageIO.write(img, "png", File("test.png"))
```
##### Handling Locres files
We will use a variable locres of the type Locres and a text of the type FText
```kotlin
// This will return the string referenced by the FText in the given Locres
val translatedString = text.textForLocres(locres)
```
#### Dependency
##### Maven
- Add the repository
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```
- Add the dependency
```xml
<dependency>
	<groupId>com.github.FabianFG</groupId>
	<artifactId>JFortniteParse</artifactId>
	<version>3.6.5</version>
</dependency>
```
##### Gradle
- Add the repository
```groovy
repositories {
	maven { url "https://jitpack.io" }
}
```
- Add the dependency
```groovy
implementation 'com.github.FabianFG:JFortniteParse:3.6.5'
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
**It's located in the same repository as the main parser**
##### Maven
```xml
<dependency>
  <groupId>com.github.FabianFG</groupId>
  <artifactId>JFortniteParseTextHotfixes</artifactId>
  <version>1.5.1</version>
</dependency>
```
##### Gradle
```groovy
implementation 'com.github.FabianFG:JFortniteParseTextHotfixes:1.5.1'
```

### Contributors
- [Asval](https://github.com/iAmAsval)
- [Amrsatrio](https://github.com/Amrsatrio)

