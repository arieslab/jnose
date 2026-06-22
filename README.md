<p align="center"><img src="https://github.com/arieslab/jnose/blob/main/src/main/webapp/logo.png?raw=true" width="70"></p>

# JNose

Web tool for automatic **Test Smells** detection and code coverage metrics in Java projects.

## Features

- Detects 21 test smells in JUnit test code
- Collects code coverage metrics via JaCoCo
- Supports JUnit 3, 4, and 5
- Web interface built with Apache Wicket 10 (Jakarta EE)
- Git integration for evolution analysis
- CSV export of results
- Built with JDK 21+, Wicket 10, Spring 7, Hibernate 7, Jetty 12 (Jakarta EE 11)

## Quick Start

## Quick Run (Linux)

### Standalone JAR (embedded Jetty)

```bash
curl -LO https://github.com/arieslab/jnose/releases/download/v2.4.1/jnose-2.4.1-standalone.jar
java -jar jnose-2.4.1-standalone.jar
```

### WAR (deploy to any servlet container)

```bash
curl -LO https://github.com/arieslab/jnose/releases/download/v2.4.1/jnose-2.4.1.war
# Deploy to Tomcat/Jetty as usual
```

### Using Docker

```shell
docker build -t jnose .
docker run -dp "8080:8080" jnose
```

Or pull from Docker Hub:

```shell
docker pull tassiovirginio/jnose
docker run -dp "8080:8080" tassiovirginio/jnose
```

### Building from source

```shell
git clone https://github.com/arieslab/jnose
cd jnose
mvn clean package -DskipTests
java -jar target/jnose-2.4.1-standalone.jar
```

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.arieslab</groupId>
    <artifactId>jnose</artifactId>
    <version>2.4.1</version>
</dependency>
```

The project depends on [jnose-core](https://github.com/arieslab/jnose-core), available on Maven Central:

```xml
<dependency>
    <groupId>io.github.arieslab</groupId>
    <artifactId>jnose-core</artifactId>
    <version>0.9.2</version>
</dependency>
```

## Tutorials

- [Tutorial PT-BR](TUTORIAL_pt-br.md)
- [Tutorial English](TUTORIAL_eng.md)

## Papers

- **JNose: Java Test Smell Detector** — Tássio Virgínio, Luana Almeida Martins, Larissa Rocha Soares, Railana Santana, Adriana Priscila Santos Cruz, Heitor Costa, Ivan Machado (2020): [CBSoft 2020](http://cbsoft2020.imd.ufrn.br/artigos.php?evento=sbes-ferramentas)
- **An Empirical Study of Automatically-Generated Tests from the Perspective of Test Smells** — Tássio Virgínio, Luana Martins, Larissa Soares, Railana Santana, Heitor Costa, Ivan Machado (2020): [CBSoft 2020](http://cbsoft2020.imd.ufrn.br/artigos.php?evento=sbes-pesquisa)
- Used to detect TestSmells and Coverage in (2019): [ACM](https://dl.acm.org/citation.cfm?doid=3350768.3350775)

## Feature requests

Please, feel very welcome to create new issues on this repository to request new features and report bugs.

## Contributors

- [Tássio Virgínio](https://github.com/tassiovirginio)
- [Daniele Valverde](https://github.com/danielevalverde)
- [Luana Martins](https://github.com/luana-martins)
- [Railana Santana](https://github.com/Railana)
- [Jonathan Bispo](https://github.com/jonathanbisp)

## Contributing

- Create an issue on this repository
- Fork this repository
- Create a branch and link the name to the related issue
- Commit and push
- Open a Pull Request

### Contact

- tassiovirginio@gmail.com

## License

Apache License 2.0
