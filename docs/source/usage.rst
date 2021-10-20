Other usages 
=============

Building JNose Test with Heroku!
---------------------------------------------

If you want to try JNose Test, we suggest you to use the `JNose Test application <http://jnose.herokuapp.com/?1>`_ hosted in Heroku!
It can give you an overview of all features suported by JNose Test, saving you the trouble of downloading and running the tool on your machine.
Please, be aware that the Heroku application has time constraints and it may not work whether you choose to analyze large software projects.


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
 
 
Building JNose Test with Docker
---------------------------------------------

Making a pull https://hub.docker.com/r/tassiovirginio/jnose

.. code-block:: console

  docker pull tassiovirginio/jnose


.. code-block:: console

  docker build -t jnose .
  docker run -dp "8080:8080" -v "$HOME/.m2":/root/.m2 --name jnose jnose:latest
  docker logs -f jnose # see logs, optionally
