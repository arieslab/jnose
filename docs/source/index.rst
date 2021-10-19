.. jnose documentation master file, created by
   sphinx-quickstart on Tue Oct 19 08:20:47 2021.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

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
   * :doc:`Feature Overview </features-overview>` 
   * :doc:`Links </links>`

.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: Getting started
   
   /requirements
   /downloading-and-running
   /usage
   /features-overview
   /links
   
Understanding the test smells 
------------------------------

Learn about test smells.

* :doc:`Test Smells </smells>`

.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: Understanding test smells
   
   /smells

About JNose Test
------------------------------

Learn about the ARIES Lab organization to find out how you can get involved and contribute to the development and success of the JNose Test project and many others!

* **Getting involved with JNose Test**:
   * :doc:`Contributing </contribute>` 
   * :doc:`Development installation </install>` 
   * :doc:`roadmap </roadmap>` 
   * :doc:`Code of conduct </code-of-conduct>`
   * :doc:`Release notes & changelog </changelog>`

* **The people behind Read the Docs**:
   * :doc:`About us </about>` 
   * :doc:`Our Team </team>` 
   * :doc:`Open source philosophy </open-source-philosophy>` 


.. toctree::
   :maxdepth: 2
   :hidden:
   :caption: About JNose Test

   /contribute
   /install
   /roadmap
   /code-of-conduct
   /changelog

   /about
   /team
   /open-source-philosophy
