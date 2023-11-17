(ns typetag.interop
  (:require 
   [clojure.set :as set]))


;; These are the built-ins you cannot access directly, e.g. `js/Iterator`
;; Iterator 
;; AsyncIterator
;; GeneratorFunction 
;; AsyncGeneratorFunction 
;; Generator 
;; AsyncGenerator 
;; AsyncFunction 
;; Errors


#?(:cljs 
   (defonce js-built-ins-by-category
     (array-map 
      "Fundamental objects"
      [[js/Object {:sym 'Object :args '[...]}]
       [js/Function {:sym 'Function :args '[...]}]
       [js/Boolean {:sym 'Boolean :args '[...]}]
       [js/Symbol {:sym 'Symbol :demo (js/Symbol "my-sym") :not-a-constructor? true :args '[s]}]]

      "Numbers and dates"
      [[js/Math {:sym 'Math :not-a-constructor? true}]
       [js/Number {:sym 'Number :demo (new js/Number "3") :args '[v]}]
       [js/BigInt {:sym 'BigInt :demo (js/BigInt "999999999999") :not-a-constructor? true :args '[v]}]
       [js/Date {:sym 'Date :demo (new js/Date) :args '[...]}]]

      "Value properties" 
      [[js/NaN {:sym 'NaN}] 
       [js/Infinity {:sym 'Infinity}] 
       [js/globalThis {:sym 'globalThis}]]

      "Control abstraction objects"
      [[js/Promise {:sym 'Promise :demo (new js/Promise (fn [x] x)) :args '[f]}]]

      "Error objects"
      [[js/AggregateError {:sym 'AggregateError :demo (new js/AggregateError #js[(new js/Error "some error")] "Hello") :args '[array]}]
       [js/EvalError {:sym 'EvalError :demo (new js/EvalError) :args '[]}]
       [js/RangeError {:sym 'RangeError :demo (new js/RangeError) :args '[]}]
       [js/ReferenceError {:sym 'ReferenceError :demo (new js/ReferenceError) :args '[]}]
       [js/SyntaxError {:sym 'SyntaxError :demo (new js/SyntaxError) :args '[]}]
       [js/TypeError {:sym 'TypeError :demo (new js/TypeError) :args '[]}]
       [js/URIError {:sym 'URIError :demo (new js/URIError) :args '[]}]
       [js/Error {:sym 'Error :demo (new js/Error) :args '[]}]]

      "Text processing"
      [[js/String {:sym 'String :demo (new js/String "hi") :args '[s]}]
       [js/RegExp {:sym 'RegExp :demo (new js/RegExp "^hi$") :args '[s]}]]

      "Function properties"
      [[js/eval {:sym 'eval :args '[script]}] 
       [js/isFinite {:sym 'isFinite :args '[v]}] 
       [js/isNaN {:sym 'isNaN :args '[v]}] 
       [js/parseFloat {:sym 'parseFloat :args '[s] }] 
       [js/parseInt {:sym 'parseInt :args '[...] }] 
       [js/decodeURI {:sym 'decodeURI :args '[uri]}] 
       [js/decodeURIComponent {:sym 'decodeURIComponent :args '[encodedUri]}] 
       [js/encodeURI {:sym 'encodeURI :args '[uri]}] 
       [js/encodeURIComponent {:sym 'encodeURIComponent :args '[uriComponent]}] 
       [js/escape {:sym 'escape :args '[str]}] 
       [js/unescape {:sym 'unescape :args '[str]}]]

      "Keyed collections"
      [
       [js/Map {:sym 'Map :demo (new js/Map #js[["a", 1], ["b", 2]]) :args '[...]}]
       [js/Set {:sym 'Set :demo (new js/Set #js[1 2]) :args '[...]}]
       [js/WeakMap {:sym  'WeakMap
                    :demo (let [wm (js/WeakMap.)
                                o  #js{:a 1}]
                            (.set wm o 100))
                    :args '[...]}]
       [js/WeakSet {:sym 'WeakSet :demo (new js/Set #js[1 2]) :args '[...]}]
       ]

      "Indexed collections"
      [
       [js/Array {:sym  'Array :demo (new js/Array #js[1 2 3]) :args '[...]}]
       [js/Int8Array {:sym  'Int8Array :demo (new js/Int8Array #js[1 2 3]) :args '[...]}]
       [js/Uint8Array {:sym  'Uint8Array :demo (new js/Uint8Array #js[1 2 3]) :args '[...]}]
       [js/Uint8ClampedArray {:sym  'Uint8ClampedArray :demo (new js/Uint8ClampedArray #js[1 2 3]) :args '[...]}]
       [js/Int16Array {:sym  'Int16Array :demo (new js/Int16Array #js[1 2 3]) :args '[...]}]
       [js/Uint16Array {:sym  'Uint16Array :demo (new js/Uint16Array #js[1 2 3]) :args '[...]}]
       [js/Int32Array {:sym  'Int32Array :demo (new js/Int32Array #js[1 2 3]) :args '[...]}]
       [js/Uint32Array {:sym  'Uint32Array :demo (new js/Uint32Array #js[1 2 3]) :args '[...]}]
       [js/BigInt64Array {:sym  'BigInt64Array :demo (new js/BigInt64Array 3) :args '[...]}]
       [js/BigUint64Array {:sym  'BigUint64Array :demo (new js/BigUint64Array 3) :args '[...]}]
       [js/Float32Array {:sym  'Float32Array :demo (new js/Float32Array #js[1 2 3]) :args '[...]}]
       [js/Float64Array {:sym  'Float64Array :demo (new js/Float64Array #js[1 2 3]) :args '[...]}]
       ]

      "Structured data"
      [[js/ArrayBuffer {:sym 'ArrayBuffer :demo (new js/ArrayBuffer 8) :args '[...]}]
       [js/JSON {:sym 'JSON :not-a-constructor? true}]
       [js/Atomics {:sym 'Atomics :not-a-constructor? true}]
       [js/DataView {:sym 'DataView :demo (new js/DataView (new js/ArrayBuffer 8)) :args '[ArrayBuffer]}]]

      "Internationalization"

      [[js/Intl {:sym 'Intl :not-a-constructor? true 
                 :args '[...]}]
       [js/Intl.Collator {:sym 'Intl.Collator
                          :demo (new js/Intl.Collator "sv")
                          :args '[...]}]
       [js/Intl.DateTimeFormat {:sym 'Intl.DateTimeFormat
                                :demo (new js/Intl.DateTimeFormat "en-US")
                                :args '[...]}]
       [js/Intl.DisplayNames {:sym 'Intl.DisplayNames
                              :demo (new js/Intl.DisplayNames #js["en"] #js {:type "region"})
                              :args '[...]}]
       [js/Intl.ListFormat {:sym 'Intl.ListFormat
                            :demo (new js/Intl.ListFormat "en-GB" #js {:style "long", :type "conjunction" })
                            :args '[...]}]
       [js/Intl.Locale {:sym 'Intl.Locale
                        :demo (new js/Intl.Locale "ko" #js{:script "Kore", :region "KR", :hourCycle "h23", :calendar "gregory"})
                        :args '[...]}]
       [js/Intl.NumberFormat {:sym 'Intl.NumberFormat 
                              :demo (new js/Intl.NumberFormat "de-DE" #js{:style "currency", :currency "EUR" })
                              :args '[...]}]
       [js/Intl.PluralRules {:sym 'Intl.PluralRules
                             :demo (new js/Intl.PluralRules "en-US") 
                             :args '[...]}]
       [js/Intl.RelativeTimeFormat {:sym 'Intl.RelativeTimeFormat
                                    :demo (new js/Intl.RelativeTimeFormat "en" #js{:style "short"}) 
                                    :args '[...]}]
       [js/Intl.Segmenter {:sym 'Intl.Segmenter
                           :demo (new js/Intl.Segmenter "fr" #js{:granularity "word"})
                           :args '[...]}]
    ;; [js/Intl.DurationFormat {:sym 'DurationFormat :args '[...]}] ;; experimental, Safari-only
       ]

      "Managing memory"
      [[js/WeakRef {:sym 'WeakRef
                    :demo (new js/WeakRef (fn [x])) :args '[f]}]
       [js/FinalizationRegistry {:sym 'FinalizationRegistry
                                 :demo (new js/FinalizationRegistry (fn [x])) :args '[f]}]]    

      "Reflection"
      [[js/Reflect {:sym 'Reflect :not-a-constructor? true}]
       [js/Proxy {:sym 'Proxy
                  :demo (new js/Proxy #js {:a 1} (fn [x])) :args '[...]}]]
      )))

#?(:cljs
   (defonce js-built-ins-by-built-in*
     (apply concat (vals js-built-ins-by-category))))

#?(:cljs 
   (defonce js-built-ins-by-built-in-for-checking-instance*
     (apply concat
            (vals (select-keys
                   js-built-ins-by-category
                   ["Numbers and dates"
                    "Error objects"
                    "Keyed collections"
                    "Indexed collections"
                    "Structured data"
                    "Internationalization"
                    "Managing memory"
                    "Reflection"])))))

#?(:cljs
   (defonce js-built-ins-by-built-in
     (into {} js-built-ins-by-built-in*)))

#?(:cljs 
   (defonce js-built-ins-which-are-not-functions*
     [js/Symbol
      js/Math
      js/JSON
      js/Atomics
      js/Intl
      js/Reflect]))

#?(:cljs 
   (defonce js-built-ins-which-are-iterables*
     (apply concat (vals (select-keys js-built-ins-by-category ["Indexed collections" "Keyed collections"])))))

#?(:cljs 
   (defonce js-built-ins-which-are-not-functions-by-built-in
     (select-keys js-built-ins-by-built-in js-built-ins-which-are-not-functions*)))


;;;; define built-in values -------------------
#?(:cljs 
   (defonce js-built-in-global-values-by-value
     (get js-built-ins-by-category "Value properties")))

#?(:cljs
   (defonce js-built-in-global-values-by-value-map
     (into {} js-built-in-global-values-by-value)))

#?(:cljs 
   (defonce js-built-in-global-values
     (into #{} (keys js-built-in-global-values-by-value-map))))




;;;; define built-in functions -------------------

#?(:cljs
   (defonce js-built-in-functions-by-function*
     (get js-built-ins-by-category "Function properties")))

#?(:cljs
   (defonce js-built-in-functions-by-function
     (into {} js-built-in-functions-by-function*)))

#?(:cljs
   (defonce js-built-in-functions
     (into #{} (keys js-built-in-functions-by-function))))




;;;; define built-in objects ---------------------

#?(:cljs
   (defonce js-built-in-objects-by-object
     (into []
           (apply concat
                  (for [k ["Fundamental objects"
                           "Error objects"
                           "Text processing"
                           "Indexed collections"
                           "Keyed collections"
                           "Numbers and dates"
                           "Structured data"
                           "Managing memory"
                           "Control abstraction objects"
                           "Reflection"
                           "Internationalization"]]
                    (get js-built-ins-by-category k))))))

#?(:cljs
   (defonce js-built-in-objects-by-object-map
     (into {} js-built-in-objects-by-object)))

#?(:cljs
   (defonce js-built-in-objects
     (into #{} (keys js-built-in-objects-by-object-map))))


;;;; helper fns ---------------------

(defn- freq [coll op]
  (->> coll 
       (frequencies)
       (filter (fn [[_ v]] (op v 1)))
       (keys)) )

#?(:cljs 
   (defn- method-names-by-object [objects]
     (into {}
           (map (fn [o]
                  (let [statics   (js->clj (js/Object.getOwnPropertyNames o))
                        instances (some-> o
                                          .-prototype
                                          js/Object.getOwnPropertyNames
                                          js->clj)]
                    [o (filter #(not (re-find #"^closure_uid|^\$|^__|^com\$|^transit\$|^portal\$|^prototype$|^constructor$|^cljs$|^clojure$" %))
                               (concat statics instances))])) 
                objects))))


#?(:cljs
   (defn- objects-by-method-name
     [method-names-by-object repeats]
     (into {}
           (map (fn [s]
                  (let [built-ins (reduce (fn [acc [k v]]
                                            (if (contains? (into #{} v) s)
                                              (conj acc k)
                                              acc))
                                          [] 
                                          method-names-by-object)]
                    [s built-ins]))
                repeats))))

(defn- objects-by-unique-method-names
  [m]
  (->> m
       (map (fn [[k v]]
              (map (fn [method-name]
                     [method-name k])
                   v)))
       (apply concat)
       (into {})))


;; Define different servings of the data
#?(:cljs
   (defonce js-built-in-methods
     (do 
       (println "def'ing js-built-in-methods")
       (let [
             ;; TODO - add the rest of the relevant built-ins from js
             objects-ordered
             (mapv (fn [[o _]] o) js-built-in-objects-by-object)

             object-names-by-object
             (into {} (map (fn [[o m]] [o (:sym m)]) js-built-in-objects-by-object-map)) 

             object-names
             (into #{} (vals object-names-by-object))

             method-names-by-object 
             (method-names-by-object js-built-in-objects)

             ;; should be #{"prototype" "name" "length" "constructor"}
             shared-names
             (do 
               (apply set/intersection '(#{"name"} #{"name"}))
               (apply set/intersection (map #(into #{} %) (vals method-names-by-object))))

             all-names
             (apply concat (vals method-names-by-object))

             freqs
             (freq all-names >)

             freqs-set (into #{} freqs)

             uniques
             (freq all-names =)

             unique-method-names-by-object
             (into {}
                   (map (fn [[o v]]
                          [o (disj (into #{} v) freqs-set)])
                        method-names-by-object))

             objects-by-unique-method-name
             (objects-by-unique-method-names unique-method-names-by-object)
             

             repeats
             (apply disj freqs-set shared-names)
             
             objects-by-method-name
             (objects-by-method-name method-names-by-object repeats)]

         {:js-built-in-objects           js-built-in-objects
          :objects-ordered               objects-ordered
          :object-names-by-object        object-names-by-object
          :object-names                  object-names
          :unique-method-names-by-object unique-method-names-by-object
          :objects-by-unique-method-name objects-by-unique-method-name
          :method-names-by-object        method-names-by-object
          :shared-names                  shared-names
          :all-names                     all-names
          :freqs                         freqs
          :uniques                       uniques
          :repeats                       repeats
          :objects-by-method-name        objects-by-method-name}))))

