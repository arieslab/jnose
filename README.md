<p align="center"><img src="https://github.com/tassiovirginio/jnose/blob/master/src/main/webapp/logo.png?raw=true" width="70"></p>

<div align="center">

  [![Documentation Status](https://readthedocs.org/projects/jnose/badge/?version=latest)](http://jnose.readthedocs.io/en/latest/?badge=latest)
  [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  [![Actions Status](https://github.com/arieslab/jnose-core/workflows/maven/badge.svg)](https://github.com/arieslab/jnose-core/actions)
  <a><img src="https://img.shields.io/badge/Powered_by-ARIES%20Lab-blueviolet.svg"/></a>
  
</div>

------------------------

# JNose: Java Test Smell Detector

JNose Test is a tool developed to automatically detect test smells in test code, and to collect coverage metrics. JNose Test implements the interface methods of the [JNose-Core API](https://github.com/arieslab/jnose-core) to detect the test smells and organizes the data flow in a web-based user interface. The JNose-Core API provides a flexible architecture that extends the test smells detection rules from the [Test Smell Detector](https://testsmells.org/index.html) tool and supports the addition of new rules.

The current verion of JNose Test:
1. detects test smells in different code granularities (line, method, block, and class);
2. detects test smells more accurately according to the literature definition;
3. calculates test coverage metrics using the JaCoCo library;
4. presents the outputs in a more user-friendly interface;

The project uses the Apache Maven to manage all library dependencies and support its compilation and execution.

## How to use JNose?

To use the JNose Test, you need a runtime environment compatible with:

 - JDK 1.8
 - Maven 3

The steps to download and install the JNose-Core and JNose Test are described as follows. <br>
For more detais, please refer to the [JNose Test documentation](https://jnose.readthedocs.io/en/latest/index.html).

### JNose-Core API

We suggest you to use the latest version of JNose-Core API through the following the steps:

1. Clone the project: ``git clone https://github.com/arieslab/jnose-core``
2. Go to the project directory: ``cd ../jnose-core``
3. Install the dependency: ``mvn install``

### JNose Test

To start the web-based user interface, please follow the steps:

1. Clone the project: ``https://github.com/arieslab/jnose``
2. Go to the project directory: ``cd ../jnose``
3. Start the JNose Test: ``mvn jetty:run``
4. Access the localhost in your browser: ``http://127.0.0.1:8080``


## Contributors
 - <a target="_blank" href="https://github.com/tassiovirginio">Tássio Virgínio</a>
 - <a target="_blank" href="https://github.com/luana-martins">Luana Martins</a>
 - <a target="_blank" href="https://github.com/Railana">Railana Santana</a>
 - <a target="_blank" href="https://github.com/danielevalverde">Daniele Valverde</a>
 - <a target="_blank" href="https://github.com/jonathanbisp">Jonathan Bispo</a>
 

## Contact us:
The best way to contact us is to post a message in our issue tracker or discussion forum. You can use it for things like asking questions about the project or requesting technical help.

Alternatively, you can email us at arieslab@outlook.com

