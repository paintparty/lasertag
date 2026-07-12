<img  src="./resources/logo3.png" width="59"></img>


# Lasertag

**A library for categorizing values in Clojure dialects\***  
<sub>\*Works with Clojure, ClojureScript, and Babashka</sub>


<br>
<br>


**[Examples]**  &nbsp;•&nbsp; **[Setup]**  &nbsp;•&nbsp;  **[Usage]**  &nbsp;•&nbsp; **[Performance]**  &nbsp;•&nbsp; **[Testing]** &nbsp;•&nbsp; **[Development]**

[Examples]:     ##examples
[Setup]:        #setup
[Usage]:        #usage
[Performance]:  #performance
[Testing]:      #testing
[Development]:  #development

<br>

<img src="https://img.shields.io/clojars/v/io.github.paintparty/lasertag.svg?labelColor=000&color=0969da&style=flat&cacheSeconds=3" alt="Lasertag on Clojars"></img>

<br>

## Why 
Lasertag took shape while developing the syntax-colorizing hifi print engine used by [Bling](https://github.com/paintparty/bling) and [Fireworks](https://github.com/paintparty/fireworks). This kind of "loose type system" is essential for any library or subsystem that does linting, syntax coloring, or similar.

<br>

## Examples 

The function `lasertag.core/tag` will return a descriptive tag:

```Clojure
(require '[lasertag.core :refer [tag tag-map]])

(tag "hi")      ; => :string
(tag :hi)       ; => :keyword
(tag "#^hi$")   ; => :regex
(tag true)      ; => :boolean
(tag [1 2 3])   ; => :vector
(tag '(1 2 3))  ; => :seq
(tag (range 3)) ; => :seq
(tag 1)         ; => :number
(tag 1.5)       ; => :number
(tag ##Inf)     ; => :number
```

<br>

`lasertag.core/tag-map` returns a map with more info:

```Clojure
(tag-map (range 3))
=>
{:tag       :seq
 :type      clojure.lang.LongRange
 :all-tags  #{:seqable
              :sequential
              :coll
              :deferred
              :coll-like
              :lazy
              :seq
              :list-like
              :carries-meta
              :range}
 :classname "clojure.lang.LongRange"}
```

<br>

### Examples
`lasertag.core/tag` vs `clojure.core/type`

| Input value                     | `lasertag.core/tag`     | `clojure.core/type`               |
| :---                            | :---                    | :---                              |
| `"hi"`                          | `:string`               | `java.lang.String`                |
| `:hi`                           | `:keyword`              | `clojure.lang.Keyword`            |
| `"^hi$"`                        | `:regex`                | `java.util.regex.Pattern`         |
| `true`                          | `:boolean`              | `java.lang.Boolean`               |
| `mysym`                         | `:symbol`               | `clojure.lang.Symbol`             |
| `nil`                           | `:nil`                  | `nil`                             |
| `[1 2 3]`                       | `:vector`               | `clojure.lang.PersistentVector`   |
| `#{1 3 2}`                      | `:set`                  | `clojure.lang.PersistentHashSet`  |
| `{:a 2, :b 3}`                  | `:map`                  | `clojure.lang.PersistentArrayMap` |
| `(map inc (range 3))`           | `:seq`                  | `clojure.lang.LazySeq`            |
| `(range 3)`                     | `:seq`                  | `clojure.lang.LongRange`          |
| `(:a :b :c)`                    | `:seq`                  | `clojure.lang.PersistentList`     |
|                      `Infinity` |             `:number` |      `java.lang.Double`           |
|                     `-Infinity` |            `:number` |      `java.lang.Double`           |
|                           `NaN` |                  `:number` |      `java.lang.Double`           |
|                           `1/3` |                `:number` |    `clojure.lang.Ratio`           |
|                      `(byte 0)` |               `:number` |        `java.lang.Byte`           |
|                     `(short 3)` |               `:number` |       `java.lang.Short`           |
|                `(double 23.44)` |               `:number` |      `java.lang.Double`           |
|                            `1M` |               `:number` |  `java.math.BigDecimal`           |
|                             `1` |               `:number` |        `java.lang.Long`           |
|                   `(float 1.5)` |               `:number` |       `java.lang.Float`           |
|                      `(char a)` |                 `:char` |   `java.lang.Character`           |
| `(java.math.BigInteger. "171")` |               `:number` |  `java.math.BigInteger`           |
|             `(java.util.Date.)` |             `:datetime` |        `java.util.Date`           |
|                `java.util.Date` |                `:class` |       `java.lang.Class`           |


<br>

### ClojureScript Examples

`lasertag.core/tag` vs `cljs.core/type`

| Input value                        | `lasertag.core/tag`| `cljs.core/type`               |
| :---                               | :---               | :---                           |
| `"hi"`                             | `:string`          | `#object[String]`              |
| `:hi`                              | `:keyword`         | `cljs.core/Keyword`            |
| `"^hi$"`                           | `:regex`           | `#object[RegExp]`              |
| `true`                             | `:boolean`         | `#object[Boolean]`             |
| `mysym`                            | `:symbol`          | `cljs.core/Symbol`             |
| `nil`                              | `:nil`             | `nil`                          |
| `[1 2 3]`                          | `:vector`          | `cljs.core/PersistentVector`   |
| `#{1 3 2}`                         | `:set`             | `cljs.core/PersistentHashSet`  |
| `{:a 2, :b 3}`                     | `:map`             | `cljs.core/PersistentArrayMap` |
| `(map inc (range 3))`              | `:seq`             | `cljs.core/LazySeq`            |
| `(range 3)`                        | `:seq`             | `cljs.core/IntegerRange`       |
| `(:a :b :c)`                       | `:seq`             | `cljs.core/List`               |
| `Infinity`                         | `:number`          | `#object[Boolean]`             |
| `-Infinity`                        | `:number`          | `#object[Boolean]`             |
| `js/parseInt`                      | `:function`        | `#object[Function]`            |
| `(new js/Date.)`                   | `:datetime`            | `#object[Date]`                |
| `(.values #js [1 2 3])`            | `:iterable`        | `#object[Object]`              |
| `(array "a" "b")`                  | `:array`           | `#object[Array]`               |
| `(new js/Int8Array #js ["a" "b"])` | `:array`           | `#object[Int8Array]`           |
| `(new js/Set #js[1 2 3])`          | `:set`             | `#object[Set]`                 |
| `(js/Promise. (fn [x] x))`         | `:promise`         | `#object[Promise]`             |

<br>

<br>

## Setup
Requires Clojure `1.11.1` or higher

If using with Babashka, requires Babashka `v1.12.210` or higher

<br>

Add as a dependency to your project:

<br>

Deps:
```clojure
io.github.paintparty/lasertag {:mvn/version "0.13.2"}
```
<br>

Leiningen:
```clojure
[io.github.paintparty/lasertag "0.13.2"]
```
<br>

Require it:
```Clojure
(require '[lasertag.core :refer [tag tag-map]])
```
<br>

Or import into your namespace:
```Clojure
(ns myns.core
  (:require
    [lasertag.core :refer [tag tag-map]]))
```
<br>

### Consuming via git

Using a tool without Maven support? See
[Consuming Lasertag as a git dependency](docs/git-deps.md).

<br>
<br>

## Usage 
`lasertag.core/tag` returns a keyword describing the type of value:

```Clojure
(tag 1) ;; => :number
```
<br>

`lasertag.core/tag-map` will return a map with additional info.

```Clojure
;; string

(tag-map "hi")
=>
{:tag       :string
 :type      java.lang.String
 :all-tags  #{:string :scalar}
 :classname "java.lang.String"}



;; map 

(tag-map {:a :foo})
=>
{:tag       :map
 :type      clojure.lang.PersistentArrayMap
 :all-tags  #{:callable
              :seqable
              :editable
              :associative
              :coll
              :array-map
              :coll-like
              :map-like
              :map
              :carries-meta}
 :classname "clojure.lang.PersistentArrayMap"}



;; range 

(tag-map (range 3))
=>
{:tag       :seq
 :type      clojure.lang.LongRange
 :all-tags  #{:seqable
              :sequential
              :coll
              :deferred
              :coll-like
              :lazy
              :seq
              :list-like
              :carries-meta
              :range}
 :classname "clojure.lang.LongRange"}



;; Record

(defrecord MyRecordType [a b c d])
(tag-map (->MyRecordType 4 8 4 5)) 
=>
{:tag       :record
 :type      my.ns.MyRecordType
 :all-tags  #{:datatype
              :coll
              :coll-like
              :record
              :map-like
              :carries-meta}
 :classname "my.ns.MyRecordType"}



;; Delay

(def my-delay (delay 100))
(tag-map my-delay)
=>
{:tag       :delay
 :type      clojure.lang.Delay
 :all-tags  #{:deferred :delay :derefable}
 :classname "clojure.lang.Delay"}



;; Multimethod definition

(defmulti different-behavior (fn [x] (:x-type x)))
(tag-map different-behavior)
=>
{:tag       :function
 :type      clojure.lang.MultiFn
 :all-tags  #{:multi-function :function :callable}
 :classname "clojure.lang.MultiFn"}



;; Var

(def bar nil)
(tag-map #'bar)
=>
{:tag       :var
 :type      clojure.lang.Var
 :all-tags  #{:reference :var :callable :derefable}
 :classname "clojure.lang.Var"}



```

<br>

<!-- ### Instance methods on JavaScript built-ins

`lasertag.core/tag-map` can help to differentiate between an instance method on a JS built-in that might have the same name as another instance method on a different JS built-in.

Consider the following 2 values. Both are instance methods on JS built-ins. Both are named `concat`, although they are different functions that expect different inputs and yield different outputs:
```Clojure
(aget "hi" "concat")
(aget #js [1 2 3] "concat")
```

<br>

Calling `js/console.log` on either of the values above would result in the same (somewhat cryptic) result in a browser dev console, the exact format of which may vary depending on the browser:

```Clojure
ƒ concat() { [native code] }
```

<br>

Calling `clojure.pprint/pprint` or `clojure.core/println`
on either of the values above would give you this:

```Clojure
#object[concat]
```

<br>

If you need enhanced reflection in situations like this, the result of `lasertag.core/tag-map` offers the following 2 entries:
 <br>`:js-built-in-method-of`<br> `:js-built-in-method-of-name`

```Clojure
(tag-map (aget "hi" "concat"))
=>
{:js-built-in-method-of      #object[String]
 :js-built-in-method-of-name "String"
 :js-built-in-function?      true
 :fn-name                    "concat"
 :type                       #object[Function]
 :all-tags                   #{:function}
 :fn-args                    []
 :tag                        :function}


(tag-map (aget #js [1 2 3] "concat"))
=>
{:js-built-in-method-of      #object[Array]
 :js-built-in-method-of-name "Array"
 :js-built-in-function?      true
 :fn-name                    "concat"
 :type                       #object[Function]
 :all-tags                   #{:function}
 :fn-args                    []
 :tag                        :function}
``` -->

### Primary Tags
List of all primary tags:<br>
```clojure
[:agent
 :array
 :atom
 :boolean
 :char
 :datetime
 :delay
 :function
 :keyword
 :list
 :map
 :nil
 :number
 :queue
 :reader-conditional
 :ref
 :regex
 :seq
 :set
 :string
 :symbol
 :throwable
 :uuid
 :var
 :vector
 :volatile]
```

List of all secondary tags (these show up in the `:all-tags` entry):<br>
```clojure
[:array-like
 :associative
 :big-decimal
 :big-int
 :byte
 :callable
 :carries-meta
 :char-sequence
 :coll
 :coll-like
 :cons
 :def
 :deferred
 :double
 :editable
 :error
 :exception
 :float
 :fractional
 :generator
 :global-this
 :hash-map
 :infinite
 :infinity
 :-infinity
 :inst
 :int
 :iterable
 :js
 :lazy
 :list-like
 :long
 :map-entry
 :map-like
 :multi-function
 :named
 :nan
 :nat-int
 :neg
 :neg-int
 :object
 :pos
 :pos-int
 :promise
 :range
 :ratio
 :real
 :record
 :reference
 :scalar
 :seqable
 :sequential
 :set-like
 :short
 :sorted
 :stack
 :subvec
 :transient
 :typed-array
 :whole
 :zero]
```

## Performance
Based on criterium quick-bench testing, most values will return in 15 ~ 50 nanoseconds, depending on hardware. More exotic values whose types are not present in Lasertag's pre-generated result-by-class map will return in 10µs ~ 50µs, depending on hardware.  


<br>

## Testing
The JVM tests require [leiningen](https://leiningen.org/) to be installed.


```Clojure
lein test
```

ClojureScript tests:

```Clojure
npm run test
```

Babashka tests:

```Clojure
bb test:bb
```

## Developing
Most of the tests are auto-generated.

### CLJ, bb testing
To regenerate tests for JVM Clojure, look at source of `lasertag.cached`.

### CLJS testing
The cljs tests can also be regenerated.

Additionally, the data structure that serves most of the results for
`lasertag.core/tag` & `lasertag.core/tag-map` in cljs needs to be pre-generated.

For both of these (slightly clunky, this will be cleaned up in future):

1) Toggle `lasertag.cljs.codegen/write-tests?` and 
`lasertag.jsi.codegen/write-classes?` to `true`.

2) In one terminal: `npm run codegen`

3) In another terminal: `npm run codegen-watch`

4) The test and classes should immediately get generated and written to disc.
Kill the `npm run codegen-watch` process.

5) Toggle the defs from step 1 back to `false`


<br>

## Status
Alpha, subject to change. Issues welcome, see [contributing](#contributing).

<br>

## Contributing
Issues for bugs, improvements, or features are very welcome. Please file an issue for discussion before starting or issuing a PR.


<br>

## License

Copyright © 2024-2026 Jeremiah Coyle

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
