package psbp.internalImplementation.materialization

import psbp.specification.materialization.Materialization

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.computation.CoResulting

import psbp.internalImplementation.computation.transformation.ReactiveTransformed

private[psbp] given reactiveTransformedMaterialization[
  C[+ _]: Computation: CoResulting:
  [C[+ _]] =>> Materialization[[Z, Y] =>> Z => C[Y], Z, Y], Z, Y]: 
  Materialization[[Z, Y] =>> Z => ReactiveTransformed[C][Y], Unit, Unit] with

  private type F[+Z] = C[Z]
  private type T[+Z] = ReactiveTransformed[F][Z] 

  private type `=>F`[-Z, +Y] = Z => F[Y]
  private type `=>T`[-Z, +Y] = Z => T[Y]

  private val coResultingF: CoResulting[F] = summon[CoResulting[F]]
  import coResultingF.coResult

  val materialize: (Unit `=>T` Unit) => Unit => Unit =
    `u>-->u` =>
      u =>
        `u>-->u`(u)(coResult)