;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;   This namespace is automatically generated in lasertag.jsi.codegen.cljs.
;;
;;   Do not manually add anything to this namespace.
;;
;;   To regenerate, following instructions in readme "Developing" section.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns
 lasertag.generated
 (:require
  [clojure.test :refer [deftest is]]
  [lasertag.core :refer [tag-map]] 
  [clojure.string :as string]))




(deftest
 js/Intl.Collator-test
 (is
  (=
   (tag-map (new js/Intl.Collator "sv"))
   {:type js/Intl.Collator,
    :classname "js/Intl.Collator", 
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 cljs.core/PersistentHashMap-test
 (is
  (=
   (tag-map (hash-map 1 2 3 4))
   {:type cljs.core/PersistentHashMap,
    :classname "cljs.core/PersistentHashMap",
    :tag :map,
    :all-tags
    #{:iterable :js :callable :seqable :associative :coll :array-map
      :coll-like :hash-map :map-like :map}})))


(deftest
 cljs.core/MultiFn-test
 (is
  (=
   (tag-map
    (do
     (defmulti different-behavior (fn [x] (:x-type x)))
     different-behavior))
   {:type cljs.core/MultiFn,
    :classname "cljs.core/MultiFn",
    :tag :function, 
    :all-tags #{:callable :multi-function :function}})))


(deftest
 cljs.core/Cons-test
 (is
  (=
   (tag-map (cons 1 (list 2 3)))
   {:type cljs.core/Cons,
    :classname "cljs.core/Cons",
    :tag :seq,
    :all-tags
    #{:seqable :sequential :coll :deferred :coll-like :lazy :seq
      :list-like :cons}})))


(deftest
 js/RegExp-test
 (is
  (=
   (tag-map (js/RegExp. "hello"))
   {:type js/RegExp,
    :classname "js/RegExp", 
    :tag :regex, 
    :all-tags #{:regex}})))


(deftest
 js/TypeError-test
 (is
  (=
   (tag-map (new js/TypeError))
   {:type js/TypeError,
    :classname "js/TypeError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/TransientVector-test
 (is
  (=
   (tag-map (transient [1 2 3]))
   {:type cljs.core/TransientVector,
    :classname "cljs.core/TransientVector",
    :tag :vector,
    :all-tags #{:callable :list-like :coll-like :vector}})))


(deftest
 js/Float64Array-test
 (is
  (=
   (tag-map (new js/Float64Array (array 1 2 3)))
   {:type js/Float64Array,
    :classname "js/Float64Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 cljs.core/PersistentArrayMap-test
 (is
  (=
   (tag-map {1 2, 3 4})
   {:type cljs.core/PersistentArrayMap,
    :classname "cljs.core/PersistentArrayMap",
    :tag :map,
    :all-tags
    #{:iterable :js :callable :seqable :associative :coll :array-map
      :coll-like :map-like :map}})))


(deftest
 js/EvalError-test
 (is
  (=
   (tag-map (new js/EvalError))
   {:type js/EvalError,
    :classname "js/EvalError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/PersistentQueue-test
 (is
  (=
   (tag-map cljs.core.PersistentQueue.EMPTY)
   {:type cljs.core/PersistentQueue,
    :classname "cljs.core/PersistentQueue",
    :tag :queue,
    :all-tags
    #{:iterable :js :seqable :queue :sequential :coll :coll-like :seq
      :list-like}})))


(deftest
 js/Intl.Locale-test
 (is
  (=
   (tag-map
    (new
     js/Intl.Locale
     "ko"
     (js-obj
      :script
      "Kore"
      :region
      "KR"
      :hourCycle
      "h23"
      :calendar
      "gregory")))
   {:type js/Intl.Locale,
    :classname "js/Intl.Locale", 
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/Int16Array-test
 (is
  (=
   (tag-map (new js/Int16Array (array 1 2 3)))
   {:type js/Int16Array,
    :classname "js/Int16Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/URIError-test
 (is
  (=
   (tag-map (new js/URIError))
   {:type js/URIError,
    :classname "js/URIError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/Keyword-test
 (is
  (=
   (tag-map :foo)
   {:type cljs.core/Keyword,
    :classname "cljs.core/Keyword",
    :tag :keyword, 
    :all-tags #{:scalar :callable :keyword}})))


(deftest
 js/ReferenceError-test
 (is
  (=
   (tag-map (new js/ReferenceError))
   {:type js/ReferenceError,
    :classname "js/ReferenceError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/IntegerRange-test
 (is
  (=
   (tag-map (range 3))
   {:type cljs.core/IntegerRange,
    :classname "cljs.core/IntegerRange",
    :tag :seq,
    :all-tags
    #{:iterable :js :seqable :sequential :coll :deferred :coll-like
      :lazy :seq :list-like}})))


(deftest
 js/Float32Array-test
 (is
  (=
   (tag-map (new js/Float32Array (array 1 2 3)))
   {:type js/Float32Array,
    :classname "js/Float32Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 cljs.core/MapEntry-test
 (is
  (=
   (tag-map (-> {:a 1} first))
   {:type cljs.core/MapEntry,
    :classname "cljs.core/MapEntry",
    :tag :vector,
    :all-tags
    #{:callable :seqable :coll :associative :sequential :list-like
      :coll-like :vector}})))


(deftest
 cljs.core/EmptyList-test
 (is
  (=
   (tag-map (list))
   {:type cljs.core/EmptyList,
    :classname "cljs.core/EmptyList",
    :tag :list,
    :all-tags
    #{:seqable :coll :seq :sequential :list-like :coll-like :list}})))


(deftest
 cljs.core/LazySeq-test
 (is
  (=
   (tag-map (map inc [1 2 3]))
   {:type cljs.core/LazySeq,
    :classname "cljs.core/LazySeq",
    :tag :seq,
    :all-tags
    #{:seqable :coll :seq :lazy :deferred :sequential :list-like
      :coll-like}})))


(deftest
 cljs.core/Subvec-test
 (is
  (=
   (tag-map (subvec [1 2 3 4 5] 1 3))
   {:type cljs.core/Subvec,
    :classname "cljs.core/Subvec",
    :tag :vector,
    :all-tags
    #{:iterable :js :callable :seqable :sequential :associative :coll
      :vector :subvec :coll-like :list-like}})))


(deftest
 js/Uint8ClampedArray-test
 (is
  (=
   (tag-map (new js/Uint8ClampedArray (array 1 2 3)))
   {:type js/Uint8ClampedArray,
    :classname "js/Uint8ClampedArray",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/Promise-test
 (is
  (=
   (tag-map (new js/Promise (fn [x] x)))
   {:type js/Promise,
    :classname "js/Promise",
    :tag :promise, 
    :all-tags #{:promise :js :deferred}})))


(deftest
 js/Boolean-test
 (is
  (=
   (tag-map true)
   {:type js/Boolean,
    :classname "js/Boolean",
    :tag :boolean, 
    :all-tags #{:scalar :boolean}})))


(deftest
 js/Uint8Array-test
 (is
  (=
   (tag-map (new js/Uint8Array (array 1 2 3)))
   {:type js/Uint8Array,
    :classname "js/Uint8Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/WeakMap-test
 (is
  (=
   (tag-map (let [wm (js/WeakMap.) o (js-obj :a 1)] (.set wm o 100)))
   {:type js/WeakMap,
    :classname "js/WeakMap",
    :tag :map, 
    :all-tags #{:map-like :coll-like :js :map}})))


(deftest
 cljs.core/TransientHashMap-test
 (is
  (=
   (tag-map (transient (hash-map 1 2 3 4)))
   {:type cljs.core/TransientHashMap,
    :classname "cljs.core/TransientHashMap",
    :tag :map, 
    :all-tags #{:callable :hash-map :map}})))


(deftest
 js/String-test
 (is
  (=
   (tag-map "foo")
   {:type js/String,
    :classname "js/String",
    :tag :string, 
    :all-tags #{:scalar :seqable :string}})))


(deftest
 js/Intl.DisplayNames-test
 (is
  (=
   (tag-map
    (new
     js/Intl.DisplayNames
     (clj->js ["en"])
     (js-obj "type" "region")))
   {:type js/Intl.DisplayNames,
    :classname "js/Intl.DisplayNames",
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/SyntaxError-test
 (is
  (=
   (tag-map (new js/SyntaxError))
   {:type js/SyntaxError,
    :classname "js/SyntaxError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/PersistentVector-test
 (is
  (=
   (tag-map [1 2 3])
   {:type cljs.core/PersistentVector,
    :classname "cljs.core/PersistentVector",
    :tag :vector,
    :all-tags
    #{:iterable :js :callable :seqable :sequential :associative :coll
      :vector :coll-like :list-like}})))


(deftest
 cljs.core/List-test
 (is
  (=
   (tag-map (list 1 2 3))
   {:type cljs.core/List,
    :classname "cljs.core/List",
    :tag :list,
    :all-tags
    #{:seqable :coll :seq :sequential :list-like :coll-like :list}})))


(deftest
 cljs.core/Repeat-test
 (is
  (=
   (tag-map (repeat 2 :a))
   {:type cljs.core/Repeat,
    :classname "cljs.core/Repeat",
    :tag :seq,
    :all-tags
    #{:seqable :coll :seq :lazy :deferred :sequential :list-like
      :coll-like}})))


(deftest
 js/Intl.DateTimeFormat-test
 (is
  (=
   (tag-map (new js/Intl.DateTimeFormat "en-US"))
   {:type js/Intl.DateTimeFormat,
    :classname "js/Intl.DateTimeFormat",
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/Array-test
 (is
  (=
   (tag-map (new js/Array 1 2 3))
   {:type js/Array,
    :classname "js/Array",
    :tag :array,
    :all-tags
    #{:seqable :list-like :coll-like :array :array-like :js}})))


(deftest
 cljs.core/TransientArrayMap-test
 (is
  (=
   (tag-map (transient (array-map 1 2 3 4)))
   {:type cljs.core/TransientArrayMap,
    :classname "cljs.core/TransientArrayMap",
    :tag :map, 
    :all-tags #{:callable :array-map :map}})))


(deftest
 js/Int32Array-test
 (is
  (=
   (tag-map (new js/Int32Array (array 1 2 3)))
   {:type js/Int32Array,
    :classname "js/Int32Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/AggregateError-test
 (is
  (=
   (tag-map
    (new
     js/AggregateError
     (array (new js/Error "some error"))
     "Hello"))
   {:type js/AggregateError,
    :classname "js/AggregateError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 js/Uint16Array-test
 (is
  (=
   (tag-map (new js/Uint16Array (array 1 2 3)))
   {:type js/Uint16Array,
    :classname "js/Uint16Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/WeakSet-test
 (is
  (=
   (tag-map (new js/Set (array 1 2)))
   {:type js/WeakSet,
    :classname "js/WeakSet",
    :tag :set, 
    :all-tags #{:set-like :coll-like :js :set}})))


(deftest
 cljs.core/TransientHashSet-test
 (is
  (=
   (tag-map (transient #{1 2 3}))
   {:type cljs.core/TransientHashSet,
    :classname "cljs.core/TransientHashSet",
    :tag :set, 
    :all-tags #{:callable :set}})))


(deftest
 cljs.core/PersistentHashSet-test
 (is
  (=
   (tag-map #{1 2 3})
   {:type cljs.core/PersistentHashSet,
    :classname "cljs.core/PersistentHashSet",
    :tag :set,
    :all-tags
    #{:callable :seqable :coll :set-like :coll-like :iterable :js
      :set}})))


(deftest
 js/Intl.NumberFormat-test
 (is
  (=
   (tag-map
    (new
     js/Intl.NumberFormat
     "de-DE"
     (js-obj :style "currency" :currency "EUR")))
   {:type js/Intl.NumberFormat,
    :classname "js/Intl.NumberFormat",
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/Intl.PluralRules-test
 (is
  (=
   (tag-map (new js/Intl.PluralRules "en-US"))
   {:type js/Intl.PluralRules,
    :classname "js/Intl.PluralRules", 
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 cljs.core/PersistentTreeMap-test
 (is
  (=
   (tag-map (sorted-map 1 2 3 4))
   {:type cljs.core/PersistentTreeMap,
    :classname "cljs.core/PersistentTreeMap",
    :tag :map,
    :all-tags
    #{:callable :seqable :associative :coll :array-map :coll-like
      :sorted :map-like :map}})))


(deftest
 js/Map-test
 (is
  (=
   (tag-map (new js/Map (array (array "a" 1) (array "b" 2))))
   {:type js/Map,
    :classname "js/Map",
    :tag :map, 
    :all-tags #{:map-like :coll-like :js :map}})))


(deftest
 js/Set-test
 (is
  (=
   (tag-map (new js/Set (array 1 2)))
   {:type js/Set,
    :classname "js/Set",
    :tag :set, 
    :all-tags #{:set-like :coll-like :js :set}})))


(deftest
 js/RangeError-test
 (is
  (=
   (tag-map (new js/RangeError))
   {:type js/RangeError,
    :classname "js/RangeError",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/PersistentTreeSet-test
 (is
  (=
   (tag-map (sorted-set 1 2 3))
   {:type cljs.core/PersistentTreeSet,
    :classname "cljs.core/PersistentTreeSet",
    :tag :set,
    :all-tags
    #{:callable :seqable :coll :sorted :set-like :coll-like :set}})))


(deftest
 js/Intl.ListFormat-test
 (is
  (=
   (tag-map
    (new
     js/Intl.ListFormat
     "en-GB"
     (js-obj :style "long" :type "conjunction")))
   {:type js/Intl.ListFormat,
    :classname "js/Intl.ListFormat", 
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/Intl.Segmenter-test
 (is
  (=
   (tag-map (new js/Intl.Segmenter "fr" (js-obj :granularity "word")))
   {:type js/Intl.Segmenter,
    :classname "js/Intl.Segmenter", 
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/Uint32Array-test
 (is
  (=
   (tag-map (new js/Uint32Array (array 1 2 3)))
   {:type js/Uint32Array,
    :classname "js/Uint32Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/BigInt64Array-test
 (is
  (=
   (tag-map (new js/BigInt64Array 3))
   {:type js/BigInt64Array,
    :classname "js/BigInt64Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 nil-test
 (is
  (=
   (tag-map nil)
   {:type nil,
    :classname "nil", 
    :tag :nil, 
    :all-tags #{:scalar :seqable :nil}})))


(deftest
 js/Number-test
 (is
  (=
   (tag-map 21)
   {:type js/Number,
    :classname "js/Number",
    :tag :number,
    :all-tags #{:scalar :real :double :float :int :number}})))


(deftest
 cljs.core/UUID-test
 (is
  (=
   (tag-map (uuid "4fe5d828-6444-11e8-8222-720007e40350"))
   {:type cljs.core/UUID,
    :classname "cljs.core/UUID", 
    :tag :uuid, 
    :all-tags #{:uuid}})))


(deftest
 js/Intl.RelativeTimeFormat-test
 (is
  (=
   (tag-map
    (new js/Intl.RelativeTimeFormat "en" (js-obj :style "short")))
   {:type js/Intl.RelativeTimeFormat,
    :classname "js/Intl.RelativeTimeFormat",
    :tag :intl, 
    :all-tags #{:intl}})))


(deftest
 js/BigUint64Array-test
 (is
  (=
   (tag-map (new js/BigUint64Array 3))
   {:type js/BigUint64Array,
    :classname "js/BigUint64Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))


(deftest
 js/Error-test
 (is
  (=
   (tag-map (new js/Error))
   {:type js/Error,
    :classname "js/Error",
    :tag :throwable, 
    :all-tags #{:throwable :error}})))


(deftest
 cljs.core/Range-test
 (is
  (=
   (tag-map (range 0 1 0.1))
   {:type cljs.core/Range,
    :classname "cljs.core/Range",
    :tag :seq,
    :all-tags
    #{:iterable :js :seqable :sequential :coll :deferred :coll-like
      :lazy :seq :list-like :range}})))


(deftest
 js/Int8Array-test
 (is
  (=
   (tag-map (new js/Int8Array (array 1 2 3)))
   {:type js/Int8Array,
    :classname "js/Int8Array",
    :tag :array, 
    :all-tags #{:typed-array :array-like :js :array}})))
