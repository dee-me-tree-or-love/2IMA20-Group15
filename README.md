# 2IMA20 - Course Project - Group 15

## Main goal

Compute a simplification set `S` of size `|S| = k` for `k >= 1`,
containing polylines `S_j` such that amount of vertices `|S_j| <= c`
for `j = 1 ... k` and `\sum_{\tau_i \in T} (min_{S_j \in S}dF(\tau_i, S_j))` is minimal.

### Input details

- We are given `T` - set of polylines, `k` - integer `>= 1`, `c` - integer `>= 1`.

## Repository structure

### Core branches

This repository contains 2 default branches:

- `main` - used for integration of all the ongoing work
- `release` - reserved for the clean "release" versions of our code

### Development branches

To keep the progres clean and reviawable, we can work on each new feature in a separe branch. (So as to avoid the `main` branch madness).

#### Naming

- `<name>-<quick feature description>`, for example `tony-faster-input-processing`.

#### Merging features

1. Once the feature is complete,
    it can be added to `main` branch
    by opening a new Pull Request.

2. Before merging, let's ask at least
    1 other team member to review.

3. Once the feature was approved,
    feel free to merge the change! :tada:

### Folder structure

- `project` - is a base of the project solution.
  - `project/ContestClient` - \[application\] source of the contest GUI (partially provided from the lectures).
  - `project/ContestCore` - \[library\] source of the main entities used within the framework
  - `project/shared` - \[precompiled\] dependencies provided for the project such as GeometryCore etc.
- `problems` is an example set of inputs for the problem.

## Getting started

To make the management (compilation, running, distribution, etc) of the project easier, `gradle` build system is used.  
So when getting started, make sure `gradle` is installed on your machine.

> See [Gradle Installation Guide](https://docs.gradle.org/current/userguide/installation.html#installing_with_a_package_manager) for more information on how to set it up.

<!-- -->
> Note: in addition, this project includes a [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). With it, instead of installing `gradle` and using it to invoke tasks `gradle <task name: e.g. build>`, all the tasks can be called using the wrapper directly. E.g. `./gradlew build` on Unix computers and `gradlew.bat build` on Windows.

### Useful commands

- `gradle build` - compiles all the source code for the project
- `gradle run` - starts the ContestClient (actually all the _\[application\]_'s included in the project)
- `gradle clean` - removes all the directories produced by a build step
- `gradle tasks` - shows a list of available tasks

## Delivering

<!-- TODO: describe deliverable requirements -->
To be elaborated...

- Report
- Contest code implementation

### Report requirements

<!-- TODO: ... --> ...

### Code requirements

<!-- TODO: ... --> ...
