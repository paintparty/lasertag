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
 lasertag.generated
 (:require
  [clojure.test :refer [deftest is]]
  [lasertag.core :refer [tag-map]]
  [clojure.string :as string]))



(deftest
 clojure.lang.PersistentArrayMap-test
 (is
  (=
   (tag-map {:a 1})
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
    :classname "clojure.lang.PersistentArrayMap"})))


(deftest
 clojure.lang.Repeat-test
 (is
  (=
   (tag-map (repeat 2 "a"))
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
    :classname "clojure.lang.Repeat"})))


(deftest
 clojure.lang.PersistentVector$TransientVector-test
 (is
  (=
   (tag-map (transient [1 2 3]))
   {:tag :map,
    :type clojure.lang.PersistentVector$TransientVector,
    :all-tags #{:callable :coll-like :transient :map :list-like},
    :classname "clojure.lang.PersistentVector$TransientVector"})))


(deftest
 nil-test
 (is
  (=
   (tag-map nil)
   {:tag :nil,
    :type nil,
    :all-tags #{:seqable :scalar :nil},
    :classname "nil"})))


(deftest
 clojure.lang.PersistentHashSet-test
 (is
  (=
   (tag-map #{1 3 2})
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
    :classname "clojure.lang.PersistentHashSet"})))


(deftest
 clojure.lang.LazySeq-test
 (is
  (=
   (tag-map (map inc [1 2 3]))
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
    :classname "clojure.lang.LazySeq"})))


(deftest
 java.util.Date-test
 (is
  (=
   (tag-map (java.util.Date.))
   {:tag :temporal,
    :type java.util.Date,
    :all-tags #{:inst :temporal},
    :classname "java.util.Date"})))


(deftest
 clojure.lang.Keyword-test
 (is
  (=
   (tag-map :foo)
   {:tag :keyword,
    :type clojure.lang.Keyword,
    :all-tags #{:callable :scalar :keyword :named},
    :classname "clojure.lang.Keyword"})))


