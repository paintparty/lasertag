;; TODO 
;; - Figure out way to do "%" for lambda args
;; - Add :array-like to all-tags
;; - Add :list-like to all-tags
;; - Add scalar-type? (or scalar?)
;; - Figure out if you need more granular tags for time contructs
;; If a value is cljs, add a :cljs tag to :all-tags
;; If a value is js, add a :js tag to :all-tags
;; If a value is clj, add a :clj tag to :all-tags
;; If a value is java, add a :java tag to :all-tags




(ns lasertag.core
  (:require 
   [clojure.string :as string]
   [lasertag.messaging :as messaging]
   #?(:cljs [lasertag.cljs-interop :as jsi]))
  #?(:clj
     (:import (clojure.lang PersistentVector$TransientVector
                            PersistentHashSet$TransientHashSet
                            PersistentArrayMap$TransientArrayMap
                            PersistentHashMap$TransientHashMap))))


(def cljc-transients 
  {#?(:cljs cljs.core/TransientVector
      ;; :bb (resolve 'PersistentVector$TransientVector)
      :clj PersistentVector$TransientVector)
   :vector

   #?(:cljs cljs.core/TransientHashSet
      ;; :bb (resolve 'PersistentHashSet$TransientHashSet)
      :clj PersistentHashSet$TransientHashSet)
   :set

   #?(:cljs cljs.core/TransientArrayMap
      ;; :bb (resolve 'PersistentArrayMap$TransientArrayMap)
      :clj PersistentArrayMap$TransientArrayMap)
   :map

   #?(:cljs cljs.core/TransientHashMap
      ;; :bb (resolve 'PersistentHashMap$TransientHashMap)
      :clj PersistentHashMap$TransientHashMap)
   :map})

