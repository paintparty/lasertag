(ns typetag.core
  (:require 
   [clojure.string :as string]
   [typetag.interop :as interop]
   [clojure.datafy :refer [datafy]]))

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

(def clj-scalar-types
  #?(:clj 
     {nil                     :nil
      clojure.lang.Symbol     :symbol
      clojure.lang.Keyword    :keyword
      java.lang.String        :string
      java.lang.Boolean       :boolean
      java.util.regex.Pattern :regex
      java.lang.Character     :char}))

(def cljs-scalar-types
  #?(:cljs 
     {cljs.core/Symbol  :symbol
      cljs.core/Keyword :keyword
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

(def cljs-coll-types
  #?(:cljs
     {cljs.core/PersistentVector   :vector
      cljs.core/PersistentArrayMap :map
      cljs.core/PersistentHashMap  :map
      cljs.core/PersistentHashSet  :set
      cljs.core/LazySeq            :seq
      cljs.core/IntegerRange       :seq
      cljs.core/List               :list}))

(def clj-coll-types
  #?(:clj
     {clojure.lang.PersistentVector   :vector
      clojure.lang.PersistentArrayMap :map
      clojure.lang.PersistentHashMap  :map
      clojure.lang.PersistentHashSet  :set
      clojure.lang.LazySeq            :seq
      clojure.lang.LongRange          :seq
      clojure.lang.PersistentList     :list}))

