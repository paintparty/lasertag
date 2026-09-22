;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;   This namespace is automatically generated in lasertag.cached/by-class*.
;;
;;   Do not manually add anything to this namespace.
;;
;;   It can be regenerated with the bb task `test:snapshot`.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns
 lasertag.core-test
 (:require
  [clojure.test :refer [deftest is]]
  [lasertag.core :refer [tag-map]]
  [clojure.string :as string]))



#?(:clj
   (deftest
    java.lang.Boolean-test
    (is
     (=
      {:tag :boolean,
       :all-tags #{:scalar :boolean},
       :category "scalars",
       :type java.lang.Boolean,
       :classname "java.lang.Boolean"}
      (tag-map true nil)))))

#?(:clj
   (deftest
    nil-test
    (is
     (=
      {:tag :nil,
       :all-tags #{:seqable :scalar :nil},
       :category "scalars",
       :type nil,
       :classname "nil"}
      (tag-map nil nil)))))

#?(:clj
   (deftest
    java.lang.Long-test
    (is
     (=
      {:tag :number,
       :all-tags #{:long :int :number :real :scalar},
       :category "scalars",
       :type java.lang.Long,
       :classname "java.lang.Long"}
      (tag-map 42 {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    java.lang.Integer-test
    (is
     (=
      {:tag :number,
       :all-tags #{:int :number :real :scalar},
       :category "scalars",
       :type java.lang.Integer,
       :classname "java.lang.Integer"}
      (tag-map (int 42) {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    java.lang.Float-test
    (is
     (=
      {:tag :number,
       :all-tags #{:number :float :real :scalar},
       :category "scalars",
       :type java.lang.Float,
       :classname "java.lang.Float"}
      (tag-map (float 3.14) {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    java.lang.Byte-test
    (is
     (=
      {:tag :number,
       :all-tags #{:int :number :real :scalar :byte},
       :category "scalars",
       :type java.lang.Byte,
       :classname "java.lang.Byte"}
      (tag-map (byte 1) {:skip-dynamic-secondary-tags? true})))))

#?(:clj
   (deftest
    java.lang.Double-test
    (is
     (=
      {:tag :number,
       :all-tags #{:double :number :float :real :scalar},
       :category "scalars",
       :type java.lang.Double,
       :classname "java.lang.Double"}
      (tag-map 3.14 {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    java.lang.Short-test
    (is
     (=
      {:tag :number,
       :all-tags #{:short :int :number :real :scalar},
       :category "scalars",
       :type java.lang.Short,
       :classname "java.lang.Short"}
      (tag-map (short 42) {:skip-dynamic-secondary-tags? true})))))

#?(:clj
   (deftest
    clojure.lang.Ratio-test
    (is
     (=
      {:tag :number,
       :all-tags #{:number :real :scalar :ratio},
       :category "scalars",
       :type clojure.lang.Ratio,
       :classname "clojure.lang.Ratio"}
      (tag-map 1/3 {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    clojure.lang.BigInt-test
    (is
     (=
      {:tag :number,
       :all-tags #{:big-int :number :real :scalar},
       :category "scalars",
       :type clojure.lang.BigInt,
       :classname "clojure.lang.BigInt"}
      (tag-map 42N {:skip-dynamic-secondary-tags? true})))))

#?(:jolt nil
   :clj
   (deftest
    java.math.BigInteger-test
    (is
     (=
      {:tag :number,
       :all-tags #{:big-int :number :real :scalar},
       :category "scalars",
       :type java.math.BigInteger,
       :classname "java.math.BigInteger"}
      (tag-map
       (java.math.BigInteger. "42")
       {:skip-dynamic-secondary-tags? true})))))

#?(:clj
   (deftest
    java.math.BigDecimal-test
    (is
     (=
      {:tag :number,
       :all-tags #{:number :real :scalar :big-decimal},
       :category "scalars",
       :type java.math.BigDecimal,
       :classname "java.math.BigDecimal"}
      (tag-map 42M {:skip-dynamic-secondary-tags? true})))))

#?(:clj
   (deftest
    java.lang.String-test
    (is
     (=
      {:tag :string,
       :all-tags #{:seqable :string :scalar :char-sequence},
       :category "scalars",
       :type java.lang.String,
       :classname "java.lang.String"}
      (tag-map "bar" nil)))))

#?(:clj
   (deftest
    java.lang.Character-test
    (is
     (=
      {:tag :char,
       :all-tags #{:scalar :char},
       :category "scalars",
       :type java.lang.Character,
       :classname "java.lang.Character"}
      (tag-map \c nil)))))

#?(:clj
   (deftest
    java.util.regex.Pattern-test
    (is
     (=
      {:tag :regex,
       :all-tags #{:regex},
       :category "scalars",
       :type java.util.regex.Pattern,
       :classname "java.util.regex.Pattern"}
      (tag-map #"^[a-z]+$" nil)))))

#?(:clj
   (deftest
    clojure.lang.Keyword-test
    (is
     (=
      {:tag :keyword,
       :all-tags #{:callable :scalar :keyword :named},
       :category "scalars",
       :type clojure.lang.Keyword,
       :classname "clojure.lang.Keyword"}
      (tag-map :foo nil)))))

#?(:clj
   (deftest
    clojure.lang.Symbol-test
    (is
     (=
      {:tag :symbol,
       :all-tags #{:callable :symbol :scalar :named :carries-meta},
       :category "scalars",
       :type clojure.lang.Symbol,
       :classname "clojure.lang.Symbol"}
      (tag-map (symbol "foo") nil)))))

#?(:clj
   (deftest
    java.util.UUID-test
    (is
     (=
      {:tag :uuid,
       :all-tags #{:uuid},
       :category "scalars",
       :type java.util.UUID,
       :classname "java.util.UUID"}
      (tag-map #uuid "4fe5d828-6444-11e8-8222-720007e40350" nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentArrayMap-test
    (is
     (=
      {:tag :map,
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
       :category "collections",
       :type clojure.lang.PersistentArrayMap,
       :classname "clojure.lang.PersistentArrayMap"}
      (tag-map {:a 1, :b 2} nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentHashMap-test
    (is
     (=
      {:tag :map,
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
       :category "collections",
       :type clojure.lang.PersistentHashMap,
       :classname "clojure.lang.PersistentHashMap"}
      (tag-map (hash-map :a 1 :b 2) nil)))))

#?(:clj
   (deftest
    clojure.lang.LazySeq-test
    (is
     (=
      {:tag :seq,
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
       :category "collections",
       :type clojure.lang.LazySeq,
       :classname "clojure.lang.LazySeq"}
      (tag-map (map inc [1 2 3]) nil)))))

#?(:clj
   (deftest
    clojure.lang.ArraySeq-test
    (is
     (=
      {:tag :seq,
       :all-tags
       #{:seqable
         :sequential
         :coll
         :coll-like
         :seq
         :list-like
         :carries-meta},
       :category "collections",
       :type clojure.lang.ArraySeq,
       :classname "clojure.lang.ArraySeq"}
      (tag-map (seq (into-array [1 2 3])) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentVector$ChunkedSeq-test
    (is
     (=
      {:tag :seq,
       :all-tags
       #{:seqable
         :sequential
         :coll
         :coll-like
         :seq
         :list-like
         :carries-meta},
       :category "collections",
       :type clojure.lang.PersistentVector$ChunkedSeq,
       :classname "clojure.lang.PersistentVector$ChunkedSeq"}
      (tag-map (seq [1 2 3]) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentVector-test
    (is
     (=
      {:tag :vector,
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
       :category "collections",
       :type clojure.lang.PersistentVector,
       :classname "clojure.lang.PersistentVector"}
      (tag-map [1 2 3] nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentHashSet-test
    (is
     (=
      {:tag :set,
       :all-tags
       #{:callable
         :seqable
         :editable
         :coll
         :coll-like
         :set
         :carries-meta
         :set-like},
       :category "collections",
       :type clojure.lang.PersistentHashSet,
       :classname "clojure.lang.PersistentHashSet"}
      (tag-map #{1 3 2} nil)))))

#?(:jolt nil
   :clj
   (deftest
    clojure.lang.APersistentVector$SubVector-test
    (is
     (=
      {:tag :vector,
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
       :category "collections",
       :type clojure.lang.APersistentVector$SubVector,
       :classname "clojure.lang.APersistentVector$SubVector"}
      (tag-map (subvec [1 2 3 4 5] 1 3) nil)))))

#?(:clj
   (deftest
    clojure.lang.Cons-test
    (is
     (=
      {:tag :seq,
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
       :category "collections",
       :type clojure.lang.Cons,
       :classname "clojure.lang.Cons"}
      (tag-map (cons 1 '(2 3)) nil)))))

#?(:clj
   (deftest
    clojure.lang.LongRange-test
    (is
     (=
      {:tag :seq,
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
       :category "collections",
       :type clojure.lang.LongRange,
       :classname "clojure.lang.LongRange"}
      (tag-map (range 3) nil)))))

#?(:clj
   (deftest
    clojure.lang.Range-test
    (is
     (=
      {:tag :seq,
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
       :category "collections",
       :type clojure.lang.Range,
       :classname "clojure.lang.Range"}
      (tag-map (range 0 1 1/5) nil)))))

#?(:jolt nil
   :clj
   (deftest
    clojure.lang.Repeat-test
    (is
     (=
      {:tag :seq,
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
       :category "collections",
       :type clojure.lang.Repeat,
       :classname "clojure.lang.Repeat"}
      (tag-map (repeat 2 "a") nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentList-test
    (is
     (=
      {:tag :list,
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
       :category "collections",
       :type clojure.lang.PersistentList,
       :classname "clojure.lang.PersistentList"}
      (tag-map (list 1 2 3) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentList$EmptyList-test
    (is
     (=
      {:tag :list,
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
       :category "collections",
       :type clojure.lang.PersistentList$EmptyList,
       :classname "clojure.lang.PersistentList$EmptyList"}
      (tag-map (list) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentTreeMap-test
    (is
     (=
      {:tag :map,
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
       :category "collections",
       :type clojure.lang.PersistentTreeMap,
       :classname "clojure.lang.PersistentTreeMap"}
      (tag-map (sorted-map :a 1 :b 2) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentTreeSet-test
    (is
     (=
      {:tag :set,
       :all-tags
       #{:callable
         :seqable
         :coll
         :coll-like
         :sorted
         :set
         :carries-meta
         :set-like},
       :category "collections",
       :type clojure.lang.PersistentTreeSet,
       :classname "clojure.lang.PersistentTreeSet"}
      (tag-map (sorted-set 3 1 2) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentArrayMap$TransientArrayMap-test
    (is
     (=
      {:tag :map,
       :all-tags
       #{:callable :array-map :coll-like :transient :map-like :map},
       :category "collections",
       :type clojure.lang.PersistentArrayMap$TransientArrayMap,
       :classname "clojure.lang.PersistentArrayMap$TransientArrayMap"}
      (tag-map (transient (array-map :a 1 :b 2)) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentHashMap$TransientHashMap-test
    (is
     (=
      {:tag :map,
       :all-tags
       #{:callable :coll-like :hash-map :transient :map-like :map},
       :category "collections",
       :type clojure.lang.PersistentHashMap$TransientHashMap,
       :classname "clojure.lang.PersistentHashMap$TransientHashMap"}
      (tag-map (transient (hash-map :a 1 :b 2)) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentVector$TransientVector-test
    (is
     (=
      {:tag :map,
       :all-tags #{:callable :coll-like :transient :map :list-like},
       :category "collections",
       :type clojure.lang.PersistentVector$TransientVector,
       :classname "clojure.lang.PersistentVector$TransientVector"}
      (tag-map (transient [1 2 3]) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentHashSet$TransientHashSet-test
    (is
     (=
      {:tag :set,
       :all-tags #{:callable :transient :set},
       :category "collections",
       :type clojure.lang.PersistentHashSet$TransientHashSet,
       :classname "clojure.lang.PersistentHashSet$TransientHashSet"}
      (tag-map (transient #{1 3 2}) nil)))))

#?(:clj
   (deftest
    clojure.lang.PersistentQueue-test
    (is
     (=
      {:tag :queue,
       :all-tags
       #{:seqable
         :queue
         :sequential
         :coll
         :coll-like
         :stack
         :list-like
         :carries-meta},
       :category "collections",
       :type clojure.lang.PersistentQueue,
       :classname "clojure.lang.PersistentQueue"}
      (tag-map clojure.lang.PersistentQueue/EMPTY nil)))))

#?(:clj
   (deftest
    clojure.lang.MapEntry-test
    (is
     (=
      {:tag :vector,
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
       :category "collections",
       :type clojure.lang.MapEntry,
       :classname "clojure.lang.MapEntry"}
      (tag-map (first {:a 1}) nil)))))

#?(:clj
   (deftest
    java.util.HashMap-test
    (is
     (=
      {:tag :map,
       :all-tags #{:seqable :coll-like :map-like :map},
       :category "collections",
       :type java.util.HashMap,
       :classname "java.util.HashMap"}
      (tag-map (java.util.HashMap. (hash-map :a 1 :b 2)) nil)))))

#?(:clj
   (deftest
    java.util.ArrayList-test
    (is
     (=
      {:tag :array,
       :all-tags #{:seqable :array :coll-like :list-like},
       :category "collections",
       :type java.util.ArrayList,
       :classname "java.util.ArrayList"}
      (tag-map (java.util.ArrayList. (range 3)) nil)))))

#?(:clj
   (deftest
    java.util.HashSet-test
    (is
     (=
      {:tag :set,
       :all-tags #{:seqable :coll-like :set :set-like},
       :category "collections",
       :type java.util.HashSet,
       :classname "java.util.HashSet"}
      (tag-map (java.util.HashSet. #{1 3 2}) nil)))))

#?(:clj
   (deftest
    java.util.ArrayDeque-test
    (is
     (=
      {:tag :array,
       :all-tags #{:seqable :array :coll-like :list-like},
       :category "collections",
       :type java.util.ArrayDeque,
       :classname "java.util.ArrayDeque"}
      (tag-map (java.util.ArrayDeque. [1 2 3]) nil)))))

#?(:clj
   (deftest
    clojure.lang.MultiFn-test
    (is
     (=
      {:tag :function,
       :all-tags #{:callable :multi-function :function},
       :category "functions",
       :type clojure.lang.MultiFn,
       :classname "clojure.lang.MultiFn"}
      (tag-map
       (do
        (defmulti area :shape)
        (defmethod area :square [m] [m] (* (:side m) (:side m)))
        (defmethod area :circle [m] (* Math/PI (:radius m) (:radius m))))
       nil)))))

#?(:clj
   (deftest
    java.util.Date-test
    (is
     (=
      {:tag :datetime,
       :all-tags #{:inst :datetime},
       :category "temporal values",
       :type java.util.Date,
       :classname "java.util.Date"}
      (tag-map (java.util.Date.) nil)))))

#?(:clj
   (deftest
    clojure.lang.Atom-test
    (is
     (=
      {:tag :atom,
       :all-tags #{:derefable :reference :atom},
       :category "identities",
       :type clojure.lang.Atom,
       :classname "clojure.lang.Atom"}
      (tag-map (atom 1) nil)))))

#?(:clj
   (deftest
    clojure.lang.Agent-test
    (is
     (=
      {:tag :agent,
       :all-tags #{:derefable :agent :reference},
       :category "identities",
       :type clojure.lang.Agent,
       :classname "clojure.lang.Agent"}
      (tag-map (agent 1) nil)))))

#?(:clj
   (deftest
    clojure.lang.Volatile-test
    (is
     (=
      {:tag :volatile,
       :all-tags #{:derefable :volatile},
       :category "identities",
       :type clojure.lang.Volatile,
       :classname "clojure.lang.Volatile"}
      (tag-map (volatile! 1) nil)))))

#?(:clj
   (deftest
    clojure.lang.Ref-test
    (is
     (=
      {:tag :ref,
       :all-tags #{:callable :ref :derefable :reference},
       :category "identities",
       :type clojure.lang.Ref,
       :classname "clojure.lang.Ref"}
      (tag-map (ref 0) nil)))))

#?(:clj
   (deftest
    clojure.lang.Var-test
    (is
     (=
      {:tag :var,
       :all-tags #{:callable :derefable :reference :var},
       :category "identities",
       :type clojure.lang.Var,
       :classname "clojure.lang.Var"}
      (tag-map (do (def my-var 42) #'my-var) nil)))))

#?(:clj
   (deftest
    clojure.lang.Delay-test
    (is
     (=
      {:tag :delay,
       :all-tags #{:derefable :deferred :delay},
       :category "identities",
       :type clojure.lang.Delay,
       :classname "clojure.lang.Delay"}
      (tag-map (delay 42) nil)))))

#?(:clj
   (deftest
    clojure.lang.ReaderConditional-test
    (is
     (=
      {:tag :reader-conditional,
       :all-tags #{:reader-conditional},
       :category "reflection",
       :type clojure.lang.ReaderConditional,
       :classname "clojure.lang.ReaderConditional"}
      (tag-map
       (reader-conditional
        '(:clj (System/getProperty "os.name") :cljs "JS")
        false)
       nil)))))

#?(:clj
   (deftest
    java.time.Instant-test
    (is
     (=
      {:tag :datetime,
       :all-tags #{:inst :datetime},
       :category "temporal values",
       :type java.time.Instant,
       :classname "java.time.Instant"}
      (tag-map (java.time.Instant/now) nil)))))

#?(:clj
   (deftest
    java.time.LocalDate-test
    (is
     (=
      {:tag :datetime,
       :all-tags #{:datetime},
       :category "temporal values",
       :type java.time.LocalDate,
       :classname "java.time.LocalDate"}
      (tag-map (java.time.LocalDate/now) nil)))))

#?(:jolt nil
   :clj
   (deftest
    java.time.ZonedDateTime-test
    (is
     (=
      {:tag :datetime,
       :all-tags #{:datetime},
       :category "temporal values",
       :type java.time.ZonedDateTime,
       :classname "java.time.ZonedDateTime"}
      (tag-map (java.time.ZonedDateTime/now) nil)))))