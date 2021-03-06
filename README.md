# About

Genetic algorithm for load balancing (galob) is a tool that allocates
runnables with interdependencies to processing units, taking into account both
load balancing and any communication overhead regarding load parallelism.

## Commit messages

The commit messages should have
[gitchangelog](https://pypi.python.org/pypi/gitchangelog)'s tool format.

# Documentation

The documentation can be accessed
[here](http://galob.readthedocs.io/en/latest/index.html).

The documentation was developed using
[Sphinx](http://www.sphinx-doc.org/en/1.4.9/). To build the documentation follow
the steps bellow.

## Virtual environment

We strongly recommend to set a *Virtual Environment* to build the documentation.
To do so, first install `virtualenv`, as follows;

```
$ pip install virtualenv
```

After installation run;

```
$ cd <project-root-dir>
$ virtualenv docs-virt
$ source docs-virt/bin/activate
```

To create and activate your virtual environment. Now install all python
dependencies by running;

```
(docs-virt)$ pip install -r <project-root-dir>/docs/requirements
```

## PlantUML

This documentation includes some UML diagrams. UML diagrams are rendered with
PlantUML. For building a documentation with all diagrams download PlantUML jar
file from this [link](http://plantuml.com/download) and install it
at `/opt/plantuml`.

PlantUML also needs `graphviz` to be installed. Install it by running;

```
apt-get install graphviz
```

## Web Documentation

After activating the *Virtual Environment* and installing all requirements,
build the documentation by running;

```
(docs-virt)$ cd <project-root-dir>/docs
(docs-virt)$ make html
```

The HTML version of the documentation is built and can be previewed by running a
web server as follows;

```
(docs-virt)$ cd <project-root-dir>/docs/build/html
(docs-virt)$ webdev <my-ip>:<my-port>
```

Now open a web browser and type [0.0.0.0:8080](0.0.0.0:8080).

You can also set your own IP and port by running;

```
(docs-virt)$ webdev <my-ip>:<my-port>
```

## PDF Documentation

After activating the *Virtual Environment* and installing all requirements,
build the documentation by running;

```
(docs-virt)$ cd <project-root-dir>/docs
(docs-virt)$ make latex
(docs-virt)$ cd <project-root-dir>/docs/build/latex
(docs-virt)$ make
```

Now you can open the `PDF` file located at
`<project-root-dir>/docs/build/latex/galob.pdf`.

# Install dependencies

The code's dependencies install instructions are provided for Ubuntu.

## Java JRE and JDK

To install Java 8 related dependencies just run;

```
$ apt-get install openjdk-8-jdk openjdk-8-jre
```

## Gradle

The build system of the project was done with [gradle](https://gradle.org/).

For the moment no `gradle wrapper` is provided within the repository. Meanwhile,
you'll need to install `gradle` running;


```
$ apt-get install gradle
```

# Build the code

Thanks to `gradle` building the code is really easy. Just run;

```
$ cd <project-root-dir>
$ gradle build
```

# Running Tests

Thanks to `gradle` running tests is really easy. Just run;

```
$ cd <project-root-dir>
$ gradle test
```

## Preview tests report

First, create a web server with `webdev` at
`<project-root-dir>/build/reports/tests/` by running;

```
$ cd <project-root-dir>/build/reports/tests/
$ source docs-virt/bin/activate
(docs-virt)$ webdev
```

Now open a web browser and type [0.0.0.0:8080](0.0.0.0:8080).

You can also set your own IP and port by running;

```
(docs-virt)$ webdev <my-ip>:<my-port>
```

You might want to create two different webservers; one for documentation and one
for test reports. For doing so just use different ports, for example 8080 for
documentation and 8081 for test reports.


# Run examples

Thanks to `gradle` executing the code is really easy. Just run;

```
$ cd <project-root-dir>
$ gradle runLoadBalancingExample
```

For *Load Balancing Optimization* example, or;

```
$ cd <project-root-dir>
$ gradle runExecutionTimeExample
```

For *Execution Time Optimization* example.