(def js-coll-types
  #?(:cljs
     {js/Map     :js/Map
      js/WeakMap :js/WeakMap
      js/Set     :js/Set
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
(defn- pwos [x] (with-out-str (print x)))

(defn- datafy-str [x] (pwos (datafy x)))

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
(defn- js-object? [x] #?(:cljs (object? x) :clj false))
(defn- js-array? [x] #?(:cljs (array? x) :clj false))
(defn- js-promise? [x] #?(:cljs (instance? js/Promise x) :clj false))
(defn- js-date? [x] #?(:cljs (instance? js/Date x) :clj false))
(defn- java-byte? [x] #?(:clj (instance? Byte x) :cljs false))
(defn- java-short? [x] #?(:clj (instance? Short x) :cljs false))
(defn- java-long? [x] #?(:clj (instance? Short x) :cljs false))
(defn- java-decimal? [x] #?(:clj (decimal? x) :cljs false))


;; cljs fn resolution functions start---------------
(defn- js-built-in-method-of [x name-prop fn-nm]
  #?(:cljs
     (when (and (string? name-prop)
                (re-find #"[^\$\.-]" name-prop))
       (let [{:keys [objects-by-method-name 
                     objects-by-unique-method-name]} interop/js-built-in-methods]
         (if-let [built-in-candidates (get objects-by-method-name fn-nm)]
           (let [o             (first (filter #(= x (aget (.-prototype %) fn-nm)) 
                                              built-in-candidates))
                 {:keys [sym]} (get interop/js-built-ins-by-built-in o)]
             {:js-built-in-method-of sym})
           (when-let [built-in (get objects-by-unique-method-name fn-nm)]
             (let [{:keys [sym]} (get interop/js-built-ins-by-built-in
                                      built-in)]
               {:js-built-in-method-of sym})))))))

(defn- cljs-defmulti [sym]
  (when (symbol? sym)
    (let [[fn-ns fn-nm] (-> sym str (string/split #"/"))]
      {:fn-ns   fn-ns
       :fn-name fn-nm
       :fn-args :typetag/multimethod})) )

(defn- cljs-fn [x s]
  (let [bits          (string/split s #"\$")
        [fn-ns fn-nm] (partition-drop-last bits)
        fn-nm         (demunge-fn-name fn-nm)]
    (merge {:fn-name fn-nm}
           (when (seq fn-ns)
             {:fn-ns (string/join "." fn-ns)}) 
           (js-built-in-method-of x s fn-nm))))

(defn- cljs-fn-alt [o]
  (let [datafied-str (pwos o)]
    (if-let [[_ fn-ns fn-nm] (re-find #"^(.+)/(.+)$" datafied-str)]
      {:fn-name           (demunge-fn-name fn-nm)
       :fn-ns             fn-ns
       :cljs-datatype-fn? true}
      {:lamda? true})))

(defn- fn-info* [x k]
  #?(:cljs 
     (let [datafied  (datafy x)
           name-prop (.-name datafied)]
       (cond
         (= k :defmulti)
         (cljs-defmulti name-prop)

         (not (string/blank? name-prop))
         (cljs-fn x name-prop)

         :else
         (cljs-fn-alt datafied)))
     :clj
     (if (= k :defmulti)
      {:fn-args :typetag/multimethod}
      (if (= k :java.lang.Class)
        (let [[_ nm] (re-find #"^class (.*)$" (str x))
              bits   (string/split nm #"\.")
              fn-ns  (string/join "." (drop-last bits))
              fn-nm  (last bits)]
          {:fn-ns   (string/replace fn-ns #"_" "-")
           :fn-name fn-nm
           :fn-args :typetag/unknown-function-signature-on-java-class})
        (let [datafied (datafy-str x)
              [_ nm*]  (re-find #"^#object\[([^\s]*)\s" datafied)]
          (when (and nm* (not (string/blank? nm*)))
            (let [[fn-ns fn-nm _anon] (string/split nm* #"\$")
                  fn-nm               (when-not _anon (demunge-fn-name fn-nm))]
              (merge (if fn-nm 
                       (if (re-find #"^fn--\d+$" fn-nm)
                         {:lamda? true}
                         {:fn-name fn-nm})
                       {:lamda? true})
                     {:fn-ns   (string/replace fn-ns #"_" "-")
                      :fn-args :typetag/unknown-function-signature-on-clj-function}))))))))

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
       (let [{:keys [_ args]} (get interop/js-built-ins-by-built-in x)]
         (merge (if args 
                  {:js-built-in-function? true
                   :fn-args               args}
                  (when (:js-built-in-method-of fn-info)
                    {:fn-args :typetag/unknown-function-signature-on-js-built-in-method})))))))

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

(defn- cljs-number-type [x]
  #?(:cljs 
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
         k))) )

(defn- cljs-iterable-type [x]
  #?(:cljs 
     (when (js-iterable? x)
       (or (get js-coll-types (type x))
           (get js-indexed-coll-types (type x))
           (if (= (str x) "[object Generator]") 
             :js/Generator
             :js/Iterable)))) )


(defn- js-object-instance [x] 
  #?(:cljs 
     (when (js-object-instance? x)
      (let [k (if-let [c (.-constructor x)] 
                (let [nm (.-name c)]
                  (if-not (string/blank? nm)
                    ;; js class instances
                    (let [ret (keyword nm)]
                      (if (= ret :Object) :js/Object ret))
                    ;; cljs datatype and recordtype instances
                    (some-> c datafy-str keyword)))
                :js/Object)]
        k))))

#?(:clj 
   (defn- clj-all-value-types [x k]
     (->> [(clj-number-type x)
           (get clj-scalar-types (type x))
           (get clj-coll-types (type x))
           (when (fn? x) :function)
           ;; Extra types, maybe useful info
           (when (coll? x) :coll)
           (when (record? x) :record)
           (when (number? x) :number)]
          (remove nil?)
          (cons k)
          (into #{}))))
#?(:cljs 
   (defn- cljs-all-value-types [x k]
     (->> [(cljs-number-type x)
           (get cljs-scalar-types (type x))
           (get cljs-coll-types (type x))
           (get js-coll-types (type x))
           (cljs-iterable-type x)
           (when (array? x) :js/Array)
           (when (object? x) :js/Object)
           (when (fn? x) :function)
           (when (js-date? x) :js/Date)
           (when (defmulti? x) :defmulti)
           (when (js-promise? x) :js/Promise)
           (when (js-global-this? x) :js/globalThis)
           ;; Extra types, maybe useful info
           (when (coll? x) :coll)
           (when (record? x) :record)
           (when (number? x) :number)

           ;; Maybe we shouldn't check js-object-instance
           ;; Because it will do things like
           ;; (js-object-instance (range 10)) => cljs.core/Interange
           #_(js-object-instance x)
           ]
          (remove nil?)
          (cons k)
          (into #{}))))

(defn- opt? [k opts]
  (if (false? (k opts)) false true))

#?(:cljs 
   (defn- cljs-coll-type? [x]
     (or (object? x)
         (array? x)
         (contains? interop/js-built-ins-which-are-iterables
                    (type x)))))

(defn- all-typetags [x k]
  (let [all-typetags #?(:cljs (cljs-all-value-types x k)
                        :clj (clj-all-value-types x k))]
    (assoc {}
           :all-typetags
           all-typetags
           :coll-type?
           (or (contains? all-typetags :coll)
               #?(:cljs (cljs-coll-type? x)))
           :number-type?
           (contains? all-typetags :number))))

(defn- tag-map*
  [x k k+ opts]
  (merge 
   {:typetag k+}

   (when (opt? :include-all-typetags? opts)
     (all-typetags x k))

   {:type #?(:cljs (if (= k :js/Generator)
                     (symbol "#object[Generator]")
                     (type x))
             :clj (type x))}
   
   (when (contains? #{:function :defmulti :java.lang.Class} k)
     (let [b (opt? :include-function-info? opts)]
       #?(:cljs (fn-info x k b)
          :clj (fn-info x k b))))

   #?(:cljs 
      (when (opt? :include-js-built-in-object-info? opts)
        (when (= k :js/Object)
          (when-let [{:keys [sym]} (get interop/js-built-ins-by-built-in x)]
            {:js-built-in-object?     true
             :js-built-in-object-name (str sym)})))
      :clj nil)))

(defn- format-result [k {:keys [format]}]
  (if format
    (case format
      :keyword k
      :symbol (symbol (subs (str k) 1))
      :string (str (subs (str k) 1))
      k)
    k))

(defn- tag* [{:keys [x extras? opts]}]
  #?(:clj
     (let [k (or (clj-number-type x)
                 (get clj-scalar-types (type x))
                 (get clj-coll-types (type x))
                 (when (fn? x) :function)
                 (when-let [c (class x)]
                   (when-let [[_ nm] (re-find #"^class (.*)$" (str c))]
                     (let [k (keyword nm)
                           k (get clj-names k k)]
                       x
                       k))))
           k+ (format-result k opts)]
       (if extras? (tag-map* x k k+ opts) k))
     :cljs
     (let [k (or (cljs-number-type x)
                 (get cljs-scalar-types (type x))
                 (get cljs-coll-types (type x))
                 (get js-coll-types (type x))
                 (cljs-iterable-type x)
                 (when (array? x) :js/Array)
                 (when (object? x) :js/Object)
                 (when (fn? x) :function)
                 (when (js-date? x) :js/Date)
                 (when (defmulti? x) :defmulti)
                 (when (js-promise? x) :js/Promise)
                 (when (js-global-this? x) :js/globalThis)
                 (js-object-instance x)
                 :typetag/value-type-unknown)
           k+ (format-result k opts)]
       (if extras? (tag-map* x k k+ opts) k))))

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

