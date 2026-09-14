(ns lasertag.fns
  (:require 
   [lasertag.core :refer [tag tag-map]]
   [clojure.string :as str]
   #?(:cljs [lasertag.jsi.native-plus :refer [objects-by-unique-method-name
                                              objects-by-method-name
                                              js-built-in-objects
                                              js-built-in-functions]])
   #?(:cljs [lasertag.jsi.native :as jsi.native])))

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

(defn- pwos [x] (with-out-str (print x)))

(defn- partition-drop-last [coll]
  [(drop-last coll)
   (last coll)])

(defn- demunge-fn-name [s]
  (reduce
   (fn [acc [k v]]
     (str/replace acc (re-pattern k) v))
   s 
   char-map))

;; TODO - find simpler way to resolve classname such as .getName
#?(:clj
   (do
     (defn- find-classname [x]
       ;; For custom datatypes ^class prefix is not present in str'd babashka classname
       (re-find #"(?:^class )?(.*)$" (str x)))

     (defn- resolve-classname [x]
       (let [[_ nm] (find-classname x)
             bits   (some-> nm (str/split #"\."))]
         {:fn-ns   (-> bits
                       drop-last
                       (->> (str/join "."))
                       (str/replace #"_" "-")) 
          :fn-name (last bits)
          :fn-args :lasertag/unknown-function-signature-on-java-class}))

     (defn- resolve-fn-name [x]
       (let [pwo-stringified (pwos x)
             [_ nm*]         (re-find #"^#object\[([^\s]*)\s" pwo-stringified)]
         (when (and nm* (not (str/blank? nm*)))
           (let [[fn-ns fn-nm _anon] (str/split nm* #"\$")
                 fn-ns               (str/replace fn-ns #"_" "-")
                 fn-nm               (when-not _anon (demunge-fn-name fn-nm))]
             (merge (when fn-nm 
                      (when-not (re-find #"^fn--\d+$" fn-nm)
                        {:fn-name fn-nm}))
                    {:fn-ns   fn-ns
                     :fn-args :lasertag/unknown-function-signature-on-clj-function})))))))

#?(:cljs 
   (do 
     (def cljs-serialized-fn-info   #"^\s*function\s*([^\(]+)\s*\(([^\)]*)\)\s*\{")
     (defn- lambda-args [args]
       (if (every? #(re-find #"_SHARP_$" (name %)) args)
         (let [num-args (count args)]
           (map-indexed (fn [idx _]
                          ;; TODO figure out a way to do just a %
                          (symbol (str "%" (inc idx))))
                        (range num-args)))
         args))

     (defn- cljs-fn-args [x fn-args fn-info]
       (when-not fn-args 
         (let [{:keys [_ args]} (get jsi.native/js-built-ins-by-built-in x)]
           (merge 
            (if args 
              {:js-built-in-function? true
               :fn-args               args}
              (when (:js-built-in-method-of fn-info)
                {:fn-args :lasertag/unknown-function-signature-on-js-built-in-method}))))))
     
     (defn- js-built-in-map [o]
       (let [{:keys [sym]} (get jsi.native/js-built-ins-by-built-in o)]
         {:js-built-in-method-of      o
          :js-built-in-method-of-name (some-> sym name)
          :js-built-in-function?      true}))

     (defn- js-built-in-method-of [x name-prop fn-nm]
       (when 
        (and (string? name-prop)
             (re-find #"[^\$\.-]" name-prop))
         (if-let [built-in-candidates
                  (get objects-by-method-name fn-nm)]
           (let [o (first (filter #(= x (aget (.-prototype %) fn-nm)) 
                                  built-in-candidates))]
             (js-built-in-map o))
           (when-let [built-in (get objects-by-unique-method-name
                                    fn-nm)]
             (js-built-in-map built-in)))))

     (defn- cljs-defmulti [sym]
       (when (symbol? sym)
         (let [[fn-ns fn-nm] (-> sym str (str/split #"/"))]
           {:fn-ns   fn-ns
            :fn-name fn-nm
            :fn-args :lasertag/multimethod})) )

     (defn- cljs-fn [x s]
       (let [bits          (str/split s #"\$")
             [fn-ns fn-nm] (partition-drop-last bits)
             fn-nm         (demunge-fn-name fn-nm)
             built-in?     (or (contains? js-built-in-objects x)
                               (contains? js-built-in-functions x))]
         (merge {:fn-name fn-nm}
                (when built-in? {:js-built-in-function? true})
                (when (seq fn-ns)
                  {:fn-ns (str/replace (str/join "." fn-ns) #"_" "-")}) 
                (when-not built-in? (js-built-in-method-of x s fn-nm)))))

     (defn- cljs-fn-alt [o]
       (let [out-str (pwos o)]
         (if-let [[_ fn-ns fn-nm] (re-find #"^(.+)/(.+)$" out-str)]
           {:fn-name           (demunge-fn-name fn-nm)
            :fn-ns             fn-ns
            :cljs-datatype-fn? true}
           {:lambda? true})))
     
     
    (defn- fn-args* [x]
      (let [[_ _ s] (re-find cljs-serialized-fn-info (str x))
            strings (some-> s
                            (str/split #","))
            syms    (some->> strings
                            ;; change to mapv?
                            (map (comp symbol
                                        str/trim)))]
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
        [fn-args defrecord?]))))

(defn multi-function-sym [mf]
  #?(:cljs
     (.-name mf)
     :clj
     (some (fn [[_ ^clojure.lang.Var v]]
             (when (and (.hasRoot v) (identical? mf (.getRawRoot v)))
               (symbol v)))                     ; 1.10+
           (mapcat ns-interns (all-ns)))))

(defn- fn-info*
  ([x k]
   (fn-info* x k nil))
  ([x k all-tags]
   (let [multi-function? (contains? all-tags :multi-function)]
    #?(:cljs 
       (let [name-prop (.-name x)]
         (cond
           multi-function?           (cljs-defmulti name-prop)
           (not (str/blank? name-prop)) (cljs-fn x name-prop)
           :else                           (cljs-fn-alt x)))
       :clj
       (if multi-function?
         (let [[fn-ns fn-name] 
               (-> x
                   multi-function-sym
                   str
                   (str/split #"/"))] 
           {:fn-args :lasertag/multimethod
            :fn-name fn-name
            :fn-ns   fn-ns})
         (if (= k :class)
           (resolve-classname x)
           (resolve-fn-name x)))))))

(defn fn-info
  ([x]
   (fn-info x nil))
  ([x k]
   (let [[k all-tags]         (let [{:keys [tag all-tags]} (tag-map x)]
                                [tag all-tags])
         fn-info              (fn-info* x k all-tags)
         [fn-args defrecord?] #?(:cljs (fn-args x fn-info) :clj nil)]
     (merge fn-info
            (when defrecord?
              {:defrecord? true})
            #?(:cljs 
               (cljs-fn-args x fn-args fn-info))))))
