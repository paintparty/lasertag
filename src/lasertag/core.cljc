
(ns lasertag.core
  (:require
   [clojure.string :as string]
   [lasertag.messaging :as messaging]
   [lasertag.cached :as cached]
   #?(:cljs [lasertag.jsi.native-plus :as jsi])
   #?(:cljs [lasertag.jsi.tag])
   #?(:cljs [lasertag.jsi.native :as jsi.native])
   [clojure.set :as set]))


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
   (do
     (defn- clj-all-value-types [{:keys [x k]}]
       (let [t (cached/cljc-type x)]
         (->> [(cached/cljc-coll-type x)
               (when (fn? x) :function)
               (when (= clojure.lang.PersistentArrayMap t) :array-map)
               (when (= clojure.lang.PersistentList t) :list)
               (when (inst? x) :inst)
               (when (record? x) :record)
               (when (record? x) :datatype)]
              (remove nil?)
              (cons k)
              (into #{}))))
     
     (defn- java-class-name [x]
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


(defn- map-like?* [x k all-tags]
  (or (contains? #{:map :js-object :js-map :js-data-view} k)
      (contains? all-tags :record)
      (contains? all-tags :datatype)
      (contains? all-tags :js-map-like-object)
      #?(:clj (instance? java.util.AbstractMap x))))


(defn- classname* [x]
  #?(:cljs
     (lasertag.jsi.tag/cljs-class-name x)
     :bb
     (let [nm (java-class-name x)]
       (if (some-> nm (string/starts-with? "sci.impl.fns"))
         "sci.impl.fns"
         nm))
     :clj
     (java-class-name x)))


(defn- all-tags* [{:keys [x k] :as m}]
  (let [all-tags  #?(:cljs (lasertag.jsi.tag/cljs-all-value-types m)
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
                          #?(:cljs (lasertag.jsi.tag/cljs-coll-like? x)))}))

(defn- anonymous-fn? [f]
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
  (let [{:keys [all-tags classname]}
        (all-tags* m)

        more-tags
        (concat
         [(when (anonymous-fn? x) :lambda)])

        all-tags
        (apply conj all-tags (remove nil? more-tags))]
    {:all-tags all-tags :classname classname}))



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
            dom-node-type-keyword] (lasertag.jsi.tag/dom-node x)]
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
                       (get jsi.native/js-built-ins-by-built-in x)]
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
     (lasertag.jsi.tag/k* x t)
     :clj
     (or 
      (cached/cljc-coll-type x)
      (when (fn? x) :function)
      (when (cached/throwable? x) :throwable)
      (when (future? x) :future)
      (when (instance? clojure.lang.IPending x) :promise)
      (when (instance? clojure.lang.IType x) :datatype)
      (when (cached/cljc-array? x) :array)
      (when (instance? java.util.AbstractMap x) :map)
      (when (instance? java.util.AbstractSet x) :set)
      (when (instance? java.util.ArrayList x) :array)
      (when (instance? java.util.AbstractList x) :list)
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
           (lasertag.jsi.tag/typed-array? x))
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
