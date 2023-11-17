# typetag

A Clojure(Script) utility for discerning, and working programmatically, with types of values.

This lib fell out of work on other Clojure(Script) dev tooling, so perhaps useful in a similar context.


## Usage

Add as a dependency to your project:

```clojure
[org.clojars.paintparty/typetag "0.1.0"]
```

Import into your namespace:

```clojure
(ns myns.core
  (:require
    [typetag.core :refer [tag tag-map]]))
```

The function `typetag.core/tag` will return a typetag.
Clojure(Script) examples:

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
(tag-map "hi") ;; =>
{:typetag      :string
 :all-typetags [:string :js/Iterable]
 :type         #object[String]}


(defn xy [x y] (+ x y))

(tag-map xy) ;; =>
{:typetag      :function
 :all-typetags [:function]
 :type         #object[Function]
 :fn-name      "xy" 
 :fn-ns        "printer.reflect"
 :fn-args      [x y]}

(tag-map js/ParseFloat) ;; =>
{:typetag :function
 :all-typetags [:function]
 :type #object[Function]
 :fn-name "parseFloat"
 :fn-args [s], 
 :js-built-in-method-of Number
 :js-built-in-function? true}

;; NOTE: :fn-args entry is only available in cljs
;; NOTE: :fn-name will not work as expected in cljs advanced compilation
```
<br>

With `tt-map`, There are 3 additional params you can pass with the optional second argument (options map). Setting these to false will exclude certain information, which, depending on how you are using the utility, could help with performance.

```clojure
:include-all-typetags?              
:include-function-info?          
:include-js-built-in-object-info?
```
<br>

Exclude the `:all-typetags` entry:

```clojure
(tt-map xy {:all-typetags? false}) ;; =>
{:typetag      :function
 :type         #object[Function]
 :fn-name      "xy" 
 :fn-ns        "printer.reflect"
 :fn-args      [x y]}
```
<br>

Exclude the function-info related entries:

```clojure
(tt-map xy {:function-info? false}) ;; =>
{:typetag      :function
 :all-typetags [:function]
 :type         #object[Function]}
```
<br>

Exclude the JS built-in-object related entries:

```clojure
(tt-map js/JSON) ;; =>
{:typetag                 :js/Object
 :all-typetags            [:js/Object]
 :type                    #object[Object]
 :js-built-in-object?     true
 :js-built-in-object-name "JSON"}

(tt-map js/JSON {:js-built-in-object-info? false}) ;; =>
{:typetag                 :js/Object
 :all-typetags            [:js/Object]
 :type                    #object[Object]}
```
<br>
<br>

## Examples 

### Clojure
Below is a table of example values in a Clojure context, and the results of passing the value to `typetag.core/tag`, and `clojure.core/type`.

| `Value`                         | `typetag.core/tag`       | `clojure.core/type`               |
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
|                      `(byte 0)` |                 `:byte` |        `java.lang.Byte`           |
|                     `(short 3)` |                `:short` |       `java.lang.Short`           |
|                `(double 23.44)` |               `:number` |      `java.lang.Double`           |
|                            `1M` |              `:decimal` |  `java.math.BigDecimal`           |
|                             `1` |                 `:long` |        `java.lang.Long`           |
|                   `(float 1.5)` |                `:float` |       `java.lang.Float`           |
|                      `(char a)` |                 `:char` |   `java.lang.Character`           |
| `(java.math.BigInteger. "171")` | `:java.math.BigInteger` |  `java.math.BigInteger`           |
|             `(java.util.Date.)` |       `:java.util.Date` |        `java.util.Date`           |
|                `java.util.Date` |      `:java.lang.Class` |       `java.lang.Class`           |


<br>

### ClojureScript

Below is a table of example values in a ClojureScript context, and the results of passing the value to `typetag.core/tag`, and `clojure.core/type`.

| `Value`                            | `typetag.core/tt` | `cljs.core/type`               |
| :---                               | :---              | :---                           |
| `"hi"`                               | `:string`         | `#object[String]`              |
| `:hi`                              | `:keyword`        | `cljs.core/Keyword`            |
| `"^hi$"`                             | `:regex`          | `#object[RegExp]`              |
| `true`                             | `:boolean`        | `#object[Boolean]`             |
| `mysym`                            | `:symbol`         | `cljs.core/Symbol`             |
| `nil`                              | `:nil`            | `nil`                          |
| `[1 2 3]`                          | `:vector`         | `cljs.core/PersistentVector`   |
| `#{1 3 2}`                         | `:set`            | `cljs.core/PersistentHashSet`  |
| `{:a 2, :b 3}`                     | `:map`            | `cljs.core/PersistentArrayMap` |
| `(map inc (range 3))`              | `:seq`            | `cljs.core/LazySeq`            |
| `(range 3)`                        | `:seq`            | `cljs.core/IntergerRange`      |
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
(defrecord MyRecordType [a b c d])

(def my-record-type (->MyRecordType 4 8 4 5))

(tag my-record-type) ;; => :printer.reflect/MyRecordType



(defmulti different-behavior (fn [x] (:x-type x)))

(tag different-behavior) ;; => :defmulti



(def mypromise (js/Promise. (fn [x] x)))

(tag my-promise) ;; => :js/Promise



;; The `:js-built-in-method-of` entry can help to differentiate between instance-methods on JS built-ins that might have the same name as another instance methods on a different JS built-in.
(tag-map (aget "hi" "concat"))
;; =>
;; {...
;;  :js-built-in-method-of String
;;  ...}

(tag-map (aget #js [1 2 3] "concat"))
;; =>
;; {...
;;  :js-built-in-method-of Array
;;  ...}
```


<br>

## License

Copyright © 2023 Jeremiah Coyle

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
