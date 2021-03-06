Biological Genetics Terms
=========================

The following class diagrams represents the relationships between
important biological genetics terms. The figure was based on [#]_.

.. uml::
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

.. [#] *Genetic Algorithms in Java Basics*, L. Jacobson, B. Kanber.
