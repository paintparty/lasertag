;; TODO
;; pregenerate a large hash map with entries of all the entries
;; across the categorical defined maps in this namespace
;; Example 


(ns lasertag.cached
  (:require [clojure.set :as set])
  #?(:cljs (:require-macros [lasertag.cached :refer [tag-maps tag-maps-by-class*]]))
  ;; #?(:clj  (:import (clojure.lang PersistentVector$TransientVector
  ;;                                 PersistentHashSet$TransientHashSet
  ;;                                 PersistentArrayMap$TransientArrayMap
  ;;                                 PersistentHashMap$TransientHashMap)))
  )

(defn ?
  "Debugging macro internal to lib"
  ([x]
   (? nil x))
  ([l x]
   (try (if l
          (println (str " " l "\n") x)
          (println x))
        (catch #?(:cljs js/Object :clj Throwable)
               e
          (println "WARNING [lasertag.core/?] Unable to print value")))
   x))

;; -----------------------------------------------------------------------------

(defn cljc-type [x]
  #?(:clj  (class x)
     :cljs (.-constructor x)))


(def transient-hash-set-class (cljc-type (transient (hash-set :a :b))))
(def transient-hash-map-class (cljc-type (transient (hash-map :a 1))))
(def transient-array-map-class (cljc-type (transient (array-map :a 1))))
(def transient-vector-class (cljc-type (transient (vector :a 1))))
(def subvec-class (cljc-type (subvec [1 2 3 4 5] 1 3)))


;; -----------------------------------------------------------------------------

(defn real-number? [n]
  (and (number? n)
       (not (infinite? n))
       (not (NaN? n))))


(defn whole-number? [n]
  (and (real-number? n)
       (zero? (mod n 1))))


(defn fractional-number? [n]
  (and (real-number? n)
       (not (whole-number? n))))


(defn add-pos-or-neg-tag [m x]
  (cond-> m
    (when (neg? x) :neg)
    (when (pos? x) :pos)))

(defn cljc-array? [x]
  #?(:cljs
     (array? x)
     :clj
     (some-> x class .isArray)))

