Other usages 
=============

How to use JNose Test as a Maven dependency
---------------------------------------------

Since version 2.0, JNose Test is available in the Maven Central Repository. 
In order to use JNose Test as a maven dependency in your project, add the following snippet to your project's build configuration file:

.. code-block:: xml

  <dependency>
    <groupId>br.ufba.jnose</groupId>
    <artifactId>jnose-core</artifactId>
    <version>0.7-SNAPSHOT</version>
  </dependency> 
 
 
Using Docker
-------------

Making a pull https://hub.docker.com/r/tassiovirginio/jnose

.. code-block:: console

  docker pull tassiovirginio/jnose


.. code-block:: console

  docker build -t jnose .
  docker run -dp "8080:8080" -v "$HOME/.m2":/root/.m2 --name jnose jnose:latest
  docker logs -f jnose # see logs, optionally
