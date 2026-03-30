
(ns lasertag.core
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [lasertag.messaging :as messaging]
   [lasertag.cached :as cached]
   #?(:cljs [lasertag.cljs-interop :as jsi])
   [clojure.set :as set])
  ;; #?(:clj
  ;;    (:import (clojure.lang PersistentVector$TransientVector
  ;;                           PersistentHashSet$TransientHashSet
  ;;                           PersistentArrayMap$TransientArrayMap
  ;;                           PersistentHashMap$TransientHashMap)))
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


#?(:cljs
   (do

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

(defn- pwos [x] (with-out-str (print x)))

;; cljs and js instance checks, cljs fn resolution functions start -------------
#?(:cljs
   (do
     (defn- js-global-this? [x] (= x js/globalThis))
     (defn- js-object-instance? [x] (instance? js/Object x))
     (defn- defmulti? [x] (= (cached/cljc-type x) cljs.core/MultiFn))
     (defn- js-promise? [x] (instance? js/Promise x))
     (defn- js-data-view? [x] (instance? js/DataView x))
     (defn- js-array-buffer? [x] (instance? js/ArrayBuffer x))
     (defn- typed-array? [x]
       (and (js/ArrayBuffer.isView x)
            (not (instance? js/ArrayBuffer x))
            (not (instance? js/DataView x))))))



