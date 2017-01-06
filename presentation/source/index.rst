=======================================================================
Genetic Algorithm for Load Balancing considering communication overhead
=======================================================================
Pedro Cuadra, Sudheera Reddy

Galob
=====

.. figure:: _static/galob.svg
  :width: 200
  :class: align-right

.. rst-class:: build

- Adding the ``build`` class to a container
- To incrementally show its contents
- Remember that *Sphinx* maps the basic ``class`` directive to ``rst-class``


Genetic Terms
=============
.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true

  class DNA
  class Genome
  class Gene
  class GenePool
  class Allele
  class Chromosome

  DNA "1" o-- "*" Gene
  DNA "1" o-- "*" Chromosome
  DNA "1" -- "1" Genome

  GenePool o-- Gene : has all possible (population wide) >
  Allele "1" o-- "1" Gene : < encodes
  Allele -- Chromosome : at locus >
  Chromosome "1" o-- "*" Gene

  Genome "1" o-- "all" Chromosome

===
Q&A
===

=========
Thank you
=========
