(ns lasertag.cached
  (:require [clojure.set :as set]
            [clojure.string :as string]))


;; -----------------------------------------------------------------------------
;; Should always be set to false, unless writing tests during dev of lasertag

(def ^:private write-tests? false)

;; -----------------------------------------------------------------------------



;; Debugging -------------------------------------------------------------------

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




;; Predefining classes so we don't need to import them -------------------------

(defn cljc-type 
  "Cljc-friendly and safe alternative to `clojure.core/type`.

   `clojure.core/type` would return `:foo` in this example case:
   `(type (with-meta [1] {:type :foo}))`

   `cljc-type` will always return the class (or constructor)."
  [x]
  #?(:clj  (class x)
     :cljs (.-constructor x)))


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

(defn cljc-coll-type
  "Provides a primary tag for custom cljc data structures that are implemented
   on top of standard cljc collection interfaces."
  [x]
  (cond (vector? x) :vector
        (record? x) :record
        (map? x)    :map
        (set? x)    :set
        (list? x)   :list
        (seq? x)    :seq
        :else
        (get cljc-transients-primary-tags-by-class
             (cljc-type x)
             nil)))
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


(defn map-like? [v]
  #?(:cljs
     (or (map? v)
         (instance? cljs.core/IMap v)
         (instance? js/Map v)
         (instance? js/WeakMap v))
     :clj
     (or (map? v)
         (instance? clojure.lang.IPersistentMap v)
         (instance? java.util.Map v)
         (array-map? v)
         (hash-map? v))))

(defn set-like? [v]
  #?(:cljs
     (or (set? v)
         (instance? js/Set v)
         (instance? js/WeakSet v))
     :clj
     (or (set? v)
         (instance? clojure.lang.IPersistentSet v)
         (instance? java.util.Set v))))

(defn queue? [v]
  #?(:cljs
     (instance? cljs.core/PersistentQueue v)
     :clj
     (or (instance? java.util.Queue v)
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
     ()))

(defn js-array-buffer? [x]
  #?(:cljs
     (instance? js/ArrayBuffer x)
     :clj
     false))

