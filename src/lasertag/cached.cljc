;; TODO
;; pregenerate a large hash map with entries of all the entries
;; across the categorical defined maps in this namespace
;; Example 


(ns lasertag.cached
  (:require [clojure.set :as set])
  #?(:cljs (:require-macros [lasertag.cached :refer [tag-maps]])))

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

#?(:clj
   (defmacro ^:private tag-maps
     "Hydrates sugared vector of vectors syntax into a map of class / maps.
      First arg is a set of shared tags.
      Contructs :all-tags entry with shared-tags, tag, and secondary-tags.

      Example:
      (tag-maps
       #{scalar}          ; <- shared-tags
       [[
         java.lang.Long   ; <- class
         :number          ; <- tag that Lasertag assigns
         #{:int :long}    ; <- secondary tags that Lasertag assigns
        ]

       [java.lang.Double
        :number
        #{:double}]])
      =>
      {java.lang.Long       {:tag       :number
                             :type      java.lang.Long
                             :all-tags  #{:long :int :number :scalar}
                             :classname \"java.lang.Long\"}
      java.lang.Double     {:tag       :number
                            :type      java.lang.Double
                            :all-tags  #{:double :number :scalar}
                            :classname \"java.lang.Double\"}"
     [shared-tags vc]
     (reduce-kv (fn [m _ [class-sym tag all-tags]]
                  (assoc m
                         class-sym
                         {:tag       tag
                          :type      class-sym
                          :all-tags  (list 'set/union
                                           shared-tags
                                           (list 'conj all-tags tag))
                          :classname (str class-sym)}))
                {}
                vc)))

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


(def infs
  (let [all-tags
        (set/union #{:infinite :number :scalar}
                   #?(:clj #{:double} :cljs #{}))
        type      #?(:cljs js/Number :clj java.lang.Double)
        classname #?(:cljs "js/Number" :clj "java.lang.Double")
        tag       :number]
    {##Inf
     {:type     type
      :tag      tag
      :classname classname
      :all-tags (set/union all-tags #{:infinity})}
     ##-Inf
     {:type     type
      :tag      tag
      :classname classname
      :all-tags (set/union all-tags #{:-infinity})}}))


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

(defn map-like? [v]
  #?(:cljs
     ()
     :clj
     (or (instance? clojure.lang.IPersistentMap v)
         (instance? java.util.Map v))))

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

(defn list-like? [v]
  #?(:cljs
     ()
     :clj
     (or (sequential? v)
         (instance? java.util.List v)
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

(defn carries-meta? [x]
  #?(:clj  (instance? clojure.lang.IObj x)
     :cljs (satisfies? IWithMeta x)))

(defn callable? [v]
  #?(:cljs
     ()
     :clj
     (do (when (keyword? v)
           (? v (ifn? v)))
         (or (ifn? v)
             (instance? java.util.concurrent.Callable v)
             (instance? Runnable v)))))

(defn all-tags* [{:keys [x tag tags]}]
  (let [scalar? (or (number? x)
                    (nil? x)
                    (string? x)
                    (boolean? x)
                    (char? x)
                    (keyword? x)
                    (symbol? x))]

    ;; TODO in all-tags*
    ;; Make everything into function e.g.  short?  big-int?
    ;; set a volatile to hold tags
    ;; (defn add-tags [vol f tags] (if (f x) (vswap! vol set/union (if (set? tags) tags #{tags}))))
    ;; (when (number? x)
    ;;   (when (and (number? x) (infinite? x))
    ;;     (add-tags! x neg? #{:neg :-infinity :infinite})
    ;;     (add-tags! x pos? #{:pos :infinity :infinite})
    ;;  )))
    
    (into #{}
          (remove
           nil?
           (concat
            [(when scalar? :scalar)
             (when (callable? x) :callable)
             (when (seqable? x) :seqable)
             #?(:clj (when (instance? java.lang.CharSequence x) :char-sequence))
             (when (carries-meta? x) :carries-meta)]

            (when (and (number? x) (infinite? x))
              (concat
               (when (neg? x) [:neg :-infinity :infinite])
               (when (pos? x) [:pos :infinity :infinite])))

            (when (and (number? x) (NaN? x))
              [:nan])

            (when (real-number? x)
              (concat
               [:real
                (when (zero? x) :zero)]
               #?(:clj
                  [(when (ratio? x) :ratio)
                   (when (or (instance? java.math.BigDecimal x)) :big-decimal)
                   (when (instance? java.lang.Short x) :short)
                   (when (double? x) :double)
                   (when (instance? java.lang.Long x) :long)
                   (when (float? x) :float)
                   (when (int? x) :int)
                   (when (or (instance? java.math.BigInteger x) (instance? clojure.lang.BigInt x)) :big-int)])))

            (when-not scalar?
              (concat
               (when (coll? x)
                 [:coll])

               (when (seq? x)
                 [:seq])

               (when (lazyish-seq? x)
                 [:lazy :deferred])

               [(when (deferred? x) :deferred)
                (when (or (coll? x) (seq? x)) :coll-like)
                (when (associative? x) :associative)
                (when (instance?  #?(:cljs cljs.core/PersistentArrayMap :clj clojure.lang.PersistentArrayMap) x) :array-map)
                (when (instance?  #?(:cljs cljs.core/PersistentHashMap :clj clojure.lang.PersistentHashMap) x) :hash-map)
                (when (map-like? x) :map-like)
                (when (set-like? x) :set-like)
                (when (list-like? x) :list-like)
                (when (coll-like? x) :coll-like)]))))))

  #?(:clj
     (def all*

       ;; TODO in all*
       ;; Nix the categories, just section off with comments or prioritize with metadata 
       ;; Maybe create a single freqs that would be array map
       {"nums"  {[java.lang.Long :number]       [21 #{:int :long}]
                 [java.lang.Double :number]     [21.42 #{:double}]
                 [java.lang.Short :number]      [(short 21) #{:int :short}]
                 [clojure.lang.Ratio :number]   [2/3 #{:ratio}]
                 [java.lang.Float :number]      [(float 21.42) #{:float}]
                 [java.lang.Integer :number]    [(int 21) #{:int}]
                 [clojure.lang.BigInt :number]  [21N #{:int}]
                 [java.math.BigInteger :number] [21N #{:int}]
                 [java.math.BigDecimal :number] [21M #{:big-decimal}]}
        "freqs" {[clojure.lang.Keyword :keyword]         [:foo #{}]
                 [java.lang.String :string]              ["foo" #{:string :scalar :char-sequence}]
                 [clojure.lang.PersistentArrayMap :map]  [{:a 1} #{:coll :array-map :coll-like :map-like :map}]
                 [clojure.lang.PersistentHashMap :map]   [(hash-map :a 1) #{:coll :hash-map :coll-like :map-like :map}]
                 [clojure.lang.LazySeq :seq]             [(map inc [1 2 3]) #{:iterable :coll :coll-like :seq :lazy :deferred :carries-meta}]
                 [clojure.lang.PersistentVector :vector] [[1 2 3] #{:coll :vector :coll-like}]
                 [clojure.lang.PersistentHashSet :set]   [#{1 2 3} #{:coll :set :coll-like :set-like}]}})))


(defmacro all []
  (? (concat
      (list 'do)
      (reduce-kv
       (fn [vc category m]
         (conj vc
               (list
                'def
                (symbol category)
                (reduce-kv (fn [m [cls tag] [x tags]]
                             (let [all-tags  (conj (all-tags* {:x    x
                                                               :tag  tag
                                                               :tags tags})
                                                   tag)]
                               (assoc m
                                      cls
                                      {:tag       tag
                                       :type      cls
                                       :all-tags  all-tags
                                       :classname (str cls)})))
                           {}
                           m))))
       []
       all*))))

#?(:cljs
   ()
   :clj
   (all))


#?(:cljs
   {js/Number {:tag       :number
               :type      js/Number
               :all-tags  #{:number :scalar}
               :classname "js/Number"}}

   :clj
   (tag-maps #{:scalar}
             [[java.lang.Long
               :number
               #{:int :long}]

              [java.lang.Double
               :number
               #{:double}]

              [java.lang.Short
               :number
               #{:int :short}]

              [clojure.lang.Ratio
               :number
               #{:ratio}]

              [java.lang.Float
               :number
               #{:float}]

              [java.lang.Integer
               :number
               #{:int}]

              [java.math.BigInteger
               :number
               #{:int :big-integer}]

              [java.math.BigDecimal
               :number
               #{:big-decimal}]]))



(def numbers
  #?(:cljs
     {js/Number {:tag       :number
                 :type      js/Number
                 :all-tags  #{:number :scalar}
                 :classname "js/Number"}}

     :clj
     (tag-maps #{:scalar}
               [[java.lang.Long
                 :number
                 #{:int :long}]

                [java.lang.Double
                 :number
                 #{:double}]

                [java.lang.Short
                 :number
                 #{:int :short}]

                [clojure.lang.Ratio
                 :number
                 #{:ratio}]

                [java.lang.Float
                 :number
                 #{:float}]

                [java.lang.Integer
                 :number
                 #{:int}]

                [java.math.BigInteger
                 :number
                 #{:int :big-integer}]

                [java.math.BigDecimal
                 :number
                 #{:big-decimal}]])))


(def by-type-frequent
  (tag-maps #{}
            [[#?(:cljs cljs.core/Keyword :clj clojure.lang.Keyword)
              :keyword
              #{:scalar}]

             [#?(:cljs js/String :clj java.lang.String)
              :string
              (set/union #{:string :scalar}  #?(:cljs #{} :clj #{:char-sequence}))]

             [#?(:cljs cljs.core/PersistentArrayMap :clj clojure.lang.PersistentArrayMap)
              :map
              #{:coll
                :array-map
                :coll-like
                :map-like
                :map}]

             [#?(:cljs cljs.core/PersistentHashMap :clj clojure.lang.PersistentHashMap)
              :map
              #{:coll
                :hash-map
                :coll-like
                :map-like
                :map}]

             [#?(:cljs cljs.core/PersistentVector :clj clojure.lang.PersistentVector)
              :vector
              #{:coll
                :vector
                :coll-like}]]))


(def by-type-common
  #?(:cljs
     {cljs.core/LazySeq            {:tag       :seq
                                    :type      cljs.core/LazySeq
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/LazySeq"}
      cljs.core/List               {:tag       :list
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
                                    :all-tags  #{:inst :literal}
                                    :classname "Date"}}

     :clj
     {clojure.lang.Keyword            {:tag       :keyword
                                       :type      clojure.lang.Keyword
                                       :all-tags  #{:keyword :scalar}
                                       :classname "clojure.lang.Keyword"}


      clojure.lang.Symbol             {:tag       :symbol
                                       :type      clojure.lang.Symbol
                                       :all-tags  #{:symbol :scalar :carries-meta}
                                       :classname "clojure.lang.Symbol"}
      java.lang.String                {:tag       :string
                                       :type      java.lang.String
                                       :all-tags  #{:string :scalar :char-sequence}
                                       :classname "java.lang.String"}

      nil                                {:tag       :nil
                                          :type      nil
                                          :all-tags  #{:nil}
                                          :classname nil}

      java.lang.Boolean               {:tag       :boolean
                                       :type      java.lang.Boolean
                                       :all-tags  #{:boolean :scalar}
                                       :classname "java.lang.Boolean"}

      clojure.lang.PersistentArrayMap {:tag       :map
                                       :type      clojure.lang.PersistentArrayMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :array-map
                                                    :coll-like
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentArrayMap"}

      clojure.lang.PersistentVector   {:tag       :vector
                                       :type      clojure.lang.PersistentVector
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :vector
                                                    :coll-like
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentVector"}
      clojure.lang.PersistentHashMap  {:tag       :map
                                       :type      clojure.lang.PersistentHashMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-like
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentHashMap"}

      clojure.lang.LazySeq            {:tag       :seq
                                       :type      clojure.lang.LazySeq
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-like
                                                    :seq
                                                    :lazy
                                                    :deferred
                                                    :carries-meta}
                                       :classname "clojure.lang.LazySeq"}}))


(def by-type
  #?(:cljs
     {cljs.core/Atom               {:tag       :atom
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
                                    :all-tags  #{:coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/Repeat"}
      cljs.core/Subvec             {:tag       :vector
                                    :type      cljs.core/Subvec
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-like
                                                 :carries-meta}
                                    :classname "cljs.core/Subvec"}
      cljs.core/TransientVector    {:tag       :vector
                                    :type      cljs.core/TransientVector
                                    :all-tags  #{:coll
                                                 :vector
                                                 :transient
                                                 :coll-like}
                                    :classname "cljs.core/TransientVector"}}
     :clj
     {clojure.lang.Atom              {:tag       :atom
                                      :type      clojure.lang.Atom
                                      :all-tags  #{:atom :reference}
                                      :classname "clojure.lang.Atom"}

      clojure.lang.Cons              {:tag       :seq
                                      :type      clojure.lang.Cons
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.Cons"}



      clojure.lang.LongRange         {:tag       :seq
                                      :type      clojure.lang.LongRange
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :lazy
                                                   :deferred
                                                   :carries-meta}
                                      :classname "clojure.lang.LongRange"}


      clojure.lang.PersistentHashSet {:tag       :set
                                      :type      clojure.lang.PersistentHashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentHashSet"}

      clojure.lang.PersistentList    {:tag       :list
                                      :type      clojure.lang.PersistentList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :list
                                                   :coll-like
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentList"}

      clojure.lang.PersistentTreeMap {:tag       :map
                                      :type      clojure.lang.PersistentTreeMap
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :map-like
                                                   :map
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentTreeMap"}

      clojure.lang.PersistentTreeSet {:tag       :set
                                      :type      clojure.lang.PersistentTreeSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentTreeSet"}


      clojure.lang.Repeat            {:tag       :seq
                                      :type      clojure.lang.Repeat
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :lazy
                                                   :deferred
                                                   :carries-meta}
                                      :classname "clojure.lang.Repeat"}

      clojure.lang.Volatile          {:tag       :volatile
                                      :type      clojure.lang.Volatile
                                      :all-tags  #{:volatile :reference}
                                      :classname "clojure.lang.Volatile"}

      java.lang.Character            {:tag       :char
                                      :type      java.lang.Character
                                      :all-tags  #{:char :scalar}
                                      :classname "java.lang.Character"}


      java.util.Date                 {:tag       :inst
                                      :type      java.util.Date
                                      :all-tags  #{:inst :literal}
                                      :classname "java.util.Date"}

      java.util.HashMap              {:tag       :map
                                      :type      java.util.HashMap
                                      :all-tags  #{:coll
                                                   :coll-like
                                                   :map-like
                                                   :map}
                                      :classname "java.util.HashMap"}


      java.util.ArrayList            {:tag       :array
                                      :type      java.util.ArrayList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :array
                                                   :coll-like}
                                      :classname "java.util.ArrayList"}


      java.util.HashSet              {:tag       :set
                                      :type      java.util.HashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :set-like}
                                      :classname "java.util.HashSet"}}))