(deftest
 java.util.HashSet-test
 (is
  (=
   (tag-map (java.util.HashSet. #{1 "a" 2 "b"}))
   {:tag :set,
    :type java.util.HashSet,
    :all-tags #{:seqable :coll-like :set :set-like},
    :classname "java.util.HashSet"})))


(deftest
 java.lang.ClassCastException-test
 (is
  (=
   (tag-map (java.lang.ClassCastException. "foo"))
   {:tag :throwable,
    :type java.lang.ClassCastException,
    :all-tags #{:exception :throwable},
    :classname "java.lang.ClassCastException"})))


(deftest
 clojure.lang.PersistentHashMap-test
 (is
  (=
   (tag-map (hash-map :a 1))
   {:tag :map,
    :type clojure.lang.PersistentHashMap,
    :all-tags
    #{:callable
      :seqable
      :editable
      :associative
      :coll
      :coll-like
      :hash-map
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentHashMap"})))


(deftest
 clojure.lang.PersistentVector-test
 (is
  (=
   (tag-map [1 2 3])
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
    :classname "clojure.lang.PersistentVector"})))


(deftest
 java.time.ZonedDateTime-test
 (is
  (=
   (tag-map (java.time.ZonedDateTime/now))
   {:tag :temporal,
    :type java.time.ZonedDateTime,
    :all-tags #{:temporal},
    :classname "java.time.ZonedDateTime"})))


(deftest
 clojure.lang.Ratio-test
 (is
  (=
   (tag-map 2/3)
   {:tag :number,
    :type clojure.lang.Ratio,
    :all-tags #{:number :real :scalar :ratio},
    :classname "clojure.lang.Ratio"})))


(deftest
 clojure.lang.MultiFn-test
 (is
  (=
   (tag-map
    (do
     (defmulti different-behavior (fn [x] (:x-type x)))
     different-behavior))
   {:tag :function,
    :type clojure.lang.MultiFn,
    :all-tags #{:callable :function :multi-function?},
    :classname "clojure.lang.MultiFn"})))


(deftest
 java.util.HashMap-test
 (is
  (=
   (tag-map (java.util.HashMap. (hash-map "a" 1 "b" 2)))
   {:tag :map,
    :type java.util.HashMap,
    :all-tags #{:seqable :coll-like :map-like :map},
    :classname "java.util.HashMap"})))


(deftest
 java.time.Instant-test
 (is
  (=
   (tag-map (java.time.Instant/now))
   {:tag :temporal,
    :type java.time.Instant,
    :all-tags #{:inst :temporal},
    :classname "java.time.Instant"})))


(deftest
 java.sql.Timestamp-test
 (is
  (=
   (tag-map (java.sql.Timestamp. (System/currentTimeMillis)))
   {:tag :temporal,
    :type java.sql.Timestamp,
    :all-tags #{:inst :temporal},
    :classname "java.sql.Timestamp"})))


(deftest
 clojure.lang.PersistentList-test
 (is
  (=
   (tag-map (list 1 2 3))
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
    :classname "clojure.lang.PersistentList"})))


(deftest
 clojure.lang.ArityException-test
 (is
  (=
   (tag-map (clojure.lang.ArityException. 3 "foo"))
   {:tag :throwable,
    :type clojure.lang.ArityException,
    :all-tags #{:exception :throwable},
    :classname "clojure.lang.ArityException"})))


(deftest
 clojure.lang.BigInt-test
 (is
  (=
   (tag-map 21N)
   {:tag :number,
    :type clojure.lang.BigInt,
    :all-tags #{:big-int :number :real :scalar},
    :classname "clojure.lang.BigInt"})))


(deftest
 java.math.BigDecimal-test
 (is
  (=
   (tag-map 21M)
   {:tag :number,
    :type java.math.BigDecimal,
    :all-tags #{:number :real :scalar :big-decimal},
    :classname "java.math.BigDecimal"})))


(deftest
 java.lang.String-test
 (is
  (=
   (tag-map "foo")
   {:tag :string,
    :type java.lang.String,
    :all-tags #{:seqable :string :scalar :char-sequence},
    :classname "java.lang.String"})))


(deftest
 clojure.lang.Cons-test
 (is
  (=
   (tag-map (cons 1 '(2 3)))
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
    :classname "clojure.lang.Cons"})))


(deftest
 clojure.lang.APersistentVector$SubVector-test
 (is
  (=
   (tag-map (subvec [1 2 3 4 5] 1 3))
   {:tag :vector,
    :type clojure.lang.APersistentVector$SubVector,
    :all-tags
    #{:callable
      :seqable
      :sequential
      :associative
      :coll
      :vector
      :subvec
      :coll-like
      :stack
      :list-like
      :carries-meta},
    :classname "clojure.lang.APersistentVector$SubVector"})))


(deftest
 clojure.lang.PersistentHashSet$TransientHashSet-test
 (is
  (=
   (tag-map (transient #{1 3 2}))
   {:tag :set,
    :type clojure.lang.PersistentHashSet$TransientHashSet,
    :all-tags #{:callable :transient :set},
    :classname "clojure.lang.PersistentHashSet$TransientHashSet"})))


(deftest
 clojure.lang.LongRange-test
 (is
  (=
   (tag-map (range 3))
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
    :classname "clojure.lang.LongRange"})))


(deftest
 clojure.lang.Agent-test
 (is
  (=
   (tag-map (agent :foo))
   {:tag :agent,
    :type clojure.lang.Agent,
    :all-tags #{:derefable :agent :reference},
    :classname "clojure.lang.Agent"})))


(deftest
 java.lang.Float-test
 (is
  (=
   (tag-map (float 21.42))
   {:tag :number,
    :type java.lang.Float,
    :all-tags #{:number :float :real :scalar},
    :classname "java.lang.Float"})))


(deftest
 clojure.lang.MapEntry-test
 (is
  (=
   (tag-map (-> {:a 1} first))
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
    :classname "clojure.lang.MapEntry"})))


(deftest
 java.lang.Short-test
 (is
  (=
   (tag-map (short 21))
   {:tag :number,
    :type java.lang.Short,
    :all-tags #{:short :int :number :real :scalar},
    :classname "java.lang.Short"})))


(deftest
 clojure.lang.ExceptionInfo-test
 (is
  (=
   (tag-map (ex-info "foo" {}))
   {:tag :throwable,
    :type clojure.lang.ExceptionInfo,
    :all-tags #{:exception :throwable},
    :classname "clojure.lang.ExceptionInfo"})))


(deftest
 java.util.ArrayList-test
 (is
  (=
   (tag-map (java.util.ArrayList. (range 6)))
   {:tag :array,
    :type java.util.ArrayList,
    :all-tags #{:seqable :array :coll-like :list-like},
    :classname "java.util.ArrayList"})))


(deftest
 java.time.LocalDate-test
 (is
  (=
   (tag-map (java.time.LocalDate/now))
   {:tag :temporal,
    :type java.time.LocalDate,
    :all-tags #{:temporal},
    :classname "java.time.LocalDate"})))


(deftest
 java.lang.Double-test
 (is
  (=
   (tag-map 21.42)
   {:tag :number,
    :type java.lang.Double,
    :all-tags #{:double :number :float :real :scalar},
    :classname "java.lang.Double"})))


(deftest
 clojure.lang.PersistentArrayMap$TransientArrayMap-test
 (is
  (=
   (tag-map (transient (array-map 1 2 3 4)))
   {:tag :map,
    :type clojure.lang.PersistentArrayMap$TransientArrayMap,
    :all-tags
    #{:callable :array-map :coll-like :transient :map-like :map},
    :classname "clojure.lang.PersistentArrayMap$TransientArrayMap"})))


(deftest
 java.lang.Long-test
 (is
  (=
   (tag-map 21)
   {:tag :number,
    :type java.lang.Long,
    :all-tags #{:long :int :number :real :scalar},
    :classname "java.lang.Long"})))


(deftest
 java.lang.Boolean-test
 (is
  (=
   (tag-map true)
   {:tag :boolean,
    :type java.lang.Boolean,
    :all-tags #{:scalar :boolean},
    :classname "java.lang.Boolean"})))


(deftest
 clojure.lang.ReaderConditional-test
 (is
  (=
   (tag-map
    (reader-conditional
     '(:clj (System/getProperty "os.name") :cljs "JS")
     false))
   {:tag :reader-conditional,
    :type clojure.lang.ReaderConditional,
    :all-tags #{:reader-conditional},
    :classname "clojure.lang.ReaderConditional"})))


(deftest
 clojure.lang.PersistentList$EmptyList-test
 (is
  (=
   (tag-map (list))
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
    :classname "clojure.lang.PersistentList$EmptyList"})))


(deftest
 java.util.ArrayDeque-test
 (is
  (=
   (tag-map (java.util.ArrayDeque. [1 2 3]))
   {:tag :array,
    :type java.util.ArrayDeque,
    :all-tags #{:seqable :array :coll-like :list-like},
    :classname "java.util.ArrayDeque"})))


(deftest
 clojure.lang.PersistentTreeMap-test
 (is
  (=
   (tag-map (sorted-map :a 1 :b 2))
   {:tag :map,
    :type clojure.lang.PersistentTreeMap,
    :all-tags
    #{:callable
      :seqable
      :associative
      :coll
      :coll-like
      :sorted
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentTreeMap"})))


(deftest
 clojure.lang.PersistentStructMap-test
 (is
  (=
   (tag-map
    (do (defstruct foo :name :color) (struct foo "strawberry" "red")))
   {:tag :map,
    :type clojure.lang.PersistentStructMap,
    :all-tags
    #{:callable
      :seqable
      :associative
      :coll
      :coll-like
      :map-like
      :map
      :carries-meta},
    :classname "clojure.lang.PersistentStructMap"})))


(deftest
 java.util.UUID-test
 (is
  (=
   (tag-map #uuid "4fe5d828-6444-11e8-8222-720007e40350")
   {:tag :uuid,
    :type java.util.UUID,
    :all-tags #{:uuid},
    :classname "java.util.UUID"})))


(deftest
 clojure.lang.PersistentHashMap$TransientHashMap-test
 (is
  (=
   (tag-map (transient (hash-map 1 2 3 4)))
   {:tag :map,
    :type clojure.lang.PersistentHashMap$TransientHashMap,
    :all-tags
    #{:callable :coll-like :hash-map :transient :map-like :map},
    :classname "clojure.lang.PersistentHashMap$TransientHashMap"})))


(deftest
 clojure.lang.Ref-test
 (is
  (=
   (tag-map (ref 0))
   {:tag :ref,
    :type clojure.lang.Ref,
    :all-tags #{:callable :ref :derefable :reference},
    :classname "clojure.lang.Ref"})))


(deftest
 clojure.lang.Volatile-test
 (is
  (=
   (tag-map (volatile! 1))
   {:tag :volatile,
    :type clojure.lang.Volatile,
    :all-tags #{:derefable :volatile},
    :classname "clojure.lang.Volatile"})))


(deftest
 clojure.lang.PersistentQueue-test
 (is
  (=
   (tag-map clojure.lang.PersistentQueue/EMPTY)
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
    :classname "clojure.lang.PersistentQueue"})))


(deftest
 clojure.lang.PersistentTreeSet-test
 (is
  (=
   (tag-map (sorted-set 3 1 2))
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
    :classname "clojure.lang.PersistentTreeSet"})))


(deftest
 java.lang.Byte-test
 (is
  (=
   (tag-map (byte 1))
   {:tag :number,
    :type java.lang.Byte,
    :all-tags #{:int :number :real :scalar :byte},
    :classname "java.lang.Byte"})))


(deftest
 java.util.regex.Pattern-test
 (is
  (=
   (tag-map #"^abc$")
   {:tag :regex,
    :type java.util.regex.Pattern,
    :all-tags #{:regex},
    :classname "java.util.regex.Pattern"})))


(deftest
 clojure.lang.Delay-test
 (is
  (=
   (tag-map (delay 21))
   {:tag :delay,
    :type clojure.lang.Delay,
    :all-tags #{:derefable :deferred :delay},
    :classname "clojure.lang.Delay"})))


(deftest
 java.lang.Integer-test
 (is
  (=
   (tag-map (int 21))
   {:tag :number,
    :type java.lang.Integer,
    :all-tags #{:int :number :real :scalar},
    :classname "java.lang.Integer"})))


(deftest
 clojure.lang.Range-test
 (is
  (=
   (tag-map (range 0 1.0 0.1))
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
    :classname "clojure.lang.Range"})))


(deftest
 clojure.lang.Var-test
 (is
  (=
   (tag-map (do (def my-var 42) #'my-var))
   {:tag :var,
    :type clojure.lang.Var,
    :all-tags #{:callable :derefable :reference :var},
    :classname "clojure.lang.Var"})))


(deftest
 java.lang.Character-test
 (is
  (=
   (tag-map \a)
   {:tag :char,
    :type java.lang.Character,
    :all-tags #{:scalar :char},
    :classname "java.lang.Character"})))


(deftest
 clojure.lang.Atom-test
 (is
  (=
   (tag-map (atom :foo))
   {:tag :atom,
    :type clojure.lang.Atom,
    :all-tags #{:derefable :reference :atom},
    :classname "clojure.lang.Atom"})))


(deftest
 clojure.lang.Symbol-test
 (is
  (=
   (tag-map (symbol "foo"))
   {:tag :symbol,
    :type clojure.lang.Symbol,
    :all-tags #{:callable :symbol :scalar :named :carries-meta},
    :classname "clojure.lang.Symbol"})))


(deftest
 java.math.BigInteger-test
 (is
  (=
   (tag-map (java.math.BigInteger. "21"))
   {:tag :number,
    :type java.math.BigInteger,
    :all-tags #{:big-int :number :real :scalar},
    :classname "java.math.BigInteger"})))
