(ns lasertag.cached
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [lasertag.macros :refer [?]]
            #?(:clj [clojure.pprint])
            #?(:cljs [lasertag.jsi.native :as jsi.native])
            #?(:cljs [lasertag.jsi.classes])
            [lasertag.messaging :as messaging]))


;; -----------------------------------------------------------------------------
(def ^:private write-tests? 
  #?(:cljs
     false
     :clj
     (= "true" (System/getenv "LASERTAG_WRITE_TESTS"))))

(def ^:private greenlit-tests
  "For only writing specific tests during dev."
  #{}
  #_#{"java.util.HashSet"})

(def gen-test-ns-name "core-test")

(def gen-test-path (str "./test/lasertag/" 
                        (str/replace gen-test-ns-name #"-" "_")
                        ".cljc"))

;; Canonical categorization ----------------------------------------------------

(def canonical-categories 
  {"scalars"         #{:keyword :number :string :symbol :boolean :nil :char :uuid :regex}
   "collections"     #{:seq :map :vector :set :list :array :queue}
   "functions"       #{:function}
   "temporal values" #{:datetime}
   "identities"      #{:volatile :atom :agent :ref :var :delay}
   "pending values"  #{:promise :future}
   "throwables"      #{:throwable}
   "reflection"      #{:reader-conditional}})

(def canonical-category-by-primary-tag
  (reduce-kv
   (fn [m category-name primary-tags]
     (reduce
      (fn [m primary-tag]
        (assoc m primary-tag category-name))
      m
      primary-tags))
   {}
   canonical-categories))


;; Predefining classes so we don't need to import them -------------------------
(defn cljc-type 
  "Cljc-friendly and safe alternative to `clojure.core/type`.

   `clojure.core/type` would return `:foo` in this example case:
   `(type (with-meta [1] {:type :foo}))`

   `cljc-type` will always return the class (or constructor in cljs)."
  [x]
  (if (meta x)
    #?(:cljs (.-constructor x) :clj (class x))
    (type x)))


(def transient-hash-set-class (cljc-type (transient (hash-set :a :b))))
(def transient-hash-map-class (cljc-type (transient (hash-map :a 1))))
(def transient-array-map-class (cljc-type (transient (array-map :a 1))))
(def transient-vector-class (cljc-type (transient (vector :a 1))))
(def cljc-transients-primary-tags-by-class
  {transient-hash-set-class  :set
   transient-hash-map-class  :map
   transient-array-map-class :map
   transient-vector-class    :vector})

(def subvec-class (cljc-type (subvec [1 2 3 4 5] 1 3)))

(defn cljc-scalar-type
  "Provides a primary tag for data structures whose class constructor may not be
   present in cached map, such as "
  [x]
  (when-not (coll? x)
    (cond (number? x) :number
          (string? x) :string
          (boolean? x) :boolean
          (keyword? x) :keyword
          (symbol? x) :symbol                          
          (boolean? x) :boolean
          (nil? x) :nil                     
          (char? x) :char
          (uuid? x) :uuid)))

(defn cljc-coll-type
  "Provides a primary tag for:
   - Data structures whose class constructor may not be present in cached map,
     such as various data structures that implement ISeq. 
   - Custom cljc data structures that are implemented on top of standard cljc
     collection interfaces."
  [x]
  (when (coll? x)
    (cond (vector? x) :vector
          (record? x) :record
          (map? x) :map
          (set? x) :set
          (list? x) :list 
          (seq? x) :seq)))

;; -----------------------------------------------------------------------------
;;                                                                                                       
;;                                                                                                       
;; PPPPPPPPPPPPPPPPP   RRRRRRRRRRRRRRRRR   EEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDD           SSSSSSSSSSSSSSS 
;; P::::::::::::::::P  R::::::::::::::::R  E::::::::::::::::::::ED::::::::::::DDD      SS:::::::::::::::S
;; P::::::PPPPPP:::::P R::::::RRRRRR:::::R E::::::::::::::::::::ED:::::::::::::::DD   S:::::SSSSSS::::::S
;; PP:::::P     P:::::PRR:::::R     R:::::REE::::::EEEEEEEEE::::EDDD:::::DDDDD:::::D  S:::::S     SSSSSSS
;;   P::::P     P:::::P  R::::R     R:::::R  E:::::E       EEEEEE  D:::::D    D:::::D S:::::S            
;;   P::::P     P:::::P  R::::R     R:::::R  E:::::E               D:::::D     D:::::DS:::::S            
;;   P::::PPPPPP:::::P   R::::RRRRRR:::::R   E::::::EEEEEEEEEE     D:::::D     D:::::D S::::SSSS         
;;   P:::::::::::::PP    R:::::::::::::RR    E:::::::::::::::E     D:::::D     D:::::D  SS::::::SSSSS    
;;   P::::PPPPPPPPP      R::::RRRRRR:::::R   E:::::::::::::::E     D:::::D     D:::::D    SSS::::::::SS  
;;   P::::P              R::::R     R:::::R  E::::::EEEEEEEEEE     D:::::D     D:::::D       SSSSSS::::S 
;;   P::::P              R::::R     R:::::R  E:::::E               D:::::D     D:::::D            S:::::S
;;   P::::P              R::::R     R:::::R  E:::::E       EEEEEE  D:::::D    D:::::D             S:::::S
;; PP::::::PP          RR:::::R     R:::::REE::::::EEEEEEEE:::::EDDD:::::DDDDD:::::D  SSSSSSS     S:::::S
;; P::::::::P          R::::::R     R:::::RE::::::::::::::::::::ED:::::::::::::::DD   S::::::SSSSSS:::::S
;; P::::::::P          R::::::R     R:::::RE::::::::::::::::::::ED::::::::::::DDD     S:::::::::::::::SS 
;; PPPPPPPPPP          RRRRRRRR     RRRRRRREEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDD         SSSSSSSSSSSSSSS   
;;                                                                                                       
;;                                                                                                      


(defn throwable? [x]
  (instance? #?(:cljs js/Error :clj java.lang.Throwable) x))

(defn exception? [x]
  #?(:clj (instance? java.lang.Exception x)
     :cljs (and (throwable? x) (instance? cljs.core/ExceptionInfo x))))

(defn error? [x]
  #?(:clj (instance? java.lang.Error x)
     :cljs (and (throwable? x) (not (exception? x)))))


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


(defn cljc-array? [x]
  #?(:cljs
     (array? x)
     :bb
     (or (try (some-> x .getClass .isArray)
              (catch Exception e))
         (try (some-> x class .isArray)
              (catch Exception e)))
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


(defn js-map? [v]
  #?(:cljs
     (or (instance? js/Map v)
         (instance? js/WeakMap v))
     :clj false))

(defn map-like? [v]
  #?(:cljs
     (or (map? v)
         (instance? cljs.core/IMap v)
         (js-map? v))
     :clj
     (or (map? v)
         (instance? clojure.lang.IPersistentMap v)
         (instance? java.util.Map v)
         (array-map? v)
         (hash-map? v))))

(defn js-set? [v]
  #?(:cljs
     (or (instance? js/Set v)
         (instance? js/WeakSet v))
     :clj
     false))

(defn set-like? [v]
  #?(:cljs
     (or (set? v)
         (js-set? v))
     :clj
     (or (set? v)
         (instance? clojure.lang.IPersistentSet v)
         (instance? java.util.Set v))))

(defn queue? [v]
  #?(:cljs
     (instance? cljs.core/PersistentQueue v)
     :clj
     (or
      (instance? java.util.Queue v)
      (instance? clojure.lang.PersistentQueue v))))

