# About

Genetic algorithm for load balancing (galob) is a tool that allocates
runnables with interdependencies to processing units, taking into account both
load balancing and any communication overhead regarding load parallelism.

Galob is the outcome of an assignment of the course ***Distributed and Parallel
Systems*** on the first semester (winter semester of 2016/17) of the Master's
program ***Embedded Systems for Mechatronics***.


# Development planning

The project´s development was planned with [Taiga.io](https://taiga.io/). To see
the development planning, click
[here](https://tree.taiga.io/project/pjcuadra-galob/kanban).

## Issues

Issues are being tracked through both [Taiga.io](https://taiga.io/) and Gitlab.

# Git workflow

The workflow for adding code to `master` will be as follows:
1. Pull master
```
$ git checkout master
$ git pull master
```
2. Create a personal branch from current master named ```personal/<my-name>/<my-change-name>```
```
$ git checkout -b personal/<my-name>/<change-name>
```
3. Implement the change on this branch and commit the changes
```
$ git add <file-names>
$ git commit -m "<type-of-change>: <audience>: <description-of-change>"
```
4. Change back to master branch
```
$ git checkout master
$ git pull master
```
5. Change back to your branch
```
$ git checkout personal/<my-name>/<change-name>
```
6. Rebase your branch to sync with the master
```
$ git rebase master
```
7. Push the branch
```
$ git push --set-upstream origin personal/<my-name>/<change-name> (only once for a new branch)
$ git push
```
8. Create a *Merge Request* on Gitlab
9. Wait for the review of your teammate who will merge the change into master
10. In case of review changes, repeat steps 3 to 8
11. If the master has been changed, then after rebasing use this command to push changes
```
$ git push -f
```
**Note:** `master` branch is protected to prevent nasty things to happen.

## Commit messages

The commit messages should have
[gitchangelog](https://pypi.python.org/pypi/gitchangelog)'s tool format.

# presentation Slides

The presentation slides are tracked as part of the git repository, but since ```.pptx```
contains binary information ```git lfs``` was used to commit the presentation.

To pull the presentation install ```git lfs``` as shown [here](https://packagecloud.io/github/git-lfs/install).

The presentation slides are also uploaded to the wiki of this project.

# Documentation

The documentation was developed using [Sphinx](http://www.sphinx-doc.org/en/1.4.9/). To build the documentation follow the steps bellow.

## Virtual environment

We strongly recommend to set a *Virtual Environment* to build the documentation. To do so, first install `virtualenv`, as follows;

```
$ pip install virtualenv
```

After installation run;

```
$ cd <project-root-dir>
$ virtualenv docs-virt
$ source docs-virt/bin/activate
```

To create and activate your virtual environment. Now install all python dependencies by running;

```
(docs-virt)$ pip install -r <project-root-dir>/docs/requirements
```

## PlantUML

This documentation includes some UML diagrams. UML diagrams are rendered with PlantUML. For building a documentation with all diagrams download PlantUML jar file from this [link](http://plantuml.com/download) and install it at ```/opt/plantuml```.

PlantUML also needs ```graphviz``` to be installed. Install it by running;

```
apt-get install graphviz
```

## Web Documentation

After activating the *Virtual Environment* and installing all requirements, build the documentation by running;

```
(docs-virt)$ cd <project-root-dir>/docs
(docs-virt)$ make html
```

The HTML version of the documentation is built and can be previewed by running a web server as follows;

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

After activating the *Virtual Environment* and installing all requirements, build the documentation by running;

```
(docs-virt)$ cd <project-root-dir>/docs
(docs-virt)$ make latex
(docs-virt)$ cd <project-root-dir>/docs/build/latex
(docs-virt)$ make
```

Now you can open the ```PDF``` file located at ``` <project-root-dir>/docs/build/latex/galob.pdf```.

# Install dependencies

The code's dependencies install instructions are provided for Ubuntu.

## Java JRE and JDK

To install Java 8 related dependencies just run;

```
$ apt-get install openjdk-8-jdk openjdk-8-jre
```

## Gradle

The build system of the project was done with [gradle](https://gradle.org/).

For the moment no ```gradle wrapper``` is provided within the repository. Meanwhile, you'll need to install ```gradle``` running;


```
$ apt-get install gradle
```

# Build the code

Thanks to ```gradle``` building the code is really easy. Just run;

```
$ cd <project-root-dir>
$ gradle build
```

# Running Tests

Thanks to ```gradle``` running tests is really easy. Just run;

```
$ cd <project-root-dir>
$ gradle test
```

## Preview tests report

First, create a web server with ```webdev``` at ```<project-root-dir>/build/reports/tests/``` by running;

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

You might want to create two different webservers; one for documentation and one for test reports. For doing so just use different ports, for example 8080 for documentation and 8081 for test reports.


# Run examples

Thanks to ```gradle``` executing the code is really easy. Just run;

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
