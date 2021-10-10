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

- `<name>-ft-<quick feature description>`, for example `tony-ft-improved-input-processing`. 

Once the feature is complete, it can easily be merged into master by opening a new Pull Request.

### Folder structure

- `project` - is a provided framework code from the IMA20 course with shared libraries
- `precompiled` - is the collection of provided jars from the IMA20 course
- `problems` is an example set of inputs for the problem

### Getting started

Make sure gradle is installed to manage the project compilation easier:

```bash
$ <your package manager> install gradle
...
done
```

> See [Gradle Installation Guide](https://docs.gradle.org/current/userguide/installation.html#installing_with_a_package_manager) for more information on how to set it up.

#### Compiling



## Delivering

- Report
- Contest code implementation

### Report requirements

<!-- TODO: ... --> ...

### Code requirements

<!-- TODO: ... --> ...