(defn subvec? [x]
  #?(:clj  (instance? subvec-class x)
     :cljs (instance? cljs.core/Subvec x)))

(defn list-like? [v]
  (or (sequential? v)
      #?(:clj (instance? java.util.List v))
      (instance? transient-vector-class v)
      (cljc-array? v)
      (queue? v)))

(defn coll-like? [v]
  (or (coll? v)
      (seq? v)
      #?(:clj (instance? java.util.Collection v))
      (map-like? v)
      (set-like? v)
      (list-like? v)))

(defn js-global-this? [x] 
  #?(:cljs
     (= x js/globalThis)
     :clj
     false))

(defn js-object-instance? [x] 
  #?(:cljs
     (instance? js/Object x)
     :clj
     false))

(defn js-promise? [x]
  #?(:cljs
     (instance? js/Promise x)
     :clj
     false))

(defn js-data-view? [x]
  #?(:cljs
     (instance? js/DataView x)
     :clj
     false))

(defn js-array-buffer? [x]
  #?(:cljs
     (instance? js/ArrayBuffer x)
     :clj
     false))

(defn js-object? [x]
  #?(:cljs (object? x) :clj false))

(defn js-array? [x]
  #?(:cljs (array? x) :clj false))

(defn ^:no-doc js-generator? [x]
  #?(:cljs
     (and (js-iterable? x)
          (= (str x) "[object Generator]"))
     :clj
     false))

(defn ^:no-doc js-typed-array? [x]
  #?(:cljs
     (and (js/ArrayBuffer.isView x)
          (not (instance? js/ArrayBuffer x))
          (not (instance? js/DataView x)))))


