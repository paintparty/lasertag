;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;   This namespace is automatically generated in lasertag.cached/by-class*.
;;
;;   Do not manually add anything to this namespace.
;;
;;   To regenerate, set `lasertag.cached/write-tests?` to `true`,
;;   then run `lein test`.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns
 lasertag.core-test
 (:require
  [clojure.test :refer [deftest is]]
  [lasertag.core :refer [tag-map]]
  [clojure.string :as string]))



(deftest
 clojure.lang.PersistentArrayMap-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentArrayMap,
    :all-tags
    #{:callable
      :seqable
      :editable
      :associative
      :coll
      :array-map
      :coll-like
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentArrayMap"}
   (tag-map {:a 1} nil))))


(deftest
 clojure.lang.Repeat-test
 (is
  (=
   {:tag :seq,
    :type clojure.lang.Repeat,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :deferred
      :coll-like
      :lazy
      :seq
      :list-like
      :carries-meta},
    :classname "clojure.lang.Repeat"}
   (tag-map (repeat 2 "a") nil))))


(deftest
 clojure.lang.PersistentVector$TransientVector-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentVector$TransientVector,
    :all-tags #{:callable :coll-like :transient :map :list-like},
    :classname "clojure.lang.PersistentVector$TransientVector"}
   (tag-map (transient [1 2 3]) nil))))


(deftest
 nil-test
 (is
  (=
   {:tag :nil,
    :type nil,
    :all-tags #{:seqable :scalar :nil},
    :classname "nil"}
   (tag-map nil nil))))


