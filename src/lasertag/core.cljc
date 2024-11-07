;; TODO 
;; - Add more tests
;; - Figure out way to do "%" for lamda args
;; - Add array-like? 
;; - Add list-like? 
;; - Add scalar-type? (or scalar?)
;; - Use :array instead of :js/Array (or any of the indexed array types)
;; - Use :set instead of :js/Set (or :js/WeakSet)
;; - Use :map instead of :js/Map (or :js/WeakMap)
;; - Use :number instead of :js/Number (or :js/BigInt)
;; - maybe lowercase :infinity and :-infinity instead of :Infinity :-Infinity
;; - maybe lowercase :nan instead of :NaN

(ns lasertag.core
  (:require 
   [clojure.string :as string]
   #?(:cljs [lasertag.cljs-interop :as cljs-interop]))
  #?(:clj
     (:import (clojure.lang PersistentVector$TransientVector
                            PersistentHashSet$TransientHashSet
                            PersistentArrayMap$TransientArrayMap
                            PersistentHashMap$TransientHashMap))))

(defn- ? 
  "Debugging macro internal to lib"
  ([x]
   (? nil x))
  ([l x]
   (println (when l (str " " l "\n")) x)
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

(def cljc-transients 
  {#?(:cljs cljs.core/TransientVector :clj PersistentVector$TransientVector) :vector
   #?(:cljs cljs.core/TransientHashSet :clj PersistentHashSet$TransientHashSet)  :set
   #?(:cljs cljs.core/TransientArrayMap :clj PersistentArrayMap$TransientArrayMap)  :map
   #?(:cljs cljs.core/TransientHashMap :clj PersistentHashMap$TransientHashMap)  :map })

(def cljc-transients-set
  (into #{} (keys cljc-transients)))

(def clj-scalar-types
  #?(:clj 
     {nil                     :nil
      clojure.lang.Symbol     :symbol
      clojure.lang.Keyword    :keyword
      java.lang.String        :string
      java.lang.Boolean       :boolean
      java.util.regex.Pattern :regex
      java.lang.Character     :char
      java.util.UUID          :uuid}))

(def cljs-scalar-types
  #?(:cljs 
     {cljs.core/Symbol  :symbol
      cljs.core/Keyword :keyword
      cljs.core/UUID    :uuid
      js/String         :string
      js/Boolean        :boolean
      js/RegExp         :regex
      nil               :nil}))

(def js-number-types
  #?(:cljs 
     {js/Number :js/Number
      js/BigInt :js/BigInt}))

(def java-number-types
  #?(:clj 
     {java.lang.Double     :double
      java.lang.Short      :short
      java.lang.Long       :long
      java.lang.Float      :float
      java.lang.Byte       :byte
      java.math.BigDecimal :decimal}))

(def scalar-types-set
  #?(:cljs
     (as-> #{} $
       (apply conj $ (vals cljs-scalar-types))
       (apply conj $ (vals js-number-types)))
     :clj
     (as-> #{} $
       (apply conj $ (vals clj-scalar-types))
       (apply conj $ (vals java-number-types)))))

(def js-map-types
  #?(:cljs
     {js/Map     :js/Map
      js/WeakMap :js/WeakMap}))

(def js-set-types
  #?(:cljs
     {js/Set     :js/Set
      js/WeakSet :js/WeakSet}))

(def js-indexed-coll-types
  #?(:cljs
     {js/Array             :js/Array
      js/Int8Array         :js/Int8Array
      js/Uint8Array        :js/Uint8Array
      js/Uint8ClampedArray :js/Uint8ClampedArray
      js/Int16Array        :js/Int16Array
      js/Uint16Array       :js/Uint16Array
      js/Int32Array        :js/Int32Array
      js/Uint32Array       :js/Uint32Array
      js/BigInt64Array     :js/BigInt64Array
      js/BigUint64Array    :js/BigUint64Array
      js/Float32Array      :js/Float32Array
      js/Float64Array      :js/Float64Array}))

(def clj-names
  {:clojure.lang.MultiFn :defmulti
   :clojure.lang.Ratio :ratio})

(defn carries-meta? [x]
  #?(:clj  (instance? clojure.lang.IObj x)
     :cljs (satisfies? IWithMeta x)))

(defn- pwos [x] (with-out-str (print x)))