(defn js-intl?
  [x]
  #?(:cljs
     (some->> x
              cljc-type
              (contains? jsi.native/js-built-in-intl-by-object))
     :clj
     false))

(defn lazy-seq?
  #?(:clj  "Returns true if x implements `clojure.lang.LazySeq`"
     :cljs "Returns true if x implements `cljs.core/LazySeq`")
  [x]
  (instance? #?(:clj clojure.lang.LazySeq :cljs cljs.core/LazySeq) x))

(defn range?
  #?(:clj  "Returns true if x implements `clojure.lang.Range`"
     :cljs "Returns true if x implements `cljs.core/Range`")
  [x]
  #?(:clj  (or (instance? clojure.lang.LongRange x)
               (instance? clojure.lang.Range x))
     :cljs (instance? cljs.core/Range x)))

(defn integer-range?
  #?(:clj  "Returns true if x implements `clojure.lang.LongRange`"
     :cljs "Returns true if x implements `cljs.core/IntegerRange`")
  [x]
  (instance? #?(:clj clojure.lang.LongRange :cljs cljs.core/IntegerRange) x))

(defn repeat?
  #?(:clj  "Returns true if x implements `clojure.lang.Repeat`"
     :cljs "Returns true if x implements `cljs.core/Repeat`")
  [x]
  (instance? #?(:clj clojure.lang.Repeat :cljs cljs.core/Repeat) x))

(defn iterate?
  #?(:clj  "Returns true if x implements `clojure.lang.Iterate`"
     :cljs "Returns true if x implements `cljs.core/Iterate`")
  [x]
  (instance? #?(:clj clojure.lang.Iterate :cljs cljs.core/Iterate) x))

(defn cons?
  #?(:clj  "Returns true if x implements `clojure.lang.Cons`"
     :cljs "Returns true if x implements `cljs.core/Cons`")
  [x]
  (instance? #?(:clj clojure.lang.Cons :cljs cljs.core/Cons) x))


(defn chunked-cons?
  #?(:clj  "Returns true if x implements `clojure.lang.ChunkedCons`"
     :cljs "Returns true if x implements `cljs.core/ChunkedCons`"
     :bb   "Returns false; ChunkedCons is not implemented natively in Babashka.")
  [x]
  #?(:cljs (instance? cljs.core/ChunkedCons x)
     :clj  (instance? clojure.lang.ChunkedCons x)))

(defn lazyish-seq? [x]
  (or
   (lazy-seq? x)
   (range? x)
   (integer-range? x)
   (repeat? x)
   (iterate? x)
   (cons? x)
   (chunked-cons? x)))

(defn deferred? [x]
  (or (delay? x)
      #?(:clj  (or (future? x) (instance? clojure.lang.IPending x))
         :cljs (js-promise? x))))

(defn java-util-class? [s]
  (boolean (some-> s (str/starts-with? "java.util"))))

(defn java-lang-class? [s]
  (boolean (some->> s (re-find #"java\.lang"))))

(defn java-class? [s]
  (boolean (re-find #"^(?:L|\[L)?java\.[a-z]+\..+" s)))

(defn data-type? [x]
  #?(:cljs false :clj (instance? clojure.lang.IType x)))

(defn derefable? [x]
  (or (volatile? x)
      #?(:clj  (instance? clojure.lang.IDeref x)
         :cljs (satisfies? cljs.core/IDeref x))))

(defn reference? 
  "Returns true if x is a reference type."
  [x]
  #?(:clj  (instance? clojure.lang.IRef x)
     :cljs (or (instance? cljs.core/Atom x)
               (instance? cljs.core/Var x))))

(defn carries-meta? [x]
  #?(:clj  (instance? clojure.lang.IObj x)
     :cljs (satisfies? cljs.core/IWithMeta x)))

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

(defn byte? [x]
  #?(:cljs
     false
     :clj
     (instance? java.lang.Byte x)))

(defn long? [x]
  #?(:cljs
     false
     :clj
     (instance? java.lang.Long x)))

(defn big-int? [x]
  #?(:cljs
     (instance? js/BigInt x)
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
;;      :cljs (instance? cljs.core/ISorted x)))


(defn editable? [x]
  #?(:clj  (instance? clojure.lang.IEditableCollection x)
     :cljs (satisfies? cljs.core/IEditableCollection x)))

(defn temporal? [x]
  (or (inst? x)
      #?(:jolt (-> x
                   type 
                   pr-str 
                   (str/starts-with? "java.time."))
         :clj (instance? java.time.temporal.TemporalAccessor x)
         :cljs false))) ;; catch-all for clarity if it isn't an inst

