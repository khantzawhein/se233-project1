# Photo Editor (A project of SE 233)

![Logo](./package/macos/PhotoEditor-background.png)

## Introduction
This is a project of SE 233, which is a photo editor. It can be used to batch edit photos, such as add watermarks, resizing, etc. It is a GUI application, and it is written in Java.

This application utilize multi-threading to improve the performance of the application. It also uses MVC design pattern to make the code more readable and maintainable.

## How to use
### Compile & Run
To compile and run the project, you need to have JDK 20 or above installed. Then, you can use the following command to compile and run the project
Run the `Launcher.java` from the `src/main/java/com/se233/PhotoEditor` directory using IntelliJ.

### Package
To package the project, you need to have JDK 20 or above installed. Then, you can use the following command to package the project

```shell
jpackage --name PhotoEditor --input . --main-jar PhotoEditor-1.0-SNAPSHOT.jar --resource-dir ../package/macos --type pkg
```

## Contributors
- Khant Zaw Hein
- Naw Joyful Awgyi