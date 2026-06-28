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
 lasertag.core-test
 (:require
  [clojure.test :refer [deftest is]]
  [lasertag.core :refer [tag-map]] 
  [clojure.string :as string]))




(deftest
 Intl_Collator-test
 (is
  (=
   {:type js/Intl.Collator,
    :classname "js/Intl.Collator",
    :tag :intl,
    :all-tags #{:intl}} 
   (tag-map (new js/Intl.Collator "sv") nil))))


(deftest
 PersistentHashMap-test
 (is
  (=
   {:type cljs.core/PersistentHashMap,
    :classname "cljs.core/PersistentHashMap",
    :tag :map,
    :all-tags
    #{:iterable :callable :seqable :editable :associative :coll
      :array-map
      :coll-like
      :hash-map
      :map-like
      :map :carries-meta}} 
   (tag-map (hash-map 1 2 3 4) nil))))


(deftest
 MultiFn-test
 (is
  (=
   {:type cljs.core/MultiFn,
    :classname "cljs.core/MultiFn",
    :tag :function,
    :all-tags #{:callable :named :multi-function :function}}
   (tag-map
    (do
     (defmulti different-behavior (fn [x] (:x-type x)))
     different-behavior) 
    nil))))


(deftest
 Cons-test
 (is
  (=
   {:type cljs.core/Cons,
    :classname "cljs.core/Cons",
    :tag :seq,
    :all-tags
    #{:seqable :sequential :coll :deferred :coll-like :lazy :seq
      :list-like
      :carries-meta :cons}} 
   (tag-map (cons 1 (list 2 3)) nil))))


