# How to create a library package and push it to the Ballerina central

## Overview
The need for custom dependencies in a programming language is a fundamental concept. And it’s very important to know how to create custom dependencies, how to use created dependencies, and how to make them available to others. Therefore, we are going to discuss how to create a custom dependency as your need, how to test them before publishing and how to make it available for anyone in ballerina. 

## What is Ballerina?
Ballerina is a programming language designed for cloud-native applications. You may wonder why to choose ballerina instead of other alternatives. This is because as a powerful language it has the following features.

* Network primitives in the language make it simpler to write services and run them in the cloud.
* Programs have both a textual syntax and an equivalent graphical form based on sequence diagrams.
* Easy and efficient concurrency with sequence diagrams and language-managed threads without the complexity of asynchronous functions.
* Explicit error handling, static types, and concurrency safety, combined with a familiar, readable syntax make programs reliable and maintainable.

If you are new to the Ballerina programming language, I recommend you to play with it before reading this article. You can find the installation process, useful examples, and everything you need on the official Ballerina website.[1]
Hope you have the latest ballerina version installed on your machine and with that let’s move on to the implementation process.

## 1. How to create a new ballerina package?

To create a new ballerina package, execute the following command in the terminal.

![image6](https://user-images.githubusercontent.com/58510122/187608863-e5aa1ea7-2041-47a4-9c87-9f1ea0b9fbb4.png)

After the execution, this command will create the `ballerina.toml` file, `demo.bal` source file, `package.md` file, `module.md` file and the resources, and test directories. 

The `ballerina.toml` file identifies the directory as a Ballerina package. You can edit the `ballerina.toml` file to change the organization, name of the package, and version of the package.

![image5](https://user-images.githubusercontent.com/58510122/187609278-cb80db6f-0c84-4df3-bf03-2864a118a48c.png)

The `package.md` file is needed when you publish a package to a repository. This file content will add a description for the package, and you change it meaningfully. The demo.bal file is the source file which we use to create our library. For more information about these files, you can refer to.[2]

## 2. Implementing the library content in the `.bal` file

As an example, I’m going to create an object binder(record type) to bind objects and access their object fields easily. I have implemented this object binder inside the `demo.bal` file. The following figure shows the example code for the need.

![image4](https://user-images.githubusercontent.com/58510122/187609622-4a4ce0a7-4dce-493a-b369-06406b801544.png)

To get a better understanding of record types in Ballerina use this.[3]

## 3. Building the created package

After you have done the modifications on the .bal file you need to build it to generate the Ballerina Archive. To generate the Ballerina archive, execute the following command,

![image10](https://user-images.githubusercontent.com/58510122/187609803-a8b8da99-3bb0-4eb5-a23d-3a307740337e.png)

If the command is executed successfully, you would get a message as follows,

![image9](https://user-images.githubusercontent.com/58510122/187609930-91193129-bd0a-400a-a040-954169b7cbdd.png)

After creating the Ballerina archive you can publish it to  [Ballerina Central](https://central.ballerina.io/). Before you publish, ensure the package works as intended because a publish is permanent. Once published to [Ballerina Central](https://central.ballerina.io/), you cannot overwrite the version or remove the package. However, the number of package versions you can push to Ballerina Central is not restricted. As a good practice before publishing the library package to Ballerina central,  we use the “local repository” first to test out the functionality of the library package.

## 4. Testing the created library package in the Local Repository 

To test the created library package in the Local repository, you need to push it to the Local repository by using the following command,

![image8](https://user-images.githubusercontent.com/58510122/187610294-f1064eb7-b613-4c3c-978c-9a902b003c05.png)

If the command is executed successfully, you would get a message as follows,

![image1](https://user-images.githubusercontent.com/58510122/187610421-e4f2d8d8-3bd3-4b7c-95ee-f0f39a4a19a4.png)

To test the library you have created,  you need to create another package by following the 1st step. After that to import the library, you have pushed to the local repository, and update the ballerina.toml file as follows.

![image2](https://user-images.githubusercontent.com/58510122/187610508-da93f79d-ed26-4c9a-bcd9-d456683a6f0f.png)

Then you can import the created library in the local repository to the .bal file as follows,

![image7](https://user-images.githubusercontent.com/58510122/187610643-bd1cee88-1bf7-4529-95b0-6dccb6b424f9.png)

After that implement the logic in the .bal file to the functionality of the created library.

## 5. Pushing the library package to the Ballerina central

After the successful testing, the next step is to push the package to the ballerina central. To do that first you need to create an account on Ballerina central (you need to name your organization according to the `org` field which is in the `ballerina.toml` file). Then navigate to the dashboard and acquire an access token. After the token generation,  download and place the `Settings.toml` file in your home repository `<USER_HOME>/.ballerina/`. If you already have a `Settings.toml` file configured in your home repository, follow the other option and copy the access token into the `Settings.toml`.

After setting up the above configurations you can execute the following command to push the package to [Ballerina central](https://central.ballerina.io/). 

![image3](https://user-images.githubusercontent.com/58510122/187611084-11f64e1a-2eda-4e67-80ff-e2246e76fffa.png)

Then any user can use your library package in the WSO2 choreo platform by importing it as below for their implementations.

![image7](https://user-images.githubusercontent.com/58510122/187610643-bd1cee88-1bf7-4529-95b0-6dccb6b424f9.png)

## References

[1]. [Ballerina Home](https://ballerina.io/)

[2]. [Ballerina package files](https://ballerina.io/learn/organize-ballerina-code/package-references/)

[3]. [Ballerina record type](https://ballerina.io/learn/by-example/records.html)
