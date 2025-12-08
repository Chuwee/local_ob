# cpanel-bi-superset-feature

This library was generated with [Nx](https://nx.dev).

This library is specifically designed to integrate with Apache Superset,
which will replace the bi-feature library used to integrate with Microstrategy.
It's very similar to that library, but has less components and its own service,
state and api layer.

It provides:
* Superset-specific report viewing and management
* Integration with Superset's authentication system
* Custom routing and navigation for Superset reports

> _THIS LIBRARY IS IN PROGRESS_  

All components have the word _superset_ in their name but the idea is to rename all of them when the former Microstrategy lib is deleted. That's why all these components are a copy of the former lib. Services layer was created apart to allow safe removal of the Microstrategy lib.

## Running unit tests

Run `nx test cpanel-bi-superset-feature` to execute the unit tests.