;; cljs value type helpers -----------------------------------------------------
#?(:cljs
   (do
     (defn- cljs-iterable-type [x]
       (when (js-iterable? x)
         (if (= (str x) "[object Generator]")
           :generator
           :iterable)))

     (defn- js-object-instance-map-like
       [x types]
       (when-not (or (-> types :coll)
                     (cached/scalar? x)
                     (-> types :literal-type)
                     (-> types :number)
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
        (get cached/cljc-transients-primary-tags-by-class
             (cached/cljc-type x)
             nil)))

#?(:clj
   (do
     (defn clj-or-bb-array? [x]
       ;; This try hack is for babashka,
       ;; because .getClass method is sometimes not allowed
       (or
        (try (some-> x .getClass .isArray)
             (catch Exception e))
        (try (some-> x class .isArray)
             (catch Exception e))))

     (defn- clj-all-value-types [{:keys [x k]}]
       (let [t           (cached/cljc-type x)]
         (->> [(cljc-coll-type x)
               (when (fn? x) :function)
               (when (= clojure.lang.PersistentArrayMap t) :array-map)
               (when (= clojure.lang.PersistentList t) :list)
               (when (inst? x) :inst)
               (when (or (contains? #{:vector :map :set :seq} k)
                         (coll? x)
                         (instance? java.util.Collection x)
                         (clj-or-bb-array? x))
                 :coll)
               (when (record? x) :record)
               (when (record? x) :datatype)]
              (remove nil?)
              (cons k)
              (into #{}))))

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
               (string/replace #";$" "")))))


;; TODO - consider removing `js-` prefixes and just adding an additional :js tag
#?(:cljs
   (defn- cljs-all-value-types [{:keys [x k dom-node-type-keyword]}]
     (let [t
           (cached/cljc-type x)

           types
           {:cljc-coll-type (cljc-coll-type x)
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
            :record         (when (record? x) :record)
            :datatype       (when (record? x) :datatype)
            :typed-array    (when (typed-array? x) :js-typed-array)
            :typed-array+   (when (typed-array? x)
                              (get js-indexed-coll-types
                                   (cached/cljc-type x)))}

           js-object-instance-map-like
           (js-object-instance-map-like x types)]

       (->> (vals types)
            (concat [js-object-instance-map-like dom-node-type-keyword])
            (remove nil?)
            (cons k)
            (into #{})))))

#?(:cljs
   (do
     (defn- cljs-coll-like? [x]
       (or (array? x)
           (object? x)
           (contains? jsi/js-built-ins-which-are-iterables
                      (cached/cljc-type x))))

     (defn- cljs-class-name [x]
       (let [ret (or (js-object-instance x)
                     ;; TODO - test whether this one is faster
                     (some->> (get jsi/js-built-ins-by-built-in
                                   (cached/cljc-type x)
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
      (contains? all-tags :datatype)
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


(defn- all-tags* [{:keys [x k] :as m}]
  (let [all-tags  #?(:cljs (cljs-all-value-types m)
                     :clj (clj-all-value-types m))
        map-like? (map-like?* x k all-tags)
        set-like? (or (contains? #{:set :js-set} k)
                      #?(:clj (instance? java.util.AbstractSet x)))]

    ;; TODO - Add :array-like? and maybe :list-like?
    {:classname       (classname* x)
     :all-tags        all-tags
     :set-like?       set-like?
     :map-like?       map-like?
     :coll-like?      (or map-like?
                          set-like?
                          (contains? all-tags :coll)
                          #?(:cljs (cljs-coll-like? x)))}))

(def eager-seq-classes
  (->> ['(:a)
        (cons :a '(:b))
        (seq [1 2 3])
        (seq "foo")
        (seq {:a 1})]
       (map type)
       set))

(defn eager-seq? [x]
  (contains? eager-seq-classes (cached/cljc-type x)))

(defn anonymous-fn? [f]
  (boolean
   (when fn? 
     #?(:cljs
        (let [n (.-name f)]
          (or (empty? n)
              (re-find #"fn__\d+" n)))
        :clj
        (re-find #"fn__\d+" (some-> f class .getName))))))

(defn- all-tags
  [{:keys [x k] :as m}]
  (let [{:keys [all-tags classname]
         :as all-tags-map}
        (all-tags* m)

        more-tags
        (concat
         [(when (anonymous-fn? x) :lambda)])

        all-tags
        (apply conj all-tags (remove nil? more-tags))]
    {:all-tags all-tags :classname classname}))


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


;;                                                                   
;;                                                                   
;; TTTTTTTTTTTTTTTTTTTTTTT         AAA                  GGGGGGGGGGGGG
;; T:::::::::::::::::::::T        A:::A              GGG::::::::::::G
;; T:::::::::::::::::::::T       A:::::A           GG:::::::::::::::G
;; T:::::TT:::::::TT:::::T      A:::::::A         G:::::GGGGGGGG::::G
;; TTTTTT  T:::::T  TTTTTT     A:::::::::A       G:::::G       GGGGGG
;;         T:::::T            A:::::A:::::A     G:::::G              
;;         T:::::T           A:::::A A:::::A    G:::::G              
;;         T:::::T          A:::::A   A:::::A   G:::::G    GGGGGGGGGG
;;         T:::::T         A:::::A     A:::::A  G:::::G    G::::::::G
;;         T:::::T        A:::::AAAAAAAAA:::::A G:::::G    GGGGG::::G
;;         T:::::T       A:::::::::::::::::::::AG:::::G        G::::G
;;         T:::::T      A:::::AAAAAAAAAAAAA:::::AG:::::G       G::::G
;;       TT:::::::TT   A:::::A             A:::::AG:::::GGGGGGGG::::G
;;       T:::::::::T  A:::::A               A:::::AGG:::::::::::::::G
;;       T:::::::::T A:::::A                 A:::::A GGG::::::GGG:::G
;;       TTTTTTTTTTTAAAAAAA                   AAAAAAA   GGGGGG   GGGG
;;                                                                   


#?(:cljs
   (defn cljs-tag-map* [x k opts]
     (let [[dom-node-type
            dom-node-type-name
            dom-node-type-keyword] (dom-node x)]
       (merge
        ;; Get all the tags
        (all-tags {:x                     x
                   :k                     k
                   :dom-node-type-keyword dom-node-type-keyword})

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
  [x k opts]
  (merge
   ;; The tag for clj & cljs
   {:tag k}

   ;; The `type` (calling clojure.core.type, or cljs.core.type) on the value 
   {:type #?(:cljs (if (= k :js-generator)
                     (symbol "#object[Generator]")
                     (cached/cljc-type x))
             :clj (cached/cljc-type x))}

   #?(:cljs (cljs-tag-map* x k opts)
      :clj  (all-tags {:x    x
                       :k    k
                       :opts opts}))))


#?(:cljs
   (defn- js-intl-object-key [x]
     (when-let [sym (some->> x
                             type
                             (get jsi/js-built-in-intl-by-object)
                             :sym)]
       (keyword (str "js/" sym)))))


;; (defn- find-classname [x]
;;   ;; For custom datatypes, ^class prefix is not present in str'd babashka classname
;;   (re-find #"(?:^class )?(.*)$" (str x)))

;; #?(:clj
;;    (defn resolve-class-name-clj [c]
;;      (let [clj-names {:java.lang.Class :class
;;                       :sci.lang.Type   :class}]
;;        (or
;;         (when-let [[_ nm] (find-classname c)]
;;           (let [k (keyword nm)
;;                 k (get clj-names k k)]
            
;;             k))
;;         ;; Use find-classname ?
;;         (when-let [nm (some-> c str)]
;;           (let [k (keyword nm)
;;                 k (get clj-names k k)]

;;             k))))))

(defn vanilla-class? [x]
  #?(:bb
     (instance? sci.lang.Type x)
     :clj
     (instance? java.lang.Class x)))

(defn- k* 
  "Assigns a primary tag for values that cannot be found in any of the public
   lasertag.cached/* maps."
  [x t]
  #?(:cljs
     (or 
      (cljc-coll-type x)
      (when (get js-map-types t) :map)
      (when (get js-set-types t) :set)
      (when (contains? js-indexed-coll-types-set t) :array)
      (cljs-iterable-type x)
      (when (object? x) :object)
      (when (fn? x) :function)
      (when (defmulti? x) :defmulti)
      (when (js-promise? x) :promise)
      (when (js-global-this? x) :js-global-this)
      (js-intl-object-key x)
      (when (js-data-view? x) :js-data-view)
      (when (js-array-buffer? x) :byte-array)
      (js-object-instance x)
      (when (cached/throwable? x) :throwable)
      :lasertag/value-type-unknown)
     :clj
     (or 
      (cljc-coll-type x)
      (when (fn? x) :function)
      (when (cached/throwable? x) :throwable)
      (when (future? x) :future)
      (when (delay? x) :delay)
      (when (instance? clojure.lang.IPending x) :promise)
      (when (instance? clojure.lang.IType x) :datatype)
      (when (clj-or-bb-array? x) :array)
      (when (instance? java.util.AbstractMap x) :map)
      (when (instance? java.util.AbstractSet x) :set)
      (when (instance? java.util.ArrayList x) :array)
      (when (instance? java.util.ArrayDeque x) :array)
      (when (instance? java.util.AbstractList x) :seq)
      (when (vanilla-class? x) :class))))


(defn merged-with-runtime-tags [x m]
  (let [number-tags  (when (cached/real-number? x) (cached/number-tags x))
        all-tags2    (cached/all-tags* x)
        all-tags-new (set/union (:all-tags m) number-tags all-tags2)]
    (assoc m :all-tags all-tags-new)))


(defn- tag* [{:keys [x extras? opts]}]
  (let [k (k* x (cached/cljc-type x))]
    (if extras?
      (let [m  (tag-map* x k opts)]
        (merged-with-runtime-tags x m))
      k)))


(defn cached-tag-map [x]
  (when-let [cached (let [x-type (cached/cljc-type x)]
                      (or
                       (get cached/by-priority-class x-type)
                       (when (number? x)
                         (or (get cached/non-finite-numbers-by-class x)
                             (get cached/by-number-class x-type)))
                       (get cached/by-class x-type)))]
    cached))


(defn tag
  "Given a value, returns a tag representing the value's type."
  ([x]
   (tag x nil))
  ([x opts]
   (or (some-> x cached-tag-map :tag)
       (tag* {:x       x
              :extras? false
              :opts    opts}))))


(defn tag-map
  "Given a value, returns a map with information about the value's type."
  ([x]
   (tag-map x nil))
  ([x opts]
   (or #?(:cljs
          ;; Temporary fix to use new all-tags* logic on cached cljs tag-maps v0.13.0
          ;; Remove when pre-compilation of cljs tag-maps is done
          (some->> (cached-tag-map x) (merged-with-runtime-tags x))
          :clj
          (cached-tag-map x)) 
       (tag* {:x       x
              :extras? true
              :opts    opts}))))


;; TODO

;; Figure out best abstraction for inheritance model for custom datatypes,
;; Maybe return :namespace and :name for anything named
;; should clj Promise go in map?



;;                                                                       
;;    SSSSSSSSSSSSSSS IIIIIIIIIIZZZZZZZZZZZZZZZZZZZEEEEEEEEEEEEEEEEEEEEEE
;;  SS:::::::::::::::SI::::::::IZ:::::::::::::::::ZE::::::::::::::::::::E
;; S:::::SSSSSS::::::SI::::::::IZ:::::::::::::::::ZE::::::::::::::::::::E
;; S:::::S     SSSSSSSII::::::IIZ:::ZZZZZZZZ:::::Z EE::::::EEEEEEEEE::::E
;; S:::::S              I::::I  ZZZZZ     Z:::::Z    E:::::E       EEEEEE
;; S:::::S              I::::I          Z:::::Z      E:::::E             
;;  S::::SSSS           I::::I         Z:::::Z       E::::::EEEEEEEEEE   
;;   SS::::::SSSSS      I::::I        Z:::::Z        E:::::::::::::::E   
;;     SSS::::::::SS    I::::I       Z:::::Z         E:::::::::::::::E   
;;        SSSSSS::::S   I::::I      Z:::::Z          E::::::EEEEEEEEEE   
;;             S:::::S  I::::I     Z:::::Z           E:::::E             
;;             S:::::S  I::::I  ZZZ:::::Z     ZZZZZ  E:::::E       EEEEEE
;; SSSSSSS     S:::::SII::::::IIZ::::::ZZZZZZZZ:::ZEE::::::EEEEEEEE:::::E
;; S::::::SSSSSS:::::SI::::::::IZ:::::::::::::::::ZE::::::::::::::::::::E
;; S:::::::::::::::SS I::::::::IZ:::::::::::::::::ZE::::::::::::::::::::E
;;  SSSSSSSSSSSSSSS   IIIIIIIIIIZZZZZZZZZZZZZZZZZZZEEEEEEEEEEEEEEEEEEEEEE
;;
;;
;;
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
     [{:keys [x tag all-tags]}]
     (cond
       (or (= :js-object tag)
           (contains? all-tags :js-map-like-object))
       (.-length (js/Object.keys x))

       (or (contains? all-tags :js-set)
           (contains? all-tags :js-map))
       (.-size x)

       (or (= tag :array)
           (typed-array? x))
       (.-length x)

       :else
       (count x))))


#?(:clj
   (defn- clj-coll-size-try
     [{:keys [x tag abstract-instance? classname]}]
     (cond
       abstract-instance?
       (.size x)

       (and (= tag :array)
            (not (cached/java-util-class? classname)))
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


(defn ^:public coll-size* [{:keys [x all-tags] :as m}]
  (when (:coll-like all-tags)
    (when-not (or (contains? all-tags :js-weak-map)
                  (contains? all-tags :js-weak-set))
      (let [debug-unknown-coll-size?
            #_true false]
        (if debug-unknown-coll-size?
          (messaging/mock-unknown-coll-size x)
          (cljc-coll-size* (assoc m
                                  :abstract-instance?
                                  (abstract-instance?* x))))))))

;; -----------------------------------------------------------------------------