(deftest
 clojure.lang.PersistentHashSet-test
 (is
  (=
   {:tag :set,
    :type clojure.lang.PersistentHashSet,
    :all-tags
    #{:callable
      :seqable
      :editable
      :coll
      :coll-like
      :set
      :carries-meta
      :set-like},
    :classname "clojure.lang.PersistentHashSet"}
   (tag-map #{1 3 2} nil))))


(deftest
 clojure.lang.LazySeq-test
 (is
  (=
   {:tag :seq,
    :type clojure.lang.LazySeq,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :deferred
      :coll-like
      :lazy
      :seq
      :list-like
      :carries-meta},
    :classname "clojure.lang.LazySeq"}
   (tag-map (map inc [1 2 3]) nil))))


(deftest
 clojure.lang.Keyword-test
 (is
  (=
   {:tag :keyword,
    :type clojure.lang.Keyword,
    :all-tags #{:callable :scalar :keyword :named},
    :classname "clojure.lang.Keyword"}
   (tag-map :foo nil))))


(deftest
 java.util.HashSet-test
 (is
  (=
   {:tag :set,
    :type java.util.HashSet,
    :all-tags #{:seqable :coll-like :set :set-like},
    :classname "java.util.HashSet"}
   (tag-map (java.util.HashSet. #{1 "a" 2 "b"}) nil))))


(deftest
 java.lang.ClassCastException-test
 (is
  (=
   {:tag :throwable,
    :type java.lang.ClassCastException,
    :all-tags #{:exception :throwable},
    :classname "java.lang.ClassCastException"}
   (tag-map (java.lang.ClassCastException. "foo") nil))))


(deftest
 clojure.lang.PersistentHashMap-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentHashMap,
    :all-tags
    #{:callable
      :seqable
      :editable
      :associative
      :coll
      :array-map
      :coll-like
      :hash-map
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentHashMap"}
   (tag-map (hash-map :a 1) nil))))


(deftest
 clojure.lang.PersistentVector-test
 (is
  (=
   {:tag :vector,
    :type clojure.lang.PersistentVector,
    :all-tags
    #{:callable
      :seqable
      :editable
      :sequential
      :associative
      :coll
      :vector
      :coll-like
      :stack
      :list-like
      :carries-meta},
    :classname "clojure.lang.PersistentVector"}
   (tag-map [1 2 3] nil))))


(deftest
 clojure.lang.Ratio-test
 (is
  (=
   {:tag :number,
    :type clojure.lang.Ratio,
    :all-tags #{:number :real :scalar :ratio},
    :classname "clojure.lang.Ratio"}
   (tag-map 2/3 {:skip-dynamic-secondary-tags? true}))))


(deftest
 clojure.lang.MultiFn-test
 (is
  (=
   {:tag :function,
    :type clojure.lang.MultiFn,
    :all-tags #{:callable :multi-function :function},
    :classname "clojure.lang.MultiFn"}
   (tag-map
    (do
     (defmulti different-behavior (fn [x] (:x-type x)))
     different-behavior)
    nil))))


(deftest
 java.util.HashMap-test
 (is
  (=
   {:tag :map,
    :type java.util.HashMap,
    :all-tags #{:seqable :coll-like :map-like :map},
    :classname "java.util.HashMap"}
   (tag-map (java.util.HashMap. (hash-map "a" 1 "b" 2)) nil))))


(deftest
 clojure.lang.PersistentList-test
 (is
  (=
   {:tag :list,
    :type clojure.lang.PersistentList,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :coll-like
      :list
      :stack
      :seq
      :list-like
      :carries-meta},
    :classname "clojure.lang.PersistentList"}
   (tag-map (list 1 2 3) nil))))


(deftest
 clojure.lang.ArityException-test
 (is
  (=
   {:tag :throwable,
    :type clojure.lang.ArityException,
    :all-tags #{:exception :throwable},
    :classname "clojure.lang.ArityException"}
   (tag-map (clojure.lang.ArityException. 3 "foo") nil))))


(deftest
 clojure.lang.BigInt-test
 (is
  (=
   {:tag :number,
    :type clojure.lang.BigInt,
    :all-tags #{:big-int :number :real :scalar},
    :classname "clojure.lang.BigInt"}
   (tag-map 21N {:skip-dynamic-secondary-tags? true}))))


(deftest
 java.math.BigDecimal-test
 (is
  (=
   {:tag :number,
    :type java.math.BigDecimal,
    :all-tags #{:number :real :scalar :big-decimal},
    :classname "java.math.BigDecimal"}
   (tag-map 21M {:skip-dynamic-secondary-tags? true}))))


(deftest
 java.lang.String-test
 (is
  (=
   {:tag :string,
    :type java.lang.String,
    :all-tags #{:seqable :string :scalar :char-sequence},
    :classname "java.lang.String"}
   (tag-map "foo" nil))))


(deftest
 clojure.lang.Cons-test
 (is
  (=
   {:tag :seq,
    :type clojure.lang.Cons,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :deferred
      :coll-like
      :lazy
      :seq
      :list-like
      :carries-meta
      :cons},
    :classname "clojure.lang.Cons"}
   (tag-map (cons 1 '(2 3)) nil))))


(deftest
 clojure.lang.PersistentHashSet$TransientHashSet-test
 (is
  (=
   {:tag :set,
    :type clojure.lang.PersistentHashSet$TransientHashSet,
    :all-tags #{:callable :transient :set},
    :classname "clojure.lang.PersistentHashSet$TransientHashSet"}
   (tag-map (transient #{1 3 2}) nil))))


(deftest
 clojure.lang.LongRange-test
 (is
  (=
   {:tag :seq,
    :type clojure.lang.LongRange,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :deferred
      :coll-like
      :lazy
      :seq
      :list-like
      :carries-meta
      :range},
    :classname "clojure.lang.LongRange"}
   (tag-map (range 3) nil))))


(deftest
 clojure.lang.Agent-test
 (is
  (=
   {:tag :agent,
    :type clojure.lang.Agent,
    :all-tags #{:derefable :agent :reference},
    :classname "clojure.lang.Agent"}
   (tag-map (agent :foo) nil))))


(deftest
 java.util.Date-test
 (is
  (=
   {:tag :datetime,
    :type java.util.Date,
    :all-tags #{:inst :datetime},
    :classname "java.util.Date"}
   (tag-map (java.util.Date.) nil))))


(deftest
 java.lang.Float-test
 (is
  (=
   {:tag :number,
    :type java.lang.Float,
    :all-tags #{:number :float :real :scalar},
    :classname "java.lang.Float"}
   (tag-map (float 21.42) {:skip-dynamic-secondary-tags? true}))))


(deftest
 clojure.lang.MapEntry-test
 (is
  (=
   {:tag :vector,
    :type clojure.lang.MapEntry,
    :all-tags
    #{:callable
      :seqable
      :sequential
      :associative
      :coll
      :vector
      :coll-like
      :stack
      :map-entry
      :list-like},
    :classname "clojure.lang.MapEntry"}
   (tag-map (-> {:a 1} first) nil))))


(deftest
 java.lang.Short-test
 (is
  (=
   {:tag :number,
    :type java.lang.Short,
    :all-tags #{:short :int :number :real :scalar},
    :classname "java.lang.Short"}
   (tag-map (short 21) {:skip-dynamic-secondary-tags? true}))))


(deftest
 clojure.lang.ExceptionInfo-test
 (is
  (=
   {:tag :throwable,
    :type clojure.lang.ExceptionInfo,
    :all-tags #{:exception :throwable},
    :classname "clojure.lang.ExceptionInfo"}
   (tag-map (ex-info "foo" {}) nil))))


(deftest
 java.util.ArrayList-test
 (is
  (=
   {:tag :array,
    :type java.util.ArrayList,
    :all-tags #{:seqable :array :coll-like :list-like},
    :classname "java.util.ArrayList"}
   (tag-map (java.util.ArrayList. (range 6)) nil))))


(deftest
 java.lang.Double-test
 (is
  (=
   {:tag :number,
    :type java.lang.Double,
    :all-tags #{:double :number :float :real :scalar},
    :classname "java.lang.Double"}
   (tag-map 21.42 {:skip-dynamic-secondary-tags? true}))))


(deftest
 java.time.LocalDate-test
 (is
  (=
   {:tag :datetime,
    :type java.time.LocalDate,
    :all-tags #{:datetime},
    :classname "java.time.LocalDate"}
   (tag-map (java.time.LocalDate/now) nil))))


(deftest
 clojure.lang.PersistentArrayMap$TransientArrayMap-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentArrayMap$TransientArrayMap,
    :all-tags
    #{:callable :array-map :coll-like :transient :map-like :map},
    :classname "clojure.lang.PersistentArrayMap$TransientArrayMap"}
   (tag-map (transient (array-map 1 2 3 4)) nil))))


(deftest
 java.lang.Long-test
 (is
  (=
   {:tag :number,
    :type java.lang.Long,
    :all-tags #{:long :int :number :real :scalar},
    :classname "java.lang.Long"}
   (tag-map 21 {:skip-dynamic-secondary-tags? true}))))


(deftest
 java.lang.Boolean-test
 (is
  (=
   {:tag :boolean,
    :type java.lang.Boolean,
    :all-tags #{:scalar :boolean},
    :classname "java.lang.Boolean"}
   (tag-map true nil))))


(deftest
 java.time.Instant-test
 (is
  (=
   {:tag :datetime,
    :type java.time.Instant,
    :all-tags #{:inst :datetime},
    :classname "java.time.Instant"}
   (tag-map (java.time.Instant/now) nil))))


(deftest
 clojure.lang.ReaderConditional-test
 (is
  (=
   {:tag :reader-conditional,
    :type clojure.lang.ReaderConditional,
    :all-tags #{:reader-conditional},
    :classname "clojure.lang.ReaderConditional"}
   (tag-map
    (reader-conditional
     '(:clj (System/getProperty "os.name") :cljs "JS")
     false)
    nil))))


(deftest
 clojure.lang.PersistentList$EmptyList-test
 (is
  (=
   {:tag :list,
    :type clojure.lang.PersistentList$EmptyList,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :coll-like
      :list
      :stack
      :seq
      :list-like
      :carries-meta},
    :classname "clojure.lang.PersistentList$EmptyList"}
   (tag-map (list) nil))))


(deftest
 java.util.ArrayDeque-test
 (is
  (=
   {:tag :array,
    :type java.util.ArrayDeque,
    :all-tags #{:seqable :array :coll-like :list-like},
    :classname "java.util.ArrayDeque"}
   (tag-map (java.util.ArrayDeque. [1 2 3]) nil))))


(deftest
 clojure.lang.PersistentTreeMap-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentTreeMap,
    :all-tags
    #{:callable
      :seqable
      :associative
      :coll
      :array-map
      :coll-like
      :sorted
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentTreeMap"}
   (tag-map (sorted-map :a 1 :b 2) nil))))


(deftest
 java.time.ZonedDateTime-test
 (is
  (=
   {:tag :datetime,
    :type java.time.ZonedDateTime,
    :all-tags #{:datetime},
    :classname "java.time.ZonedDateTime"}
   (tag-map (java.time.ZonedDateTime/now) nil))))


(deftest
 java.util.UUID-test
 (is
  (=
   {:tag :uuid,
    :type java.util.UUID,
    :all-tags #{:uuid},
    :classname "java.util.UUID"}
   (tag-map #uuid "4fe5d828-6444-11e8-8222-720007e40350" nil))))


(deftest
 clojure.lang.PersistentHashMap$TransientHashMap-test
 (is
  (=
   {:tag :map,
    :type clojure.lang.PersistentHashMap$TransientHashMap,
    :all-tags
    #{:callable :coll-like :hash-map :transient :map-like :map},
    :classname "clojure.lang.PersistentHashMap$TransientHashMap"}
   (tag-map (transient (hash-map 1 2 3 4)) nil))))


(deftest
 clojure.lang.Ref-test
 (is
  (=
   {:tag :ref,
    :type clojure.lang.Ref,
    :all-tags #{:callable :ref :derefable :reference},
    :classname "clojure.lang.Ref"}
   (tag-map (ref 0) nil))))


(deftest
 clojure.lang.Volatile-test
 (is
  (=
   {:tag :volatile,
    :type clojure.lang.Volatile,
    :all-tags #{:derefable :volatile},
    :classname "clojure.lang.Volatile"}
   (tag-map (volatile! 1) nil))))


(deftest
 clojure.lang.PersistentQueue-test
 (is
  (=
   {:tag :queue,
    :type clojure.lang.PersistentQueue,
    :all-tags
    #{:seqable
      :queue
      :sequential
      :coll
      :coll-like
      :stack
      :list-like
      :carries-meta},
    :classname "clojure.lang.PersistentQueue"}
   (tag-map clojure.lang.PersistentQueue/EMPTY nil))))


(deftest
 clojure.lang.PersistentTreeSet-test
 (is
  (=
   {:tag :set,
    :type clojure.lang.PersistentTreeSet,
    :all-tags
    #{:callable
      :seqable
      :coll
      :coll-like
      :sorted
      :set
      :carries-meta
      :set-like},
    :classname "clojure.lang.PersistentTreeSet"}
   (tag-map (sorted-set 3 1 2) nil))))


(deftest
 java.lang.Byte-test
 (is
  (=
   {:tag :number,
    :type java.lang.Byte,
    :all-tags #{:int :number :real :scalar :byte},
    :classname "java.lang.Byte"}
   (tag-map (byte 1) {:skip-dynamic-secondary-tags? true}))))


(deftest
 java.util.regex.Pattern-test
 (is
  (=
   {:tag :regex,
    :type java.util.regex.Pattern,
    :all-tags #{:regex},
    :classname "java.util.regex.Pattern"}
   (tag-map #"^abc$" nil))))


(deftest
 clojure.lang.Delay-test
 (is
  (=
   {:tag :delay,
    :type clojure.lang.Delay,
    :all-tags #{:derefable :deferred :delay},
    :classname "clojure.lang.Delay"}
   (tag-map (delay 21) nil))))


(deftest
 java.lang.Integer-test
 (is
  (=
   {:tag :number,
    :type java.lang.Integer,
    :all-tags #{:int :number :real :scalar},
    :classname "java.lang.Integer"}
   (tag-map (int 21) {:skip-dynamic-secondary-tags? true}))))


(deftest
 clojure.lang.Range-test
 (is
  (=
   {:tag :seq,
    :type clojure.lang.Range,
    :all-tags
    #{:seqable
      :sequential
      :coll
      :deferred
      :coll-like
      :lazy
      :seq
      :list-like
      :carries-meta
      :range},
    :classname "clojure.lang.Range"}
   (tag-map (range 0 1.0 0.1) nil))))


(deftest
 clojure.lang.Var-test
 (is
  (=
   {:tag :var,
    :type clojure.lang.Var,
    :all-tags #{:callable :derefable :reference :var},
    :classname "clojure.lang.Var"}
   (tag-map (do (def my-var 42) #'my-var) nil))))


(deftest
 java.lang.Character-test
 (is
  (=
   {:tag :char,
    :type java.lang.Character,
    :all-tags #{:scalar :char},
    :classname "java.lang.Character"}
   (tag-map \a nil))))


(deftest
 clojure.lang.Atom-test
 (is
  (=
   {:tag :atom,
    :type clojure.lang.Atom,
    :all-tags #{:derefable :reference :atom},
    :classname "clojure.lang.Atom"}
   (tag-map (atom :foo) nil))))


(deftest
 clojure.lang.Symbol-test
 (is
  (=
   {:tag :symbol,
    :type clojure.lang.Symbol,
    :all-tags #{:callable :symbol :scalar :named :carries-meta},
    :classname "clojure.lang.Symbol"}
   (tag-map (symbol "foo") nil))))


(deftest
 java.math.BigInteger-test
 (is
  (=
   {:tag :number,
    :type java.math.BigInteger,
    :all-tags #{:big-int :number :real :scalar},
    :classname "java.math.BigInteger"}
   (tag-map
    (java.math.BigInteger. "21")
    {:skip-dynamic-secondary-tags? true}))))
