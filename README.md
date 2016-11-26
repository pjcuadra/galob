# About

Genetic algorithm for load balancing (galob) is a tool that allocates
runnables with interdependencies to processing units, taking into account both
load balancing and any communication overhead regarding load parallelism.

Galob is the outcome of an assignment of the course ***Distributed and Parallel
Systems*** on the first semester (winter semester of 2016/17) of the Master's
program ***Embedded Systems for Mechatronics***.


## Development planning

The project´s development was planned with [Taiga.io](https://taiga.io/). To see
the development planning, click
[here](https://tree.taiga.io/project/pjcuadra-galob/kanban).

### Issues

Issues are being tracked through both [Taiga.io](https://taiga.io/) and Gitlab.

## Git workflow

The workflow for adding code to `master` will be as follows:

1. Create a personal branch from current master named
   `personal/<my-name>/<my-change-name>`
2. Implement the change on this branch
3. Push the branch and create a *Merge Request* on Gitlab
4. Wait for the review of your team mate who will merge the change into master

**Note:** `master` branch is protected to prevent nasty things to happen.

### Commit messages

The commit messages should have
[gitchangelog](https://pypi.python.org/pypi/gitchangelog)'s tool format.

## Documentation

The documentation was developed using [Sphinx](http://www.sphinx-doc.org/en/1.4.9/). To build the documentation follow the steps bellow.

### Virtual environment

We strongly recommend to set a *Virtual Environment* to build the documentation. To do so, first install `virtualenv`, as follows;

```
$ pip install virtualenv
```

After installation run;

```
$ cd <project-root-dir>
$ virutalenv docs-virt
$ source docs-virt/bin/activate
```

To create and activate your virtual environment. Now install all python dependencies by running;

```
(docs-virt)$ pip install -r <project-root-dir>/docs/requirements
```

### PlantUML

This documentation includes some UML diagrams. UML diagrams are rendered with PlantUML. For building a documentation with all diagrams download PlantUML jar file from this [link](http://plantuml.com/download) and install it at ```/opt/plantuml```.

PlantUML also needs ```graphviz``` to be installed. Install it by running;

```
apt-get install graphviz
```

### Web Documentation

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

### PDF Documentation

After activating the *Virtual Environment* and installing all requirements, build the documentation by running;

```
(docs-virt)$ cd <project-root-dir>/docs
(docs-virt)$ make latex
(docs-virt)$ cd <project-root-dir>/docs/build/latex
(docs-virt)$ make
```

Now you can open the ```PDF``` file located at ``` <project-root-dir>/docs/build/latex/galob.pdf```.
