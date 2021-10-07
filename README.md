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

- `framework` - is a provided framework code from the IMA20 course
- `precompiled` - is the provided library bins from the IMA20 course
- `problems` is an example set of inputs for the problem

## Delivering

- Report
- Contest code implementation

### Report requirements

<!-- TODO: ... --> ...

### Code requirements

<!-- TODO: ... --> ...