(defn js-object? [x]
  #?(:cljs (object? x) :clj false))

(defn js-array? [x]
  #?(:cljs (array? x) :clj false))

(defn lazyish-seq? [x]
  (or (instance? #?(:cljs cljs.core/LazySeq :clj clojure.lang.LazySeq) x)
      (instance? #?(:cljs cljs.core/Range :clj clojure.lang.Range) x)
      (instance? #?(:cljs cljs.core/IntegerRange :clj clojure.lang.LongRange) x)
      (instance? #?(:cljs cljs.core/Repeat :clj clojure.lang.Repeat) x)
      (instance? #?(:cljs cljs.core/Iterate :clj clojure.lang.Iterate) x)
      (instance? #?(:cljs cljs.core/Cons :clj clojure.lang.Cons) x)
      (instance? #?(:cljs cljs.core/ChunkedCons :clj clojure.lang.ChunkedCons) x)))

(defn deferred? [x]
  (or #?(:cljs (delay? x) :clj (delay? x))
      #?(:cljs false :clj (future? x))
      #?(:cljs (js-promise? x) :clj (instance? clojure.lang.IPending x))))


(defn java-util-class? [s]
  (boolean (some-> s (string/starts-with? "java.util"))))

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
     :cljs (or (satisfies? cljs.core/Atom x)
               (satisfies? cljs.core/Var x))))

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

(defn multi-function? [x]
  #?(:clj  (instance? clojure.lang.MultiFn x)
     :cljs (satisfies? cljs.core/MultiFn x)))

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

(defn number-tags
  "All secondary number tags, for values at runtime."
  [x]
  (let [vol             (volatile! #{})
        tag!       (partial add-tags!* x vol)
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
  "All tags, for values at macroexansion."
  ([x]
   (all-tags* x nil))
  ([x {:keys [runtime?]}]
   (let [vol        (volatile! #{})
         tag!  (partial add-tags!* x vol)
         x-is-scalar? (scalar? x)
         is-real-number? (real-number? x)]

     (when x-is-scalar? (vswap! vol conj :scalar))
     (tag! callable? :callable)
     (tag! seqable? :seqable)
     (tag! char-sequence? :char-sequence)
     (tag! carries-meta? :carries-meta)
     (tag! named? :named)

     (when is-real-number?
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
           (tag! multi-function? :multi-function?)
           (tag! sorted? :sorted)
           (tag! stack? :stack)
           (tag! cons? :cons)
           (tag! range? :range)
           (tag! subvec? :subvec)
           (tag! associative? :associative)
           (tag! sequential? :sequential)
           (tag! cljc-map-entry? :map-entry)
           (tag! array-map? :array-map)
           (tag! hash-map? :hash-map)
           (tag! map-like? :map-like)
           (tag! set-like? :set-like)
           (tag! list-like? :list-like)
           (tag! coll-like? :coll-like)
           (tag! js-object? :js-object)
           (tag! js-array? :js-array)
           )))
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


(def gen-test-path
   "./test/lasertag/generated.clj")



#?(:clj
   (do 

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
        ";;   To regenerate, set `lasertag.cached/write-tests?` to `true`,"
        "\n"
        ";;   then run `lein test`."
        "\n"
        ";;\n"
        ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" ))

     (defn spit-test-header []
       (require '[clojure.pprint :refer [pprint]])
       (spit (str 
              comment-box-text
              "\n\n"
              (with-out-str 
                (clojure.pprint/pprint
                 '(ns lasertag.generated
                    (:require
                     [clojure.test :refer [deftest is]]
                     [lasertag.core :refer [tag-map]]
                     [clojure.string :as string]))))
              "\n")
             :append false))

     (defn spit-test-body [body]
       (spit gen-test-path 
             body
             :append true))

     (defmacro by-class* [m]
       (when write-tests? (spit-test-header))
       (reduce-kv (fn [m [cls tag] x*]
                    (let [x         (eval x*)
                          all-tags  (conj (all-tags* x) tag)
                          classname (if (nil? cls) "nil" (str cls))
                          result    {:tag       tag
                                     :type      cls
                                     :all-tags  all-tags
                                     :classname classname}]

                      (when write-tests?
                        (require '[clojure.pprint :refer [pprint]])
                        (spit (str "./test/lasertag/generated" ".clj") 
                              (str
                               "\n\n"
                               (with-out-str
                                 (clojure.pprint/pprint
                                  (list 
                                   'deftest (symbol (str classname "-test"))
                                   (list 'is (list '= (list 'tag-map x*) result))))))
                              :append true))

                      (assoc m cls result)))
                  {}
                  m))))


;; Add to jvm test generation
;; [java.util.concurrent.Future :future]  (future (Thread/sleep 10) (+ 1 2))
;; some functions
;; some arrays
;; throwables
;; promise
;; abstract datatype
;; different real numbers whose tag maps have to be supplemented at runtime
;; things implementing abstract java maps, sets, and lists
;; vanilla classes


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
                                    :classname "cljs.core/TransientVector"}
      cljs.core/Volatile           {:tag       :volatile
                                    :type      cljs.core/Volatile
                                    :all-tags  #{:volatile
                                                 :reference}
                                    :classname "cljs.core/Volatile"}
      cljs.core/Var                {:tag       :var
                                    :type      cljs.core/Var
                                    :all-tags  #{:var
                                                 :reference}
                                    :classname "cljs.core/Var"}}
     :clj
     (by-class*
      {;; numbers
       [java.lang.Long :number]                                 21
       [java.lang.Byte :number]                                 (byte 1)
       [java.lang.Double :number]                               21.42
       [java.lang.Short :number]                                (short 21)
       [clojure.lang.Ratio :number]                             2/3
       [java.lang.Float :number]                                (float 21.42)
       [java.lang.Integer :number]                              (int 21)
       [clojure.lang.BigInt :number]                            21N
       [java.math.BigInteger :number]                           (java.math.BigInteger. "21")
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
       [clojure.lang.Range :seq]                                (range 0 1.0 0.1)
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
       [clojure.lang.PersistentStructMap :map]                  (do (defstruct foo :name :color) (struct foo "strawberry" "red"))
       [clojure.lang.MapEntry :vector]                          (-> {:a 1} first)
       [java.util.HashMap :map]                                 (java.util.HashMap. (hash-map "a" 1 "b" 2))
       [java.util.ArrayList :array]                             (java.util.ArrayList. (range 6))
       [java.util.HashSet :set]                                 (java.util.HashSet. #{"a" 1 "b" 2})
       [java.util.ArrayDeque :array]                            (java.util.ArrayDeque. [1 2 3])                            

       ;; Constructors
       [clojure.lang.MultiFn :function]                         (do (defmulti different-behavior (fn [x] (:x-type x)))
                                                                    different-behavior)

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
       [clojure.lang.Delay :delay]                              (delay 21)

       ;; Java Errors
       ;;  [java.lang.AssertionError :throwable]                    (java.lang.AssertionError. "foo")
       ;;  [java.lang.NoClassDefFoundError :throwable]              (java.lang.NoClassDefFoundError. "foo")
       ;;  [java.lang.UnsatisfiedLinkError :throwable]              (java.lang.UnsatisfiedLinkError. "foo")
       ;;  [java.lang.ClassCircularityError :throwable]             (java.lang.ClassCircularityError. "foo")
       ;;  [java.lang.OutOfMemoryError :throwable]                  (java.lang.OutOfMemoryError. "foo")
       ;;  [java.lang.StackOverflowError :throwable]                (java.lang.StackOverflowError. "foo")
       ;;  [java.lang.InternalError :throwable]                     (java.lang.InternalError. "foo")
       ;;  ;; [java.lang.ThreadDeath :throwable]                    (java.lang.ThreadDeath. "foo")           ;;<- class not found
       ;;  ;; [java.lang.IOError :throwable]                        (java.lang.IOError. "foo")               ;;<- class not found
       
       ;; Java Exceptions
       ;;  [java.lang.CloneNotSupportedException :throwable]        (java.lang.CloneNotSupportedException. "foo")
       ;;  [java.lang.InterruptedException :throwable]              (java.lang.InterruptedException. "foo")
       ;;  [java.io.FileNotFoundException :throwable]               (java.io.FileNotFoundException. "foo")
       ;;  [java.net.SocketException :throwable]                    (java.net.SocketException. "foo")
       ;;  [java.sql.SQLException :throwable]                       (java.sql.SQLException. "foo")
       ;;  [java.lang.ClassNotFoundException :throwable]            (java.lang.ClassNotFoundException. "foo")
       ;;  [java.lang.NoSuchMethodException :throwable]             (java.lang.NoSuchMethodException. "foo")
       ;;  [java.lang.IllegalAccessException :throwable]            (java.lang.IllegalAccessException. "foo")
       ;;  [java.lang.NullPointerException :throwable]              (java.lang.NullPointerException. "foo")
       ;;  [java.lang.IllegalArgumentException :throwable]          (java.lang.IllegalArgumentException. "foo")
       ;;  [java.lang.IllegalStateException :throwable]             (java.lang.IllegalStateException. "foo")
       ;;  [java.lang.ArithmeticException :throwable]               (java.lang.ArithmeticException. "foo")
       ;;  [java.lang.ArrayIndexOutOfBoundsException :throwable]    (java.lang.ArrayIndexOutOfBoundsException. "foo")
       ;;  [java.lang.StringIndexOutOfBoundsException :throwable]   (java.lang.StringIndexOutOfBoundsException. "foo")
       ;;  [java.lang.UnsupportedOperationException :throwable]     (java.lang.UnsupportedOperationException. "foo")
       ;;  [java.util.concurrent.CancellationException :throwable]  (java.util.concurrent.CancellationException. "foo")
       ;;  [java.util.NoSuchElementException :throwable]            (java.util.NoSuchElementException. "foo")
       [java.lang.ClassCastException :throwable]                (java.lang.ClassCastException. "foo")

       ;; Clojure Exceptions
       [clojure.lang.ExceptionInfo :throwable]                  (ex-info "foo" {})
       [clojure.lang.ArityException :throwable]                 (clojure.lang.ArityException. 3 "foo")

       ;; reflection
       [clojure.lang.ReaderConditional :reader-conditional]     (reader-conditional '(:clj  (System/getProperty "os.name")
                                                                                            :cljs "JS")
                                                                                    false) 
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




