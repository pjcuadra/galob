Architecture Overview
=====================

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true

  package alg {
    class ExecutionTime
    class LoadBalancing

    package util {
      class Scheduler
      class Util

      package genetics {
        class ScheduleAllele
        class ScheduleChromosome
        class ScheduleGene
        class ScheduleMutator
      }
    }
  }

  package examples {
    class ExecutionTimeExample
    class LoadBalancingExample
  }
