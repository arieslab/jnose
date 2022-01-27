Requirements
=======================

To use the JNose Test, you need a runtime environment compatible with:

 - JDK 1.8 
 - Maven 3 

JNose Test implements the interface methods of the JNose-Core API and organizes the data flow in a web-based user interface. Therefore, the tool is platform independent, and is known to run on GNU/Linux and Windows.

We use the following snippet in the pom.xml of JNose Test to add the JNose-Core API. To use the latest version of the JNose-Core API (which you should), please refer to `Downloading and running <https://jnose.readthedocs.io/en/latest/downloading-and-running.html#jnose-core-api>`_.

.. code-block:: xml

    <dependencies>
        <dependency>
            <groupId>br.ufba.jnose</groupId>
            <artifactId>jnose-core</artifactId>
            <version>0.7-SNAPSHOT</version>
        </dependency>
    </dependencies>

