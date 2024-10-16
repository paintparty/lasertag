# Changelog
[Lasertag](https://github.com/paintparty/lasertag): Clojure(Script) utility for discerning types of values.
## Unreleased
-

<br>

## 0.6.0
2024-10-16
### Added
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
