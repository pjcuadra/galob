Architecture Overview
=====================

.. figure:: ../_static/DesignArch.png

Design View
-----------

Schedulers
++++++++++

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  package alg {
    class ExecutionTime {
      + ExecutionTime(etcmatrix: double[][], delta: double[][])
      + getFitness(scheduleSeq: ISeq<ScheduleGene>): double
    }
    class LoadBalancing {
      + LoadBalancing(etcmatrix: double[][], delta: double[][])
      + LoadBalancing(etcmatrix: double[][], delta: double[][], alpha: double)
      + LoadBalancing(etc: double[][], delta: double[][], alpha: double, comCost: double[][])
      + LoadBalancing(etc: double[][], delta: double[][], comCost: double[][])
      + getFitness(scheduleSeq: ISeq<ScheduleGene>): double
      --
      - alpha: double
    }

    package util {
      class Scheduler {
        + Scheduler(ETC: double[][], delta: double[][])
        + Scheduler(ETC: double[][], delta: double[][], comCost: double[][])
        + createOmegaMatrix(scheduleSeq: ISeq<ScheduleGene>): int[][]
        + getNodesExecutionTime(scheduleSeq: ISeq<ScheduleGene>): double[]
        + getTotalTime(scheduleSeq: ISeq<ScheduleGene>): double
        + getAverageTime(scheduleSeq: ISeq<ScheduleGene>): double
        + {abstract} getFitness(scheduleSeq: ISeq<ScheduleGene>): double
        --
        # ETC: double[][]
        # delta: double[][]
        # comCost: double[][]
      }

      ExecutionTime -up-|> Scheduler
      LoadBalancing -up-|> Scheduler

    }
  }


Genetics
++++++++

Structure
~~~~~~~~~

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  package alg.util.genetics {
    class ScheduleAllele {
      + ScheduleAllele(numTasks: int, numExecutors: int)
      + ScheduleAllele(numTask: int, numExecutors: int, taskId: int)
      + getTaskId(): int
      + getExecutorId(): int
      + toString(): String
      + equals(object: Object): boolean
      --
      - taskId: int
      - executorId: int
    }

    class ScheduleChromosome {
      + ScheduleChromosome(delta: double[][], numExecutors: int)
      + ScheduleChromosome(delta: double[][], numExecutors: int, genes: ISeq<ScheduleGene>)
      + of(delta: double[][], numExecutors: int): Chromosome<ScheduleGene>
      + toString(): String
      + clone(): ScheduleChromosome
      --
      - numTasks: int
      - numExecutors: int
      - delta: double[][]
    }

    class ScheduleGene {
      + ScheduleGene(numTasks: int, numExecutors: int)
      + ScheduleGene(numTasks: int, numExecutors: int, allele: ScheduleAllele)
      + toString(): String
      + of(numTasks: int, numExecutors: int): ScheduleGene
      + mutate(value: ScheduleAllele)
      + equals(object: Object): boolean
      --
      - numTasks: int
      - numExecutors: int
    }

    ScheduleChromosome "1"*--"*" ScheduleGene
    ScheduleGene "1"*-left-"1" ScheduleAllele

  }

  package org.jenetics {
    interface Gene {
      + getAllele(): ScheduleAllele
      + newInstance(): ScheduleGene
      + newInstance(value: ScheduleAllele): ScheduleGene
      + isValid(): boolean
    }

    interface Chromosome {
      + newInstance(): Chromosome<ScheduleGene>
      + newInstance(genes: ISeq<ScheduleGene>): Chromosome<ScheduleGene>
      + getGene(index: int): ScheduleGene
      + iterator(): Iterator<ScheduleGene>
      + length(): int
      + toSeq(): ISeq<ScheduleGene>
      + isValid(): boolean
    }

  }

  Chromosome <|.. ScheduleChromosome
  Gene <|.. ScheduleGene

Alterers
~~~~~~~~

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  package alg.util.genetics {
    class ScheduleChromosome
    class ScheduleGene

    class ScheduleMutator {
      + ScheduleMutator(delta: double[][], probMutator: double)
      + mutateChromosome(chromosome: ScheduleChromosome): ScheduleChromosome
      + alter(population: Population<ScheduleGene, Double>, generation: long): int
      --
      - numTasks: int
      - probMutator: double
      - levels: ArrayList<ArrayList<Integer>>
    }

    class ScheduleCrossover {
      + ScheduleCrossover(delta: double[][], probCrossover: double)
      + ScheduleCrossover(delta: double[][], probCrossover: double, simAnne: SimulatedAnneling)
      # crossover(that: MSeq<ScheduleGene>, other: MSeq<ScheduleGene> ): int
      - getLevel(tasknum: int): int
      --
      - isSimulated: boolean
      - simAnne: SimulatedAnneling
      - levels: ArrayList<ArrayList<Integer>>
    }

    ScheduleMutator ..> ScheduleChromosome
    ScheduleMutator ..> ScheduleGene
    ScheduleCrossover ..> ScheduleGene

  }

  package org.jenetics {
    class SinglePointCrossover
    Interface Alterer
  }

  ScheduleCrossover -up-|> SinglePointCrossover
  Alterer .down.|> ScheduleMutator


Util
++++

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  package alg.util {
    class Util {
      + {static} getOnesMatrix(rows: int, int cols: int): double[][]
      + {static} getDeltaMatrix(numTasks: int): double[][]
      + {static} getComcostmatrix(delta: double[][]): double[][]
      + {static} copyMatrix(matrix: double[][]): double[][]
      + {static} getRowSum(matrix: double[][], int row: int): double
      + {static} checkColZero(matrix: double[][], col: int): boolean
      + {static} clearRow(matrix: double[][], row: int)
      + {static} matrixParallelMultiply(matrixA: double[][], matrixB: double[][]): double[][]
      + {static} intMatrixtoDouble(matrix: int[][]): double[][]
      + {static} getDependenciesLevels(delta: double[][])
      + {static} decrementRow(commCost: double[][], row: int)
      + {static} allocComCost(commCost: double[][], omega: int[][])
    }

  }

Implementation View
-------------------

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  [UserCode] ..> [galob]
  [LoadBalancingExample] ..> [galob]
  [UserCode] ..> [jenetics]
  [LoadBalancingExample] ..> [jenetics]
  [galob] ..> [jenetics] : use


Deployment View
---------------

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  component galob
  component UserCode
  component jenetics

  node system {
    artifact UserCode.jar

    node java_libray_path {
      artifact galob.jar
      artifact jenetics.jar
    }
  }

  UserCode.jar ..> UserCode : <<manifest>>
  galob.jar ..> galob : <<manifest>>
  jenetics.jar ..> jenetics : <<manifest>>


Process View
------------

* Threads
* Active classes
* Processes

Use Case View
-------------

.. uml::
  :scale: 50 %
  :align: center

  skinparam monochrome true
  skinparam backgroundColor #d9d9d9

  Actor User
  Actor "Jenetic's Engine"

  rectangle alg.util {

    :User: --> (Create MutationAlterer)
    :User: --> (Create CrossoverAlterer)
    :User: --> (Create Load Balancing Scheduler)
    :User: --> (Run Exmaples)

    :Jenetic's Engine: --> (Mutate Population)
    :Jenetic's Engine: --> (Crossover Population)
    :Jenetic's Engine: --> (Use fitness function)


    (Use fitness function) ..> (Create Load Balancing Scheduler)
    (Create MutationAlterer) <.. (Mutate Population)
    (Create CrossoverAlterer) <.. (Crossover Population)

  }
