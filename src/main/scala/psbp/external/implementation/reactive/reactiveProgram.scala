package psbp.implementation.reactive

import psbp.specification.program.Program

import psbp.internal.specification.computation.Computation

import psbp.implementation.programFromComputation

import psbp.internal.implementation.computation.transformation.reactiveTransformedComputation

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given reactiveComputation: Computation[Reactive] = reactiveTransformedComputation[Active]

given reactiveProgram: Program[`=>R`] = programFromComputation[Reactive]

// given reactiveComputation: Computation[Reactive] with

//   private[psbp] def result[Z]: Z => Reactive[Z] =
//     z => 
//       `z=>u` =>
//         `z=>u`(z)

//   private[psbp] def bind[Z, Y] (cz: Reactive[Z], `z=>cy`: => Z => Reactive[Y]): Reactive[Y] =
//     `y=>u` =>
//       cz { 
//         z =>
//           `z=>cy`(z)(`y=>u`)
//       }