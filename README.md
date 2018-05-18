# OPLA-Tool

## Description
Desenvolvimento da OPLA-Memetic.

You need download all projects before to build the OPLA-Memetic.

## Requirements
Before to compile the code, you need to install the following softwares on your PC:
- Java Development Kit (Version >= 6)
- Git - http://git-scm.com
- Maven - http://maven.apache.org 

## How to Build
This section show the step-by-step that you should follow to build the OPLA-Tool. 

- Create a directory to build OPLA-Tool:
```sh
mkdir opla-tool
```
- Access the folder:
```sh
cd opla-tool
```
- Download all projects:
```sh

git clone https://jchomax@bitbucket.org/jchomax/opla-memetic.git
```
- Install dependencies:
```sh
sh architecture-representation/buildDeps.sh
```
- Compile all projects. The sequence is important:
```sh
cd architecture-representation && mvn clean && mvn install
cd opla-patterns && mvn clean && mvn install
cd opla-core && mvn clean && mvn install
cd opla-tool && mvn clean && mvn install
```
- Open OPLA-Tool:
```sh
java -jar opla-tool/target/opla-tool-0.0.1-jar-with-dependencies.jar
```





