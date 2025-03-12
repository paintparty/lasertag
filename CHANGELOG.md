# Changelog
[Lasertag](https://github.com/paintparty/lasertag): Lasertag is a library for categorizing types of values in Clojure, ClojureScript, and Babashka.


## Unreleased

<br>
<br>

## 0.10.0
2025-03-12
### Fixed
- Length of java primitive array using `alength`
- Added error message for try-catch in all-tags, when resolving `coll-size`

<br>

## 0.10.0
2024-11-24
### Breaking changes
- Changed the following tags (result of calling `lasertag.core/tag`):<br>
`:js/Set`     -> `:set`              
`:js/Map`     -> `:map`              
`:js/WeakSet` -> `:set`              
`:js/WeakMap` -> `:map`              
`:js/Array`   -> `:array`              
`:js/ArrayBuffer`   -> `:byte-array`              

- Changed all tags that were namespaced with `js/` to use a unified kebab-case
convention, e.g.:<br>
`js/Object`    -> `:js-object`<br>
`js/Int8Array` -> `:js-int8-array`

- All typed arrays in JS now get tagged with `:array`, with a more granular tag
e.g. `:js-int8-array` in the `:all-tags` entry of return value from `lasertag.core/tag-map`   

<br>

## 0.9.0
2024-11-18
### Breaking changes
- Removed the following options:
```Clojure
:include-all-tags?              
:include-function-info?          
:include-js-built-in-object-info?
```
- Added the following option (replaces the above 3)
```Clojure
(tag-map js/JSON
 {:exclude [:all-tags?              
            :function-info?          
            :built-in-object-info?]})
```
<br>

## 0.8.4
2024-11-18
### Breaking changes
- `:inst` tags [#15](https://github.com/paintparty/lasertag/issues/15)
- `:number` tags [#14](https://github.com/paintparty/lasertag/issues/14)
- renamed tag `:NaN` -> `:nan`
- renamed tag `:Infinity` -> `:infinity`
- renamed tag `:-Infinity` -> `:-infinity`

### Changed
- Converted all `:demo` entries in `lasertag.cljs-interop` to strings

### Fixed
- `nil` value TypeError in cljs [#13](https://github.com/paintparty/lasertag/issues/13)

<br>

## 0.8.3
2024-11-15
### Added
- Safety code for babashka

<br>

## 0.8.2
2024-11-8
### Fixed 
- 0f05e70: Fix bug with adding `:js-object` and `js-array` to `:all-tags` map

<br>

## 0.8.1
2024-11-7
### Breaking changes
- Fixed spelling of :lambda? in the return value of `lasertag.core/tag-map`


<br>

## 0.8.0
2024-11-6
### Removed 
Entries with a boolean value in the return value of `lasertag.core/tag-map`:<br>
  - `:carries-meta?` 
  - `:coll-type?` 
  - `:map-like?` 
  - `:set-like?` 
  - `:transient?` 
  - `:number-type?` 
  - `:java-lang-class?` 
  - `:java-util-class?` 
  - `:js-object?`
  - `:js-array?`
  
### Added
The following tags are conditionally added to the :all-tags entry (hashset) in
the return value of `lasertag.core/tag-map`:<br>
  - `:carries-meta` 
  - `:coll-type` 
  - `:map-like` 
  - `:set-like` 
  - `:transient` 
  - `:number-type` 
  - `:java-lang-class` 
  - `:java-util-class` 
  - `:js-object`
  - `:js-array`
```


<br>

## 0.7.0
2024-11-3
### Added
- The following new entries in the return value of `lasertag.core/tag-map`:<br>
  - `:set-like?`
  - `:java-util-class?`
  - `:java-lang-class?`
  - `:classname`

### Breaking changes
- Some instances of java classes are now given tags like `:set`, `:map`, `:seq`,
or `:array`.
  Examples:
```Clojure
  (tag (java.util.HashMap. {"a" 1 "b" 2}))   ; => :map
  (tag (java.util.HashSet. {"a" "b" "c"}))   ; => :set
  (tag (java.util.ArrayList. [1 2 3]))       ; => :array
```


<br>

## 0.6.0
2024-10-16
### Added
- If tag of coll is `:seq`, and coll is a list such as
`clojure.lang.PersistentList`, then `:list` is added to the :all-tags entry in
the return value from `tag-map`.

- If coll is an array-map, `:array-map` is added to the :all-tags entry in the
return value from `tag-map`:
```Clojure
(tag-map (array-map {"one" 1 "two" 2 "three" 3}))
=>
{:tag           :map
 :type          clojure.lang/PersistentArrayMap[]
 :carries-meta? true
 :all-tags      #{:coll :array-map :map}
 :coll-type?    true
 :map-like?     true
 :number-type?  false
 :coll-size     9}
```


<br>

## 0.5.1
2024-07-06
### Fixed
- Conditionally defines `Atomics` (`js/Atomics`), in case device does not support it. 

### Breaking Changes
- Some reflection features may work differently if you are reflecting on an instance of `js/Atomics`.


<br>

## 0.5.0
2024-06-23
### Added
- Adds :carries-meta? entry to `tag-map` return value
- Tag `cljs.core/Cons` and `clojure.lang.Cons` with `:seq`

### Changed
- All colls that pass the pred `seq?` will get tagged with `:seq`. No colls will get tagged with `:list`.


<br>

## 0.4.0 (Lasertag)
2024-03-16
- Changed name of project to Lasertag [#1](https://github.com/paintparty/lasertag/issues/1)


<br>

## 0.3.0
2024-02-27

### Added
- Collections that implement `java.util.Collection` get a `:coll` tag in `:all-tags` entry of result of `typetag.core/tag-map`
- Support for `PersistentTreeSet` -> `:set`

### Changed
- Renamed `:all-typetags` -> `:all-tags`
- Refactored js-built-in method resolver
- Lowered Clojure dep from `v1.11.0` -> `v1.9.0`


<br>

## 0.2.0
2024-02-13

### Added
- Support for tagging dom nodes
- Support for `js/ArrayBuffer`
- Support for `js/Intl.*`
- Support for `js/TypedArray`
- Support for returning string or symbol
- `:map-like?` entry to result of `typetag.core/tag-map`
- Support for instance properties on some js built-ins 
- Add support for `#uuid` and `#inst`

### Fixed
- js built-in tag for native fns


<br>

## 0.1.0
2023-11-17

### Initial Commit