(defn transient? [v]
  #?(:clj  (instance? clojure.lang.ITransientCollection v)
     :cljs (satisfies? cljs.core/ITransientCollection v)))

(defn stack? [x]
  #?(:clj  (instance? clojure.lang.IPersistentStack x)
     :cljs (satisfies? cljs.core/IStack x)))

(defn named? [x]
  #?(:clj  (instance? clojure.lang.Named x)
     :cljs (satisfies? cljs.core/INamed x)))

(defn multi-function? [x]
  #?(:clj  (instance? clojure.lang.MultiFn x)
     :cljs (instance? cljs.core/MultiFn x)))

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


(defn add-tags!* [x vol f & tags]
  (when (f x)
    (vswap! vol set/union (into #{} tags))))


(defn number-tags
  "All secondary number tags, for values at runtime."
  [x]
  (let [vol             (volatile! #{})
        tag!            (partial add-tags!* x vol)
        is-scalar?      (scalar? x)
        is-real-number? (real-number? x)]
    (when is-real-number?
      (tag! zero? :zero)
      (tag! whole-number? :whole)
      (tag! fractional-number? :fractional)
      (tag! nat-int? :nat-int)
      (tag! neg? :neg)
      (tag! neg-int? :neg-int)
      (tag! pos? :pos)
      (tag! pos-int? :pos-int) 
      (tag! big-int? :big-int))
    @vol))


(defn all-tags*
  "All tags, for values at macroexpansion."
  ([x]
   (all-tags* x nil))
  ([x {:keys [runtime?]}]
   (let [vol        (volatile! #{})
         tag!  (partial add-tags!* x vol)
         x-is-scalar? (scalar? x)
         x-is-real-number? (real-number? x)]

     (when x-is-scalar? (vswap! vol conj :scalar))
     (tag! callable? :callable)
     (tag! seqable? :seqable)
     (tag! char-sequence? :char-sequence)
     (tag! carries-meta? :carries-meta)
     (tag! named? :named)

     (when x-is-real-number?
       (vswap! vol conj :real)
       (tag! byte? :byte)
       (tag! cljc-ratio? :ratio)
       (tag! big-decimal? :big-decimal)
       (tag! short? :short)
       (tag! double? :double)
       (tag! long? :long)
       (tag! float? :float)
       (tag! int? :int)
       (tag! big-int? :big-int))

     (when-not x-is-scalar?
       (cond 
         (throwable? x)
         (do
           (vswap! vol conj :throwable)
           (tag! error? :error)
           (tag! exception? :exception))
         
         :else
         (do  
           (tag! reference? :reference)
           (tag! derefable? :derefable)
           (tag! inst? :inst)
           (tag! coll? :coll)
           (tag! seq? :seq)
           (tag! lazyish-seq? :lazy :deferred)
           (tag! deferred? :deferred)
           (tag! transient? :transient)
           (tag! editable? :editable)
           (tag! multi-function? :multi-function)
           (tag! sorted? :sorted)
           (tag! stack? :stack)
           (tag! cons? :cons)
           (tag! range? :range)
           (tag! subvec? :subvec)
           (tag! associative? :associative)
           (tag! sequential? :sequential)
           (tag! cljc-map-entry? :map-entry)
           (tag! map? :array-map)
           (tag! array-map? :array-map)
           (tag! hash-map? :hash-map)
           (tag! map-like? :map-like)
           (tag! set-like? :set-like)
           (tag! list-like? :list-like)
           (tag! coll-like? :coll-like)
           (tag! cljc-array? :array)
           #?(:cljs
              (do
                (tag! iterable? :iterable)
                (tag! js-object? :object :map-like :js)
                (tag! js-array? :array :array-like :list-like :js)
                (tag! js-map? :map-like :js)
                (tag! js-set? :set-like :js)
                (tag! js-generator? :generator :js)
                (tag! js-promise? :promise :js)
                (tag! js-global-this? :global-this :js)
                (tag! js-typed-array? :typed-array :array-like :js)
                )))))
     #_(when runtime? 
         (add-tag! runtime? :record))
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
   (do 
     (def examples-by-classname
       (atom {}))

     (def examples-ordered
       (atom []))

     (def comment-box-text
       "This is used to generate the header of a test file saved in `gen-test-path`"
       (str 
        ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"
        ";;\n"
        ";;\n"
        ";;   This namespace is automatically generated in lasertag.cached/by-class*.\n"
        ";;\n"
        ";;   Do not manually add anything to this namespace.\n"
        ";;\n"
        ";;   It can be regenerated with the bb task `test:snapshot`.\n"
        ";;\n"
        ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" ))

     (defn spit-test-header []
       (require '[clojure.pprint :refer [pprint]])
       (spit gen-test-path
             (str 
              comment-box-text
              "\n\n"
              (with-out-str 
                (clojure.pprint/pprint
                 (list 'ns
                       (symbol (str "lasertag." gen-test-ns-name))
                       (list :require
                             '[clojure.test :refer [deftest is]]
                             '[lasertag.core :refer [tag-map]]
                             '[clojure.string :as string]))))
              "\n")
             :append false))

     (defn- indented-string [indent-str s]
       (some-> s
               str
               (str/split #"\n")
               (->> (mapv #(str indent-str  %))
                    (str/join "\n"))))

     (defn- deftest-str* [deftest-name result form evaled-form]
       (str/replace 
        (with-out-str
          (clojure.pprint/pprint
           (list 
            'deftest deftest-name
            (list 'is
                  (list '=
                        result
                        (list 'tag-map
                              form
                         ; This check needs to match check in lasertag.core/tag-map
                         ; if we need to get more tags at runtime. If it 
                         ; matches, we will skip adding the secondary tags
                         ; (like would happen at runtime), in order to match
                         ; the result of lookup by class from the map of 
                         ; cached tag-maps. 
                              (when (number? evaled-form)
                                {:skip-dynamic-secondary-tags? true})))))))
        #"\n$"
        ""))




     (defn- cljc-form-with-elided-branches-str
       [s elided-branches]
       (str
        "\n\n"
        "#?("
        (str/join "\n   "
                  (mapv #(str %  " " "nil") 
                        elided-branches))
        (when elided-branches "\n   ")
        ":clj\n"
        (indented-string "   " s)
        ")"))

     (def elided-branches-for-clj-tests
       {"java.time.ZonedDateTime"                  [:jolt]
        "clojure.lang.Repeat"                      [:jolt]
        "clojure.lang.BigInt"                      [:jolt]
        "java.lang.Float"                          [:jolt]
        "java.lang.Short"                          [:jolt]
        "java.lang.Byte"                           [:jolt]
        "java.lang.Integer"                        [:jolt]
        "java.math.BigInteger"                     [:jolt]
        "clojure.lang.APersistentVector$SubVector" [:jolt]})

     (defmacro by-class* 
       "Supplied with any number of maps of example values, merges them and
        generates a map of cached results for calls to lasertag.core/tag-map.

        Args are variadic incase some classes need to be elided for a certain
        dialect, such as the time lib in Jolt.

        Example entry input:
          [[clojure.lang.PersistentHashMap :map] (hash-map :a 1)]

        Example entry output:
          [clojure.lang.PersistentHashMap 
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
             :classname \"clojure.lang.PersistentHashMap\"}]
        "
       [& maps]
       (when write-tests?
         (println "\nLASERTAG_WRITE_TESTS is true, writing tests...")
         (when elided-branches-for-clj-tests
           (println 
            (str "\nGenerated deftests for some classes will have elided branches:\n"
                 (str/replace
                  (with-out-str
                    (clojure.pprint/pprint elided-branches-for-clj-tests))
                  #"\n$"
                  ""))))
         (spit-test-header))
       (reduce (fn [m [[cls tag] form]]
                 (let [evaled-form (eval form)
                       all-tags    (conj (all-tags* evaled-form) tag)
                       classname   (if (nil? cls) "nil" (str cls))
                       result      {:tag       tag
                                    :all-tags  all-tags
                                    :category  (get canonical-category-by-primary-tag tag)
                                    :type      cls
                                    :classname classname}]

                   ;; add entry to the examples-by-class-atom
                   ;; used for reference by other tools such as atlas-spec
                   (swap! examples-by-classname
                          assoc
                          classname 
                          {:code    
                           (pr-str form)
                           :value     
                           form})
                   (swap! examples-ordered conj classname)

                   (when write-tests?
                     (require '[clojure.pprint :refer [pprint]])
                     (spit (str "./test/lasertag/core_test" ".cljc") 
                           (when (or (empty? greenlit-tests)
                                     (and (not (empty? greenlit-tests))
                                          (contains? greenlit-tests classname)))
                             (let [elided-branches 
                                   (some->> classname
                                            (get elided-branches-for-clj-tests)
                                            seq)
                                   deftest-name
                                   (symbol (str classname "-test"))

                                   deftest-str
                                   (deftest-str* deftest-name
                                     result 
                                     form
                                     evaled-form)] 
                               
                               (cljc-form-with-elided-branches-str
                                deftest-str
                                elided-branches)))
                           :append true))

                   (assoc m cls result)))
               {}
               (partition 2 (apply concat maps))))))


;; TODO - describe what this is and how it works
(def by-class
  #?(:cljs
     lasertag.jsi.classes/by-class
     :clj
     (by-class*
      [
       ;; Scalars
       [java.lang.Boolean :boolean]                             true
       [nil :nil]                                               nil
       [java.lang.Long :number]                                 42
       [java.lang.Integer :number]                              (int 42)
       [java.lang.Float :number]                                (float 3.14)
       [java.lang.Byte :number]                                 (byte 1)
       [java.lang.Double :number]                               3.14
       [java.lang.Short :number]                                (short 42)
       [clojure.lang.Ratio :number]                             1/3
       [clojure.lang.BigInt :number]                            42N
       [java.math.BigInteger :number]                           (java.math.BigInteger. "42")
       [java.math.BigDecimal :number]                           42M
       [java.lang.String :string]                               "bar"
       [java.lang.Character :char]                              \c
       [java.util.regex.Pattern :regex]                         #"^[a-z]+$"
       [clojure.lang.Keyword :keyword]                          :foo
       [clojure.lang.Symbol :symbol]                            (symbol "foo")
       [java.util.UUID :uuid]                                   #uuid "4fe5d828-6444-11e8-8222-720007e40350"

       ;; Collections
       ;; TODO - Add to bb
       ;; [clojure.lang.StringSeq :seq]                         (seq "ab")
       [clojure.lang.PersistentArrayMap :map]                   {:a 1 
                                                                 :b 2}
       [clojure.lang.PersistentHashMap :map]                    (hash-map :a 1 :b 2)
       [clojure.lang.LazySeq :seq]                              (map inc [1 2 3])
       [clojure.lang.ArraySeq :seq]                             (seq (into-array [1 2 3]))
       [clojure.lang.PersistentVector$ChunkedSeq :seq]          (seq [1 2 3])
       [clojure.lang.PersistentVector :vector]                  [1 2 3]
       [clojure.lang.PersistentHashSet :set]                    #{1 2 3}
       [clojure.lang.APersistentVector$SubVector :vector]       (subvec [1 2 3 4 5] 1 3)
       [clojure.lang.Cons :seq]                                 (cons 1 '(2 3))
       [clojure.lang.LongRange :seq]                            (range 3)
       [clojure.lang.Range :seq]                                (range 0 1 1/5)
       [clojure.lang.Repeat :seq]                               (repeat 2 "a")
       [clojure.lang.PersistentList :list]                      (list 1 2 3)
       [clojure.lang.PersistentList$EmptyList :list]            (list)
       [clojure.lang.PersistentTreeMap :map]                    (sorted-map :a 1 :b 2)
       [clojure.lang.PersistentTreeSet :set]                    (sorted-set 3 1 2)
       [clojure.lang.PersistentArrayMap$TransientArrayMap :map] (transient (array-map :a 1 :b 2))
       [clojure.lang.PersistentHashMap$TransientHashMap :map]   (transient (hash-map  :a 1 :b 2))
       [clojure.lang.PersistentVector$TransientVector :map]     (transient [1 2 3])
       [clojure.lang.PersistentHashSet$TransientHashSet :set]   (transient #{1 2 3})
       [clojure.lang.PersistentQueue :queue]                    clojure.lang.PersistentQueue/EMPTY
       [clojure.lang.MapEntry :vector]                          (first {:a 1})
       [java.util.HashMap :map]                                 (java.util.HashMap. (hash-map  :a 1 :b 2))
       [java.util.ArrayList :array]                             (java.util.ArrayList. (range 3))
       [java.util.HashSet :set]                                 (java.util.HashSet. #{1 2 3})
       [java.util.ArrayDeque :array]                            (java.util.ArrayDeque. [1 2 3])                            

       ;; Functions 
       [clojure.lang.MultiFn :function]                         (do (defmulti area :shape)
                                                                    (defmethod area :square [m]
                                                                      [m]
                                                                      (* (:side m) (:side m)))
                                                                    (defmethod area :circle [m]
                                                                      (* Math/PI (:radius m) (:radius m))))
       ;; Temporal Values
       [java.util.Date :datetime]                               (java.util.Date.)
       ;; leave out for bb for now 
       ;; [java.sql.Timestamp :datetime]                        (java.sql.Timestamp. (System/currentTimeMillis))
       
       ;; Identities
       [clojure.lang.Atom :atom]                                (atom 1)
       [clojure.lang.Agent :agent]                              (agent 1)
       [clojure.lang.Volatile :volatile]                        (volatile! 1)
       [clojure.lang.Ref :ref]                                  (ref 0)
       [clojure.lang.Var :var]                                  (do (def my-var 42) #'my-var)
       [clojure.lang.Delay :delay]                              (delay 42)

       
       ;; Reflection
       [clojure.lang.ReaderConditional :reader-conditional]     (reader-conditional
                                                                 '(:clj  (System/getProperty "os.name")
                                                                         :cljs "JS")
                                                                 false)
       ]

      ;; second arg to by-class*
      ;; temporal constructs not supported by jolt core.
      ;; they are only in jolt.time lib
      ;; so we will elide them explicitly for jolt
      ;; for jolt runtime, these values will be resolved by cached/temporal? and tagged :datetime
      #?(:jolt nil
         :clj [
               ;; Datetime
               [java.time.Instant :datetime]       (java.time.Instant/now)
               [java.time.LocalDate :datetime]     (java.time.LocalDate/now)
               [java.time.ZonedDateTime :datetime] (java.time.ZonedDateTime/now) ]))))


(def by-number-class
  (select-keys 
   #?(:cljs lasertag.jsi.classes/by-class :clj by-class)
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
