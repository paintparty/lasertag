# Typetag

A Clojure(Script) utility for categorizing types of values.

For a quick summary of how this differs from `clojure.core/type`, view [the tables below](#examples).

This lib fell out of work on other Clojure(Script) dev tooling, so perhaps it may be useful in a similar context.

<br>

## Usage
Requires Clojure `1.9.0` or higher

If using with Babashka, requires Babashka `v1.3.187` or higher

<br>

Add as a dependency to your project:

```clojure
[io.github.paintparty/typetag "0.3.0"]
```
<br>

Import into your namespace:

```clojure
(ns myns.core
  (:require
    [typetag.core :refer [tag tag-map]]))
```
<br>

The function `typetag.core/tag` will return a typetag:

```clojure
(tag 1)         ;; => :int
(tag 1.5)       ;; => :float
(tag "hi")      ;; => :string
(tag :hi)       ;; => :keyword
(tag "#^hi$")   ;; => :regex
(tag [1 2 3])   ;; => :vector
(tag '(1 2 3))  ;; => :list
(tag (range 3)) ;; => :seq
```
<br>

The typetag is a keyword, by default. You can pass an options map if you want a string or symbol:
```clojure
(tag 1 {:format :string}) ;; => "int"
(tag 1 {:format :symbol}) ;; => int
```
<br>

The function `typetag.core/tag-map` will return a map with additional info.

```clojure
(tag-map "hi")
=>
{:tag          :string
 :all-tags     #{:string :js/Iterable}
 :type         #object[String]
 :coll-type?   false
 :map-like?    false
 :number-type? false}

(defn xy [x y] (+ x y))

(tag-map xy)
=>
{:tag          :function
 :all-tags     #{:function}
 :type         #object[Function]
 :fn-name      "xy" 
 :fn-ns        "myns.core"
 :fn-args      [x y]
 :coll-type?   false
 :map-like?    false
 :number-type? false}

(tag-map js/ParseFloat)
=>
{:tag                   :function
 :all-tags              #{:function}
 :type                  #object[Function]
 :fn-name               "parseFloat"
 :fn-args               [s]
 :js-built-in-method-of Number
 :js-built-in-function? true
 :coll-type?            false
 :map-like?             false
 :number-type?          false}

;; NOTE: :fn-args entry is only available in cljs
;; NOTE: :fn-name will not work as expected in cljs advanced compilation
```
<br>

With `tag-map`, There are 3 additional params you can pass with the optional second argument (options map). Setting these to `false` will exclude certain information. Depending on how you are using `tag-map`, this could also help with performance.

```clojure
:include-all-tags?              
:include-function-info?          
:include-js-built-in-object-info?
```
<br>

The following example excludes the `:all-tags` entry, as well as the related `:coll-type?`, `:map-like?`, `:number-type?` and `:coll-size?` entries:

```clojure

(tag-map xy) 
=>
{:tag          :function
 :all-tags     #{:function}
 :type         #object[Function]
 :fn-name      "xy" 
 :fn-ns        "myns.core"
 :fn-args      [x y]
 :coll-type?   false
 :map-like?    false
 :number-type? false}


(tag-map xy {:include-all-tags? false}) 
=>
{:tag          :function
 :type         #object[Function]
 :fn-name      "xy" 
 :fn-ns        "myns.core"
 :fn-args      [x y]}
```
<br>

Excluding the function-info related entries:

```clojure
(tag-map xy)
=>
{:tag          :function
 :all-tags     #{:function}
 :type         #object[Function]
 :fn-args      [x y]
 :fn-name      "xy"
 :fn-ns        "myns.core"
 :coll-type?   false
 :map-like?    false
 :number-type? false}


(tag-map xy {:include-function-info? false})
=>
{:tag          :function
 :all-tags     #{:function}
 :type         #object[Function]
 :coll-type?   false
 :map-like?    false
 :number-type? false}
```
<br>

Excluding the JS built-in-object related entries:

```clojure
(tag-map js/JSON)
=>
{:tag                     :js/Object
 :all-tags                #{:js/Object}
 :type                    #object[Object]
 :js-built-in-object?     true
 :js-built-in-object-name "JSON"
 :coll-type?              true
 :map-like?               true
 :number-type?            false}

(tag-map js/JSON {:include-js-built-in-object-info? false})
=>
{:tag                     :js/Object
 :all-tags                #{:js/Object}
 :type                    #object[Object]
 :coll-type?              true
 :map-like?               true
 :number-type?            false}
```
<br>
<br>

## Examples 

### Clojure
Below is a table of example values in a JVM Clojure context, and the results of passing each value to `typetag.core/tag`, and `clojure.core/type`.

| Input value                     | `typetag.core/tag`       | `clojure.core/type`               |
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
| `(:a :b :c)`                    | `:list`                 | `clojure.lang.PersistentList`     |
|                      `Infinity` |             `:Infinity` |      `java.lang.Double`           |
|                     `-Infinity` |            `:-Infinity` |      `java.lang.Double`           |
|                           `NaN` |                  `:NaN` |      `java.lang.Double`           |
|                           `1/3` |                `:ratio` |    `clojure.lang.Ratio`           |
|                      `(byte 0)` |                  `:int` |        `java.lang.Byte`           |
|                     `(short 3)` |                  `:int` |       `java.lang.Short`           |
|                `(double 23.44)` |               `:double` |      `java.lang.Double`           |
|                            `1M` |              `:decimal` |  `java.math.BigDecimal`           |
|                             `1` |                  `:int` |        `java.lang.Long`           |
|                   `(float 1.5)` |                `:float` |       `java.lang.Float`           |
|                      `(char a)` |                 `:char` |   `java.lang.Character`           |
| `(java.math.BigInteger. "171")` | `:java.math.BigInteger` |  `java.math.BigInteger`           |
|             `(java.util.Date.)` |       `:java.util.Date` |        `java.util.Date`           |
|                `java.util.Date` |      `:java.lang.Class` |       `java.lang.Class`           |


<br>

### ClojureScript

Below is a table of example values in a ClojureScript context, and the results of passing each value to `typetag.core/tag`, and `cljs.core/type`.

| Input value                        | `typetag.core/tt` | `cljs.core/type`               |
| :---                               | :---              | :---                           |
| `"hi"`                             | `:string`         | `#object[String]`              |
| `:hi`                              | `:keyword`        | `cljs.core/Keyword`            |
| `"^hi$"`                           | `:regex`          | `#object[RegExp]`              |
| `true`                             | `:boolean`        | `#object[Boolean]`             |
| `mysym`                            | `:symbol`         | `cljs.core/Symbol`             |
| `nil`                              | `:nil`            | `nil`                          |
| `[1 2 3]`                          | `:vector`         | `cljs.core/PersistentVector`   |
| `#{1 3 2}`                         | `:set`            | `cljs.core/PersistentHashSet`  |
| `{:a 2, :b 3}`                     | `:map`            | `cljs.core/PersistentArrayMap` |
| `(map inc (range 3))`              | `:seq`            | `cljs.core/LazySeq`            |
| `(range 3)`                        | `:seq`            | `cljs.core/IntegerRange`       |
| `(:a :b :c)`                       | `:list`           | `cljs.core/List`               |
| `Infinity`                         | `:Infinity`       | `#object[Boolean]`             |
| `-Infinity`                        | `:-Infinity`      | `#object[Boolean]`             |
| `js/parseInt`                      | `:function`       | `#object[Function]`            |
| `(new js/Date.)`                   | `:js/Date`        | `#object[Date]`                |
| `(.values #js [1 2 3])`            | `:js/Iterator`    | `#object[Object]`              |
| `(array "a" "b")`                  | `:js/Array`       | `#object[Array]`               |
| `(new js/Int8Array #js ["a" "b"])` | `:js/Int8Array`   | `#object[Int8Array]`           |
| `(new js/Set #js[1 2 3])`          | `:js/Set`         | `#object[Set]`                 |

<br>

### Additional ClojureScript Examples

```clojure
;; A Record type

(defrecord MyRecordType [a b c d])

(def my-record-type (->MyRecordType 4 8 4 5))

(tag my-record-type) 
=> :myns.core/MyRecordType



;; Multimethod definition

(defmulti different-behavior (fn [x] (:x-type x)))

(tag different-behavior)
=> :defmulti



;; Javascript Promise

(def my-promise (js/Promise. (fn [x] x)))

(tag my-promise) 
=> :js/Promise
```

### Instance methods on JavaScript built-ins

`typetag.core/tag-map` can help to differentiate between an instance method on a JS built-in that might have the same name as another instance method on a different JS built-in.

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

If you need enhanced reflection in situations like this, the result of `typetag.core/tag-map` offers the following 2 entries:
 <br>`:js-built-in-method-of`<br> `:js-built-in-method-of-name`

```Clojure
(tag-map (aget "hi" "concat"))
=>
{:js-built-in-method-of      #object[String]
 :js-built-in-method-of-name "String"
 :js-built-in-function?      true
 :coll-type?                 false
 :map-like?                  false
 :fn-name                    "concat"
 :type                       #object[Function]
 :all-tags                   #{:function}
 :fn-args                    []
 :tag                        :function
 :number-type?               false}


(tag-map (aget #js [1 2 3] "concat"))
=>
{:js-built-in-method-of      #object[Array]
 :js-built-in-method-of-name "Array"
 :js-built-in-function?      true
 :coll-type?                 false
 :map-like?                  false
 :fn-name                    "concat"
 :type                       #object[Function]
 :all-tags                   #{:function}
 :fn-args                    []
 :tag                        :function
 :number-type?               false}
```


<br>

## Test
 The JVM tests require [leiningen](https://leiningen.org/) to be installed.

```Clojure
lein test
```

The ClojureScript tests:

```Clojure
npm run test
```


<br>

## Status
Alpha, subject to change. Currently, the enhanced interop reflection is focused more on the ClojureScript side. It would be nice to add more support for categorizing Java types. Issues welcome, see [contributing](#contributing).

<br>

## Contributing
Issues for bugs, improvements, or features are very welcome. Please file an issue for discussion before starting or issuing a PR.


<br>

## License

Copyright © 2024 Jeremiah Coyle

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
