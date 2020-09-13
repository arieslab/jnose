<p align="center"><img src="https://github.com/tassiovirginio/jnose/blob/master/src/main/webapp/logo.png?raw=true" width="70"></p>

# JNose - Tutorial
Java TestSmells Detection

JNose Test is a tool developed to automatically detect test smells in test code, and to collect coverage metrics. The Jnose was basead on TsDetecet. Besides presentig the test smells numbers detected by class, our toll shows:
- The collection of code metrics and test coverage using the JaCoCo library
- An unified result for all projects under analysis
- And a graphical interface.
In addition, the project uses Apache Maven to manage all library dependencies and offer support to the build and run of the JNose test tool.

## Prerequisites
 - JDK 1.8 
 - Maven 3 
 - GIT

## Download and Run
 - git clone https://github.com/tassiovirginio/jnose.git
 - cd jnose
 - mvn jetty:run
 - acessar: http://127.0.0.1:8080

## Home Screen
<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_01.png?raw=true" width="800">
</p>

On the home screen we have the description of each search option and the configuration option:
 - by ClassTest: Performs the search based on the test class, returning the quantity of each type of test smells found in each class.
 - by TestSmells: performs the search based on the smell of the test, displaying in which class and in which line that it was found.
 - Evolution: Search in the project repository(git) looking for test smells in each commit/tag done.
 - Configuration: We have the option to choose which test smells we want to perform the research, by default all are selected.
 
## By ClassTest
<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_02.png?raw=true" width="800">
</p>

Initially we "paste" the folder address where the projects are located "Folders with projects".

E.g:
 - Linux: /home/nome/projetos
 - Windows: C:\users\name\projetos
<p align="center">
 <img src="docs/screenshot.png" width="800">
</p>

After "paste" the address of the projects folder that is on your machine, we click on "Select Directory".In the box below, all projects within the selected folder will be displayed.

<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_02_01.png?raw=true" width="800">
</p>

We can select all the projects that are in the list, or select only a few of them, with the checkbox option that each project has on the side of its name.

Then we will have the search for test smells being executed by clicking on the "Process" button.

The Search will start and we will be able to follow through the progress bar in each project and in the general progress bar.

At the end of the process, a CSV will be generated with the results obtained.

Return(CSV): Project Name, Test Class, Production Class, LOC, Number of Methods and the number of each of the 21 test_smells found per test class.

## By TestSmells
<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_03.png?raw=true" width="800">
</p>

In this option, we select "Folders with projects" the same way that we did before and the search by test smells will display the following return: Project Name, Test Class, Production Class, name of test smells, method name, occurrence line, initial occurrence line, final occurrence line.

## Evolution
<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_04.png?raw=true" width="800">
</p>

Using GIT version control, we can perform a search for test smell on each commit performed or tag existing in the project history. The return is the same as ByClassTest, with additional commit information: 
- confirmation ID
- commit name
- commit date
- commit message

To use this option the project must be cloned: git clone https: //address.do.project

## Configuration
<p align="center">
  <img src="https://github.com/tassiovirginio/jnose/blob/master/docs/tela_05.png?raw=true" width="800">
</p>

### Contact email:
- tassiovirginio@gmail.com