(defn- lamda-args [args]
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
(defn- java-negative-infinity? [x] #?(:clj (and (number? x) (= "-Infinity" (str x)))))
(defn- js-global-this? [x] #?(:cljs (= x js/globalThis)))
(defn- js-object-instance? [x] #?(:cljs (instance? js/Object x)))
(defn- defmulti? [x] #?(:cljs (= (type x) cljs.core/MultiFn)))

;; (defn- js-object? [x] #?(:cljs (object? x) :clj false))
;; (defn- js-array? [x] #?(:cljs (array? x) :clj false))

(defn- js-promise? [x] #?(:cljs (instance? js/Promise x) :clj false))
(defn- js-date? [x] #?(:cljs (instance? js/Date x) :clj false))
(defn- js-data-view? [x] #?(:cljs (instance? js/DataView x) :clj false))
(defn- js-array-buffer? [x] #?(:cljs (instance? js/ArrayBuffer x) :clj false))

;; (defn- java-byte? [x] #?(:clj (instance? Byte x) :cljs false))
;; (defn- java-short? [x] #?(:clj (instance? Short x) :cljs false))
;; (defn- java-long? [x] #?(:clj (instance? Short x) :cljs false))
;; (defn- java-decimal? [x] #?(:clj (decimal? x) :cljs false))

#?(:cljs 
   (defn typed-array? [x]
     (and (js/ArrayBuffer.isView x)
          (not (instance? js/ArrayBuffer x))
          (not (instance? js/DataView x)))))

;; cljs fn resolution functions start---------------
#?(:cljs 
   (do 
     (defn- js-built-in-map [o]
       (let [{:keys [sym]} (get cljs-interop/js-built-ins-by-built-in o)]
         {:js-built-in-method-of      o
          :js-built-in-method-of-name (some-> sym name)
          :js-built-in-function?      true}))

     (defn- js-built-in-method-of [x name-prop fn-nm]
       (when 
        (and (string? name-prop)
             (re-find #"[^\$\.-]" name-prop))
         (if-let [built-in-candidates (get cljs-interop/objects-by-method-name fn-nm)]
           (let [o             (first (filter #(= x (aget (.-prototype %) fn-nm)) 
                                              built-in-candidates))]
             (js-built-in-map o))
           (when-let [built-in (get cljs-interop/objects-by-unique-method-name fn-nm)]
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
             built-in?     (or (contains? cljs-interop/js-built-in-objects x)
                               (contains? cljs-interop/js-built-in-functions x))]
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
           {:lamda? true})))))

(defn- fn-info* [x k]
  #?(:cljs 
     (let [name-prop (.-name x)]
       (cond
         (= k :defmulti)
         (cljs-defmulti name-prop)

         (not (string/blank? name-prop))
         (cljs-fn x name-prop)

         :else
         (cljs-fn-alt x)))
     :clj
     (if (= k :defmulti)
      {:fn-args :lasertag/multimethod}
      (if (= k :java.lang.Class)
        (let [[_ nm] (re-find #"^class (.*)$" (str x))
              bits   (string/split nm #"\.")
              fn-ns  (string/join "." (drop-last bits))
              fn-nm  (last bits)]
          {:fn-ns   (string/replace fn-ns #"_" "-")
           :fn-name fn-nm
           :fn-args :lasertag/unknown-function-signature-on-java-class})
        (let [pwo-stringified (pwos x)
              [_ nm*]         (re-find #"^#object\[([^\s]*)\s" pwo-stringified)]
          (when (and nm* (not (string/blank? nm*)))
            (let [[fn-ns fn-nm _anon] (string/split nm* #"\$")
                  fn-nm               (when-not _anon (demunge-fn-name fn-nm))]
              (merge (if fn-nm 
                       (if (re-find #"^fn--\d+$" fn-nm)
                         {:lamda? true}
                         {:fn-name fn-nm})
                       {:lamda? true})
                     {:fn-ns   (string/replace fn-ns #"_" "-")
                      :fn-args :lasertag/unknown-function-signature-on-clj-function}))))))))

(defn- fn-args* [x]
  (let [[_ _ s] (re-find cljs-serialized-fn-info (str x))
        strings (some-> s
                        (string/split #","))
        syms    (some->> strings
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

(defn- fn-args-lamda [coll fn-info]
  (if (:lamda? fn-info) (lamda-args coll) coll))

(defn- fn-args [x fn-info]
  (let [fn-args              (fn-args* x)
        [fn-args defrecord?] (fn-args-defrecord fn-args fn-info)
        fn-args              (fn-args-lamda fn-args fn-info)]
    [fn-args defrecord?]))

#?(:cljs 
   (defn- cljs-fn-args [x fn-args fn-info]
     (when-not fn-args 
       (let [{:keys [_ args]} (get cljs-interop/js-built-ins-by-built-in x)]
         (merge (if args 
                  {:js-built-in-function? true
                   :fn-args               args}
                  (when (:js-built-in-method-of fn-info)
                    {:fn-args :lasertag/unknown-function-signature-on-js-built-in-method})))))))

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

;; function resolution functions end---------------

#?(:clj 
   (defn- clj-number-type [x]
     (when-let [k (get java-number-types (type x))]
       (cond
         (contains? #{:long :short :byte} k)
         :int
         (= k :double)
         (cond 
           (cljc-NaN? x) :NaN
           (java-negative-infinity? x) :-Infinity
           (infinity? x) :Infinity
           :else
           k)
         :else
         k))))

#?(:cljs 
   (do
     (defn- cljs-number-type [x]
       (when-let [k (get js-number-types (type x))]
         (if (= k :js/Number)
           (cond (int? x)
                 :int
                 (float? x)
                 (cond 
                   (js/Number.isNaN x)
                   :NaN
                   (= x js/Number.POSITIVE_INFINITY)
                   :Infinity
                   (= x js/Number.NEGATIVE_INFINITY)
                   :-Infinity
                   :else
                   :float)
                 :else
                 :number)
           k)))

     (defn- cljs-iterable-type [x]
       (when (js-iterable? x)
         (if (= (str x) "[object Generator]") 
           :js/Generator
           :js/Iterable)))
     
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
         (when (js-object-instance? x)
           :js/map-like-object)))

     (defn- js-classname [x]
       (let [k (if-let [c (.-constructor x)] 
                 (let [nm (.-name c)]
                   (if-not (string/blank? nm)
                     ;; js class instances
                     (let [ret (keyword nm)]
                       (if (= ret :Object) :js/Object ret))

                     ;; cljs datatype and recordtype instances
                     (some-> c pwos keyword)))
                 :js/Object)]
         k))

     (defn- js-object-instance [x] 
       #?(:cljs 
          (when (js-object-instance? x)
            (js-classname x))))))


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
  (cond (vector? x)             :vector
        (and (map? x)
             (not (record? x))) :map
        (set? x)                :set
        (seq? x)                :seq
        :else
        (get cljc-transients (type x) nil)))

#?(:clj 
   (defn- clj-all-value-types [x k]
     (->> [(clj-number-type x)
           (get clj-scalar-types (type x))
           (cljc-coll-type x)
           (when (fn? x) :function)
           (when (contains? cljc-transients-set (type x)) :transient)
           ;; Extra types - useful info
           (when (= clojure.lang.PersistentArrayMap (type x)) :array-map)
           (when (= clojure.lang.PersistentList (type x)) :list)
           (when (inst? x) :inst)
           (when (or (contains? #{:vector :map :set :seq} k)
                     (coll? x)
                     (instance? java.util.Collection x)
                     (some-> x .getClass .isArray))
             :coll)

          ;; TODO - leave this out for now
          ;;  (when (instance? java.util.AbstractList x)
          ;;    :java.util.AbstractList)

           (when (record? x) :record)
           (when (number? x) :number)]
          (remove nil?)
          (cons k)
          (into #{}))))
#?(:cljs 
   (defn- cljs-all-value-types [x k dom-node-type-keyword]
     (let [types 
           {:number-type    (cljs-number-type x)
            :scalar-type    (get cljs-scalar-types (type x))
            :cljc-coll-type (cljc-coll-type x)
            :js-map-types   (get js-map-types (type x))
            :js-set-types   (get js-set-types (type x))
            :iterable       (cljs-iterable-type x)
            :array          (when (array? x) :js/Array)
            :array-map      (when (= cljs.core/PersistentArrayMap (type x))
                              :array-map)
            :list           (when (= cljs.core/List (type x)) :list)
            :object         (when (object? x) :js/Object)
            :fn             (when (fn? x) :function)
            :inst           (when (inst? x) :inst)
            :js-date        (when (js-date? x) :js/Date)
            :defmulti       (when (defmulti? x) :defmulti)
            :js-promise     (when (js-promise? x) :js/Promise)
            :js-global-this (when (js-global-this? x) :js/globalThis)
             ;; Extra types - useful info
            :coll           (when (or (contains? #{:vector :map :set :seq} k)
                                      (coll? x))
                              :coll)
            :transient      (when (contains? cljc-transients-set (type x))
                              :transient)
            :record         (when (record? x) :record)
            :number         (when (number? x) :number)
            :typed-array    (when (typed-array? x) :js/TypedArray)}

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
           (contains? cljs-interop/js-built-ins-which-are-iterables
                      (type x))))

     (defn- cljs-class-name [x]
       (let [ret (or (js-object-instance x)
                     ;; TODO - test whether this one is faster
                     (some->> (get cljs-interop/js-built-ins-by-built-in
                                   (type x)
                                   nil)
                              :sym
                              name
                              (str "js/"))
                     ;; TODO - or this one is faster
                     ;; TODO - test this with custom classes
                     (js-classname x))]
         (if (keyword? ret) (subs (str ret) 1) ret)))))

#?(:clj
   (do
     (defn- java-util-class? [s]
       (boolean (some-> s (string/starts-with? "java.util"))))

     (defn- java-lang-class? [s]
       (boolean (some->> s (re-find #"java\.lang"))))
     
     (defn java-class-name [x]
       (some-> x
               type
               .getName
               ;; Example of what these last 2 do:
               ;; "[Ljava.lang.Object;" -> "Ljava.lang.Object"
               (string/replace #"^\[" "")
               (string/replace #";$" "")))))

;; TODO - Add array-like? and maybe list-like?
(defn- all-tags [x k dom-node-type-keyword]
  (let [ all-tags #?(:cljs (cljs-all-value-types x k dom-node-type-keyword)
                    :clj (clj-all-value-types x k))
        map-like?    (or (contains? #{:map :js/Object :js/Map :js/DataView} k)
                         (contains? all-tags :record)
                         (contains? all-tags :js/map-like-object)
                         #?(:clj (instance? java.util.AbstractMap x)))
        set-like?    (or (contains? #{:set :js/Set} k)
                         #?(:clj (instance? java.util.AbstractSet x)))
        coll-type?   (or map-like?
                         set-like?
                         (contains? all-tags :coll)
                         #?(:cljs (cljs-coll-type? x)))
        coll-size    (when coll-type?
                       (cond 
                         (or (= :js/Object k)
                             (contains? all-tags :js/map-like-object))
                         #?(:cljs
                            (.-length (js/Object.keys x))
                            :clj nil)

                         (or (= k :js/Array)
                             #?(:cljs (typed-array? x)))  
                         (.-length x)

                         (contains? #{:js/Set :js/Map} k)
                         (.-size x)

                         :else
                         (count x)))

        ;; TODO - make sure this scalar-type? is accurate for all
        ;; js and java scalars/primitives, then include it in the returned
        ;; entries from tag-map?, and/or include :scalar (or :primitive, or
        ;; both) in the :all-tags set.
        scalar-type? (contains? scalar-types-set k) 
        classname    #?(:cljs
                        (cljs-class-name x)
                        :clj
                        (java-class-name x))
        java-lang-class? (boolean (when-not scalar-type?
                                    #?(:clj (java-lang-class? classname)
                                       :cljs nil)))
        java-util-class? (boolean (when-not scalar-type?
                                    #?(:clj (java-util-class? classname)
                                       :cljs nil)))
        transient?       (contains? all-tags :transient)
        number-type?     (contains? all-tags :number)
        carries-meta?    (carries-meta? x)
        all-tags         (apply conj
                                all-tags
                                (remove nil?
                                        (concat
                                         [(when coll-type? :coll-type)
                                          (when carries-meta? :carries-meta)
                                          (when map-like? :map-like)
                                          (when set-like? :set-like)
                                          (when transient? :transient)
                                          (when number-type? :number-type)
                                          (when java-lang-class? :java-lang-class)
                                          (when java-util-class? :java-util-class)]
                                         #?(:cljs
                                            (merge (when (object? x)
                                                     :js-object)
                                                   (when (array? x)
                                                     :js-array))))))]
    (merge 
     {:all-tags  all-tags
      :classname classname}
     (when coll-size {:coll-size coll-size}))))

(defn- dom-node 
  "Helper fn which takes an HTML dom element and returns a 3-element vector.
   Ex:
   (dom-node `<div>hi</div>`)
   =>
   [1                   ; <- type code of node
    \"ELEMENT_NODE\"    ; <- canonical tag name of node
    :dom-element-node]  ; <- kw representation available for consumers of lasertag.core/tag-map"  
  [x]
  (when-let [t (when (js-object-instance? x) (some->> x .-nodeType))]
    (let [n (dec t)]
      [t
       (nth dom-node-types n nil)
       (nth dom-node-type-keywords n nil)])))

(defn- tag-map*
  [x k k+ opts]
    (merge 
     ;; The lasertag for clj & cljs
     {:tag k+}

     ;; The `type` (result of calling clojure.core.type), or cljs.core.type on the value 
     {:type #?(:cljs (if (= k :js/Generator) (symbol "#object[Generator]") (type x))
               :clj (type x))}
     
     ;; Optionaly get reflective function info, same for clj & cljs
     (when (contains? #{:function :defmulti :java.lang.Class} k)
       (let [b (opt? :include-function-info? opts)]
         #?(:cljs (fn-info x k b)
            :clj (fn-info x k b))))

      ;; Just for ClojureScript
     #?(:cljs 
        (let [[dom-node-type
               dom-node-type-name
               dom-node-type-keyword]    (dom-node x)]

          (merge
           ;; Get all the tags
           (when (opt? :include-all-tags? opts)
             (all-tags x k dom-node-type-keyword))

           ;; Get dom node info 
           (when dom-node-type
             {:dom-node-type      dom-node-type
              :dom-node-type-name dom-node-type-name})

           ;; Get dom element node info 
           (when (= 1 dom-node-type)
             {:dom-element-tag-name x.tagName})

           ;; Enhanced reflection for built-in js objects
           (when (opt? :include-js-built-in-object-info? opts)
             (when (= k :js/Object)
               (when-let [{:keys [sym]} 
                          (get cljs-interop/js-built-ins-by-built-in x)]
                 {:js-built-in-object?     true
                  :js-built-in-object-name (str sym)})))))
        

        ;; Just for Clojure
        :clj (when (opt? :include-all-tags? opts)
               ;; Get all the tags
               (all-tags x k nil)))))


(defn- format-result [k x {:keys [format]}]
  (if format
    (case format
      :keyword k
      :symbol (symbol (subs (str k) 1))
      :string (str (subs (str k) 1))
      k)
    #?(:cljs k
       :clj (if (record? x)
              (-> k name (string/split #"\.") last keyword)
              k))))

#?(:cljs 
   (defn- js-intl-object-key [x]
     (when-let [sym (some->> x
                             type 
                             (get cljs-interop/js-built-in-intl-by-object)
                             :sym)]
       (keyword (str "js/" sym)))))


(defn- tag* [{:keys [x extras? opts]}]
  #?(:clj
     (let [k  (or (clj-number-type x)
                  (get clj-scalar-types (type x))
                  (cljc-coll-type x)
                  (when (fn? x) :function)
                  (when-let [c (class x)]
                    (or 
                     (when (instance? java.util.AbstractMap x) :map)
                     (when (instance? java.util.AbstractSet x) :set)
                     (when (some-> x .getClass .isArray) :array)
                     (when (instance? java.util.ArrayList x) :array)
                     (when (instance? java.util.ArrayDeque x) :array)
                     (when (instance? java.util.AbstractList x) :seq)
                     (when-let [[_ nm] (re-find #"^class (.*)$" (str c))]
                       (let [k (keyword nm)
                             k (get clj-names k k)]
                         k)))))
           k+ (format-result k x opts)]
       (if extras? (tag-map* x k k+ opts) k+))
     :cljs
     (let [k  (or (cljs-number-type x)
                  (get cljs-scalar-types (type x))
                  (cljc-coll-type x)
                  (get js-map-types (type x))
                  (get js-set-types (type x))
                  (get js-indexed-coll-types (type x))
                  (cljs-iterable-type x)
                  (when (array? x) :js/Array)
                  (when (object? x) :js/Object)
                  (when (fn? x) :function)
                  (when (js-date? x) :js/Date)
                  (when (defmulti? x) :defmulti)
                  (when (js-promise? x) :js/Promise)
                  (when (js-global-this? x) :js/globalThis)
                  (js-intl-object-key x)
                  (when (js-data-view? x) :js/DataView)
                  (when (js-array-buffer? x) :js/ArrayBuffer)
                  (js-object-instance x)
                  :lasertag/value-type-unknown)
           k+ (format-result k x opts)]
       (if extras? (tag-map* x k k+ opts) k+))))

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