(def cljc-transients-set
  (into #{} (keys cljc-transients)))


(defn ? 
  "Debugging macro internal to lib"
  ([x]
   (? nil x))
  ([l x]
   (if l
     (println (str " " l "\n") x)
     (println x))
   x))

(def cljs-serialized-fn-info   #"^\s*function\s*([^\(]+)\s*\(([^\)]*)\)\s*\{")

(def char-map
  [["_PERCENT_"     "%"]
   ["_AMPERSAND_"   "&"]
   ["_BANG_"        "!"]
   ["_QMARK_"       "?"]
   ["_PLUS_"        "+"]
   ["_SHARP_"       "#"]
   ["_BAR_"         "|"]
   ["_EQ_"          "="]
   ["_GT_"          ">"]
   ["_LT_"          "<"]
   ["_STAR_"        "*"]
   ["_"             "-"]])  

;; TODO - change names `scalar` -> `sev`
#?(:clj 
   (do 
     (def clj-scalar-types
       {
        ;; TODO - remove or comment out ---
        clojure.lang.Symbol     :symbol
        clojure.lang.Keyword    :keyword
        java.lang.String        :string
        java.lang.Boolean       :boolean
        ;; --------------------------------

        nil                     :nil
        java.util.regex.Pattern :regex
        java.lang.Character     :char
        java.util.UUID          :uuid})
     (def java-number-types
       {java.lang.Double     :double
        java.lang.Short      :short
        java.lang.Long       :long
        java.lang.Float      :float
        java.lang.Byte       :byte
        java.math.BigDecimal :decimal
        java.math.BigInteger :big-int})))


#?(:cljs 
   (do
     (def cljs-scalar-types
       {

        ;; TODO - remove or comment out ---
        cljs.core/Symbol  :symbol
        cljs.core/Keyword :keyword
        js/String         :string
        js/Boolean        :boolean
        ;; --------------------------------

        cljs.core/UUID    :uuid
        js/RegExp         :regex
        nil               :nil})
          
     (def js-number-types
       {js/Number :js-number
        js/BigInt :js-big-int})

     (def js-map-types
       {js/Map     :js-map
        js/WeakMap :js-weak-map})

     (def js-set-types
       {js/Set     :js-set
        js/WeakSet :js-weak-set})

     (def js-indexed-coll-types
       {js/Array             :array
        js/Int8Array         :js-int8-array
        js/Uint8Array        :js-unsigned-int8-array
        js/Uint8ClampedArray :js-unsigned-int8-clamped-array
        js/Int16Array        :js-int16-array
        js/Uint16Array       :js-unsigned-int16-array
        js/Int32Array        :js-int32-array
        js/Uint32Array       :js-unsigned-int32-array
        js/BigInt64Array     :js-big-int64-array
        js/BigUint64Array    :js-big-unsigned-int64-array
        js/Float32Array      :js-float32-array
        js/Float64Array      :js-float64-array})

     (def js-indexed-coll-types-set
       (->> js-indexed-coll-types
            keys
            (into #{})))))

(def clj-names
  {:clojure.lang.MultiFn :defmulti
   :clojure.lang.Ratio   :ratio
   :java.lang.Class      :class
   :sci.lang.Type        :class})

(def scalar-types-set
  #?(:cljs
     (as-> #{} $
       (apply conj $ (vals cljs-scalar-types))
       (apply conj $ (vals js-number-types)))
     :clj
     (as-> #{} $
       (apply conj $ (vals clj-scalar-types))
       (apply conj $ (vals java-number-types)))))

(defn carries-meta? [x]
  #?(:clj  (instance? clojure.lang.IObj x)
     :cljs (satisfies? IWithMeta x)))

(defn- pwos [x] (with-out-str (print x)))

(defn- lambda-args [args]
  #?(:cljs
     (if (every? #(re-find #"_SHARP_$" (name %)) args)
       (let [num-args (count args)]
         (map-indexed (fn [idx _]
                        ;; TODO figure out a way to do just a %
                        (symbol (str "%" (inc idx))))
                      (range num-args)))
       args)))

(defn- demunge-fn-name [s]
  (reduce
   (fn [acc [k v]]
     (string/replace acc (re-pattern k) v))
   s 
   char-map))

(defn- partition-drop-last [coll]
  [(drop-last coll)
   (last coll)])

(defn- cljc-NaN? [x] (and (number? x) (= "NaN" (str x))))
(defn- infinity? [x] (and (number? x) (= "Infinity" (str x))))
(defn- java-negative-infinity? [x] #?(:clj (and (number? x)
                                                (= "-Infinity" (str x)))))


;; cljs and js instance checks, cljs fn resolution functions start -------------
#?(:cljs 
   (do 
     (defn- js-global-this? [x] (= x js/globalThis))
     (defn- js-object-instance? [x] (instance? js/Object x))
     (defn- defmulti? [x] (= (type x) cljs.core/MultiFn))
     (defn- js-promise? [x] (instance? js/Promise x))
     (defn- js-data-view? [x] (instance? js/DataView x))
     (defn- js-array-buffer? [x] (instance? js/ArrayBuffer x))
     (defn- typed-array? [x]
       (and (js/ArrayBuffer.isView x)
            (not (instance? js/ArrayBuffer x))
            (not (instance? js/DataView x))))
     (defn- js-built-in-map [o]
       (let [{:keys [sym]} (get jsi/js-built-ins-by-built-in o)]
         {:js-built-in-method-of      o
          :js-built-in-method-of-name (some-> sym name)
          :js-built-in-function?      true}))

     (defn- js-built-in-method-of [x name-prop fn-nm]
       (when 
        (and (string? name-prop)
             (re-find #"[^\$\.-]" name-prop))
         (if-let [built-in-candidates
                  (get jsi/objects-by-method-name fn-nm)]
           (let [o (first (filter #(= x (aget (.-prototype %) fn-nm)) 
                                  built-in-candidates))]
             (js-built-in-map o))
           (when-let [built-in (get jsi/objects-by-unique-method-name
                                    fn-nm)]
             (js-built-in-map built-in)))))

     (defn- cljs-defmulti [sym]
       (when (symbol? sym)
         (let [[fn-ns fn-nm] (-> sym str (string/split #"/"))]
           {:fn-ns   fn-ns
            :fn-name fn-nm
            :fn-args :lasertag/multimethod})) )

     (defn- cljs-fn [x s]
       (let [bits          (string/split s #"\$")
             [fn-ns fn-nm] (partition-drop-last bits)
             fn-nm         (demunge-fn-name fn-nm)
             built-in?     (or (contains? jsi/js-built-in-objects x)
                               (contains? jsi/js-built-in-functions x))]
         (merge {:fn-name fn-nm}
                (when built-in? {:js-built-in-function? true})
                (when (seq fn-ns)
                  {:fn-ns (string/join "." fn-ns)}) 
                (when-not built-in? (js-built-in-method-of x s fn-nm)))))

     (defn- cljs-fn-alt [o]
       (let [out-str (pwos o)]
         (if-let [[_ fn-ns fn-nm] (re-find #"^(.+)/(.+)$" out-str)]
           {:fn-name           (demunge-fn-name fn-nm)
            :fn-ns             fn-ns
            :cljs-datatype-fn? true}
           {:lambda? true})))))

#?(:clj
   (do
     (defn- java-util-class? [s]
       (boolean (some-> s (string/starts-with? "java.util"))))

     (defn- java-lang-class? [s]
       (boolean (some->> s (re-find #"java\.lang"))))
     
     (defn java-class-name [x]
       ;; TODO - Consider using clojure.lang.Compiler/demunge here: 
       ;; (some-> x type .getName Compiler/demunge)
       (some-> (or (try (some-> x type .getName)
                        (catch Exception e))
                   (some-> x
                           type
                           str
                           (string/replace #"^class (.*)$" "")))

               ;; Example of what these last 2 do:
               ;; "[Ljava.lang.Object;" -> "Ljava.lang.Object"
               (string/replace #"^\[" "")
               (string/replace #";$" "")))
     
     (defn- find-classname [x]
       ;; For custom datatypes ^class prefix is not present in str'd babashka classname
       (re-find #"(?:^class )?(.*)$" (str x)))

     (defn- resolve-classname [x]
       (let [[_ nm] (find-classname x)
             bits   (some-> nm (string/split #"\."))]
         {:fn-ns   (-> bits
                       drop-last
                       (->> (string/join "."))
                       (string/replace #"_" "-")) 
          :fn-name (last bits)
          :fn-args :lasertag/unknown-function-signature-on-java-class}))

     (defn- resolve-fn-name [x]
       (let [pwo-stringified (pwos x)
             [_ nm*]         (re-find #"^#object\[([^\s]*)\s" pwo-stringified)]
         (when (and nm* (not (string/blank? nm*)))
           (let [[fn-ns fn-nm _anon] (string/split nm* #"\$")
                 fn-ns               (string/replace fn-ns #"_" "-")
                 fn-nm               (when-not _anon (demunge-fn-name fn-nm))]
             (merge (if fn-nm 
                      (if (re-find #"^fn--\d+$" fn-nm)
                        {:lambda? true}
                        {:fn-name fn-nm})
                      {:lambda? true})
                    {:fn-ns   fn-ns
                     :fn-args :lasertag/unknown-function-signature-on-clj-function})))))))

(defn- fn-info* [x k]
  #?(:cljs 
     (let [name-prop (.-name x)]
       (cond
         (= k :defmulti)                 (cljs-defmulti name-prop)
         (not (string/blank? name-prop)) (cljs-fn x name-prop)
         :else                           (cljs-fn-alt x)))
     :clj
     (if (= k :defmulti)
       {:fn-args :lasertag/multimethod}
       (if (= k :class)
         (resolve-classname x)
         (resolve-fn-name x)))))

(defn- fn-args* [x]
  (let [[_ _ s] (re-find cljs-serialized-fn-info (str x))
        strings (some-> s
                        (string/split #","))
        syms    (some->> strings
                         ;; change to mapv?
                         (map (comp symbol
                                    string/trim)))]
    syms))

(defn- fn-args-defrecord [coll fn-info]
  (if (:cljs-datatype-fn? fn-info)
    (if (and (seq coll) 
             (= (take-last 3 coll)
                '(__meta __extmap __hash)))
      [(drop-last 3 coll) true]
      [coll false])
    [coll false]))

(defn- fn-args-lambda [coll fn-info]
  (if (:lambda? fn-info) (lambda-args coll) coll))

(defn- fn-args [x fn-info]
  (let [fn-args              (fn-args* x)
        [fn-args defrecord?] (fn-args-defrecord fn-args fn-info)
        fn-args              (fn-args-lambda fn-args fn-info)]
    [fn-args defrecord?]))

#?(:cljs 
   (defn- cljs-fn-args [x fn-args fn-info]
     (when-not fn-args 
       (let [{:keys [_ args]} (get jsi/js-built-ins-by-built-in x)]
         (merge 
          (if args 
            {:js-built-in-function? true
             :fn-args               args}
            (when (:js-built-in-method-of fn-info)
              {:fn-args
               :lasertag/unknown-function-signature-on-js-built-in-method})))))))

(defn- fn-info [x k include-fn-info?]
  (when include-fn-info?
    (let [fn-info              (fn-info* x k)
          [fn-args defrecord?] (fn-args x fn-info)
          fn-args              (some->> fn-args seq (into []))]
      (merge fn-info
             (when fn-args
               {:fn-args (into [] fn-args)})
             (when defrecord?
               {:defrecord? true})
             #?(:cljs 
                (cljs-fn-args x fn-args fn-info))))))

;; function resolution functions end--------------------------------------------


(defn lsb? [k]
  (contains? #{:long :short :byte} k))

#?(:clj 
   (defn- clj-number-type [x]
     (when-let [k (get java-number-types (type x))]
       (cond
         (lsb? k)      :int
         (= k :double) (cond 
                         (cljc-NaN? x)               :nan
                         (java-negative-infinity? x) :-infinity
                         (infinity? x)               :infinity
                         :else                       k)
         :else         k))))



;; cljs value type helpers -----------------------------------------------------

#?(:cljs 
   (do
     (defn- cljs-number-type [x]
       (when-let [k (get js-number-types (type x))]
         (if (= k :js-number)
           (cond 
             (int? x)   :int
             (float? x) (cond 
                          (cljc-NaN? x)                     :nan
                          (= x js/Number.POSITIVE_INFINITY) :infinity
                          (= x js/Number.NEGATIVE_INFINITY) :-infinity
                          :else                             :float)
             :else      :number)
           k)))

     (defn- cljs-iterable-type [x]
       (when (js-iterable? x)
         (if (= (str x) "[object Generator]") 
           :generator
           :iterable)))
     
     (defn- js-object-instance-map-like
       [x types]
       (when-not (or (-> types :coll)
                     (-> types :scalar-type)
                     (-> types :number-type)
                     (->> [:record         
                           :number         
                           :js-set-types
                           :iterable      
                           :array         
                           :fn            
                           :inst          
                           :js-date       
                           :defmulti      
                           :js-global-this
                           :typed-array]
                          (select-keys types)
                          vals
                          (some #(when (keyword? %) %))))
         
         ;; TODO is having :js-map-like-object and :js-object redundant?
         ;; maybe we don't need either of those, just :map-like and :js are fine

         (when (js-object-instance? x)
           :js-map-like-object)))

     (defn- js-classname [x]
       (when-not (nil? x)
        (let [k (if-let [c (.-constructor x)] 
                  (let [nm (.-name c)]
                    (if-not (string/blank? nm)
                      ;; js class instances
                      (let [ret (keyword nm)]
                        (if (= ret :Object) :Object ret))
                      ;; cljs datatype and recordtype instances
                      (some-> c pwos keyword)))
                  :Object)]
          k)))

     (defn- js-object-instance [x] 
       #?(:cljs 
          (when (js-object-instance? x)
            (js-classname x))))))

;; cljs value type helpers end -------------------------------------------------


(def dom-node-types
  ["ELEMENT_NODE"
   "ATTRIBUTE_NODE"
   "TEXT_NODE"
   "CDATA_SECTION_NODE"
   "PROCESSING_INSTRUCTION_NODE"
   "COMMENT_NODE"
   "DOCUMENT_NODE"
   "DOCUMENT_TYPE_NODE"
   "DOCUMENT_FRAGMENT_NODE"])

(def dom-node-type-keywords
  [:dom-element-node
   :dom-attribute-node
   :dom-text-node
   :dom-cdata-section-node
   :dom-processing-instruction-node
   :dom-comment-node
   :dom-document-node
   :dom-document-type-node
   :dom-document-fragment-node])

(defn- cljc-coll-type [x]
  ;; TODO remove vector, map, and set as these are covered in quicktag
  (cond (vector? x) :vector
        (record? x) :record
        (map? x)    :map
        (set? x)    :set
        (seq? x)    :seq
        :else
        (get cljc-transients (type x) nil)))

#?(:clj
   (defn clj-or-bb-array? [x]
     ;; This try hack is for babashka,
     ;; because .getClass method is sometimes not allowed
     (or 
      (try (some-> x .getClass .isArray)
           (catch Exception e))
      (try (some-> x class .isArray)
           (catch Exception e)))))

(defn- cljc-number? 
  ([x]
   (cljc-number? x nil))
  ([x k]
   (and (number? x)
        (not (contains? #{:nan :-infinity :infinity} k)))))

#?(:clj 
   (defn- clj-all-value-types [{:keys [x k]}]
     (let [number-type (clj-number-type x)]
       (->> [number-type
             (get clj-scalar-types (type x))
             (cljc-coll-type x)
             (when (fn? x) :function)
             (when (contains? cljc-transients-set (type x)) :transient)

             ;; Remove?
             (when (= clojure.lang.PersistentArrayMap (type x)) :array-map)

             ;; Remove?
             (when (= clojure.lang.PersistentList (type x)) :list)

             (when (inst? x) :inst)
             (when (or (contains? #{:vector :map :set :seq} k)
                       (coll? x)
                       (instance? java.util.Collection x)
                       (clj-or-bb-array? x))
               :coll)
             (when (record? x) :record)
             (when (record? x) :datatype)
             (when (cljc-number? x number-type) :number)]
            (remove nil?)
            ;; remove cons and just use (into #{k}), or why not put k in vector?
            (cons k)
            ;; would (apply hash-set) be faster?
            (into #{})))))

;; TODO - consider removing `js-` prefixes and just adding an additional :js tag
#?(:cljs 
   (defn- cljs-all-value-types [{:keys [x k dom-node-type-keyword]}]
     (let [number-type
           (cljs-number-type x)

           t
           (type x)

           types 
           {:number-type    number-type
            :scalar-type    (get cljs-scalar-types t)
            :cljc-coll-type (cljc-coll-type x)
            :js-map-types   (get js-map-types t)
            :js-set-types   (get js-set-types t)
            :iterable       (cljs-iterable-type x)
            :array          (when (array? x) :array)
            :array-map      (when (= cljs.core/PersistentArrayMap t) :array-map)
            :list           (when (= cljs.core/List t) :list)
            :object         (when (object? x) :object)
            :fn             (when (fn? x) :function)
            :inst           (when (inst? x) :inst)
            :defmulti       (when (defmulti? x) :defmulti)
            :js-promise     (when (js-promise? x) :js-promise)
            :js-global-this (when (js-global-this? x) :js-global-this)
            :coll           (when (or (contains? #{:vector :map :set :seq} k)
                                      (coll? x))
                              :coll)
            :transient      (when (contains? cljc-transients-set t)
                              :transient)
            :record         (when (record? x) :record)
            :number         (when (cljc-number? x number-type) :number)
            :typed-array    (when (typed-array? x) :js-typed-array)
            :typed-array+   (when (typed-array? x)
                              (get js-indexed-coll-types
                                   (type x)))}

           js-object-instance-map-like
           (js-object-instance-map-like x types)]

       (->> (vals types)
            (concat [js-object-instance-map-like dom-node-type-keyword])
            (remove nil?)
            (cons k)
            (into #{})))))


(defn- opt? [k opts]
  (if (false? (k opts)) false true))

#?(:cljs 
   (do
     (defn- cljs-coll-type? [x]
       (or (array? x)
           (object? x)
           (contains? jsi/js-built-ins-which-are-iterables
                      (type x))))

     (defn- cljs-class-name [x]
       (let [ret (or (js-object-instance x)
                     ;; TODO - test whether this one is faster
                     (some->> (get jsi/js-built-ins-by-built-in
                                   (type x)
                                   nil)
                              :sym
                              name
                              (str "js/"))
                     ;; TODO - or this one is faster
                     ;; TODO - test this with custom classes
                     (js-classname x))]
         (if (keyword? ret) (subs (str ret) 1) ret)))))


(defn- map-like?* [x k all-tags]
  (or (contains? #{:map :js-object :js-map :js-data-view} k)
      (contains? all-tags :record)
      (contains? all-tags :js-map-like-object)
      #?(:clj (instance? java.util.AbstractMap x))))

(defn- classname* [x]
  #?(:cljs
     (cljs-class-name x)
     :bb
     (let [nm (java-class-name x)]
       (if (some-> nm (string/starts-with? "sci.impl.fns"))
         "sci.impl.fns"
         nm))
     :clj
     (java-class-name x)))

#?(:clj
   (defn- java-*-class? [scalar-type? classname f]
     (boolean (when-not scalar-type?
                (f classname)))))

(defn- cljc-iterable? [x]
  #?(:cljs (js-iterable? x)
     :clj (instance? java.lang.Iterable x)))

(defn- scalar-type? [k]
  ;; TODO - make sure this scalar-type? is accurate for all
  ;; js and java scalars/primitives, then include it in the returned
  ;; entries from tag-map?, and/or include :scalar (or :primitive, or
  ;; both) in the :all-tags set.
  (contains? scalar-types-set k))


;; TODO open PR to add java.util.AbstractCollection to bb,
;; then eliminate this branch

(defn- abstract-instance?* [x]
  #?(:bb
     (instance? java.util.AbstractMap x)
     :clj
     (or (instance? java.util.AbstractCollection x)
         (instance? java.util.AbstractMap x))
     :cljs
     nil))


#?(:cljs
   (defn- cljs-coll-size-try 
     [{:keys [x k all-tags]}]
     (cond
       (or (= :js-object k)
           (contains? all-tags :js-map-like-object))
       (.-length (js/Object.keys x))

       (or (contains? all-tags :js-set)
           (contains? all-tags :js-map))
       (.-size x)
       
       (or (= k :array)
           (typed-array? x))
       (.-length x)

       :else
       (count x))))


#?(:clj
   (defn- clj-coll-size-try 
     [{:keys [x k all-tags abstract-instance?]}]
     (cond
       abstract-instance?
       (.size x)

       (and (= k :array)
            (not (contains? all-tags :java-util-class)))
       (alength x)

       :else
       (count x))))


(defn- cljc-coll-size* [m] 
  (try #?(:clj 
          (clj-coll-size-try m)
          :cljs
          (cljs-coll-size-try m))
       (catch #?(:cljs js/Object :clj Throwable)  e
         (when (:suppress-coll-size-warning? (:opts m))
           (messaging/print-unknown-coll-size-warning e))
         :lasertag.core/unknown-coll-size)))


(defn- coll-size* [{:keys [x coll-type? all-tags] :as m}]
  (when coll-type?
    (when-not (or (contains? all-tags :js-weak-map) ;; <- Add lazy seqs to this, so that coll-size is nil or not set
                  (contains? all-tags :js-weak-set))
      (let [debug-unknown-coll-size?
            #_true false] 
        (if debug-unknown-coll-size? 
          (messaging/mock-unknown-coll-size x)
          (cljc-coll-size* (assoc m
                                  :abstract-instance?
                                  (abstract-instance?*
                                   x))))))))

(defn- cljc-datatypes [x]
  #?(:cljs
     [(when (object? x) :js-object)
      (when (array? x) :js-array)]
     :bb
     nil
     :clj 
     [(when (instance? clojure.lang.IType x)
        :datatype)]))

(defn- all-tags* [{:keys [x k] :as m}]
  (let [all-tags   #?(:cljs (cljs-all-value-types m)
                      :clj (clj-all-value-types m))
        map-like?  (map-like?* x k all-tags)
        set-like?  (or (contains? #{:set :js-set} k)
                       #?(:clj (instance? java.util.AbstractSet x)))]

    ;; TODO - Add :array-like? and maybe :list-like?
    {:classname    (classname* x)
     :scalar-type? (scalar-type? k)
     :all-tags     all-tags
     :set-like?    set-like?
     :map-like?    map-like?
     :coll-type?   (or map-like?
                       set-like?
                       (contains? all-tags :coll)
                       #?(:cljs (cljs-coll-type? x)))}))

#?(:clj
   (defn- java-classes
     [{:keys [scalar-type? classname]}]
     (let [f (partial java-*-class? scalar-type? classname)]
       [(when (f java-lang-class?)
          :java-lang-class)
        (when (f java-util-class?)
          :java-util-class)])))

(defn- all-tags
  [{:keys [x] :as m}]
  (let [{:keys [map-like? set-like? coll-type? all-tags classname]
         :as all-tags-map}
        (all-tags* m)

        more-tags
        (concat  
         (cljc-datatypes x)
         
         #?(:clj (java-classes all-tags-map))
         [(when coll-type? :coll-type)
          (when map-like? :map-like)
          (when set-like? :set-like)
          (when (carries-meta? x) :carries-meta)
          (when (contains? all-tags :transient) :transient)
          (when (contains? all-tags :number) :number-type)
          (when (cljc-iterable? x) :iterable)])

        all-tags   
        (apply conj all-tags (remove nil? more-tags))

        coll-size
        (coll-size* (merge m all-tags-map {:all-tags all-tags}))]
    (merge {:all-tags  all-tags :classname classname}
           (when coll-size {:coll-size coll-size}))))


#?(:cljs
   (defn- dom-node 
     "Helper fn which takes an HTML dom element and returns a 3-element vector.
   Ex:
   (dom-node `<div>hi</div>`)
   =>
   [1                   ; <- type code of node
    \"ELEMENT_NODE\"    ; <- canonical tag name of node
    :dom-element-node]  ; <- kw representation available for consumers of
                             lasertag.core/tag-map"  
     [x]
     (when-let [t (when (js-object-instance? x) (some->> x .-nodeType))]
       (let [n (dec t)]
         [t
          (nth dom-node-types n nil)
          (nth dom-node-type-keywords n nil)]))))


#?(:cljs
   (defn cljs-tag-map* [x k opts]
     (let [[dom-node-type
            dom-node-type-name
            dom-node-type-keyword] (dom-node x)]
       (merge
        ;; Get all the tags
        (when-not (-> opts :include-all-tags? false?)
          (all-tags {:x                     x
                     :k                     k 
                     :dom-node-type-keyword dom-node-type-keyword}))

        ;; Get dom node info 
        (when dom-node-type
          {:dom-node-type      dom-node-type
           :dom-node-type-name dom-node-type-name})

        ;; Get dom element node info 
        (when (= 1 dom-node-type)
          {:dom-element-tag-name x.tagName})
        
        ;; Enhanced reflection for built-in js objects
        (when-not (-> opts :include-js-built-in-object-info? false?)
          (when (= k :object)
            (when-let [{:keys [sym]} 
                       (get jsi/js-built-ins-by-built-in x)]
              {:js-built-in-object?     true
               :js-built-in-object-name (str sym)})))))))

(defn- tag-map*
  [x k k+ opts]
    (merge 
     ;; The lasertag for clj & cljs
     {:tag k+}

     ;; The `type` (calling clojure.core.type, or cljs.core.type) on the value 
     {:type #?(:cljs (if (= k :js-generator)
                       (symbol "#object[Generator]")
                       (type x))
               :clj (type x))}
     
     ;; Optionaly get reflective function info, same for clj & cljs
     (when (contains? #{:function :defmulti :class} k)
       (let [b (not (-> opts :include-function-info? false?))]
         (fn-info x k b)))

     #?(:cljs (cljs-tag-map* x k opts)
        :clj  (when-not (-> opts :include-all-tags? false?)
                (all-tags {:x    x
                           :k    k 
                           :opts opts})))))

(defn- format-result [k {:keys [format]}]
  (if format
    (case format
      :keyword k
      :symbol (symbol (subs (str k) 1))
      :string (subs (str k) 1)
      k)
    k))

#?(:cljs 
   (defn- js-intl-object-key [x]
     (when-let [sym (some->> x
                             type 
                             (get jsi/js-built-in-intl-by-object)
                             :sym)]
       (keyword (str "js/" sym)))))

(defn numberish-type [x]
  (let [f #?(:cljs cljs-number-type :clj clj-number-type)]
    (when-let [t (f x)]
      (if (contains? #{:nan :-infinity :infinity} t)
        t :number))))

#?(:clj
   (defn resolve-class-name-clj [c]
     (or 
      ;; Use find-classname
      (when-let [[_ nm] (re-find #"^class (.*)$" (str c))]
        (let [k (keyword nm)
              k (get clj-names k k)]
          k))
      ;; Use find-classname
      (when-let [nm (some-> c str)]
        (let [k (keyword nm)
              k (get clj-names k k)]
          k)))))

(defn- k* [x t]
  #?(:cljs
     (or (numberish-type x)
         (get cljs-scalar-types t)
         (cljc-coll-type x)
         (when (get js-map-types t) :map)
         (when (get js-set-types t) :set)
         (when (contains? js-indexed-coll-types-set t) :array)
         (cljs-iterable-type x)
         (when (object? x) :object)
         (when (fn? x) :function)
         (when (inst? x) :inst)
         (when (defmulti? x) :defmulti)
         (when (js-promise? x) :promise)
         (when (js-global-this? x) :js-global-this)
         (js-intl-object-key x)
         (when (js-data-view? x) :js-data-view)
         (when (js-array-buffer? x) :byte-array)
         (js-object-instance x)
         (when (js/Number.isNaN x) :nan)
         :lasertag/value-type-unknown)
     :clj
     (or (numberish-type x)
         (get clj-scalar-types t)
         (cljc-coll-type x)
         (when (fn? x) :function)
         (when (inst? x) :inst)
         (when-let [c (type x)]
           (or 
            (when (instance? java.util.AbstractMap x) :map)
            (when (instance? java.util.AbstractSet x) :set)
            (when (clj-or-bb-array? x) :array)
            (when (instance? java.util.ArrayList x) :array)
            (when (instance? java.util.ArrayDeque x) :array)
            (when (instance? java.util.AbstractList x) :seq)
            (resolve-class-name-clj c))))))


;; TODO - Figure out optimal path of execution for perf
;;      - Use macros to generate the cached maps of map-tags?

;;         ;; gen-cached-map (example value of 1.5) would be a macro that uses tag-map to generate a static map which could be returned for common types
;;         (gen-cached-map 1.5) => {:tag :number :class java.lang.Double :all-tags {...} :classname "java.lang.Double"}



(def ^:private coll-type-all-tags
  #{:iterable
    :coll
    :coll-type
    :carries-meta})


(def keyword-class #?(:clj clojure.lang.Keyword :cljs cljs.core/Keyword))
(def keyword-classname #?(:clj "clojure.lang.Keyword" :cljs "cljs.core/Keyword"))

(def string-class #?(:clj java.lang.String :cljs js/String))
(def string-classname #?(:clj "java.lang.String" :cljs "js/String"))

(def boolean-class #?(:clj java.lang.Boolean :cljs js/Boolean))
(def boolean-classname #?(:clj "java.lang.Boolean" :cljs "js/Boolean"))

(def symbol-class #?(:clj clojure.lang.Symbol :cljs cljs.core/Symbol))
(def symbol-classname #?(:clj "clojure.lang.Symbol" :cljs "cljs.core/Symbol"))


(def cached-primitive-types
  {keyword-class {:tag       :keyword
                  :type      keyword-class
                  :all-tags  #{:keyword}
                  :classname keyword-classname}


   string-class  {:tag       :string
                  :type      string-class
                  :all-tags  #{:string}
                  :classname string-classname}

   boolean-class {:tag       :boolean
                  :type      boolean-class
                  :all-tags  #{:boolean}
                  :classname boolean-classname}

   nil              {:tag       :nil
                     :type      nil
                     :all-tags  #{:nil}
                     :classname nil}

   symbol-class  {:tag       :symbol
                  :type      symbol-class
                  :all-tags  #{:symbol :carries-meta}
                  :classname symbol-classname}})


(def integer-class #?(:clj java.lang.Long :cljs :js/Number))
(def integer-classname #?(:clj "java.lang.Long" :cljs "js/Number"))


;; TODO - Add rest of number types
#?(:clj
   (def cached-java-number-types
     {java.lang.Double     {:tag       :number
                            :type      java.lang.Double
                            :all-tags  #{:number-type
                                         :double
                                         :number
                                         :java-lang-class}
                            :classname "java.lang.Double"}

      java.lang.Short      {:tag       :number
                            :type      java.lang.Short
                            :all-tags  #{:number-type
                                         :int
                                         :number
                                         :short
                                         :java-lang-class}
                            :classname "java.lang.Short"}

      java.lang.Long       {:tag       :number
                            :type      java.lang.Long
                            :all-tags  #{:number-type
                                         :int
                                         :number
                                         :long
                                         :java-lang-class}
                            :classname "java.lang.Long"}

      java.lang.Float      {:tag       :number
                            :type      java.lang.Float
                            :all-tags  #{:number-type
                                         :number
                                         :float
                                         :java-lang-class}
                            :classname "java.lang.Float"}

      java.lang.Byte       {:tag       :number
                            :type      java.lang.Byte
                            :all-tags  #{:number-type
                                         :int
                                         :number
                                         :byte
                                         :java-lang-class}
                            :classname "java.lang.Byte"}

      java.math.BigDecimal {:tag       :number
                            :type      java.math.BigDecimal
                            :all-tags  #{:number-type :number :decimal :big-decimal}
                            :classname "java.math.BigDecimal"}

      clojure.lang.BigInt  {:tag      :number
                            :type     clojure.lang.BigInt
                            :all-tags #{:number-type
                                        :number
                                        :big-int}}}
     ))




;; (def array-map-class #?(:clj clojure.lang.PersistentArrayMap :cljs cljs.core/PersistentArrayMap))
;; (def array-map-classname #?(:clj "clojure.lang.PersistentArrayMap" :cljs "cljs.core/PersistentArrayMap"))

;; (def hash-map-class #?(:clj clojure.lang.PersistentHashMap :cljs cljs.core/PersistentHashMap))
;; (def hash-map-classname #?(:clj "clojure.lang.PersistentArrayMap" :cljs "cljs.core/PersistentArrayMap"))

;; (def vector-class #?(:clj clojure.lang.PersistentVector :cljs cljs.core/PersistentVector))
;; (def vector-classname #?(:clj "clojure.lang.PersistentVector" :cljs "cljs.core/PersistentVector"))

;; (def hash-set-class #?(:clj clojure.lang.PersistentHashSet :cljs cljs.core/PersistentHashSet))
;; (def hash-set-classname #?(:clj "clojure.lang.PersistentHashSet" :cljs "cljs.core/PersistentHashSet"))

;; (def lazyseq-class #?(:clj clojure.lang.LazySeq :cljs cljs.core/LazySeq))
;; (def lazyseq-classname #?(:clj "clojure.lang.LazySeq" :cljs "cljs.core/LazySeq"))

;; (def list-class #?(:clj clojure.lang.PersistentList :cljs cljs.core/List))
;; (def list-classname #?(:clj "clojure.lang.PersistentList" :cljs "cljs.core/List"))

(defn cached-coll-types* [& colls]
  (reduce (fn [acc [class classname k keyset]]
            (assoc acc 
                   class 
                   {:tag      k
                    :type     class
                    :all-tags (apply conj
                                     (conj coll-type-all-tags k)
                                     (into keyset))
                    :classname classname}))
          {}
          colls))




(def cached-coll-types
  (cached-coll-types*
   [#?(:clj clojure.lang.PersistentArrayMap :cljs cljs.core/PersistentArrayMap)
    #?(:clj "clojure.lang.PersistentArrayMap" :cljs "cljs.core/PersistentArrayMap") 
    :map 
    #{:array-map :map-like}]
   
   [#?(:clj clojure.lang.PersistentHashMap :cljs cljs.core/PersistentHashMap)
    #?(:clj "clojure.lang.PersistentArrayMap" :cljs "cljs.core/PersistentArrayMap") 
    :map 
    #{:array-map :map-like :map}]
   
   [#?(:clj clojure.lang.PersistentVector :cljs cljs.core/PersistentVector)
    #?(:clj "clojure.lang.PersistentVector" :cljs "cljs.core/PersistentVector") 
    :vector]
   
   [#?(:clj clojure.lang.PersistentHashSet :cljs cljs.core/PersistentHashSet)
    #?(:clj "clojure.lang.PersistentHashSet" :cljs "cljs.core/PersistentHashSet")
    :set
    #{:set-like}]
   
   [#?(:clj clojure.lang.PersistentList :cljs cljs.core/List)
    #?(:clj "clojure.lang.PersistentList" :cljs "cljs.core/List") 
    :seq 
    #{:list}]
   
   [#?(:clj clojure.lang.LazySeq :cljs cljs.core/LazySeq)
    #?(:clj "clojure.lang.LazySeq" :cljs "cljs.core/LazySeq")
    :seq
    #{:lazy}]
   
   ;; TODO - add more
   ))


(defn- cached-tag
  "Short circuit resolution for common value types. Very fast."
  [x t]
  (cond
    (coll? x)
    (when-let [m (get cached-coll-types (type x))]
      (if-not (contains? (:all-tags m) :lazy)
        (assoc m :coll-size (count x))
        m))
    
    (number? x)
    #?(:cljs
       ()
       :clj
       (let [t (type x)]
         (when-let [m (get cached-java-number-types t nil)]
           (if (or (when (= t Float) (java.lang.Float/isInfinite x))
                   (java.lang.Double/isInfinite x))
             (let [k (if (= x ##Inf) :infinity :negative-infinity)]
               (update-in m [:all-tags] into [:infinite k])) 
             m))))
    

    :else
    (get cached-primitive-types (type x) nil)))


(defn- tag* [{:keys [x extras? opts]}]
  (let [t (type x)] 
    (or (when-let [m (cached-tag x t)]
          (if extras?
            m
            (:tag m)))
        (let [k  (k* x t)
              k+ (format-result k opts)]
          (if extras? (tag-map* x k k+ opts) k+)))))

(defn tag
  "Given a value, returns a tag representing the value's type."
  ([x]
   (tag x nil))
  ([x opts] 
   (tag* {:x x :extras? false :opts opts})))

(defn tag-map
  "Given a value, returns a map with information about the value's type."
  ([x]
   (tag-map x nil))
  ([x opts]
   (tag* {:x x :extras? true :opts opts})))
