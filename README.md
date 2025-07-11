<div>
  <a href="https://clojars.org/io.github.paintparty/bling">
    <img src="https://img.shields.io/clojars/v/io.github.paintparty/lasertag.svg?color=0969da&style=flat-square&cacheSeconds=3" alt="Lasertag Clojars badge"></img>
  </a>
</div>

<br>
 
# Lasertag

Lasertag is a library for categorizing types of values in Clojure, ClojureScript, and Babashka. This library fell out of work on the colorizing pretty-printing engine that powers [Fireworks](https://github.com/paintparty/fireworks).

For a quick summary of the functionality, check out [this table](#examples).

<br>

## Usage
Requires Clojure `1.9.0` or higher

If using with Babashka, requires Babashka `v1.12.196` or higher

<br>

Add as a dependency to your project:

```clojure
[io.github.paintparty/lasertag "0.11.5"]
```
<br>

Require it:
```Clojure
(require '[lasertag.core :refer [tag tag-map]])
```


Or import into your namespace:
```Clojure
(ns myns.core
  (:require
    [lasertag.core :refer [tag tag-map]]))
```
<br>

The function `lasertag.core/tag` will return a descriptive tag:

```Clojure
(tag 1)         ;; => :number
(tag 1.5)       ;; => :number
(tag "hi")      ;; => :string
(tag :hi)       ;; => :keyword
(tag "#^hi$")   ;; => :regex
(tag [1 2 3])   ;; => :vector
(tag '(1 2 3))  ;; => :seq
(tag (range 3)) ;; => :seq
```
<br>

The tag is a keyword by default but you can pass an options map if you want a string or symbol:
```Clojure
(tag 1 {:format :string}) ;; => "number"
(tag 1 {:format :symbol}) ;; => number
```
<br>

The function `lasertag.core/tag-map` will return a map with additional info.

```Clojure
;; string
(tag-map "hi")
=>
{:tag       :string
 :type      java.lang.String
 :all-tags  #{:string}
 :classname "java.lang.String"}


;; map 
{:a :foo}
=>
{:tag       :map
 :type      clojure.lang.PersistentArrayMap
 :all-tags  #{:coll
              :array-map
              :coll-type
              :map-like
              :map
              :carries-meta}
 :classname "clojure.lang.PersistentArrayMap"
 :coll-size 1}


;; function in ClojureScript
(ns foo.core)
(defn xy [x y] (+ x y))

(tag-map xy)
=>
{:tag       :function
 :type      #object[Function]
 :fn-name   "xy"
 :fn-ns     "visual_testing.shared"
 :fn-args   [x y]
 :all-tags  #{:function}
 :classname "Function"}


;; JS function
(tag-map js/ParseFloat)
=>
{:tag                   :function
 :all-tags              #{:function}
 :type                  js/ParseFloat
 :fn-name               "parseFloat"
 :fn-args               [s]
 :js-built-in-method-of js/Number
 :js-built-in-function? true}
```

<br>

> [!WARNING]
> Currently, the `:fn-args` entry is only available in ClojureScript.
> The `:fn-name` will not work as expected in ClojureScript advanced compilation.

With `tag-map`, There are 3 additional params you can pass with the optional second argument (options map). Setting these to `false` will exclude certain information. Depending on how you are using `tag-map`, this could help with performance.

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
{:tag      :function
 :all-tags #{:function}
 :type     #object[Function]
 :fn-name  "xy" 
 :fn-ns    "myns.core"
 :fn-args  [x y]}


(tag-map xy {:include-all-tags? false}) 
=>
{:tag     :function
 :type    #object[Function]
 :fn-name "xy" 
 :fn-ns   "myns.core"
 :fn-args [x y]}
```
<br>

Excluding the function-info related entries:

```clojure
(tag-map xy)
=>
{:tag      :function
 :all-tags #{:function}
 :type     #object[Function]
 :fn-args  [x y]
 :fn-name  "xy"
 :fn-ns    "myns.core"}


(tag-map xy {:include-function-info? false})
=>
{:tag      :function
 :all-tags #{:function}
 :type     #object[Function]}
```
<br>

Excluding the JS built-in-object related entries:

```clojure
(tag-map js/JSON)
=>
{:tag                     :object
 :all-tags                #{:object :js-object :map-like :coll-type}
 :type                    #object[Object]
 :js-built-in-object?     true
 :js-built-in-object-name "JSON"}

(tag-map js/JSON {:include-js-built-in-object-info? false})
=>
{:tag      :object
 :all-tags #{:object :js-object :map-like :coll-type}
 :type     #object[Object]}
```
<br>
<br>

## Examples 

### Clojure
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
|                      `Infinity` |             `:infinity` |      `java.lang.Double`           |
|                     `-Infinity` |            `:-infinity` |      `java.lang.Double`           |
|                           `NaN` |                  `:nan` |      `java.lang.Double`           |
|                           `1/3` |               `:number` |    `clojure.lang.Ratio`           |
|                      `(byte 0)` |               `:number` |        `java.lang.Byte`           |
|                     `(short 3)` |               `:number` |       `java.lang.Short`           |
|                `(double 23.44)` |               `:number` |      `java.lang.Double`           |
|                            `1M` |               `:number` |  `java.math.BigDecimal`           |
|                             `1` |               `:number` |        `java.lang.Long`           |
|                   `(float 1.5)` |               `:number` |       `java.lang.Float`           |
|                      `(char a)` |                 `:char` |   `java.lang.Character`           |
| `(java.math.BigInteger. "171")` |               `:number` |  `java.math.BigInteger`           |
|             `(java.util.Date.)` |                 `:inst` |        `java.util.Date`           |
|                `java.util.Date` |                `:class` |       `java.lang.Class`           |


<br>

### ClojureScript

`lasertag.core/tag` vs `cljs.core/type`

| Input value                        | `lasertag.core/tt`| `cljs.core/type`               |
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
| `(:a :b :c)`                       | `:seq`            | `cljs.core/List`               |
| `Infinity`                         | `:infinity`       | `#object[Boolean]`             |
| `-Infinity`                        | `:-infinity`      | `#object[Boolean]`             |
| `js/parseInt`                      | `:function`       | `#object[Function]`            |
| `(new js/Date.)`                   | `:inst`           | `#object[Date]`                |
| `(.values #js [1 2 3])`            | `:iterable`       | `#object[Object]`              |
| `(array "a" "b")`                  | `:array`          | `#object[Array]`               |
| `(new js/Int8Array #js ["a" "b"])` | `:array`          | `#object[Int8Array]`           |
| `(new js/Set #js[1 2 3])`          | `:set`            | `#object[Set]`                 |
| `(js/Promise. (fn [x] x))`         | `:promise`        | `#object[Promise]`             |

<br>

### Additional ClojureScript Examples

```clojure
;; Record type

(defrecord MyRecordType [a b c d])

(def my-record-type (->MyRecordType 4 8 4 5))

(tag my-record-type) 
=> :record



;; Multimethod definition

(defmulti different-behavior (fn [x] (:x-type x)))

(tag different-behavior)
=> :defmulti



;; Javascript Promise

(def my-promise (js/Promise. (fn [x] x)))

(tag my-promise) 
=> :promise
```
<br>

### Instance methods on JavaScript built-ins

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
```


<br>

## Test
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

<br>

## Status
Alpha, subject to change. Issues welcome, see [contributing](#contributing).

<br>

## Contributing
Issues for bugs, improvements, or features are very welcome. Please file an issue for discussion before starting or issuing a PR.


<br>

## License

Copyright © 2024-2025 Jeremiah Coyle

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
