#!/usr/bin/env python

# Author: Stephan Gerhard <gerste@unidesign.ch>
# July 2009

# Additionally:
# hierarchically hypergraphs, INTERACTIONS ^= space-time atoms!
# hierarchically structured space-time atoms

""" A framework for the representation of every spatio-temporal pattern
imaginable. The basic intuition is that every existing "thing" of our perceived
world is necessarily extended in space and time, and this dimensions are not separable.
The vision is helping to clarify any world model we would like to make, enforcing us
o define the spatial as well as the temporal resolution of our basic building blocks.

The distinction between process/event and object should be overcome by thinking
of objects as temporarily stable entity for the particular time duration, but also
extended spatially.

The basic building blocks
-------------------------
* a model
compromising all defined building blocks: state, space-time-atom, attribute

* a state
is composed of a set of space-time-atoms. each item in this set is perfectly
"located" (meaning located in space and time), identifiable and well defined.

* a space-time-atom
an entity containing a set of attributes pertaining to all entities in the
set of space-time-atoms of a state. attributes could also be conceived as
dimension, parameters, type or properties, denoting the same "thing" in this context

* an attribute
a particular attribute has a specified range of values it can take, this range
is not confined to numbers and units such as physical measures, but these can
be represented as well. data types available by the programming language, as well
as custom defined can be used. the value zero takes the special role of expressing
that the particular attribute does not exist in a specific state of a space-time-atom

See also
http://en.wikipedia.org/wiki/Level_of_measurement
http://en.wikipedia.org/wiki/Measurement
http://en.wikipedia.org/wiki/Qualitative_data

Examples
- Enumeration(Characteristice 1, Characteristic 2)
- ...

* a state transition
the possibility to define the logical, possible changes of values of attributes
needs to be implemented. a valid state transitions is given by conformance to
this criteria.

in the beginning, it might be hard to imagine state transitions not as a sequence
in time from a particular configuration to another, but as changes in a more
abstract space.

Usage
-----

An instance of a state is defined by the particular values of the attributes
of all its space-time-atoms. 

The act of gaining empirical data by means of measurements should be followed by
entry of this data into an instance of the model, bit by bit completing the overall
course of things of happenings.

This evolving data sets defined on an abstract basis can then further be processed
e.g in visualization of them, to gain deeper understanding in spatio-temporal patterns
occuring.

Extensions
----------
* Every space-time-atom has it's intrinsic history of the state-transitions stored
* A space-time-atom can be partitioned to represent data of finer spatial and/or
temporal resolution

Implementation with Python
--------------------------

possibly use Traits mechanism

* class Model
defines:
- spatial resolution and thus structure
- temporal resolution and thus structure
- set of attributes per space-time-atom
- set of values per attribute
- set of possible and necessary changes of values of attributes, implicitly defining
the criteria for valid state transitions

- a linked structure of instances of objects of type State, potentially circular
- a validator of the linked State structure checking valid changes in values of attributes,
valid attributes, ...

* class State
has_a: dictionary containing objects of type SpaceTimeAtom
key: a tuple, uniquely representing the spatial and temporal structure
    spatial structure: e.g. location(x,y,z), spatial extent (surface, volume), ...
    temporal structure: e.g. time point, time duration, ...
value: it's corresponding SpaceTimeAtom object

* class SpaceTimeAtom
has_a: dictionary representing the attributes
key: the different attributes
values: the values of the attributes

Information Interaction Networks
================================
From potentiality to actuality

See White Book.
"""
