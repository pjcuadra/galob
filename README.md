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

The documentation of the project will be available soon on [Read The Docs](https://readthedocs.org/).