(deftest
 RegExp-test
 (is
  (=
   {:type js/RegExp,
    :classname "js/RegExp",
    :tag :regex,
    :all-tags #{:regex}} 
   (tag-map (js/RegExp. "hello") nil))))


(deftest
 TypeError-test
 (is
  (=
   {:type js/TypeError,
    :classname "js/TypeError",
    :tag :throwable,
    :all-tags #{:throwable :error}} 
   (tag-map (new js/TypeError) nil))))


(deftest
 TransientVector-test
 (is
  (=
   {:type cljs.core/TransientVector,
    :classname "cljs.core/TransientVector",
    :tag :vector,
    :all-tags #{:callable :transient :list-like :coll-like :vector}}
   (tag-map (transient [1 2 3]) nil))))


(deftest
 Float64Array-test
 (is
  (=
   {:type js/Float64Array,
    :classname "js/Float64Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Float64Array (array 1 2 3)) nil))))


(deftest
 PersistentArrayMap-test
 (is
  (=
   {:type cljs.core/PersistentArrayMap,
    :classname "cljs.core/PersistentArrayMap",
    :tag :map,
    :all-tags
    #{:iterable :callable :seqable :editable :associative :coll
      :array-map
      :coll-like
      :map-like :map :carries-meta}} 
   (tag-map {1 2, 3 4} nil))))


(deftest
 EvalError-test
 (is
  (=
   {:type js/EvalError,
    :classname "js/EvalError",
    :tag :throwable,
    :all-tags #{:throwable :error}} 
   (tag-map (new js/EvalError) nil))))


(deftest
 Date-test
 (is
  (=
   {:type js/Date,
    :classname "js/Date",
    :tag :datetime,
    :all-tags #{:inst :datetime}} 
   (tag-map (js/Date.) nil))))


(deftest
 PersistentQueue-test
 (is
  (=
   {:type cljs.core/PersistentQueue,
    :classname "cljs.core/PersistentQueue",
    :tag :queue,
    :all-tags
    #{:iterable :seqable :queue :sequential :coll :coll-like :stack
      :seq
      :list-like
      :carries-meta}} 
   (tag-map cljs.core.PersistentQueue.EMPTY nil))))


(deftest
 Intl_Locale-test
 (is
  (=
   {:type js/Intl.Locale,
    :classname "js/Intl.Locale",
    :tag :intl,
    :all-tags #{:intl}}
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
      "gregory")) 
    nil))))


(deftest
 Int16Array-test
 (is
  (=
   {:type js/Int16Array,
    :classname "js/Int16Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Int16Array (array 1 2 3)) nil))))


(deftest
 URIError-test
 (is
  (=
   {:type js/URIError,
    :classname "js/URIError",
    :tag :throwable,
    :all-tags #{:throwable :error}} 
   (tag-map (new js/URIError) nil))))


(deftest
 Keyword-test
 (is
  (=
   {:type cljs.core/Keyword,
    :classname "cljs.core/Keyword",
    :tag :keyword,
    :all-tags #{:scalar :callable :named :keyword}}
   (tag-map :foo nil))))


(deftest
 ReferenceError-test
 (is
  (=
   {:type js/ReferenceError,
    :classname "js/ReferenceError",
    :tag :throwable,
    :all-tags #{:throwable :error}}
   (tag-map (new js/ReferenceError) nil))))


(deftest
 IntegerRange-test
 (is
  (=
   {:type cljs.core/IntegerRange,
    :classname "cljs.core/IntegerRange",
    :tag :seq,
    :all-tags
    #{:iterable :seqable :sequential :coll :deferred :coll-like :lazy
      :seq :list-like :carries-meta}} 
   (tag-map (range 3) nil))))


(deftest
 Float32Array-test
 (is
  (=
   {:type js/Float32Array,
    :classname "js/Float32Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Float32Array (array 1 2 3)) nil))))


(deftest
 MapEntry-test
 (is
  (=
   {:type cljs.core/MapEntry,
    :classname "cljs.core/MapEntry",
    :tag :vector,
    :all-tags
    #{:callable :seqable :sequential :associative :coll :vector
      :coll-like
      :stack
      :list-like :carries-meta}} 
   (tag-map (-> {:a 1} first) nil))))


(deftest
 EmptyList-test
 (is
  (=
   {:type cljs.core/EmptyList,
    :classname "cljs.core/EmptyList",
    :tag :list,
    :all-tags
    #{:seqable :sequential :coll :coll-like :list :stack :seq
      :list-like :carries-meta}} 
   (tag-map (list) nil))))


(deftest
 LazySeq-test
 (is
  (=
   {:type cljs.core/LazySeq,
    :classname "cljs.core/LazySeq",
    :tag :seq,
    :all-tags
    #{:seqable :sequential :coll :deferred :coll-like :lazy :seq
      :list-like :carries-meta}} 
   (tag-map (map inc [1 2 3]) nil))))


(deftest
 Subvec-test
 (is
  (=
   {:type cljs.core/Subvec,
    :classname "cljs.core/Subvec",
    :tag :vector,
    :all-tags
    #{:iterable :callable :seqable :sequential :associative :coll
      :vector
      :subvec
      :coll-like
      :stack
      :list-like
      :carries-meta}} 
   (tag-map (subvec [1 2 3 4 5] 1 3) nil))))


(deftest
 Uint8ClampedArray-test
 (is
  (=
   {:type js/Uint8ClampedArray,
    :classname "js/Uint8ClampedArray",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Uint8ClampedArray (array 1 2 3)) nil))))


(deftest
 Promise-test
 (is
  (=
   {:type js/Promise,
    :classname "js/Promise",
    :tag :promise,
    :all-tags #{:promise :js :deferred}}
   (tag-map (new js/Promise (fn [x] x)) nil))))


(deftest
 Boolean-test
 (is
  (=
   {:type js/Boolean,
    :classname "js/Boolean",
    :tag :boolean, 
    :all-tags #{:scalar :boolean}} 
   (tag-map true nil))))


(deftest
 Uint8Array-test
 (is
  (=
   {:type js/Uint8Array,
    :classname "js/Uint8Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Uint8Array (array 1 2 3)) nil))))


(deftest
 WeakMap-test
 (is
  (=
   {:type js/WeakMap,
    :classname "js/WeakMap",
    :tag :map,
    :all-tags #{:map-like :coll-like :js :map}}
   (tag-map
    (let [wm (js/WeakMap.) o (js-obj :a 1)] (.set wm o 100)) 
    nil))))


(deftest
 TransientHashMap-test
 (is
  (=
   {:type cljs.core/TransientHashMap,
    :classname "cljs.core/TransientHashMap",
    :tag :map,
    :all-tags #{:callable :transient :hash-map :map}}
   (tag-map (transient (hash-map 1 2 3 4)) nil))))


(deftest
 String-test
 (is
  (=
   {:type js/String,
    :classname "js/String",
    :tag :string,
    :all-tags #{:scalar :seqable :string}} 
   (tag-map "foo" nil))))


(deftest
 Intl_DisplayNames-test
 (is
  (=
   {:type js/Intl.DisplayNames,
    :classname "js/Intl.DisplayNames",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map
    (new
     js/Intl.DisplayNames
     (clj->js ["en"]) 
     (js-obj "type" "region")) 
    nil))))


(deftest
 SyntaxError-test
 (is
  (=
   {:type js/SyntaxError,
    :classname "js/SyntaxError",
    :tag :throwable,
    :all-tags #{:throwable :error}}
   (tag-map (new js/SyntaxError) nil))))


(deftest
 PersistentVector-test
 (is
  (=
   {:type cljs.core/PersistentVector,
    :classname "cljs.core/PersistentVector",
    :tag :vector,
    :all-tags
    #{:iterable :callable :seqable :editable :sequential :associative
      :coll
      :vector
      :coll-like
      :stack :list-like :carries-meta}} 
   (tag-map [1 2 3] nil))))


(deftest
 List-test
 (is
  (=
   {:type cljs.core/List,
    :classname "cljs.core/List",
    :tag :list,
    :all-tags
    #{:seqable :sequential :coll :coll-like :list :stack :seq
      :list-like :carries-meta}} 
   (tag-map (list 1 2 3) nil))))


(deftest
 Repeat-test
 (is
  (=
   {:type cljs.core/Repeat,
    :classname "cljs.core/Repeat",
    :tag :seq,
    :all-tags
    #{:seqable :sequential :coll :deferred :coll-like :lazy :seq
      :list-like :carries-meta}} 
   (tag-map (repeat 2 :a) nil))))


(deftest
 Intl_DateTimeFormat-test
 (is
  (=
   {:type js/Intl.DateTimeFormat,
    :classname "js/Intl.DateTimeFormat",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map (new js/Intl.DateTimeFormat "en-US") nil))))


(deftest
 Array-test
 (is
  (=
   {:type js/Array,
    :classname "js/Array",
    :tag :array,
    :all-tags #{:seqable :list-like :coll-like :array :array-like :js}}
   (tag-map (new js/Array 1 2 3) nil))))


(deftest
 TransientArrayMap-test
 (is
  (=
   {:type cljs.core/TransientArrayMap,
    :classname "cljs.core/TransientArrayMap",
    :tag :map,
    :all-tags #{:callable :transient :array-map :map}}
   (tag-map (transient (array-map 1 2 3 4)) nil))))


(deftest
 Int32Array-test
 (is
  (=
   {:type js/Int32Array,
    :classname "js/Int32Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Int32Array (array 1 2 3)) nil))))


(deftest
 AggregateError-test
 (is
  (=
   {:type js/AggregateError,
    :classname "js/AggregateError",
    :tag :throwable,
    :all-tags #{:throwable :error}}
   (tag-map
    (new js/AggregateError (array (new js/Error "some error")) "Hello")
    nil))))


(deftest
 Uint16Array-test
 (is
  (=
   {:type js/Uint16Array,
    :classname "js/Uint16Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Uint16Array (array 1 2 3)) nil))))


(deftest
 WeakSet-test
 (is
  (=
   {:type js/WeakSet,
    :classname "js/WeakSet",
    :tag :set,
    :all-tags #{:set-like :coll-like :js :set}}
   (tag-map (js/WeakSet. (array (js-obj :id 1)) (js-obj :id 2)) nil))))


(deftest
 TransientHashSet-test
 (is
  (=
   {:type cljs.core/TransientHashSet,
    :classname "cljs.core/TransientHashSet",
    :tag :set,
    :all-tags #{:callable :transient :set}}
   (tag-map (transient #{1 2 3}) nil))))


(deftest
 PersistentHashSet-test
 (is
  (=
   {:type cljs.core/PersistentHashSet,
    :classname "cljs.core/PersistentHashSet",
    :tag :set,
    :all-tags
    #{:iterable :callable :seqable :editable :coll :coll-like :set
      :carries-meta :set-like}} 
   (tag-map #{1 2 3} nil))))


(deftest
 Intl_NumberFormat-test
 (is
  (=
   {:type js/Intl.NumberFormat,
    :classname "js/Intl.NumberFormat",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map
    (new
     js/Intl.NumberFormat
     "de-DE" 
     (js-obj :style "currency" :currency "EUR")) 
    nil))))


(deftest
 Intl_PluralRules-test
 (is
  (=
   {:type js/Intl.PluralRules,
    :classname "js/Intl.PluralRules",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map (new js/Intl.PluralRules "en-US") nil))))


(deftest
 PersistentTreeMap-test
 (is
  (=
   {:type cljs.core/PersistentTreeMap,
    :classname "cljs.core/PersistentTreeMap",
    :tag :map,
    :all-tags
    #{:callable :seqable :associative :coll :array-map :coll-like
      :sorted
      :map-like
      :map :carries-meta}} 
   (tag-map (sorted-map 1 2 3 4) nil))))


(deftest
 Map-test
 (is
  (=
   {:type js/Map,
    :classname "js/Map",
    :tag :map,
    :all-tags #{:map-like :coll-like :js :map}}
   (tag-map (new js/Map (array (array "a" 1) (array "b" 2))) nil))))


(deftest
 Set-test
 (is
  (=
   {:type js/Set,
    :classname "js/Set",
    :tag :set,
    :all-tags #{:set-like :coll-like :js :set}}
   (tag-map (new js/Set (array 1 2)) nil))))


(deftest
 RangeError-test
 (is
  (=
   {:type js/RangeError,
    :classname "js/RangeError",
    :tag :throwable,
    :all-tags #{:throwable :error}} 
   (tag-map (new js/RangeError) nil))))


(deftest
 PersistentTreeSet-test
 (is
  (=
   {:type cljs.core/PersistentTreeSet,
    :classname "cljs.core/PersistentTreeSet",
    :tag :set,
    :all-tags
    #{:callable :seqable :carries-meta :coll :sorted :set-like
      :coll-like :set}} 
   (tag-map (sorted-set 1 2 3) nil))))


(deftest
 Intl_ListFormat-test
 (is
  (=
   {:type js/Intl.ListFormat,
    :classname "js/Intl.ListFormat",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map
    (new
     js/Intl.ListFormat
     "en-GB" 
     (js-obj :style "long" :type "conjunction")) 
    nil))))


(deftest
 Intl_Segmenter-test
 (is
  (=
   {:type js/Intl.Segmenter,
    :classname "js/Intl.Segmenter",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map
    (new js/Intl.Segmenter "fr" (js-obj :granularity "word")) 
    nil))))


(deftest
 Uint32Array-test
 (is
  (=
   {:type js/Uint32Array,
    :classname "js/Uint32Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Uint32Array (array 1 2 3)) nil))))


(deftest
 BigInt64Array-test
 (is
  (=
   {:type js/BigInt64Array,
    :classname "js/BigInt64Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/BigInt64Array 3) nil))))


(deftest
 nil-test
 (is
  (=
   {:type nil,
    :classname "nil",
    :tag :nil, 
    :all-tags #{:scalar :seqable :nil}} 
   (tag-map nil nil))))


(deftest
 Number-test
 (is
  (=
   {:type js/Number,
    :classname "js/Number",
    :tag :number,
    :all-tags #{:scalar :real :double :float :int :number}}
   (tag-map 21 {:skip-dynamic-secondary-tags? true}))))


(deftest
 UUID-test
 (is
  (=
   {:type cljs.core/UUID,
    :classname "cljs.core/UUID",
    :tag :uuid,
    :all-tags #{:uuid}}
   (tag-map (uuid "4fe5d828-6444-11e8-8222-720007e40350") nil))))


(deftest
 Intl_RelativeTimeFormat-test
 (is
  (=
   {:type js/Intl.RelativeTimeFormat,
    :classname "js/Intl.RelativeTimeFormat",
    :tag :intl,
    :all-tags #{:intl}}
   (tag-map
    (new js/Intl.RelativeTimeFormat "en" (js-obj :style "short"))
    nil))))


(deftest
 BigUint64Array-test
 (is
  (=
   {:type js/BigUint64Array,
    :classname "js/BigUint64Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/BigUint64Array 3) nil))))


(deftest
 Error-test
 (is
  (=
   {:type js/Error,
    :classname "js/Error",
    :tag :throwable,
    :all-tags #{:throwable :error}} 
   (tag-map (new js/Error) nil))))


(deftest
 Range-test
 (is
  (=
   {:type cljs.core/Range,
    :classname "cljs.core/Range",
    :tag :seq,
    :all-tags
    #{:iterable :seqable :sequential :coll :deferred :coll-like :lazy
      :seq
      :list-like
      :carries-meta :range}} 
   (tag-map (range 0 1 0.1) nil))))


(deftest
 Int8Array-test
 (is
  (=
   {:type js/Int8Array,
    :classname "js/Int8Array",
    :tag :array,
    :all-tags #{:typed-array :array-like :js :array}}
   (tag-map (new js/Int8Array (array 1 2 3)) nil))))
