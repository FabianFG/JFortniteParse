# JFortniteParse

A java based Unreal Engine Resource Packages parser

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Building the source

* This project was made with eclipse so if you use eclipse you can simply import the project
* If you use an different IDE you need to create a project based on the source and add all jars from the /lib/ folder as dependencies
* Also this project needs to be a maven project

pom.xml should be included in the downloaded sources

## Use the precompiled sources

* Download the latest build from the releases page as a .jar file
* Add it as library to your own project

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

### Usage

Some examples for pak files
```
//Create the reader by giving it a pak file
//Note: This will only read the pak header, **not the file list**

PakFileReader reader = new PakFileReader(String pakFilePath);

//Test whether the pak is encrypted and load the index

//If the pak is encrypted
if((reader.isEncrypted() && reader.testKey("0x67d061efa8e049f7c62f1c460f14cd5ad7e601c13f3fb66f0fb090b72b721acc"))) {
	reader.setKey("0x67d061efa8e049f7c62f1c460f14cd5ad7e601c13f3fb66f0fb090b72b721acc");
	List<GameFile> files = reader.readIndex(); //Note: You can also get this list afterwards by calling: reader.getFileList();
}
//If the pak isn't encrypted
if(!reader.isEncrypted()) {
	reader.readIndex();
}

//Extract the first file in the pak
byte[] fileData = reader.extractSelected(files.get(0));

//If the game file is an uasset file, the GameFile class will have the uexp and (optional) the ubulk stored as members
GameFile testPackage = files.get(0);
byte[] uasset = reader.extractSelected(testPackage);
byte[] uexp = null;
if(testPackage.hasUexp()) {
	uexp = reader.extractSelected(testPackage.getUexp());
}
if(testPackage.hasUbulk()) {
	ubulk = reader.extractSelected(testPackage.getUbulk());
}
```

Some examples for parsing packages
```
//You can create a package from files and byte[]
//Note: If you don't have a ubulk simply use null as ubulk;

//From Files
Package p = Package.fromFiles(File uassetFile, File uexpFile, File ubulkFile);

//From byte[]
Package p = new Package(String name, byte[] uasset, byte[] uexp, byte[] ubulk);

//Read the export of an Package
for(Object export : p.getExports()) {
	if(export instanceof UTexture2D) {
		UTexture2D textureObject = (UTexture2D) export;
		BufferedImage texture = Texture2DToBufferedImage.readTexture(textureObject);
		ImageIO.write(texture, "png", new File("test.png"));
	}
}
```


## Authors

* **FunGamesLeaks** - [FunGamesLeaks](https://github.com/FunGamesLeaks)
