(ns lasertag.jsi.tag
  (:require [lasertag.cached :as cached]
            [clojure.string :as string]
            [lasertag.jsi.native-plus :as jsi]
            [lasertag.jsi.native :as jsi.native]))

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

(defn- pwos [x] (with-out-str (print x)))


(defn ^:no-doc typed-array? [x]
  (and (js/ArrayBuffer.isView x)
       (not (instance? js/ArrayBuffer x))
       (not (instance? js/DataView x))))


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
  (when (cached/js-object-instance? x)
    (js-classname x)))


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
       (into #{})))


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
    (when (cached/js-object-instance? x)
      :js-map-like-object)))


(defn ^:no-doc cljs-all-value-types [{:keys [x k dom-node-type-keyword]}]
  (let [t
        (cached/cljc-type x)

        types
        {:cljc-coll-type (cached/cljc-coll-type x)
         :js-map-types   (get js-map-types t)
         :js-set-types   (get js-set-types t)
         :iterable       (cljs-iterable-type x)
         :array          (when (array? x) :array)
         :array-map      (when (= cljs.core/PersistentArrayMap t) :array-map)
         :list           (when (= cljs.core/List t) :list)
         :object         (when (object? x) :object)
         :fn             (when (fn? x) :function)
         :inst           (when (inst? x) :inst)
         :defmulti       (when (cached/multi-function? x) :defmulti)
         :js-promise     (when (cached/js-promise? x) :js-promise)
         :js-global-this (when (cached/js-global-this? x) :js-global-this)
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
         (into #{}))))


(defn ^:no-doc cljs-coll-like? [x]
  (or (array? x)
      (object? x)
      (contains? jsi.native/js-built-ins-which-are-iterables
                 (cached/cljc-type x))))


(defn ^:no-doc cljs-class-name [x]
  (let [ret (or (js-object-instance x)
                ;; TODO - test whether this one is faster
                (some->> (get jsi.native/js-built-ins-by-built-in
                              (cached/cljc-type x)
                              nil)
                         :sym
                         name
                         (str "js/"))
                ;; TODO - or this one is faster
                ;; TODO - test this with custom classes
                (js-classname x))]
    (if (keyword? ret) (subs (str ret) 1) ret)))


(defn ^:no-doc dom-node
  "Helper fn which takes an HTML dom element and returns a 3-element vector.
   Ex:
   (dom-node `<div>hi</div>`)
   =>
   [1                   ; <- type code of node
    \"ELEMENT_NODE\"    ; <- canonical tag name of node
    :dom-element-node]  ; <- kw representation available for consumers of
                             lasertag.core/tag-map"
  [x]
  (when-let [t (when (cached/js-object-instance? x) (some->> x .-nodeType))]
    (let [n (dec t)]
      [t
       (nth dom-node-types n nil)
       (nth dom-node-type-keywords n nil)])))


(defn- js-intl-object-key
  [x]
  (when-let [sym (some->> x
                          cached/cljc-type
                          (get jsi.native/js-built-in-intl-by-object)
                          :sym)]
    (keyword (str "js/" sym))))


(defn k*
  "Assigns a primary tag for values that cannot be found in any of the public
   lasertag.cached/* maps."
  [x t]
  (or
   ;; If coll, returns tag for coll type
   (cached/cljc-coll-type x)

   ;; If map-like native JS data structure, returns :map tag
   (when (get js-map-types t) :map)

   ;; If set-like native JS data structure, returns :set tag
   (when (get js-set-types t) :set)

   ;; If native JS Indexed array, returns :array tag
   (when (contains? js-indexed-coll-types-set t) :array)

   ;; If native JS Iterable, returns :iterable tag
   ;; If a Generator, returns :generator tag
   (cljs-iterable-type x)

   ;; If native JS Object, returns :object tag
   (when (object? x) :object)

   ;; If function, returns :function tag. Pretty sure this needs to come after
   ;; what is above, or some of those would be tagged :function
   (when (fn? x) :function)

   ;; If multi-function, returns :multi-function tag
   ;;           TODO - Move this to cached
   (when (cached/multi-function? x) :multi-function)

   ;; If native js-promise, returns :promise tag
   ;;           TODO - Move this to cached
   (when (cached/js-promise? x) :promise)

   ;; If js global this var, returns :js-global-this tag
   (when (cached/js-global-this? x) :js-global-this)
   
   ;; Returns a keyword representation of a js built-in Internationalization
   ;; class such as js/Intl.Local => :js/Intl.Local
   ;;           TODO - Move this to cached
   (js-intl-object-key x)

   ;; If value is an instance of js/DataView, returns a :data-view tag
   ;;           TODO - Move this to cached
   (when (cached/js-data-view? x) :js-data-view)

   ;; If value is an instance of js/ArrayBuffer, returns a :byte-array tag
   ;;           TODO - Move this to cached
   (when (cached/js-array-buffer? x) :byte-array)

   ;; Catch all for other objects, returns a keyword of the classname
   ;; Returns :Object if the classname can't be resolved
   ;;           TODO - maybe use :object, don't keywordize classname?
   (js-object-instance x)

   ;; If instance of js/Error, returns :throwable tag
   (when (cached/throwable? x) :throwable)

   ;; Returns namespaced keyword if no tag was produced
   :lasertag/value-type-unknown))

;; These are the built-ins you cannot access directly, e.g. `js/Iterator`
;; Iterator 
;; AsyncIterator
;; GeneratorFunction 
;; AsyncGeneratorFunction 
;; Generator 
;; AsyncGenerator 
;; AsyncFunction 
;; Errors

