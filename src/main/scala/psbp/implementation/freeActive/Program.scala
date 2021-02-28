package psbp.implementation.freeActive

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.computation.programFromComputation

import psbp.internalSpecification.computation.transformation.freeTransformedComputation

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given freeActiveComputation: Computation[FreeActive] = freeTransformedComputation[Active]

given freeActiveProgram: Program[`=>FA`] = programFromComputation[FreeActive]
