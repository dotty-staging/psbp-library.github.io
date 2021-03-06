# Program Specification Based Programming

This document describes `PSBP`, a `Scala` library for *Program Specification Based Programming*.

```scala
//          ________    ________     __         _______
//         / ___   /\  / ______/\   / /\       / ___  /\
//        / /  /  / / / /\_____\/  / / /      / /  / / /
//       / /__/  / / / /_/____    / /_/__    / /__/ / /
//      / ______/ / /______  /\  / __   /\  / _____/ /   
//     / / _____\/  \____ / / / / /  / / / / / ____\/
//    / / /        ______/ / / / /__/ / / / / /  
//   /_/ /        /_______/ / /______/ / /_/ /
//   \_\/         \_______\/  \______\/  \_\/
//          
```

[source code](https://github.com/PSBP-Library/psbp-library.github.io)

The document describes both 

- the *definition* of the `PSBP` library,
- the *usage* of the `PSBP` library.

## Definition

The definition of the `PSBP` library separates *specifications* from *implementations*.

- `PSBP` specifications are defined as `trait`'s that define *typeclasses*. 
- `PSBP` implementations are defined as `given`'s that define types to belong to typeclasses. 

## Usage

The usage of the `PSBP` library is in terms of examples that are 

- *defined* only using members of specification `trait`'s,
- *used* using dependency on implementation `given`'s that are resolved using injection by `import`.

# Programs

The *specification part* of the `PSBP` library models *programming*.

Think about it as explaining to `Scala` what programming is all about.

Members of specification `trait`'s are referred to as *programming ingredients*.

A `val` that is defined in terms of programming ingredients is referred to as a *program*. 

A program has a *descriptive* nature, in fact it would be more appropriate to refer to it as a *program description*.

Compare this with the famous painting [Ceci n'est pas une pipe](https://en.wikipedia.org/wiki/The_Treachery_of_Images) of [René Magritte](https://en.wikipedia.org/wiki/Ren%C3%A9_Magritte).

The painting is not a *pipe*, it is a *pipe description*.

In a way a program is a generalized descriptive version of a *function*.

Running a function of type `Z => Y` *transforms* an *argument* of type `Z` to a *result* of type `Y`.

A program of type `Z >--> Y` *describes* running a function of type `Z => Y` transforming an argument of type `Z` to a result of type `Y`.

A program may, or may not, *describe* performing side effects along the way.

A described side effect is referred to as an *effect*.

## Basic Programming ingredients

The *basic* programming ingredients are

- being *functional*

and

- *sequential composition*
- *construction*
- *condition*

Programming ingredients, just like the corresponding function ingredients have a *denotational* nature.

They are also *pointfree*.

There also exists a *pointful* function ingredient

- *application*

A *function application* is an *expression*.

## Main programs

A *main program* is a program of type `Unit >--> Unit`.

Programs of type `Z >--> Y` are, somehow, combined with *producers* of type `Unit >--> Z` and *consumers* of type `(Z && Y) >--> Unit` to main programs.

Main programs, together with the producers, programs and consumers in terms of which they are defined, are *materialized* to functions.

*Running materialized main program implementations*, typically, somehow behave as follows

- the *producer implementation*, somehow, produces an argument from an *input source*, possibly performing side effects along the way, 
- the *program implementation*, somehow, transforms the produced argument to a to be consumed result, possibly performing side effects along the way,
- the *consumer implementation*,somehow, consumes the argument and result to an *output sink*, possibly performing side effects along the way.

Side effects can be *external* or *internal*.

External side effects interact with the external world.

Internal side effects typically manifest themselves in the function types of program implementations.

Materialized main program implementations are, eventually, used in a *main* `Scala` *programs*.

## `Functional`

```scala
package psbp.specification.program

trait Functional[>-->[- _, + _]]:

  // declared

  private[psbp] def toProgram[Z, Y]: (Z => Y) => Z >--> Y

  // defined extensions

  extension [Z, Y] (`z=>y`: Z => Y) def asProgram: Z >--> Y =
    toProgram(`z=>y`)
```

`Functional` specifies that *functions can be used as programs*.

The public member `asProgram` is an `extension` that can be used as postfix operator.

The full power that comes with the `PSBP` library is not available for functions that are used as programs using `asProgram`, so, which functions to use as programs using `asProgram` is an important choice.

Funny looking names like `` `z=>y` `` can be thought of as *typeful generic names* for functions.

## `Composition`

```scala
package psbp.specification.program

trait Composition[>-->[- _, + _]]:

  // declared

  private[psbp] def andThen[Z, Y, X](`z>-->y`: Z >--> Y, `y>-->x`: => Y >--> X): Z >--> X

  // defined extensions
  
  extension [Z, Y, X] (`z>-->y`: Z >--> Y) def >-->(`y>-->x`: => Y >--> X): Z >--> X =
    andThen(`z>-->y`, `y>-->x`)
```

`Composition` specifies that *programs can be composed sequentially*.

Sequential composition is simply referred to as composition.

The public member `>-->` is an `extension` that can be used as infix operator.

The type of the second parameter of `andThen` is a *call-by-name* one.

Funny looking names like `` `z>-->y` `` can be thought of as typeful generic names for programs.

## `Construction`

```scala
package psbp.specification.program

import psbp.specification.types.&&

trait Construction[>-->[- _, + _]]:

  // declared

  private[psbp] def construct[Z, Y, X] (`z>-->y`: Z >--> Y, `z>-->x`: => Z >--> X): Z >--> (Y && X) 

  // defined extensions

  extension [Z, Y, X] (`z>-->y`: Z >--> Y) def &&(`z>-->x`: => Z >--> X): Z >--> (Y && X) =
    construct(`z>-->y`, `z>-->x`)
```

where

```scala
package psbp.specification.types

// product

type &&[+Z, +Y] = (Z, Y)

// ...
```

`Construction` specifies that *programs can construct product based composite data*.

The public member `&&` is an `extension` that can be used as infix operator.

The type of the second argument of `construct` is a call-by-name one.

`&&` is, somewhat artificially, sequentially biased towards it's first argument.

## `Condition`

```scala
package psbp.specification.program

import psbp.specification.types.||

trait Condition[>-->[- _, + _]]:

  // declared

  private[psbp] def conditionally[Z, Y, X] (`y>-->z`: => Y >--> Z, `x>-->z`: => X >--> Z): (Y || X) >--> Z

  // defined extensions
  
  extension [Z, Y, X] (`y>-->z`: => Y >--> Z) def ||(`x>-->z`: => X >--> Z): (Y || X) >--> Z =
    conditionally(`y>-->z`, `x>-->z`)
```

where

```scala
package psbp.specification.types

// ...

// sum

enum ||[+Z, +Y]:
  case Left(z: Z) extends (Z || Y)
  case Right(y: Y) extends (Z || Y)

  def foldSum[X](`z=>x`: => Z => X, `y=>x`: => Y => X): X =
    this match
      case Left(z) =>
        `z=>x`(z)
      case Right(y) =>
        `y=>x`(y)  
```

`foldSum` hides the `enum` representation of the sum type.

`Condition` specifies that *programs can perform sum based conditional logic*.

The public member `||` is an extension that can be used as infix operator.

The types of the arguments of `conditionally` are call-by-name ones.

## `Program`

```scala
package psbp.specification.program

import psbp.specification.types.{ &&, || }

import psbp.specification.functional.{ 
  `z>-->z`, `(z&&y)>-->z`, `(z&&y)>-->y`, `z>-->(z||y)`, `y>-->(z||y)`, `(z&&b)>-->(z||z)` }

trait Program[>-->[- _, + _]]
  extends Functional[>-->]
  with Composition[>-->]
  with Construction[>-->]
  with Condition[>-->]:

    private implicit val program: Program[>-->] = this

    // defined extensions

    extension [Z, Y, X, W] (`z>-->x`: Z >--> X) def &&&(`y>-->w`: => Y >--> W): (Z && Y) >--> (X && W) =
      (`(z&&y)>-->z` >--> `z>-->x`) && (`(z&&y)>-->y` >--> `y>-->w`)  

    extension [Z, Y, X, W] (`x>-->z`: => X >--> Z) def |||(`w>-->y`: => W >--> Y): (X || W) >--> (Z || Y) =
      (`x>-->z` >--> `z>-->(z||y)`) || (`w>-->y` >--> `y>-->(z||y)`)  

    extension [Z, Y] (program: Z >--> Y) def toMainWith(producer: Unit >--> Z, consumer: (Z && Y) >--> Unit) =
      producer >--> {
        Let { 
          program
        } In { 
          consumer 
        }
      }
    
    // defined
    
    def Let[Z, Y, X](`z>-->y`: Z >--> Y): In[Z, Y, X] =
      new {
        def In(`(z&&y)>-->x`: => (Z && Y) >--> X): Z >--> X =
          (`z>-->z` && `z>-->y`) >--> `(z&&y)>-->x`
      } 

    private[psbp] trait In[Z, Y, X]:
      def In(`(z&&y)>-->x`: => (Z && Y) >--> X): Z >--> X
    
    def If[Z, Y](`z>-->b`: Z >--> Boolean): Apply[Z, Y] =
      new {
        override def apply(`z>-t->y`: => Z >--> Y): Else[Z, Y] =
          new {
            override def Else(`z>-f->y`: => Z >--> Y): Z >--> Y =
              Let {
                `z>-->b`
              } In {
                `(z&&b)>-->(z||z)`
              } >--> {
                `z>-t->y` || `z>-f->y`
              }  
          }
      } 

    private[psbp] trait Apply[Z, Y]:
      def apply(`z>-t->y`: => Z >--> Y): Else[Z, Y]

    private[psbp] trait Else[Z, Y]:
      def Else(`z>-f->y`: => Z >--> Y): Z >--> Y
```

where

```scala
package psbp.specification.functional

import scala.language.postfixOps

import psbp.specification.types.{ &&, || }

import psbp.specification.program.Functional

import psbp.specification.function._

// functional

def `z>-->z`[>-->[- _, + _]: Functional, Z]: Z >--> Z =
  `z=>z` asProgram

def identity[>-->[- _, + _]: Functional, Z]: Z >--> Z =
  `z=>z` asProgram  

// construction

def `(z&&y)>-->z`[>-->[- _, + _]: Functional, Z, Y]: (Z && Y) >--> Z =
  `(z&&y)=>z` asProgram
    
def `(z&&y)>-->y`[>-->[- _, + _]: Functional, Z, Y]: (Z && Y) >--> Y =
  `(z&&y)=>y` asProgram

// condition

def `z>-->(z||y)`[>-->[- _, + _]: Functional, Z, Y]: Z >--> (Z || Y) =
  `z=>(z||y)` asProgram
  
def `y>-->(z||y)`[>-->[- _, + _]: Functional, Z, Y]: Y >--> (Z || Y) =
  `y=>(z||y)` asProgram 

// construction and condition
  
def `(z&&b)>-->(z||z)`[>-->[- _, + _]: Functional, Z]: (Z && Boolean) >--> (Z || Z) =
  `(z&&b)=>(z||z)` asProgram 

// ...  
```

are program utilities,

where

```scala
package psbp.specification.function

// functional

def `z=>z`[Z]: Z => Z = 
  z =>
    z

// construction

def `(z&&y)=>z`[Z, Y]: (Z && Y) => Z =
  (z, _) => 
    z

def `(z&&y)=>y`[Z, Y]: (Z && Y) => Y =
  (_, y) => 
    y  

// condition

import ||.{ Left, Right }

def `z=>(z||y)`[Z, Y]: Z => (Z || Y) =
  z =>
    Left(z)

def `y=>(z||y)`[Z, Y]: Y => (Z || Y) =
  y =>
    Right(y)   

// construction and condition

def `(z&&b)=>(z||z)`[Z]: (Z && Boolean) => (Z || Z) =
  (z, b) => 
    (z, b) match {
      case (_, true) => Left(z)
      case (_, false) => Right(z) 
    } 
    
// ...    
```

are function utilities.

The members `&&&` resp. `|||` are `extension`'s that are more complex versions of `&&` resp. `||`.

The `Let {} In {}` programming ingredient specifies that programs can *define local values*.

The `If() {} Else {}` programming ingredient specifies that programs can *perform if-then-else based logic*.

`Let {} In {}` and `If() {} Else {}` are perfect examples of the **sca**lable **la**nguage ingredient of `Scala`.

The member `toMainWith` is an `extension` that combines a program with a producer and a consumer to a main program.

The simple, pointfree definition of `factorial` has abstracted away complexity of the pointful definition.

On the one hand this is an advantage because being confronted with complexity is not fun.

On the other hand this is a disadvantage because abstraction needs to be understood.

But here is the thing: often different definitions have similar complexity.

Understanding the abstraction of similar complexity of different definitions needs to be done *only once*.

Being confronted with the complexity of different situations happens *over and over again*.

Let's explain the pointfree definition of `toMainWith` in a pointful way using an argument `()` (thinking of programs as functions).

At level `1`, `toMainWith` looks like

```scala
      producer >--> {
        Let { 
          program
        } In { 
          consumer 
        }
      }  
```

```scala
      // level 1.1 
        >--> 
          // level 1.2
```

at level `1.1`, `toMainWith` looks like

```scala
    producer
```

The result is `producer(())`, which equals, say, `z` a produced argument value of type `Z`.

At level `1.2`, `toMainWith` looks like

```scala
        Let { 
          // level 2.1
        } In { 
          // level 2.2
        }
      }  
```

At level `2.1`, `toMainWith` looks like

```scala
          program
```

a local result value, say `y`, which equals `program(z)` is constructed, which is available as the second component of a product value `(z, y)`.

At level `2.2`, `factorial` looks like

```scala
          consumer 
```

The result is `consumer(z, y)`, which equals `consumer(z, program(z))`, which equals `consumer(producer(()), program(producer(())))`.

## About power of expression of `&&&` and `Let {} In {}`

```scala
package examples.specification.program

import psbp.specification.types.&&

import psbp.specification.program.Program

import psbp.specification.functional.{ `z>-->(z&&z)`, `(z&&y)>-->z` , `(z&&y&&x)>-->(y&&x)` }

def `construct using &&&`[>-->[- _, + _]: Program, Z, Y, X] 
  (`z>-->y`: Z >--> Y, `z>-->x`: => Z >--> X): Z >--> (Y && X) =
  `z>-->(z&&z)` >--> (`z>-->y` &&& `z>-->x`)

def constructUsingLet[>-->[- _, + _]: Program, Z, Y, X] 
  (`z>-->y`: Z >--> Y, `z>-->x`: => Z >--> X): Z >--> (Y && X) =
 
  val program: Program[>-->] = summon[Program[>-->]]
  import program.Let

  Let {
    `z>-->y`
  } In {
    Let {
      `(z&&y)>-->z` >--> `z>-->x`
    } In {
      `(z&&y&&x)>-->(y&&x)`
    }
  }
```

where

```scala
package psbp.specification.program

// ...

// construction

// ...

def `z>-->(z&&z)`[>-->[- _, + _]: Functional, Z]: Z >--> (Z && Z) =
  `z=>(z&&z)` asProgram  

def `(z&&y&&x)>-->(y&&x)`[>-->[- _, + _]: Functional, Z, Y, X]: (Z && Y && X) >--> (Y && X) =
  `(z&&y&&x)=>(y&&x)` asProgram
```

are program utilities,

where

```scala
package psbp.specification.program

// ...

// construction

// ...

def `z=>(z&&z)`[Z]: Z => (Z && Z) =
  z =>
    (z, z)   
    
def `(z&&y&&x)=>(y&&x)`[Z, Y, X]: (Z && Y && X) => (Y && X) =
  case ((_, y), x) =>
    (y, x) 
```

are function utilities.

`&&&` and `Let {} In {}` have the same power of expression as `construct` and `&&`.

## About power of expression of `|||` and `If() {} Else {}`

```scala
package examples.specification.program

import psbp.specification.types.||

import psbp.specification.program.Program

import psbp.specification.functional.{ `(z||z)>-->z`, `(y||x)>-->b`, `(y||x)>-->y`, `(y||x)>-->x` }

def `conditionally using |||`[>-->[- _, + _]: Program, Z, Y, X]
  (`y>-->z`: => Y >--> Z, `x>-->z`: => X >--> Z): (Y || X) >--> Z =
  (`y>-->z` ||| `x>-->z`) >--> `(z||z)>-->z`

def conditionallyUsingIf[>-->[- _, + _]: Program, Z, Y, X]
  (`y>-->z`: => Y >--> Z, `x>-->z`: => X >--> Z): (Y || X) >--> Z =

  val program: Program[>-->] = summon[Program[>-->]]
  import program.If

  If(`(y||x)>-->b`) {
    `(y||x)>-->y` >--> `y>-->z`
  } Else {
    `(y||x)>-->x` >--> `x>-->z`
  }
```
where

```scala
package psbp.specification.program

// ...

// condition

def `(z||z)>-->z`[>-->[- _, + _]: Functional, Z]: (Z || Z) >--> Z =
  `(z||z)=>z` asProgram  
  
def `(y||x)>-->b`[>-->[- _, + _]: Functional, Y, X]: (Y || X) >--> Boolean =
  `(y||x)=>b` asProgram

def `(y||x)>-->y`[>-->[- _, + _]: Functional, Y, X]: (Y || X) >--> Y =
  `(y||x)=>y` asProgram

def `(y||x)>-->x`[>-->[- _, + _]: Functional, Y, X]: (Y || X) >--> X =
  `(y||x)=>x` asProgram

// ...
```

are program utilities,

where

```scala
package psbp.specification.function

// ...

// condition

// ...

def `(z||z)=>z`[Z]: (Z || Z) => Z =
  _.foldSum(z => z, z => z)  
  
def `(y||x)=>b`[Y, X]: (Y || X) => Boolean =
  _.foldSum(_ => true, _ => false)

def `(y||x)=>y`[Y, X]: (Y || X) => Y =
  _.foldSum(y => y, _ => ???) 

def `(y||x)=>x`[Y, X]: (Y || X) => X =
  _.foldSum(_ => ???, x => x)
```

are function utilities.

`|||` and `If() {} Else {}` have the same power of expression as `conditionally` and `||`.

## `factorial`

```scala
package examples.specification.program

import psbp.specification.program.Program

import examples.specification.functional.{ isZero, one, subtractOne, multiply }

def factorial[>-->[- _, + _]: Program]: BigInt >--> BigInt =

  val program: Program[>-->] = summon[Program[>-->]]
  import program.{ Let, If }

  If(isZero) {
    one
  } Else {
    Let {
      subtractOne >--> factorial
    } In {
      multiply
    }
  }
```

where

```scala
package examples.specification.functional

import scala.language.postfixOps

import psbp.specification.types.&&

import psbp.specification.program.Functional

import examples.specification.function

def isZero[>-->[- _, + _]: Functional]: BigInt >--> Boolean =  
  function.isZero asProgram

def one[>-->[- _, + _]: Functional, Z]: Z >--> BigInt =
  function.one asProgram

def subtractOne[>-->[- _, + _]: Functional]: BigInt >--> BigInt =
  function.subtractOne asProgram

def multiply[>-->[- _, + _]: Functional]: (BigInt && BigInt) >--> BigInt =  
  function.multiply asProgram


// ...
```

are program utilities,

where

```scala
package examples.specification.function

import psbp.specification.types.&&

val isZero: BigInt => Boolean =
  n =>
   n == 0
  
def one[Z]: Z => BigInt =
  _ =>
    1
  
val subtractOne: BigInt => BigInt =
  n =>
    n - 1
    
val multiply: BigInt && BigInt => BigInt =
  (n, m) =>
    n * m

// ...
```

are function utilities.

`factorial` is a program that makes use of programming ingredients 

- `asProgram` (being functional)
- `>-->` (sequential composition)
- `Let {} In {}` (defining local values)
- `If() {} Else {}` (performing if-then-else logic)

Let's explain the pointfree definition of `factorial` in a pointful way using an argument `i` (thinking of programs as functions).

At level `1`, `factorial` looks like

```scala
  If(isZero) {
    // level 2.1
  } Else {
    // level 2.2
  }
```

If `isZero(i)`, in other words, if `i == 0` then, 

at level `2.1`, `factorial` looks like

```scala
    one
```

The result is `one(i)`, which equals `1`.

Otherwise, in other words, if `i != 0` then, 

at level `2.2`, `factorial` looks like

```scala
    Let {
      // level 3.1
    } In {
      // level 3.2
    }
```

At level `3.1`, `factorial` looks like

```scala
      subtractOne >--> factorial
```

a local value, which equals `(subtractOne >--> factorial)(i)`, which equals `factorial(subtractOne(i))`, which equals `factorial(i - 1)` is constructed, which is available as the second component of a product value `(i, factorial(i - 1))`.

At level `3.2`, `factorial` looks like

```scala
      multiply
```

The result is `multiply(i, factorial(i - 1))`, which equals `i * factorial(i - 1)`.

## `fibonacci`

```scala
package examples.specification.program

import psbp.specification.program.Program

import examples.specification.functional.{ isZero, zero, isOne, one, subtractOne, subtractTwo, add }

def fibonacci[>-->[- _, + _]: Program]: BigInt >--> BigInt =

  val program: Program[>-->] = summon[Program[>-->]]
  import program.If

  If(isZero) {
    zero
  } Else {
    If(isOne) {
      one
    } Else {
      (subtractOne && subtractTwo) >-->
        (fibonacci &&& fibonacci) >-->
        add
    }
  }
```

where

```scala
package examples.specification.program

// ...

def zero[Z]: Z => BigInt =
  _ =>
    0

val isOne: BigInt => Boolean =
  n =>
   n == 1
   
val subtractTwo: BigInt => BigInt =
  n =>
    n - 2   

val add: BigInt && BigInt => BigInt =
  (n, m) =>
    n + m

// ...
```

are program utilities,

where

```scala
package examples.specification.functional

// ..

def isOne[>-->[- _, + _]: Functional]: BigInt >--> Boolean =  
  function.isOne asProgram  

def subtractTwo[>-->[- _, + _]: Functional]: BigInt >--> BigInt =
  function.subtractTwo asProgram  

def add[>-->[- _, + _]: Functional]: (BigInt && BigInt) >--> BigInt =  
  function.add asProgram 

// ...
```

are function utilities.

`fibonacci` is a program that makes use of programming ingredients 

- `asProgram` (being functional)
- `>-->` (sequential composition)
- `&&` and `&&&` (construction)
- `If() {} Else {}` (performing if-then-else logic)

Let's explain the pointfree definition of `fibonacci` in a pointful way using an argument `i` (thinking of programs as functions).

At level `1`, `fibonacci` looks like

```scala
  If(isZero) {
    // level 2.1
  } Else {
    // level 2.2
  }
```

If `isZero(i)`, in other words, if `i == 0` then, 

at level `2.1`, `fibonacci` looks like

```scala
    zero
```

The result is `zero(i)`, which equals `0`.

Otherwise, in other words, if `i != 0` then, 

at level `2.2`, `fibonacci` looks like

```scala
    If(isOne) {
      // level 3.1
    } Else {
      // level 3.2
    }
```

If `isOne(i)` in other words if `i == 1` then, 

at level `3.1`, `fibonacci` looks like

```scala
      one
```

The result is `one(i)`, which equals `1`.

Otherwise, in other words, if `i != 1` then, 

at level `3.2`, `fibonacci` looks like

```scala
      (subtractOne && subtractTwo) >-->
        (fibonacci &&& fibonacci) >-->
        add
```

A first product value `(subtractOne && subtractTwo)(i)`, which equals `(i - 1, i - 2)`, is constructed.

A second product value `(fibonacci &&& fibonacci)(i - 1, i - 2)` which equals `(fibonacci(i - 1), fibonacci(i - 2))` is constructed.

The result is `add(fibonacci(i - 1), fibonacci(i - 2))` which equals `fibonacci(i - 1) + fibonacci(i - 2)`.

## `mainFactorial`

```scala
package examples.specification.program.effectful

import psbp.specification.program.Program

import examples.specification.program.factorial

def mainFactorial[>-->[- _, + _]: Program]: Unit >--> Unit =
  factorial toMainWith (
    producer = intProducer,
    consumer = factorialConsumer
  )
```

where


```scala
package examples.specification.program.effectful

import scala.language.postfixOps

import psbp.specification.program.Program

def intProducer[>-->[- _, + _]: Program]: Unit >--> BigInt = 
  { (_: Unit) =>
      println("Please type an integer")
      BigInt(scala.io.StdIn.readInt)
  } asProgram
```

and where

```scala
package examples.specification.program.effectful

import scala.language.postfixOps

import psbp.specification.types.&&

import psbp.specification.program.Program 

def factorialConsumer[>-->[- _, + _]: Program]: (BigInt && BigInt) >--> Unit =
  {
    (`i&&j`: BigInt && BigInt) =>
      val i = `i&&j`._1
      val j = `i&&j`._2
      println(s"applying factorial to the integer argument $i yields result $j")
  } asProgram
```

`mainFactorial` is a main program that, for now, makes use of an *effectful* producer and an *effectful* consumer.

## `mainFibonacci`

```scala
package examples.specification.program.effectful

import psbp.specification.program.Program

import examples.specification.program.fibonacci

def mainFibonacci[>-->[- _, + _]: Program]: Unit >--> Unit =
  fibonacci toMainWith (
    producer = intProducer,
    consumer = fibonacciConsumer
  )
```

where

```scala
package examples.specification.program.effectful

// ...

def fibonacciConsumer[>-->[- _, + _]: Program]: (BigInt && BigInt) >--> Unit =
  {
    (`i&&j`: BigInt && BigInt) =>
      val i = `i&&j`._1
      val j = `i&&j`._2
      println(s"applying fibonacci to the integer argument $i yields result $j")
  } asProgram
```

`mainFibonacci` is a main program that, for now, makes use of an effectful producer and an effectful consumer.

## Materialization

```scala
package psbp.specification.materialization

trait Materialization[>-->[- _, + _], -Z, +Y]:

  // declared

  private[psbp] val materialize: (Unit >--> Unit) => Z => Y

  // defined extensions

  extension (`u>-->u`: Unit >--> Unit) def materialized: Z => Y =
    materialize(`u>-->u`)
```

Main programs are materialized to functions.

The function type `Z => Y` is generic to allow for various materializations of main programs.

The public member `materialized` is an `extension` that can be used as postfix operator.

# Computations

An *internal specification part* of the `PSBP` library models *computing*.

Think about it as explaining to `Scala` what computing is all about.

Members of internal specification `trait`'s are referred to as *computing ingredients*.

A `val` that is defined in terms of computing ingredients is referred to as a *computation*. 

A computation has a *descriptive* nature, in fact it would be more appropriate to refer to it as a *computation description*.

In a way a computation, is a generalized descriptive version of an *expression*.

In a way computing, performing a computation, is a generalized descriptive version of *evaluating an expression*.

Evaluating an expression of type `C[Y]` yields *result* of type `Y`.

A computation *describes* evaluating an expression of type `Y` yielding a result of type `Y`.

A computation may, or may not, *describe* performing side effects along the way.

## Basic computing ingredients

The *basic* computing ingredients are

- *resulting*
- *binding*

Computing ingredients, just like the corresponding expression evaluation ingredients have an *operational* nature.

They are also *pointful*.

## `Resulting`

```scala
package psbp.internalSpecification.computation

private[psbp] trait Resulting[C[+ _]]:

  // declared

  private[psbp] def result[Z]: Z => C[Z]
```

The `result` member of `Computation` specifies that executing a computation yields a *result*.

## `Binding`

```scala
package psbp.internalSpecification.computation

private[psbp] trait Binding[C[+ _]]:

  // declared

  private[psbp] def bind[Z, Y] (cz: C[Z], `z=>cy`: => Z => C[Y]): C[Y]

  // defined

  private[psbp] def join[Z] (ccz: C[C[Z]]): C[Z] =
    bind(ccz, identity)

  // defined extensions

  extension [Z, Y] (cz: C[Z]) def >=(`z=>cy`: => Z => C[Y]): C[Y] =
    bind(cz, `z=>cy`)

  extension [Z, Y] (ccz: C[C[Z]]) def joined =
    join(ccz) 
```

The `bind` computing ingredient specifies that while executing a computation, the result yielded by executing an inner computation can be *bound* to the argument of a *continuation function* transforming it to a result that is an outer computation where executing continues with.

Compare this with evaluating expressions.

The `join` computing ingredient specifies *nested computations*.

Compare this with nested expressions.

The member `>=` is an`extension` that can be used as infix operator.

The member `joined` is an`extension` that can be used as postfix operator.

## `Computation`

```scala
package psbp.internalSpecification.computation

private[psbp] trait Computation[C[+ _]] 
  extends Resulting[C] 
  with Binding[C]
```

## `programFromComputation`

```scala
package psbp.internalImplementation.computation

import psbp.specification.types.{ &&, || }

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

private[psbp] given programFromComputation[C[+ _]: Computation]: Program[[Z, Y] =>> Z => C[Y]] with
  
  private val computation: Computation[C] = summon[Computation[C]]
  import computation.result

  private type `=>C`[-Z, +Y] = Z => C[Y]

  private[psbp] override def toProgram[Z, Y]: (Z => Y) => Z `=>C` Y = 
    `z=>y` => 
      z =>
        result(`z=>y`(z))

  private[psbp] override def andThen[Z, Y, X]
    (`z>-->y`: Z `=>C` Y, `y>-->x`: => Y `=>C` X): Z `=>C` X =
    z =>
      `z>-->y`(z) >= 
        `y>-->x`

  private[psbp] override def construct[Z, Y, X]
    (`z>-->y`: Z `=>C` Y, `z>-->x`: => Z `=>C` X): Z `=>C` (Y && X) =
    z =>
      `z>-->y`(z) >= { y => 
        `z>-->x`(z) >= { x =>
          result(y, x)
        }
      }

  private[psbp] override def conditionally[Z, Y, X]
    (`y>-->z`: => Y `=>C` Z, `x>-->z`: => X `=>C` Z): (Y || X) `=>C` Z =
    _.foldSum(`y>-->z`, `x>-->z`) 
```

`programFromComputation` is a `given` that defines the basic programming ingredients of a program of type `Z => C[Y]` in terms of the basic computing ingredients of the computation of type `C[Y]`.

Compare this with functions being defined in terms of expressions.

Using injection by `import` of `programFromComputation`, a generic `given` implementation of `Program`, only `given` implementations of  `Computation` need to be injected by `import`.

## Setting the scene

*The* `PSBP` *library limits the program types* `>-->[- _, + _]` *to program types* `[Z, Y] =>> Z => C[Y]` *defined in terms of computation types* `[Y] =>> C[Y]`, or `C[+ _]` for short.

The `PSBP` specification `trait`''s define an *application developer API*.

The `PSBP` internal specification `trait`''s define a *library developer API*.

It is a deliberate choice to not let application developers make use of the pointful library developer API, forcing them to think in a pointfree way.

# Active programming

So far no implementation of the basic computing ingredients has been given yet.

A completely trivial way to implement computations of type `C[Y]` is as expressions of type `Y`, referred to as *active computations*.

As a consequence, programs of type `Z => C[Y]`, are implemented as functions of type `Z => Y`, referred to as *active programs*.

## `activeProgram` 

```scala
package psbp.implementation.active

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.programFromComputation

given activeComputation: Computation[Active] with

  private[psbp] def result[Z]: Z => Active[Z] =
    z =>
      z

  private[psbp] def bind[Z, Y] (cz: Active[Z], `z=>cy`: => Z => Active[Y]): Active[Y] =
    `z=>cy`(cz)

given activeProgram: Program[`=>A`] = programFromComputation[Active]
```

where

```scala
package psbp.implementation.active

type Active[+Y] = Y

type `=>A`[-Z, +Y] = Z => Active[Y]
```

`Active` is a  `given` implementation of `Computation`.

As a consequence, `` `=>A` `` is an implementation of `Program`.

Think of active programming as *pure functional programming*.

Of course, using `Scala`, it is possible to write functions that perform side effects along the way of transforming their argument to a result.

For example, inside of `mainFactorial`, `intProducer` are `factorialConsumer` perform side effects.

Eventually side effects are unavoidable, but the intention is to push them as far as possible to the outside of programs in general, and main programs in particular.

## `activeMaterialization` 

```scala
package psbp.implementation.active

import psbp.specification.materialization.Materialization

given activeMaterialization: Materialization[`=>A`, Unit, Unit] with

  val materialize: (Unit `=>A` Unit) => Unit => Unit =
    identity
```

Materialization of `` `=>A` `` is completely trivial.

## Running `active` `factorial` (effectful producer and consumer)

```scala
package examples.implementation.active.program.effectful

import psbp.implementation.active.given

import examples.specification.program.effectful.mainFactorial

@main def factorial(args: String*): Unit =
  mainFactorial materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.active.program.effectful.factorial 
Please type an integer
10
applying factorial to the integer argument 10 yields result 3628800
[success] ...
```

## Running `active` `fibonacci` (effectful producer and consumer)

```scala
package examples.implementation.active.program.effectful

import psbp.implementation.active.given

import examples.specification.program.effectful.mainFibonacci

@main def fibonacci(args: String*): Unit =
  mainFibonacci materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.active.program.effectful.fibonacci 
Please type an integer
10
applying fibonacci to the integer argument 10 yields result 55
[success] ...
```

# Reactive programming

A less trivial way to implement computations of type `C[Y]` is as *callback handlers* of type `(Y => Unit) => Unit`, referred to as *reactive computations*.

As a consequence, programs of type `Z => C[Y]`, are implemented as functions to callback handlers, referred to as *reactive programs*.

## `reactiveProgram` 

```scala
package psbp.implementation.reactive

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.programFromComputation

given reactiveComputation: Computation[Reactive] = reactiveTransformedComputation[Active]

  private[psbp] def result[Z]: Z => Reactive[Z] =
    z => 
      `z=>u` =>
        `z=>u`(z)

  private[psbp] def bind[Z, Y] (cz: Reactive[Z], `z=>cy`: => Z => Reactive[Y]): Reactive[Y] =
    `y=>u` =>
      cz { 
        z =>
          `z=>cy`(z)(`y=>u`)
      }

given reactiveProgram: Program[`=>R`] = programFromComputation[Reactive]
```

where

```scala
package psbp.implementation.reactive

type Reactive = [Y] =>> (Y => Unit) => Unit

type `=>R` = [Z, Y] =>> Z => Reactive[Y]
```

`Reactive` is a  `given` implementation of `Computation`.

As a consequence, `` `=>R` `` is an implementation of `Program`.

## `reactiveMaterialization` 

```scala
package psbp.implementation.reactive

import psbp.specification.materialization.Materialization

given reactiveMaterialization: Materialization[`=>R`, Unit, Unit] with

  val materialize: (Unit `=>R` Unit) => Unit => Unit =
    `u>-->u` =>
      u =>
        `u>-->u`(u)(identity)
```

Materialization of `` `=>R` `` is less trivial.

## Getting the types right

For reactive programming, there is really only one reasonable way to define `result`, `bind` and `materialize` in a way to get the types right.

This is a recurring theme in pure functional programming and, as a generalization, in program specification based programming.

Often an almost trivial choice for a function component, resp. program component does the trick to get the types right.

## Running `reactive` `factorial` (effectful producer and consumer)

```scala
package examples.implementation.reactive.program.effectful

import psbp.implementation.reactive.given

import examples.specification.program.effectful.mainFactorial

@main def factorial(args: String*): Unit =
  mainFactorial materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.reactive.program.effectful.factorial 
Please type an integer
10
applying factorial to the integer argument 10 yields result 3628800
[success] ...
```

The only difference with the active version is the usage of a different injection by `import`.

## Running `reactive` `fibonacci` (effectful producer and consumer)

```scala
package examples.implementation.reactive.program.effectful

import psbp.implementation.reactive.given

import examples.specification.program.effectful.mainFibonacci

@main def fibonacci(args: String*): Unit =
  mainFibonacci materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.reactive.program.effectful.fibonacci 
Please type an integer
10
applying fibonacci to the integer argument 10 yields result 55
[success] ...
```
## Some remarks

Active `fibonacci` is extremely inefficient. 

Reactive `fibonacci` is even worse.

Time for *optimization*!

# Accumulator based optimization

```scala
package psbp.specification.program

import psbp.specification.types.&&

import psbp.specification.program.Program

import psbp.specification.functional.{ `(z&&y)>-->z`, `(z&&y)>-->y` }

// accumulator based optimization

def optimizeWith[>-->[- _, + _]: Program, A, Z, Y](
  accumulatorInitializer: Z >--> A,
  argumentPredicate: Z >--> Boolean,
  updater: (Z && A) >--> (Z && A),
  resultExtractor: A >--> Y): Z >--> Y =

  val program: Program[>-->] = summon[Program[>-->]]
  import program.{ Let, If }

  val argument: (Z && A) >--> Z = `(z&&y)>-->z`
  val accumulator: (Z && A) >--> A = `(z&&y)>-->y`

  lazy val recursiveAccumulatorUpdater: (Z && A) >--> A =
    If(argument >--> argumentPredicate) {
      accumulator
    } Else {
      updater >--> recursiveAccumulatorUpdater
    }
    
  Let {
    accumulatorInitializer
  } In {
    recursiveAccumulatorUpdater >--> resultExtractor
  }    
```

Recursive programs like `fibonacci` can be optimized using an *accumulator*.

`recursiveAccumulatorUpdater` is also a recursive program, but, when carefully designed, it should be efficient.

## `optimizedFactorial`

```scala
package examples.specification.program

import psbp.specification.program.Program

import psbp.specification.functional.{ `(z&&y)>-->z` => argument, `z>-->z` => accumulator }

import psbp.specification.program.optimizeWith

import examples.specification.functional.{ isZero, one, subtractOne, multiply }

def optimizedFactorial[>-->[- _, + _]: Program]: BigInt >--> BigInt =
  optimizeWith(
    accumulatorInitializer = one, 
    argumentPredicate = isZero, 
    updater = (argument >--> subtractOne) && multiply, 
    resultExtractor = accumulator
  )
```

For `optimizedFactorial` the accumulator type is `BigInt`.

One `BigInt` optimizes one recursive occurrence.

## `optimizedFibonacci`

```scala
package examples.specification.program

import psbp.specification.program.Program

import psbp.specification.functional.{ `(z&&y)>-->z` => firstAccumulator, `(z&&y)>-->y` => secondAccumulator }

import psbp.specification.program.optimizeWith

import examples.specification.functional.{ isZero, zero, isOne, one, subtractOne, subtractTwo, add }

def optimizedFibonacci[>-->[- _, + _]: Program]: BigInt >--> BigInt =
  optimizeWith(
    accumulatorInitializer = one && one, 
    argumentPredicate = isZero, 
    updater = subtractOne &&& (secondAccumulator && add), 
    resultExtractor = firstAccumulator
  )
```

For `optimizedFibonacci` the accumulator type is `BigInt && BigInt`.

Two `BigInt`'s optimize two recursive occurrences.

## `mainOptimizedFactorial`

```scala
package examples.specification.program.effectful

import psbp.specification.program.Program

import examples.specification.program.optimizedFactorial

def mainOptimizedFactorial[>-->[- _, + _]: Program]: Unit >--> Unit =
  optimizedFactorial toMainWith (
    producer = intProducer,
    consumer = factorialConsumer
  )
```

## `mainOptimizedFibonacci`

```scala
package examples.specification.program.effectful

import psbp.specification.program.Program

import examples.specification.program.optimizedFibonacci

def mainOptimizedFibonacci[>-->[- _, + _]: Program]: Unit >--> Unit =
  optimizedFibonacci toMainWith (
    producer = intProducer,
    consumer = fibonacciConsumer
  )
```

## Running `active` `optimizedFactorial` (effectful producer and consumer)

```scala
package examples.active.program.effectful

import psbp.active.program.given

import psbp.active.materialization.given

import examples.specification.program.effectful.mainOptimizedFactorial

@main def optimizedFactorial(args: String*): Unit =
  mainOptimizedFactorial materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.active.program.effectful.optimizedFactorial 
Please type an integer
10
applying factorial to the integer argument 10 yields result 3628800
[success] ...
```

## Running `active` `optimizedFibonacci` (effectful producer and consumer)

```scala
package examples.implementation.active.program.effectful

import psbp.implementation.active.given

import examples.specification.program.effectful.mainOptimizedFibonacci

@main def optimizedFibonacci(args: String*): Unit =
  mainOptimizedFibonacci materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.active.program.effectful.optimizedFibonacci 
Please type an integer
10
applying fibonacci to the integer argument 10 yields result 89
[success] ...
```

You can also try it with `1000` instead of `10`.

With `10000` you will get a stack overflow.

## Running `reactive` `optimizedFactorial` (effectful producer and consumer)

```scala
package examples.implementation.reactive.program.effectful

import psbp.implementation.reactive.given

import examples.specification.program.effectful.mainOptimizedFactorial

@main def optimizedFactorial(args: String*): Unit =
  mainOptimizedFactorial materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.reactive.program.effectful.optimizedFactorial 
Please type an integer
10
applying factorial to the integer argument 10 yields result 3628800
[success] ...
```

## reactive optimized `fibonacci`

```scala
package examples.implementation.reactive.program.effectful

import psbp.implementation.reactive.given

import examples.specification.program.effectful.mainOptimizedFibonacci

@main def optimizedFibonacci(args: String*): Unit =
  mainOptimizedFibonacci materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.reactive.program.effectful.optimizedFibonacci 
Please type an integer
10
applying fibonacci to the integer argument 10 yields result 89
[success] ...
```
You can also try it with `250` instead of `10`.

With `1000` you will get a stack overflow.

# `Transformation`

Until now active and reactive `given` implementations of `Program` have been dealt with.

Using *computation transformations* one `given` implementation can be transformed to another one.

## `~>`

```scala
package psbp.internalSpecification.naturalTransformation

private[psbp] trait ~>[F[+ _], T[+ _]]:

  // declared

  private[psbp] def apply[Z]: F[Z] => T[Z] 
```

A *natural transformation* of type `F ~> T` is like a function of type `F[Z] => T[Z]`. 

Instead of transforming at type level it transforms at *type constructor* level.

## `Transformation`

```scala
package psbp.internalSpecification.computation.transformation

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.naturalTransformation.~>

private[psbp] trait Transformation[F[+ _]: Computation, T[+ _]] extends Computation[T]:

  // declared

  private[psbp] val `f~>t`: F ~> T
  
  // defined
  
  override private[psbp] def result[Z]: Z => T[Z] = 

    import `f~>t`.{ apply => `fz=>tz` }

    val computationF: Computation[F] = summon[Computation[F]]
    import computationF.{ result => `z=>fz` }
    
    `z=>fz` andThen `fz=>tz`
```

A *computation transformation* uses a natural transformation to define `result` of the `T[+ _]` computation in terms of `result` of the `F[+ _]` computation.

`Transformation` is defined as a `trait`.

It cannot be defined as a `given` since `bind` is not defined yet.

## `ReactiveTransformed`

```scala
package psbp.internalImplementation.computation.transformation

// ReactiveTransformed

private[psbp] type ReactiveTransformed[C[+ _]] = [Z] =>> (C[Z] => Unit) => Unit

// ...
```

A reactive transformed computation is a *computation callback handler*, a computation handling *computation* callbacks.

## `reactiveTransformedComputation`

```scala
package psbp.internalImplementation.computation.transformation

import psbp.internalSpecification.naturalTransformation.~>

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.computation.transformation.Transformation

private[psbp] given reactiveTransformedComputation[
  C[+ _]: Computation]: Transformation[C, ReactiveTransformed[C]] 
  with Computation[ReactiveTransformed[C]] with 

  private type F[+Z] = C[Z]
  private type T[+Z] = ReactiveTransformed[F][Z]
  
  private val computationF: Computation[F] = summon[Computation[F]]
  import computationF.{ result => resultF, bind => bindF }
    
  override private[psbp] val `f~>t`: F ~> T = new {
    def apply[Z]: F[Z] => T[Z] =
      fz => 
        `fz=>u` =>
          `fz=>u`(fz)
  }
  
  override private[psbp] def bind[Z, Y] (tz: T[Z], `z=>ty` : => Z => T[Y]): T[Y] =
    `fy=>u` =>
      tz { 
        fz =>
          bindF(fz, { z =>
            resultF(`z=>ty`(z)(`fy=>u`))
          })
      }
```

Any computation of type `C[Z]` can, using `reactiveTransformedComputation`, be transformed to a computation of type `(C[Z] => Unit) => Unit` that is a computation callback handler. 

The `` `f~>t` `` member trivially uses a computation as a computation callback handler of type using function application. 

## `CoResulting`

```scala
package psbp.internalSpecification.computation

private[psbp] trait CoResulting[C[+ _]]:

  // declared

  private[psbp] def coResult[Z]: C[Z] => Z
```

Reactive transformed materialization below depends on *co-resulting*, the *dual* of resulting.

## `reactiveTransformedMaterialization`

```scala
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
```

Transforming materialization of `[Z, Y] =>> Z => C[Y], Z, Y`, to materialization, of `[Z, Y] =>> Z => (C[Y] => Unit) => Unit, Unit, Unit`, is done using `reactiveTransformedMaterialization` which makes use of `coResult`. 

The definition of `materialize` is the only reasonable one to get the types right.

## `activeCoResulting`

```scala
package psbp.implementation.active

import psbp.internalSpecification.computation.CoResulting

given activeCoResulting: CoResulting[Active] with
  override def coResult[Z]: Active[Z] => Z =
    identity
```

Co-resulting of `Active` is completely trivial.

## `reactiveProgram` revisited

```scala
package psbp.implementation.reactive

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.programFromComputation

import psbp.internalImplementation.computation.transformation.reactiveTransformedComputation

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given reactiveComputation: Computation[Reactive] = reactiveTransformedComputation[Active]

given reactiveProgram: Program[`=>R`] = programFromComputation[Reactive]
```

where

```scala
package psbp.implementation.reactive

import psbp.internalImplementation.computation.transformation.ReactiveTransformed

import psbp.implementation.active.Active

type Reactive = [Y] =>> ReactiveTransformed[Active][Y] 

type `=>R` = [Z, Y] =>> Z => Reactive[Y]
```

Transforming from active programming with `` `=>A` `` to reactive programming with `` `=>R` `` is done by using a combination of `reactiveTransformedComputation` and `programFromComputation`.

## `reactiveMaterialization` revisited

```scala
package psbp.implementation.reactive

import psbp.specification.materialization.Materialization

import psbp.internalImplementation.materialization.reactiveTransformedMaterialization

import psbp.implementation.active.Active

import psbp.implementation.active.{ activeComputation, activeMaterialization, activeCoResulting }

given reactiveMaterialization: Materialization[`=>R`, Unit, Unit] =
  reactiveTransformedMaterialization[Active, Unit, Unit]
```

Transforming from active materialization of `` `=>A` `` to reactive materialization of `` `=>R` `` is done by using `reactiveTransformedMaterialization`.

All reactive examples above still run fine, but you will get a stack overflow with large numbers.

Time for *tail recursive optimization*!

# Tail recursive optimization

## `FreeTransformed`

```scala
package psbp.internalImplementation.computation.transformation

//...

// FreeTransformed

private[psbp] enum Free[C[+ _], +Z]:

  private[psbp] case Transform[C[+ _], +Z](cz: C[Z]) extends Free[C, Z]

  private[psbp] case Result[C[+ _], +Z](z: Z) extends Free[C, Z]
  
  private[psbp] case Bind[C[+ _], -Z, ZZ <: Z, +Y]
    (fczz: Free[C, ZZ], `z=>fcy`: Z => FreeTransformed[C][Y]) extends Free[C, Y]

private[psbp] type FreeTransformed[C[+ _]] = [Z] =>> Free[C, Z]

// ...
```

A free transformed computation is a *computation algebraic data type*, also known as *computation ADT*.

It has the following `case`'s 

- `Transform` for transforming,
- `Result` for resulting,
- `Bind` for binding.

## `freeTransformedComputation`

```scala
package psbp.internalImplementation.computation.transformation

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.naturalTransformation.~>

import psbp.internalSpecification.computation.transformation.Transformation

import Free._

private[psbp] given freeTransformedComputation[C[+ _]: Computation]: Transformation[C, FreeTransformed[C]] with

  private type F[+Z] = C[Z]
  private type T[+Z] = FreeTransformed[F][Z] 
    
  override private[psbp] val `f~>t`: F ~> T = new {
    def apply[Z]: F[Z] => T[Z] =
      fz => 
        Transform(fz)
  }    

  override private[psbp] def result[Z]: Z => T[Z] =
    z =>
      Result(z)

  override private[psbp] def bind[Z, Y] (tz: T[Z], `z=>ty` : => Z => T[Y]): T[Y] = 
    Bind(tz, `z=>ty`) 
```

Any computation of type `C[Z]` can, using `freeTransformedComputation`, be transformed to a computation of type `Free[C, Y]` that is a computation ADT.

The `` `f~>t` `` member trivially stores a computation of type in a computation ADT of type. 

The `result` and `bind` members further *unfold* a stored computation of type in a computation ADT. 

## `foldFree`

```scala
package psbp.internalImplementation.computation.transformation

// ...

import Free._

import psbp.internalSpecification.computation.Computation

private[psbp] def foldFree[Z, C[+ _]: Computation](fcz: FreeTransformed[C][Z]): C[Z] =

  type F[+Z] = C[Z]
  type T[+Z] = FreeTransformed[F][Z]
 
  val computationF: Computation[F] = summon[Computation[F]]
  import computationF.{ result => resultF, bind => bindF } 

  fcz match {
    case Transform(fz) => 
      fz
    case Result(z) =>
      resultF(z)
    case Bind(Transform(fy), y2tz) =>
      bindF(resultF(fy), y2tz andThen foldFree)
    case Bind(Result(y), y2tz) =>
      foldFree(y2tz(y))
    case Bind(Bind(tx, x2ty), y2tz) =>
      foldFree(Bind(tx, { x => Bind(x2ty(x), y2tz) }))
    case any =>
      sys.error(s"Impossible, since, 'foldFree' eliminates the case for $any")
  }

// ...  
```

`foldFree` *folds* a computation ADT of type `Free[C, Y]` in which a computation of type `C[Y]` is stored by `Transform`, and *restores* it to a computation of type `C[Y]`.

Although `foldFree` is not fully tail recursive, the `Scala` compiler performs tail recursive optimization for those cases where it is tail recursive.

The one but last `case` in `foldFree` corresponds to the *associativity of binding*.

## `freeTransformedMaterialization`

```scala
package psbp.internalImplementation.materialization

import psbp.specification.materialization.Materialization

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.transformation.{ Free, foldFree, FreeTransformed }

import Free._

private[psbp] given freeTransformedMaterialization[
  C[+ _]: Computation: 
  [C[+ _]] =>> Materialization[[Z, Y] =>> Z => C[Y], Z, Y], Z, Y]: 
  Materialization[[Z, Y] =>> Z => FreeTransformed[C][Y], Z, Y] with

  private type F[+Z] = C[Z]
  private type T[+Z] = FreeTransformed[F][Z] 

  private type `=>F`[-Z, +Y] = Z => F[Y]
  private type `=>T`[-Z, +Y] = Z => T[Y]

  private val materializationF: Materialization[`=>F`, Z, Y] = summon[Materialization[`=>F`, Z, Y]]
  import materializationF.{ materialize => materializeF }  

  private def `tu=>fu`: T[Unit] => F[Unit] =
    foldFree

  override val materialize: (Unit `=>T` Unit) => Z => Y =
    `u=>tu` =>
      materializeF(`u=>tu` andThen `tu=>fu`)
```

Transforming materialization of `[Z, Y] =>> Z => C[Y], Z, Y`, to materialization, of `[Z, Y] =>> Z => Free[C, Y], Z, Y`, is done using `freeTransformedMaterialization` which makes use of `foldFree`.

## `freeCoResulting`

```scala
package psbp.internalImplementation.computation.transformation

import psbp.internalSpecification.computation.{ Computation, CoResulting }

private[psbp] given freeCoResulting[C[+ _]: Computation: CoResulting]: CoResulting[FreeTransformed[C]] with

  private type F[+Z] = C[Z]
  private type T[+Z] = FreeTransformed[F][Z] 
  
  private val coResultingF : CoResulting[F] = summon[CoResulting[F]]
  import coResultingF.{ coResult => `fz=>z` }

  private def `tz=>fz`[Z]: T[Z] => F[Z] =
    foldFree

  override def coResult[Z]: T[Z] => Z =
    `tz=>fz` andThen `fz=>z`
```

Transforming co-resulting to free co-resulting is done using `freeCoResulting` which also makes use of `foldFree`.

## `freeActiveProgram`

```scala
package psbp.implementation.freeActive

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.programFromComputation

import psbp.internalImplementation.computation.transformation.freeTransformedComputation

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given freeActiveComputation: Computation[FreeActive] = freeTransformedComputation[Active]

given freeActiveProgram: Program[`=>FA`] = programFromComputation[FreeActive]
```

where

```scala
package psbp.implementation.freeActive

import psbp.internalImplementation.computation.transformation.FreeTransformed

import psbp.implementation.active.Active

type FreeActive = [Y] =>> FreeTransformed[Active][Y] 

type `=>FA`= [Z, Y] =>> Z => FreeActive[Y]
```

Transforming from active programming with `` `=>A` `` to tail recursive active programming with `` `=>FA` `` is done by using a combination of `freeTransformedComputation` and `programFromComputation`.

## `freeActiveMaterialization`

```scala
package psbp.implementation.freeActive

import psbp.specification.materialization.Materialization

import psbp.internalImplementation.materialization.freeTransformedMaterialization

import psbp.implementation.active.Active

import psbp.implementation.active.{ activeComputation, activeMaterialization }

given freeActiveMaterialization: Materialization[`=>FA`, Unit, Unit] = 
  freeTransformedMaterialization[Active, Unit, Unit]
```

Transforming from active materialization of `` `=>A` `` to free active materialization of `` `=>FA` `` is done by using `freeTransformedMaterialization`.

## Running `freeActive` `factorial` (effectful producer and consumer)

```scala
package examples.implementation.freeActive.program.effectful

import psbp.implementation.freeActive.given

import examples.specification.program.effectful.mainFactorial

@main def factorial(args: String*): Unit =
  mainFactorial materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.freeActive.program.effectful.factorial 
Please type an integer
10
applying factorial to the integer argument 10 yields result 3628800
[success] ...
```

Again, the only difference with the active and reactive versions is the usage of a different injection by `import`.

## Running tail `freeActive` `optimizedFibonacci` (effectful producer and consumer)

```scala
package examples.implementation.freeActive.program.effectful

import psbp.implementation.freeActive.given

import examples.specification.program.effectful.mainOptimizedFibonacci

@main def optimizedFibonacci(args: String*): Unit =
  mainOptimizedFibonacci materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.freeActive.program.effectful.optimizedFibonacci 
Please type an integer
10
applying fibonacci to the integer argument 10 yields result 89
[success] ...
```

You can also try it with `10000` instead of `10`.

You will not get a stack overflow.

# Programming with state

## `State`

```scala
package psbp.specification.program.state

trait State[S, >-->[- _, + _]]:

  // declared

  private[psbp] def `u>-->s`: Unit >--> S

  private[psbp] def `s>-->u`: S >--> Unit

trait Initial[S]:
 
  // declared

  val s: S
```

`State` specifies that *programs can read and write state*.

`Initial` specifies that `S` has an *initial state*. 

## `ProgramWithState`

```scala
package psbp.specification.programWithState

import psbp.specification.program.Program

import psbp.specification.program.state.State

import psbp.specification.functional.`z>-->u`

trait ProgramWithState[S, >-->[- _, + _]] extends Program[>-->] with State[S, >-->]:

  private implicit val program: Program[>-->] = this

  // defined

  def readState[Z]: Z >--> S =
    `z>-->u` >--> `u>-->s`  

  def writeState: S >--> Unit =
    `s>-->u` 
    
  def modifyStateWith[Z]: (S >--> S) => (Z >--> Unit) =
    `s>-->s` =>
      readState >--> `s>-->s` >--> writeState
  
  def readStateModifiedWith[Z]: (S >--> S) => (Z >--> S) =
    `s>-->s` =>
      modifyStateWith(`s>-->s`) >--> readState
```

where

```scala
package psbp.specification.program

// ...
// functional

//...

def `z>-->u`[>-->[- _, + _]: Functional, Z]: Z >--> Unit =
  `z=>u` asProgram 

// ...  
```
is a program utility

where

```scala
package psbp.specification.function

// functional

// ...

def `z=>u`[Z]: Z => Unit = 
  z =>
    ()   

// ...    
```

is a function utility.

`readState`, `writeState`, `modifyStateWith` and `readStateModifiedWith` are useful state handling programs

## `programWithState`

```scala
package psbp.internalImplementation.programWithState

import psbp.specification.program.Program

import psbp.specification.program.state.State

import psbp.specification.programWithState.ProgramWithState

given programWithState[S, >-->[- _, + _]: Program: [>-->[- _, + _]] =>> State[S, >-->]]: ProgramWithState[S, >-->] with
 
  private val program: Program[>-->] = summon[Program[>-->]]

  private val state: State[S, >-->] = summon[State[S, >-->]]

  export program.toProgram
  export program.andThen
  export program.construct
  export program.conditionally

  export state.`u>-->s`
  export state.`s>-->u`
```

Using injection by `import` of `programWithState`, a generic `given` implementation of `ProgramWithState`, only `given` implementations of  `Program` and `State` need to be injected by `import`.

## `random`

```scala
package examples.specification.programWithState

import scala.language.postfixOps

import psbp.specification.programWithState.ProgramWithState

import examples.specification.program.negateIfNegative

type Seed = Long

def random[Z, >-->[- _, + _]: [>-->[- _, + _]] =>> ProgramWithState[Seed, >-->]]: Z >--> BigInt =

  val programWithSeedState: ProgramWithState[Seed, >-->] = summon[ProgramWithState[Seed, >-->]]
  import programWithSeedState.readStateModifiedWith
  
  object function {
    val seedModifier: Seed => Seed = 
      seed =>
        (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
  
    val seed2randomBigInt: Seed => BigInt = 
      seed =>
        BigInt((seed >>> 16).toInt)
        
    val moduloSomeLong: BigInt => BigInt =
      n =>
        n % 9876543210L     
  }
 
  val seedModifier: Seed >--> Seed =
    function.seedModifier asProgram

  val seed2randomBigInt =
    function.seed2randomBigInt asProgram

  val moduloSomeLong =
    function.moduloSomeLong asProgram

  val readModifiedSeed = readStateModifiedWith(seedModifier)   

  readModifiedSeed >--> seed2randomBigInt >--> negateIfNegative >--> moduloSomeLong 
```

where

```scala
package examples.specification.functional

// ...

def isNotNegative[>-->[- _, + _]: Functional]: BigInt >--> Boolean =  
  function.isNotNegative asProgram 

def negate[>-->[- _, + _]: Functional]: BigInt >--> BigInt =
  function.negate asProgram   
```
are program utilities

and

```scala
package examples.specification.program
  
import psbp.specification.program.Program  

import psbp.specification.functional.identity

import examples.specification.functional.{ isNotNegative, negate }

def negateIfNegative[>-->[- _, + _]: Program]: BigInt >--> BigInt =

  val program: Program[>-->] = summon[Program[>-->]]
  import program.If

  If(isNotNegative) {
    identity
  } Else {
    negate
  }
```

are program utilities

where

```scala
package examples.specification.function

// ...

val isNotNegative: BigInt => Boolean =
  n =>
   n >= 0

val negate: BigInt => BigInt =
  n =>
    -n  
```

are function utilities.

The implementation details are not important: while, somehow , transforming any argument to a random integer result, `random` modifies a seed computation state along the way.

## `twoRandoms`

```scala
package examples.specification.programWithState

import psbp.specification.types.&&

import psbp.specification.programWithState.ProgramWithState

def twoRandoms[Z, >-->[- _, + _]: [>-->[- _, + _]] =>> ProgramWithState[Seed, >-->]]: Z >--> (BigInt && BigInt) =
  random && random
```

Illustrating statefulness can be done by using `twoRandoms`, somehow , transforming any argument to two random integer results.

When running materialized main `twoRandoms` implementations the two random integer results are not equal.

## `mainTwoRandoms`

```scala
package examples.specification.programWithState.effectful

import psbp.specification.types.&&

import psbp.specification.programWithState.ProgramWithState

import examples.specification.programWithState.{ Seed, twoRandoms } 

def mainTwoRandoms[>-->[- _, + _]: [>-->[- _, + _]] =>> ProgramWithState[Seed, >-->]]: Unit >--> Unit =
  twoRandoms toMainWith (
    producer = unitProducer,
    consumer = twoRandomsConsumer
  )
```

where

```scala
package examples.specification.programWithState.effectful

import scala.language.postfixOps

import psbp.specification.program.Program

def unitProducer[>-->[- _, + _]: Program]: Unit >--> Unit = 
  { (_: Unit) =>
      ()
  } asProgram 
```

and where

```scala
package examples.specification.programWithState.effectful

import scala.language.postfixOps

import psbp.specification.types.&&

import psbp.specification.program.Program

def twoRandomsConsumer[>-->[- _, + _]: Program]: (Unit && (BigInt && BigInt)) >--> Unit =
  {
    (`u&&(i&&j)`: Unit && (BigInt && BigInt)) =>
      val `i&&j` = `u&&(i&&j)`._2
      println(s"generating two random integers yields result ${`i&&j`}")
  } asProgram
```

`mainRandom` is a main program that, makes use of a trivial producer and, for now, makes use of an effectful consumer.

## `StateTransformed`

```scala
package psbp.internalImplementation.computation.transformation

// StateTransformed

private[psbp] type StateTransformed[S, C[+ _]] = [Z] =>> S => C[(S, Z)]

// ...
```

A state transformed computation is a *computation state handler*, a computation handling computation state while being performed.

## `stateTransformedComputation`

```scala
package psbp.internalImplementation.computation.transformation

import psbp.specification.program.state.State

import psbp.internalSpecification.computation.Computation

import psbp.internalSpecification.computation.transformation.Transformation

import psbp.internalSpecification.naturalTransformation.~>

private[psbp] given stateTransformedComputation[
  S,
  C[+ _]: Computation]: Transformation[C, StateTransformed[S, C]] 
  with Computation[[Z] =>> StateTransformed[S, C][Z]] with

  private type F[+Z] = C[Z]
  private type T[+Z] = StateTransformed[S, C][Z]

  private type `=>T` = [Z, Y] =>> Z => T[Y]

  private val computationF: Computation[F] = summon[Computation[F]]
  import computationF.{ result => resultF, bind => bindF }

  override private[psbp] val `f~>t`: F ~> T = new {
    def apply[Z]: F[Z] => T[Z] =
      fz =>
        s =>
          bindF(fz, z => resultF((s, z)))
  }

  override private[psbp] def bind[Z, Y] (tz: T[Z], `z=>ty`: => Z => T[Y]): T[Y] =
    s =>
      bindF(tz(s), (s, z) => `z=>ty`(z)(s))    


private[psbp] given stateTransformedState[
  S,
  C[+ _]: Computation]: State[S, [Z, Y] =>> Z => StateTransformed[S, C][Y]] with 

  private type F[+Z] = C[Z]
  private type T[+Z] = StateTransformed[S, C][Z]

  private type `=>T` = [Z, Y] =>> Z => T[Y]

  private val computationF: Computation[F] = summon[Computation[F]]
  import computationF.{ result => resultF, bind => bindF }

  override private[psbp] def `u>-->s`: Unit `=>T` S =
    _ => 
      s =>
        resultF((s, s))

  override private[psbp] def `s>-->u`: S `=>T` Unit =
    s => 
      _ =>
        resultF((s, ()))
```

Any computation of type `C[Z]` can, using `stateTransformedComputation`, be transformed to a computation of type `S => C[(S, Z)]` that handles computation state .

The `` `f~>t` `` member trivially uses a computation as a computation that handles computation state, not doing any computation executing and not doing any computation state handling. 

The  `bind` member binds a computation that handles computation state not doing any state handling along the way. 

The  `` `u>-->s` `` member reads the state not doing any computation executing.

The  `` `s>-->u` `` member writes the state not doing any computation executing.

## `stateTransformedMaterialization`

```scala
package psbp.internalImplementation.materialization

import psbp.specification.program.state.Initial

import psbp.specification.materialization.Materialization

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.transformation.StateTransformed

private[psbp] given stateTransformedMaterialization[
  S: Initial,
  C[+ _]: Computation: 
  [C[+ _]] =>> Materialization[[Z, Y] =>> Z => C[Y], Z, Y], Z, Y]: 
  Materialization[[Z, Y] =>> Z => StateTransformed[S, C][Y], Z, C[Y]] with

  private type F[+Z] = C[Z]
  private type T[+Z] = StateTransformed[S, C][Z]

  private type `=>F`[-Z, +Y] = Z => F[Y]
  private type `=>T`[-Z, +Y] = Z => T[Y]

  private val Materialization: Materialization[`=>F`, Z, Y] = summon[Materialization[`=>F`, Z, Y]]
  import Materialization.{ materialize => materializeF }

  private val computation: Computation[C] = summon[Computation[F]]
  import computation.{ result => resultF, bind => bindF }  

  private val initial: Initial[S] = summon[Initial[S]]
  import initial.{ s => initialS }

  override val materialize: (Unit `=>T` Unit) => Z => C[Y] =
    `u=>tu` =>
      z =>
        bindF(`u=>tu`(())(initialS), (s, _) => resultF(materializeF(resultF)(z)))
```

Transforming materialization of `[Z, Y] =>> Z => C[Y], Z, Y`, to materialization, of `[Z, Y] =>> Z => (S => C[(S, Y)]), Z, C[Y]`, is done using `stateTransformedMaterialization` which makes use of `initialS`. 

## `stateActiveProgram` and `stateActiveState`

```scala
package psbp.implementation.stateActive

import psbp.specification.program.Program

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.programFromComputation

import psbp.internalImplementation.computation.transformation.stateTransformedComputation

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given stateActiveComputation[S]: Computation[StateActive[S]] = stateTransformedComputation[S, Active]

given stateActiveProgram[S]: Program[`=>SA`[S]] = programFromComputation[StateActive[S]]
```

and

```scala
package psbp.implementation.stateActive

import psbp.specification.program.state.State

import psbp.internalImplementation.computation.transformation.stateTransformedState

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given stateActiveState[S]: State[S, `=>SA`[S]] = stateTransformedState[S, Active]
```

where

```scala
package psbp.implementation.stateActive

import psbp.internalImplementation.computation.transformation.StateTransformed

import psbp.implementation.active.Active

type StateActive[S] = [Y] =>> StateTransformed[S, Active][Y] 

type `=>SA`[S] = [Z, Y] =>> Z => StateActive[S][Y]
```

Transforming from active programming with `` `=>A` `` to active programming with state with `` `=>SA[S]` `` is done by using a combination of `stateTransformedComputation`, `stateTransformedState` and `programFromComputation`.

## `stateActiveMaterialization`

```scala
package psbp.implementation.stateActive

import psbp.specification.types.&&

import psbp.specification.program.state.Initial

import psbp.specification.materialization.Materialization

import psbp.internalImplementation.materialization.stateTransformedMaterialization

import psbp.implementation.active.Active

import psbp.implementation.active.{ activeComputation, activeMaterialization }

given stateActiveMaterialization[S: Initial]: Materialization[`=>SA`[S], Unit, Unit] =
  stateTransformedMaterialization[S, Active, Unit, Unit]
```

Transforming from active materialization of `` `=>A` `` to active materialization with state of `` `=>SA`[S] `` is done by using `stateTransformedMaterialization`.

## Running `stateActive` `twoRandoms` (effectful consumer)

```scala
package examples.implementation.active.programWithState.effectful

import psbp.specification.program.state.Initial

import psbp.internalImplementation.programWithState.given

import psbp.implementation.stateActive.given

import examples.specification.programWithState.Seed

import examples.specification.programWithState.effectful.mainTwoRandoms

given initialSeedState: Initial[Seed] = 
  new { 
    override val s = 1L 
  }

@main def twoRandoms(args: String*): Unit =
  mainTwoRandoms materialized ()
```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.active.programWithState.effectful.twoRandoms 
generating two random integers yields result (384748,1151252339)
[success] ...
```

Again, the main difference with the active, reactive and free versions is the usage of a different injection by `import`.

Also a `given` implementation of `Initial` for `Seed` needs to be provided.

When running the materialized main program implementation, the implementation of program `twoRandoms`, somehow, transforms its argument, in this case no argument, to a result, in this case a product result.

This product result consists of two *different* random integers, `384748` and `1151252339`. 

They are different because, while transforming, internal side effects are happening along the way.

More precisely, the `Seed` computation state modifies.

The important takeway is that programming with state can be achieved *without using any* `var`*'s*.

Instead, state manifests itself, internally, in the function type `Z => (S => [(S, Y)])` of program implementations.

# Programming with parallelism

## `Parallel`

```scala
package psbp.specification.program.parallel

import psbp.specification.types.&&

trait Parallel[>-->[- _, + _]]:
  
  // declared

  private[psbp] def parallel[Z, Y, X, W] (`z>-->x`: Z >--> X, `y>-->w`: Y >--> W): (Z && Y) >--> (X && W)

  private[psbp] def asynchronous[Z, Y](`z>-->y`: Z >--> Y): Z >--> Y 
  
  // defined extensions

  extension [Z, Y, X, W] (`z>-->x`: Z >--> X) def |&&&|(`y>-->w`: Y >--> W): (Z && Y) >--> (X && W) =
    parallel(`z>-->x`, `y>-->w`)

  extension [Z, Y] (`z>-->y`: Z >--> Y) def async: Z >--> Y  =
    asynchronous(`z>-->y`)
```

`Parallel` specifies that programs can be *run in parallel* and can *run asynchronously*.

The public member `|&&&|` is an extension that can be used as infix operator.

The public member `async` is an extension that can be used as postfix operator.

## `ProgramWithParallel`

```scala
package psbp.specification.programWithParallel

import psbp.specification.types.&&

import psbp.specification.program.Program

import psbp.specification.program.parallel.Parallel

import psbp.specification.functional.{ `u>-->u`, `z>-->(z&&u)`, `(y&&u)>-->y`, `z>-->(z&&z)` }

trait ProgramWithParallel[>-->[- _, + _]] extends Program[>-->] with Parallel[>-->]:

  private implicit val program: Program[>-->] = this

  // defined
  
  override private[psbp] def asynchronous[Z, Y](`z>-->y`: Z >--> Y): Z >--> Y =
    `z>-->(z&&u)` >--> (`z>-->y` |&&&| `u>-->u`[>-->]) >--> `(y&&u)>-->y`

  // defined extensions

  extension [Z, Y, X] (`z>-->y`: Z >--> Y) def |&&|(`z>-->x`: Z >--> X): Z >--> (Y && X) =
    `z>-->(z&&z)` >--> (`z>-->y` |&&&| `z>-->x`)
```

where

```scala
package psbp.specification.program

import scala.language.postfixOps

// functional

// ...  

def `u>-->u`[>-->[- _, + _]: Functional]: Unit >--> Unit =
  `u=>u` asProgram

// construction

// ...
    
def `z>-->(z&&u)`[>-->[- _, + _]: Functional, Z]: Z >--> (Z && Unit) =
  `z=>(z&&u)` asProgram

def `(y&&u)>-->y`[>-->[- _, + _]: Functional, Y]: (Y && Unit) >--> Y =
  `(y&&u)=>y` asProgram 

// ...
```

are program utilities

where

```scala
package psbp.specification.function

// functional

// ...
    
def `u=>u`: Unit => Unit = 
  `z=>z`[Unit]     

// construction

// ...  

def `z=>(z&&u)`[Z]: Z => (Z && Unit) =
  z =>
    (z, ()) 

def `(y&&u)=>y`[Y]: (Y && Unit) => Y =
  (y, _) => 
    y       

// ...
```

are function utilities.

`asynchronous` can, by default, be defined, albeit in a somewhat sub-optimal way, in terms of `parallel`. 

The public member `|&&|` is an extension that can be used as infix operator.

## `programWithParallel`

```scala
package psbp.internalImplementation.programWithParallel

import psbp.specification.program.Program

import psbp.specification.program.parallel.Parallel

import psbp.specification.programWithParallel.ProgramWithParallel

given programWithParallel[>-->[- _, + _]: Program: Parallel]: ProgramWithParallel[>-->] with
 
  private val program: Program[>-->] = summon[Program[>-->]]

  private val parallel: Parallel[>-->] = summon[Parallel[>-->]]

  export program.toProgram
  export program.andThen
  export program.construct
  export program.conditionally

  export parallel.parallel
```

Using injection by `import` of `programWithParallel`, a generic `given` implementation of `ProgramWithParallel`, only given implementations of `Program` and `Parallel` need to be injected by `import`.

## `parallelFibonacci`

```scala
package examples.specification.programWithParallel

import psbp.specification.programWithParallel.ProgramWithParallel

import examples.specification.functional.{ isZero, zero, isOne, one, subtractOne, subtractTwo, add }

def parallelFibonacci[>-->[- _, + _]: ProgramWithParallel]: BigInt >--> BigInt =

  val programWithParallel: ProgramWithParallel[>-->] = summon[ProgramWithParallel[>-->]]
  import programWithParallel.If

  If(isZero) {
    zero
  } Else {
    If(isOne) {
      one
    } Else {
      (subtractOne && subtractTwo) >-->
        (parallelFibonacci |&&&| parallelFibonacci) >-->
        add
    }
  }
```

The definition of `parallelFibonacci` only differs from the definion of `fibonacci` by its usage of `|&&&|` instead of `&&&`.

## `reactiveTransformedParallel`

```scala
package psbp.internalImplementation.computation.transformation

import akka.actor.typed.{ ActorSystem, ActorRef, Behavior }

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }

import Behaviors.{ receive, stopped }

import ch.qos.logback.classic.{ Logger, LoggerContext, Level }

import Level.{ INFO, ERROR}

import org.slf4j.LoggerFactory.getILoggerFactory

import language.implicitConversions

import psbp.specification.types.&&

import psbp.specification.program.parallel.Parallel

import psbp.internalSpecification.computation.Computation

import psbp.internalImplementation.computation.transformation.ReactiveTransformed

val packageName = "psbp.internalImplementation.computation.transformation"

def log[Z](actorContext: ActorContext[Z])(message: String): Unit = {
  val logger: Logger = getILoggerFactory().asInstanceOf[LoggerContext].getLogger(packageName);
  logger.setLevel(INFO);
  actorContext.log.info(message)
  logger.setLevel(ERROR);  
}

private[psbp] given reactiveTransformedParallel[
  C[+ _]: Computation]: Parallel[[Z, Y] =>> Z => ReactiveTransformed[C][Y]] with

  private type F[+Z] = C[Z]
  private type T[+Z] = ReactiveTransformed[C][Z]
  
  private val computation: Computation[F] = summon[Computation[F]]
  import computation.{ result => resultF, bind => bindF }
  
  override private[psbp] def parallel[Z, Y, X, W](`z=>tx`: Z => T[X], `y=>tw`: Y => T[W]): (Z && Y) => T[X && W] =
    (z, y) => 
      `f(x,w)=>u` =>
        val `(x,w)=>f(x,w)` = resultF[(X && W)]
        val `(x,w)=>u` = `(x,w)=>f(x,w)` andThen `f(x,w)=>u`
  
        lazy val reactor = ActorSystem(Reactor(), s"reactor")
        lazy val leftActor = ActorSystem(LeftActor(reactor), s"leftActor")
        lazy val rightActor = ActorSystem(RightActor(reactor), s"rightActor")
  
        import Reactor.React
        import React.{ LeftReact, RightReact }
  
        import LeftActor.LeftAct
        import RightActor.RightAct
  
        object LeftActor:
  
          case object LeftAct
  
          def apply(reactor: ActorRef[React[X, W]]): Behavior[LeftAct.type] =
            receive { (context, message) =>
              log(context)(s"leftActor received LeftAct")
              val tx: T[X] = `z=>tx`(z)
              tx { 
                fx =>
                  bindF (fx, { 
                    x =>
                      resultF(reactor ! LeftReact(x))
                  })
              }
              stopped
            }
          
        object RightActor:
  
          case object RightAct
  
          def apply(reactor: ActorRef[React[X, W]]): Behavior[RightAct.type] =
            receive { (context, message) =>
              log(context)(s"rightActor received RightAct")
              val tw: T[W] = `y=>tw`(y)
              tw { 
                fw =>
                  bindF( fw, { 
                    w =>
                      resultF(reactor ! RightReact(w))
                  })
              }
              stopped
            }
  
        object Reactor:
  
          enum React[X, W]:
            case LeftReact(x: X) extends React[X, W]
            case RightReact(w: W) extends React[X, W]
  
          def react(`option[x]`: Option[X], `option[w]`: Option[W]): Behavior[React[X, W]] =
            receive { (context, message) =>
              message match {
                case LeftReact(x) => 
                  `option[w]` match {
                    case Some(w) =>
                      log(context)(s"reactor received both LeftReact($x) and RightReact($w)")
                      `(x,w)=>u`(x,w)
                      stopped
                    case None => 
                      react(Some(x), None)
                  }
                case RightReact(w) => 
                  `option[x]` match {
                    case Some(x) => 
                      log(context)(s"reactor received both RightReact($w) and LeftReact($x)")
                      `(x,w)=>u`(x,w)
                      stopped
                    case None => 
                      react(None, Some(w))
                  }              
              }
            }  
  
          def apply(): Behavior[React[X, W]] =
            react(None, None)      
  
        leftActor ! LeftAct
        rightActor ! RightAct
  
  override private[psbp] def asynchronous[Z, Y](`z=>ty`: Z => T[Y]): Z => T[Y] =
    z => 
      `fy=>u` =>
        val `y=>fy` = resultF[Y]
        val `y=>u` = `y=>fy` andThen `fy=>u`
    
        lazy val reactor = ActorSystem(Reactor(), s"reactor")
        lazy val actor = ActorSystem(Actor(reactor), s"actor")        
         
        import Reactor.React
        import Actor.Act
     
        object Actor:
    
          case object Act
    
          def apply(reactor: ActorRef[React[Y]]): Behavior[Act.type] =
            receive { (context, message) =>
              val ty: T[Y] = `z=>ty`(z)
              ty { 
                fy =>
                  bindF(fy, { 
                    y =>
                      resultF(reactor ! React(y))
                  })
              }
              stopped
            }
            
        object Reactor:
    
          case class React[Y](y: Y)
    
          def react(`option[y]`: Option[Y]): Behavior[React[Y]] =
            receive { (context, message) =>
              message match {
                case React(y) => 
                  log(context)(s"reactor received React($y)")
                  `y=>u`(y)
                  stopped
              }            
            }
    
          def apply(): Behavior[React[Y]] =
            react(None)
        
        actor ! Act        
    
```

Implementing parallelism can easily be done using `actor`'s.

Some logging has been added in to illustrate how `leftActor` and `rightActor` act, resp. `reactor` reacts.

A `leftActor` and `rightActor` act together in parallel sending their results to a `reactor` to react to.

## `reactiveParallel`

```scala
package psbp.implementation.reactive

import psbp.specification.program.parallel.Parallel

import psbp.internalImplementation.computation.transformation.reactiveTransformedParallel

import psbp.implementation.active.Active

import psbp.implementation.active.activeComputation

given reactiveParallel: Parallel[`=>R`] = reactiveTransformedParallel[Active]
```

Transforming from active programming with `` `=>A` `` to reactive programming with parallelism with `` `=>R` `` is done by using `reactiveTransformedParallel`.

## `mainParallelFibinacci`

```scala
package examples.specification.programWithParallel.effectful

import psbp.specification.types.&&

import psbp.specification.programWithParallel.ProgramWithParallel

import examples.specification.programWithParallel.parallelFibonacci 

import examples.specification.program.effectful.intProducer

def mainParallelFibonacci[>-->[- _, + _]: [>-->[- _, + _]] =>> ProgramWithParallel[>-->]]: Unit >--> Unit =
  parallelFibonacci toMainWith (
    producer = intProducer,
    consumer = parallelFibonacciConsumer
  )
```

where

```scala
package examples.specification.programWithParallel.effectful

import scala.language.postfixOps

import psbp.specification.types.&&

import psbp.specification.program.Program 

import akka.actor.typed.{ ActorSystem, Behavior }

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
  
import Behaviors.{ receive, stopped }
  
import ch.qos.logback.classic.{Level, Logger, LoggerContext}
import Level.{ INFO, ERROR}
  
import org.slf4j.LoggerFactory
  
val loggerContext:  LoggerContext = LoggerFactory.getILoggerFactory().asInstanceOf[LoggerContext]
val logger: Logger = loggerContext.getLogger("examples.specification.program.effectful")
  
def log[Z](actorContext: ActorContext[Z])(message: String): Unit = {
  logger.setLevel(INFO);
  actorContext.log.info(message)
  logger.setLevel(ERROR);  
}
  
def actorParallelFibonacciConsumer[>-->[- _, + _]: Program]: (BigInt && BigInt) >--> Unit =
    
  object Consumer {

    case class Consume(i: BigInt, j: BigInt)

    def apply(): Behavior[Consume] = receive { (context, message) =>
      message match {
        case Consume(i, j) =>
          log(context)(s"applying parallel fibonacci to argument $i yields result $j")
          stopped
      }
    }

  }

  val consumer = ActorSystem(Consumer(), "consumer")  
   
  import Consumer.Consume

  {
    (`i&&j`: BigInt && BigInt) =>
      val i = `i&&j`._1
      val j = `i&&j`._2
      consumer ! Consume(i, j)
  } asProgram 

// ...  
```

## Running `reactive` `parallelFibonacci` (effectful producer and consumer)

```scala
package examples.implementation.reactive.programWithParallel.effectful

import psbp.implementation.reactive.given

import psbp.internalImplementation.programWithParallel.given

import examples.specification.programWithParallel.effectful.mainParallelFibonacci

@main def fibonacci(args: String*): Unit =
  mainParallelFibonacci materialized ()

```

Let's run it

```scala
sbt:PSBP> run
...
[info] running examples.implementation.reactive.programWithParallel.effectful.fibonacci 
Please type an integer
5
[2021-03-03 11:36:00,472] - leftActor received LeftAct 
[2021-03-03 11:36:00,497] - rightActor received RightAct 
[2021-03-03 11:36:00,530] - leftActor received LeftAct 
[2021-03-03 11:36:00,571] - leftActor received LeftAct 
[2021-03-03 11:36:00,576] - rightActor received RightAct 
[2021-03-03 11:36:00,651] - rightActor received RightAct 
[2021-03-03 11:36:00,699] - leftActor received LeftAct 
[2021-03-03 11:36:00,735] - leftActor received LeftAct 
[2021-03-03 11:36:00,795] - rightActor received RightAct 
[2021-03-03 11:36:00,796] - rightActor received RightAct 
[2021-03-03 11:36:00,797] - leftActor received LeftAct 
[2021-03-03 11:36:00,797] - reactor received both RightReact(0) and LeftReact(1) 
[2021-03-03 11:36:00,911] - rightActor received RightAct 
[2021-03-03 11:36:00,911] - reactor received both RightReact(0) and LeftReact(1) 
[2021-03-03 11:36:00,912] - reactor received both LeftReact(1) and RightReact(1) 
[2021-03-03 11:36:00,933] - leftActor received LeftAct 
[2021-03-03 11:36:00,998] - rightActor received RightAct 
[2021-03-03 11:36:00,999] - reactor received both RightReact(0) and LeftReact(1) 
[2021-03-03 11:36:01,002] - reactor received both LeftReact(1) and RightReact(1) 
[2021-03-03 11:36:01,002] - reactor received both LeftReact(2) and RightReact(1) 
[2021-03-03 11:36:01,002] - reactor received both LeftReact(3) and RightReact(2) 
[2021-03-03 11:36:01,018] - applying parallel fibonacci to argument 5 yields result 5 
[success] ...
```

Again, the main difference with the active, reactive, free and state versions is the usage of a different injection by `import`.


