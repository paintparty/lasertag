# Changelog

## Unreleased
 - 

## 0.3.0
2024-02-27

### Added
- Collections that implement `java.util.Collection` get a `:coll` tag in `:all-tags` entry of result of `typetag.core/tag-map`
- Support for `PersistentTreeSet` -> `:set`

### Changed
- Renamed `:all-typetags` -> `:all-tags`
- Refactored js-built-in method resolver
- Lowered Clojure dep from `v1.11.0` -> `v1.9.0`

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

## 0.1.0
2023-11-17

### Initial Commit