(defn array-map? [x]
  (or (instance? #?(:cljs cljs.core/PersistentArrayMap :clj clojure.lang.PersistentArrayMap) x)
      (instance? #?(:cljs cljs.core/TransientArrayMap :clj transient-array-map-class) x)))

(defn hash-map? [x]
  (or (instance?  #?(:cljs cljs.core/PersistentHashMap :clj clojure.lang.PersistentHashMap) x)
      (instance?  #?(:cljs cljs.core/TransientHashMap :clj transient-hash-map-class) x)))

(defn hash-set? [x]
  (or (instance?  #?(:cljs cljs.core/PersistentHashSet :clj clojure.lang.PersistentHashSet) x)
      (instance?  #?(:cljs cljs.core/TransientHashSet :clj transient-hash-set-class) x)))


(defn map-like? [v]
  #?(:cljs
     ()
     :clj
     (or (instance? clojure.lang.IPersistentMap v)
         (instance? java.util.Map v)
         (array-map? v)
         (hash-map? v))))

(defn set-like? [v]
  #?(:cljs
     ()
     :clj
     (or (instance? clojure.lang.IPersistentSet v)
         (instance? java.util.Set v))))

(defn queue? [v]
  #?(:cljs
     ()
     :clj
     (or (instance? java.util.Queue v)
         (instance? clojure.lang.PersistentQueue v))))

(defn subvec? [x]
  #?(:clj  (instance? subvec-class x)
     :cljs (instance? cljs.core/Subvec x)))

(defn list-like? [v]
  #?(:cljs
     ()
     :clj
     (or (sequential? v)
         (instance? java.util.List v)
         (instance? transient-vector-class v)
         (cljc-array? v)
         (queue? v))))

(defn coll-like? [v]
  #?(:cljs
     ()
     :clj
     (or (coll? v)
         (seq? v)
         (instance? java.util.Collection v)
         (map-like? v)
         (set-like? v)
         (list-like? v))))


(def eager-seq-classes
  (->> ['(:a)
        (cons :a '(:b))
        (seq [1 2 3])
        (seq "foo")
        (seq {:a 1})]
       (map type)
       set))

(defn eager-seq? [x]
  (contains? eager-seq-classes (type x)))

(defn lazyish-seq? [x]
  (or (instance? #?(:cljs () :clj clojure.lang.LazySeq) x)
      (instance? #?(:cljs () :clj clojure.lang.Range) x)
      (instance? #?(:cljs () :clj clojure.lang.LongRange) x)
      (instance? #?(:cljs () :clj clojure.lang.Repeat) x)
      (instance? #?(:cljs () :clj clojure.lang.Iterate) x)
      (instance? #?(:cljs () :clj clojure.lang.Cons) x)
      (instance? #?(:cljs () :clj clojure.lang.ChunkedCons) x)))

(defn deferred? [x]
  (or #?(:cljs () :clj (delay? x))
      #?(:cljs () :clj (future? x))
      #?(:cljs () :clj (instance? clojure.lang.IPending x))))

(def reference-types
#?(:cljs
   {cljs.core.Atom     :atom
    cljs.core.Volatile :volatile
    cljs.core.Var      :var}
   :clj
   {clojure.lang.Atom     :atom
    clojure.lang.Volatile :volatile
    clojure.lang.Agent    :agent
    clojure.lang.Ref      :ref
    clojure.lang.Var      :var}))

(defn reference-type? [x]
  (boolean (get reference-types (cljc-type x))))

(defn carries-meta? [x]
  #?(:clj  (instance? clojure.lang.IObj x)
     :cljs (satisfies? IWithMeta x)))

(defn callable? [v]
  (or (ifn? v)
      #?(:clj
         (or (instance? java.util.concurrent.Callable v)
             (instance? Runnable v)))))

(defn char-sequence? [x]
  #?(:cljs
     false
     :clj
     (instance? java.lang.CharSequence x)))

(defn big-decimal? [x]
  #?(:cljs
     false
     :clj
     (instance? java.math.BigDecimal x)))

(defn short? [x]
  #?(:cljs
     false
     :clj
     (instance? java.lang.Short x)))

(defn long? [x]
  #?(:cljs
     false
     :clj
     (instance? java.lang.Long x)))

(defn big-int? [x]
  #?(:cljs
     false
     :clj
     (or (instance? java.math.BigInteger x)
         (instance? clojure.lang.BigInt x))))

(defn cljc-map-entry? [x]
  #?(:cljs
     false
     :clj
     (or (map-entry? x)
         (instance? clojure.lang.MapEntry x))))

(defn cljc-ratio? [x]
  #?(:cljs
     false
     :clj
     (ratio? x)))

;; (defn sorted? [x]
;;   #?(:clj  (instance? clojure.lang.Sorted x)
;;      :cljs (satisfies? cljs.core/ISorted x)))

(defn editable? [x]
  (instance?  #?(:cljs cljs.core/IEditableCollection :clj clojure.lang.IEditableCollection) x))

(defn temporal? [x]
  (or (inst? x)
      #?(:clj (instance? java.time.temporal.TemporalAccessor x))))

(defn transient? [v]
  #?(:clj  (instance? clojure.lang.ITransientCollection v)
     :cljs (satisfies? cljs.core/ITransientCollection v)))

(defn stack? [x]
  (instance?  #?(:cljs cljs.core/IStack :clj clojure.lang.IPersistentStack) x))

(defn cons? [x]
  #?(:clj  (instance? clojure.lang.Cons x)
     :cljs (instance? cljs.core/Cons x)))

(defn range? [x]
  #?(:clj  (or (instance? clojure.lang.LongRange x)
               (instance? clojure.lang.Range x))
     :cljs (instance? cljs.core/Range x)))

(defn named? [x]
  #?(:clj  (instance? clojure.lang.Named x)
     :cljs (satisfies? cljs.core/INamed x)))

(defn scalar? [x]
  (or (number? x)
      (nil? x)
      (string? x)
      (boolean? x)
      (char? x)
      (keyword? x)
      (symbol? x)))

;;                                                                                     
;;                                                                                     
;;TTTTTTTTTTTTTTTTTTTTTTT         AAA                  GGGGGGGGGGGGG   SSSSSSSSSSSSSSS 
;;T:::::::::::::::::::::T        A:::A              GGG::::::::::::G SS:::::::::::::::S
;;T:::::::::::::::::::::T       A:::::A           GG:::::::::::::::GS:::::SSSSSS::::::S
;;T:::::TT:::::::TT:::::T      A:::::::A         G:::::GGGGGGGG::::GS:::::S     SSSSSSS
;;TTTTTT  T:::::T  TTTTTT     A:::::::::A       G:::::G       GGGGGGS:::::S            
;;        T:::::T            A:::::A:::::A     G:::::G              S:::::S            
;;        T:::::T           A:::::A A:::::A    G:::::G               S::::SSSS         
;;        T:::::T          A:::::A   A:::::A   G:::::G    GGGGGGGGGG  SS::::::SSSSS    
;;        T:::::T         A:::::A     A:::::A  G:::::G    G::::::::G    SSS::::::::SS  
;;        T:::::T        A:::::AAAAAAAAA:::::A G:::::G    GGGGG::::G       SSSSSS::::S 
;;        T:::::T       A:::::::::::::::::::::AG:::::G        G::::G            S:::::S
;;        T:::::T      A:::::AAAAAAAAAAAAA:::::AG:::::G       G::::G            S:::::S
;;      TT:::::::TT   A:::::A             A:::::AG:::::GGGGGGGG::::GSSSSSSS     S:::::S
;;      T:::::::::T  A:::::A               A:::::AGG:::::::::::::::GS::::::SSSSSS:::::S
;;      T:::::::::T A:::::A                 A:::::A GGG::::::GGG:::GS:::::::::::::::SS 
;;      TTTTTTTTTTTAAAAAAA                   AAAAAAA   GGGGGG   GGGG SSSSSSSSSSSSSSS   
;;                                                                                     
;;                                                                                     
;;                                                                                     
;;                                                                                     
;;                                                                                     
;;                                                                                     
;;                                                                                      

(defn add-tags!* [x vol f & tags]
  (when (f x)
    (vswap! vol set/union (into #{} tags))))

(defn all-tags-rt* 
  "All tags, for values at runtime."
  [x]
  (let [vol             (volatile! #{})
        add-tags!       (partial add-tags!* x vol)
        is-scalar?      (scalar? x)
        is-real-number? (real-number? x)]
    (when is-real-number?
      (add-tags! zero? :zero)
      (add-tags! whole-number? :whole)
      (add-tags! fractional-number? :fractional)
      (add-tags! nat-int? :nat-int)
      (add-tags! neg? :neg)
      (add-tags! neg-int? :neg-int)
      (add-tags! pos? :pos)
      (add-tags! pos-int? :pos-int) 
      (add-tags! big-int? :big-int))
    (when-not is-scalar?
      (add-tags! record? :record))
    @vol))

(defn number-tags
  "All secondary number tags, for values at runtime."
  [x]
  (let [vol             (volatile! #{})
        add-tags!       (partial add-tags!* x vol)
        is-scalar?      (scalar? x)
        is-real-number? (real-number? x)]
    (when is-real-number?
      (add-tags! zero? :zero)
      (add-tags! whole-number? :whole)
      (add-tags! fractional-number? :fractional)
      (add-tags! nat-int? :nat-int)
      (add-tags! neg? :neg)
      (add-tags! neg-int? :neg-int)
      (add-tags! pos? :pos)
      (add-tags! pos-int? :pos-int) 
      (add-tags! big-int? :big-int))
    @vol))


(defn all-tags*
  "All tags, for values at macroexansion."
  ([x]
   (all-tags* x nil))
  ([x {:keys [runtime?]}]
   (let [vol        (volatile! #{})
         add-tags!  (partial add-tags!* x vol)
         is-scalar? (scalar? x)
         is-real-number? (real-number? x)]
     (add-tags! scalar? :scalar)
     (add-tags! callable? :callable)
     (add-tags! seqable? :seqable)
     (add-tags! char-sequence? :char-sequence)
     (add-tags! carries-meta? :carries-meta)
     (add-tags! named? :named)

     (when is-real-number?
       (vswap! vol conj :real)
       (add-tags! cljc-ratio?  :ratio)
       (add-tags! big-decimal?  :big-decimal)
       (add-tags! short?  :short)
       (add-tags! double?  :double)
       (add-tags! long?  :long)
       (add-tags! float?  :float)
       (add-tags! int?  :int)
       (add-tags! big-int?  :big-int))

     (when-not is-scalar?
       (add-tags! coll? :coll)
       (add-tags! coll? :coll)
       (add-tags! seq? :seq)
       (add-tags! lazyish-seq? :lazy :deferred)
       (add-tags! deferred? :deferred)
       (add-tags! reference-type? :reference)
       (add-tags! inst? :inst)
       (add-tags! transient? :transient)
       (add-tags! editable? :editable)
       (add-tags! sorted? :sorted)
       (add-tags! stack? :stack)
       (add-tags! cons? :cons)
       (add-tags! range? :range)
       (add-tags! subvec? :subvec)
       (add-tags! coll-like? :coll-like)
       (add-tags! associative? :associative)
       (add-tags! sequential? :sequential)
       (add-tags! cljc-map-entry? :map-entry)
       (add-tags! array-map? :array-map)
       (add-tags! hash-map? :hash-map)
       (add-tags! map-like? :map-like)
       (add-tags! set-like? :set-like)
       (add-tags! list-like? :list-like)
       (add-tags! coll-like? :coll-like))

     #_(when runtime? 
         (add-tags! runtime? :record))
     (when (subvec? x)
       (? :VOL @vol))
     @vol)))


;;                                                                 
;;                                                                 
;;         CCCCCCCCCCCCCLLLLLLLLLLL                SSSSSSSSSSSSSSS 
;;      CCC::::::::::::CL:::::::::L              SS:::::::::::::::S
;;    CC:::::::::::::::CL:::::::::L             S:::::SSSSSS::::::S
;;   C:::::CCCCCCCC::::CLL:::::::LL             S:::::S     SSSSSSS
;;  C:::::C       CCCCCC  L:::::L               S:::::S            
;; C:::::C                L:::::L               S:::::S            
;; C:::::C                L:::::L                S::::SSSS         
;; C:::::C                L:::::L                 SS::::::SSSSS    
;; C:::::C                L:::::L                   SSS::::::::SS  
;; C:::::C                L:::::L                      SSSSSS::::S 
;; C:::::C                L:::::L                           S:::::S
;;  C:::::C       CCCCCC  L:::::L         LLLLLL            S:::::S
;;   C:::::CCCCCCCC::::CLL:::::::LLLLLLLLL:::::LSSSSSSS     S:::::S
;;    CC:::::::::::::::CL::::::::::::::::::::::LS::::::SSSSSS:::::S
;;      CCC::::::::::::CL::::::::::::::::::::::LS:::::::::::::::SS 
;;         CCCCCCCCCCCCCLLLLLLLLLLLLLLLLLLLLLLLL SSSSSSSSSSSSSSS   
;;                                                                 
;;                                                                 
;;                                                                

;; We need to define non-finite numbers seperately
(def NaN
  #?(:cljs
     {:tag       :nan
      :type      js/Number
      :all-tags  #{:number :scalar}
      :classname "js/Number"}
     :clj
     {:tag       :nan
      :type      java.lang.Double
      :all-tags  #{:number :double :scalar}
      :classname "java.lang.Double"}))


(def non-finite-numbers-by-class
  (let [all-tags
        (set/union #{:number :scalar :non-finite}
                   #?(:clj #{:double} :cljs #{}))
        type      #?(:cljs js/Number :clj java.lang.Double)
        classname #?(:cljs "js/Number" :clj "java.lang.Double")
        tag       :number]
    {##Inf
     {:type     type
      :tag      tag
      :classname classname
      :all-tags (set/union all-tags #{:infinity :infinite})}
     ##-Inf
     {:type     type
      :tag      tag
      :classname classname
      :all-tags (set/union all-tags #{:-infinity :infinite})}
     ##NaN
     {:type     type
      :tag      tag
      :classname classname
      :all-tags (set/union all-tags #{:nan})}}))

#?(:clj
   (defmacro by-class* [m]
     (reduce-kv (fn [m [cls tag] x]
                  (let [x         (eval x)
                        all-tags  (conj (all-tags* x) tag)]
                    (assoc m
                           cls
                           {:tag       tag
                            :type      cls
                            :all-tags  all-tags
                            :classname (str cls)})))
                {}
                m)))

(def by-class
  ;; TODO in all*
  ;; Nix the categories, just section off with comments or prioritize with metadata 
  ;; Maybe create a single freqs that would be array map
  #?(:cljs
     {js/Number                    {:tag       :number
                                    :type      js/Number
                                    :all-tags  #{:number :scalar}
                                    :classname "js/Number"}
      cljs.core/Keyword            {:tag       :keyword
                                    :type      cljs.core/Keyword
                                    :all-tags  #{:keyword :scalar}
                                    :classname "cljs.core/Keyword"}
      cljs.core/LazySeq            {:tag       :seq
                                    :type      cljs.core/LazySeq
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/LazySeq"}
      cljs.core/List               {:tag       :seq
                                    :type      cljs.core/List
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :list
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/List"}
      cljs.core/PersistentArrayMap {:tag       :map
                                    :type      cljs.core/PersistentArrayMap
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :array-map
                                                 :coll-like
                                                 :map-like
                                                 :map
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentArrayMap"}
      cljs.core/PersistentHashMap  {:tag       :map
                                    :type      cljs.core/PersistentHashMap
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :hash-map
                                                 :coll-like
                                                 :map-like
                                                 :map
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentHashMap"}
      cljs.core/PersistentHashSet  {:tag       :set
                                    :type      cljs.core/PersistentHashSet
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :set-map
                                                 :coll-like
                                                 :set-like
                                                 :set
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentHashMap"}
      cljs.core/PersistentVector   {:tag       :vector
                                    :type      cljs.core/PersistentVector
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-like
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentVector"}
      cljs.core/Symbol             {:tag       :symbol
                                    :type      cljs.core/Symbol
                                    :all-tags  #{:symbol :carries-meta :scalar}
                                    :classname "cljs.core/Symbol"}
      js/Boolean                   {:tag       :boolean
                                    :type      js/Boolean
                                    :all-tags  #{:boolean :scalar}
                                    :classname "js/Boolean"}
      js/Date                      {:tag       :inst
                                    :type      js/Date
                                    :all-tags  #{:inst :temporal}
                                    :classname "Date"}
      js/String                    {:tag       :string
                                    :type      js/String
                                    :all-tags  #{:iterable :string :scalar}
                                    :classname "js/String"}
      cljs.core/Atom               {:tag       :atom
                                    :type      cljs.core/Atom
                                    :all-tags  #{:atom
                                                 :reference}
                                    :classname "cljs.core/Atom"}
      cljs.core/Cons               {:tag       :seq
                                    :type      cljs.core/Cons
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/Cons"}
      cljs.core/IntegerRange       {:tag       :seq
                                    :type      cljs.core/IntegerRange
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/IntegerRange"}
      cljs.core/PersistentQueue    {:tag       :seq
                                    :type      cljs.core/PersistentQueue
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentQueue"}
      cljs.core/Repeat             {:tag       :seq
                                    :type      cljs.core/Repeat
                                    :all-tags  #{:seqable
                                                 :sequential
                                                 :coll
                                                 :deferred
                                                 :coll-like
                                                 :lazy
                                                 :seq
                                                 :list-like
                                                 :carries-meta}
                                    :classname "cljs.core/Repeat"}
      cljs.core/Subvec             {:tag       :vector
                                    :type      cljs.core/Subvec
                                    :all-tags  #{:callable
                                                 :seqable
                                                 :sequential
                                                 :associative
                                                 :coll
                                                 :subvec
                                                 :coll-like
                                                 :stack
                                                 :list-like
                                                 :carries-meta}
                                    :classname "cljs.core/Subvec"}
      cljs.core/TransientVector    {:tag       :vector
                                    :type      cljs.core/TransientVector
                                    :all-tags  #{:callable
                                                 :coll-like
                                                 :transient
                                                 :map
                                                 :list-like}
                                    :classname "cljs.core/TransientVector"}}
     :clj
     (by-class*
      {;; numbers
       [java.lang.Long :number]                                 21
       [java.lang.Double :number]                               21.42
       [java.lang.Short :number]                                (short 21)
       [clojure.lang.Ratio :number]                             2/3
       [java.lang.Float :number]                                (float 21.42)
       [java.lang.Integer :number]                              (int 21)
       [clojure.lang.BigInt :number]                            21N
       [java.math.BigInteger :number]                           21N
       [java.math.BigDecimal :number]                           21M

       ;; scalars
       [clojure.lang.Keyword :keyword]                          :foo
       [java.lang.String :string]                               "foo"
       [clojure.lang.Symbol :symbol]                            (symbol "foo")
       [java.lang.Boolean :boolean]                             true
       [nil :nil]                                               nil
       [java.lang.Character :char]                              \a

       ;; literal types
       [java.util.UUID :uuid]                                   #uuid "4fe5d828-6444-11e8-8222-720007e40350"
       [java.util.regex.Pattern :regex]                         #"^abc$"

       ;; data-structures
       [clojure.lang.PersistentArrayMap :map]                   {:a 1}
       [clojure.lang.PersistentHashMap :map]                    (hash-map :a 1)
       [clojure.lang.LazySeq :seq]                              (map inc [1 2 3])
       [clojure.lang.PersistentVector :vector]                  [1 2 3]
       [clojure.lang.PersistentHashSet :set]                    #{1 2 3}
       [clojure.lang.APersistentVector$SubVector :vector]       (subvec [1 2 3 4 5] 1 3)
       [clojure.lang.Cons :seq]                                 (cons 1 '(2 3))
       [clojure.lang.LongRange :seq]                            (range 3)
       [clojure.lang.Range :seq]                                (range 3)
       [clojure.lang.Repeat :seq]                               (repeat 2 "a")

       [clojure.lang.PersistentList :list]                      (list 1 2 3)
       [clojure.lang.PersistentTreeMap :map]                    (sorted-map :a 1 :b 2)
       [clojure.lang.PersistentTreeSet :set]                    (sorted-set 3 1 2)
       [clojure.lang.PersistentArrayMap$TransientArrayMap :map] (transient (array-map 1 2 3 4))
       [clojure.lang.PersistentHashMap$TransientHashMap :map]   (transient (hash-map 1 2 3 4))
       [clojure.lang.PersistentVector$TransientVector :map]     (transient [1 2 3])
       [clojure.lang.PersistentHashSet$TransientHashSet :set]   (transient #{1 2 3})
       [clojure.lang.PersistentList$EmptyList :list]            (list)
       [clojure.lang.PersistentQueue :queue]                    clojure.lang.PersistentQueue/EMPTY
       [clojure.lang.PersistentStructMap :map]                  (do (defstruct food :name :color) (struct food "strawberry" "red"))
       [clojure.lang.MapEntry :vector]                          (-> {:a 1} first)

       [java.util.HashMap :map]                                 (java.util.HashMap. (hash-map "a" 1 "b" 2))
       [java.util.ArrayList :array]                             (java.util.ArrayList. (range 6))
       [java.util.HashSet :set]                                 (java.util.HashSet. #{"a" 1 "b" 2})

       ;; temporal
       [java.util.Date :temporal]                               (java.util.Date.)
       [java.time.Instant :temporal]                            (java.time.Instant/now)
       [java.time.LocalDate :temporal]                          (java.time.LocalDate/now)
       [java.time.ZonedDateTime :temporal]                      (java.time.ZonedDateTime/now)
       [java.sql.Timestamp :temporal]                           (java.sql.Timestamp. (System/currentTimeMillis))

       ;; reference types
       [clojure.lang.Volatile :volatile]                        (volatile! 1)
       [clojure.lang.Atom :atom]                                (atom :foo)
       [clojure.lang.Agent :agent]                              (agent :foo)
       [clojure.lang.Ref :ref]                                  (ref 0)
       [clojure.lang.Var :var]                                  (do (def my-var 42) #'my-var)

       ;; reflection
       [clojure.lang.ReaderConditional :reader-conditional]     (reader-conditional '(:clj (System/getProperty "os.name") :cljs "JS") false) 
       })))


(def by-number-class
  (select-keys by-class
               #?(:cljs
                  [js/Number]
                  :clj
                  [java.lang.Long
                   java.lang.Double
                   java.lang.Short
                   clojure.lang.Ratio
                   java.lang.Float
                   java.lang.Integer
                   clojure.lang.BigInt
                   java.math.BigInteger
                   java.math.BigDecimal])))


(def by-priority-class
  (select-keys by-class
               #?(:cljs
                  [cljs.core/Keyword            
                   js/String            
                   js/Boolean 
                   cljs.core/LazySeq  
                   cljs.core/PersistentVector  
                   cljs.core/PersistentArrayMap   
                   cljs.core/PersistentHashMap                   
                   cljs.core/PersistentHashSet]
                  :clj
                  [clojure.lang.Keyword
                   java.lang.String
                   java.lang.Boolean
                   clojure.lang.LazySeq
                   clojure.lang.PersistentVector
                   clojure.lang.PersistentArrayMap
                   clojure.lang.PersistentHashMap
                   clojure.lang.PersistentHashSet])))




