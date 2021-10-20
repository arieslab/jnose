Welcome to JNose's documentation!
=================================

Test smells are bad practices to either design or implement a test code. Their presence may reduce the test code quality, harming the software testing activities, primarily from a maintenance perspective. The objective of this project is to provide an undertanding and automated support to help developers handling test smells. To this extent, this documentation provides examples and definitions of test smells and an open-source tool to detect the different smell types in the test code.

JNose Test is a tool developed to automatically detect test smells in test code, and to collect coverage metrics. JNose Test is an extension of the `Test Smell Detector <https://testsmells.org/index.html/>`_. Besides presenting the number of test smells detected by class, our tool shows the collection of code metrics and test coverage using the JaCoCo library; a unified result for all projects under analysis; and a graphical interface. In addition, the project uses the Apache Maven to manage all library dependencies and support the compilation and execution of the JNose Test tool.

This manual is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License.
.. _Read the docs: https://readthedocs.org/


Getting started
------------------------------

Learn how to configure and run the JNose Test.

   * :doc:`Requirements </requirements>` 
   * :doc:`Downloading and running </downloading-and-running>` 
   * :doc:`Other usages </usage>`
   * :doc:`Features </features>` 
   * :doc:`Links </links>`

.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: Getting started
   
   /requirements
   /downloading-and-running
   /usage
   /feature
   /links
   
Understanding the test smells 
------------------------------

Learn about test smells.

* :doc:`Assertion Roulette </smells/assertion-roulette>`
* :doc:`Conditional Test Logic </smells/conditional-test-logic>`

.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: Understanding test smells
   
   /smells/assertion-roulette
   /smells/conditional-test-logic

About JNose Test
------------------------------

Learn about the ARIES Lab organization to find out how you can get involved and contribute to the development and success of the JNose Test project and many others!

* :doc:`Contributing </contribute>`
* :doc:`Release notes & changelog </changelog>`
* :doc:`Publications</publications>`
* :doc:`Our Team </team>`


.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: About JNose Test

   /contribute
   /changelog
   /publications
   /team
