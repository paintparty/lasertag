(ns lasertag.jsi.codegen
  (:require 
   ["fs"             :as fs]
   ["path"           :as path]
   ["child_process"  :as cp]
   [lasertag.core :refer [tag tag-map]]
   [lasertag.cached :refer [by-class]]
   [clojure.pprint :refer [pprint]]
   [cljs.reader :as reader]
   [lasertag.cached :as cached]))


(defn ?
  "Debugging macro internal to lib"
  ([x]
   (? nil x))
  ([l x]
   (try (if l
          (println (str " " l "\n") x)
          (pprint x))
        (catch js/Object
               e
          (println "WARNING [lasertag.cljs.testgen/?] Unable to print value")))
   x))

(declare js-built-ins-by-category)

(fn [m [classname tag] x]
     (assoc  m
             (symbol (str classname))
             {:class     (symbol (str classname))
              :classname (if (nil? classname) "nil" (str classname))
              :tag       tag
              :all-tags  (conj (cached/all-tags* x) tag)}))

(defn js-built-in-category->vc [[category tag skip]]
  (reduce
   (fn [m [o {:keys [sym demo]}]]
     (if (= o skip)
       m
       (assoc m
              (symbol (str "js/" sym))
              {:class     (symbol (str "js/" sym))
               :classname (str "js/" sym)
               :tag       tag
               :all-tags  (conj (cached/all-tags* demo) tag)})))
   {}
   (->> category (get js-built-ins-by-category))))


(def cljs-classes
  {
   ;; scalars
   ["cljs.core/Keyword" :keyword]         :foo
   ["js/String" :string]                  "foo"
   ["cljs.core/Symbol" :symbol]           'foo
   ["js/Boolean" :boolean]                true
   ["js/Number" :number]                  21

   ;; literal types
   [nil :nil]                             nil
   ["cljs.core/UUID" :uuid ]              #uuid "4fe5d828-6444-11e8-8222-720007e40350"

   ;; data-structures
   ["js/RegExp" :regex]                   (js/RegExp. "hello")
   ["cljs.core/PersistentArrayMap" :map]  {1 2 3 4}
   ["cljs.core/PersistentHashMap" :map]   (hash-map 1 2 3 4)
   ["cljs.core/LazySeq" :seq]             (map inc [1 2 3])
   ["cljs.core/PersistentVector" :vector] [1 2 3]
   ["cljs.core/PersistentHashSet" :set]   #{1 2 3}
   ["cljs.core/Subvec" :vector]           (subvec [1 2 3 4 5] 1 3)
   ["cljs.core/Cons" :seq]                (cons 1 '(2 3))
   ["cljs.core/IntegerRange" :seq]        (range 3)
   ["cljs.core/Range" :seq]               (range 0 1.0 0.1)
   ["cljs.core/Repeat" :seq]              (repeat 2 :a)
   ["cljs.core/List" :list]               (list 1 2 3)
   ["cljs.core/EmptyList" :list]          (list)
   ["cljs.core/PersistentTreeMap" :map]   (sorted-map 1 2 3 4)
   ["cljs.core/PersistentTreeSet" :set]   (sorted-set 1 2 3)
   ["cljs.core/TransientArrayMap" :map]   (transient (array-map 1 2 3 4))
   ["cljs.core/TransientHashMap" :map]    (transient (hash-map 1 2 3 4))
   ["cljs.core/TransientVector" :vector]  (transient [1 2 3])
   ["cljs.core/TransientHashSet" :set]    (transient #{1 2 3})
   ["cljs.core/PersistentQueue" :queue]   cljs.core.PersistentQueue.EMPTY
   ["cljs.core/MapEntry" :vector]         (-> {:a 1} first)})

(def cljs-class-map
  (reduce-kv 
   (fn [m [classname tag] x]
     (assoc  m
             (symbol (str classname))
             {:class     (symbol (str classname))
              :classname (if (nil? classname) "nil" (str classname))
              :tag       tag
              :all-tags  (conj (cached/all-tags* x) tag)}))
   {}
   cljs-classes))

(def built-in-classes
  (mapv (fn [c] (js-built-in-category->vc c))
        [["Error objects" :throwable]
         ["Indexed collections" :array]
         ["Internationalization" :intl js/Intl]
         ["Keyed collections" :intl js/Intl]]))

(defn write-file-sync!
  "Write `content` to `filepath`, creating or overwriting it."
  [filepath content]
  (.writeFileSync fs filepath content)
  (println "Wrote" filepath))

(defn main []
  (let [by-class  (apply merge
                         (into [cljs-class-map] built-in-classes))]

    ;; (write-file-sync! "../src/")
    ))



(defonce Atomics
  (try js/Atomics
       (catch js/Object e nil)))

(def js-built-ins-by-category
  (array-map 
   "Fundamental objects"
   [[js/Object {:sym 'Object :args '[...]}]
    [js/Function {:sym 'Function :args '[...]}]
    [js/Boolean {:sym 'Boolean :args '[...]}]
    [js/Symbol {:sym 'Symbol
                :demo (js/Symbol "my-sym")
                :not-a-constructor? true
                :args '[s]}]]

   "Numbers and dates"
   [[js/Math {:sym 'Math :not-a-constructor? true}]
    [js/Number {:sym 'Number :demo (new js/Number "3") :args '[v]}]
    [js/BigInt {:sym 'BigInt
                ;; :demo (js/BigInt "999999999999")
                :not-a-constructor? true
                :args '[v]}]
    [js/Date {:sym 'Date :demo (new js/Date) :args '[...]}]]

   "Value properties" 
   [[js/NaN {:sym 'NaN}] 
    [js/Infinity {:sym 'Infinity}] 
    [js/globalThis {:sym 'globalThis}]]

   "Control abstraction objects"
   [[js/Promise {:sym 'Promise #_#_:demo (new js/Promise (fn [x] x)) :args '[f]}]]

   "Error objects"
   [[js/AggregateError {:sym  'AggregateError
                        :demo (new js/AggregateError
                                   #js[(new js/Error "some error")] "Hello")
                        :args '[array]}]
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
   [[js/Map {:sym 'Map :demo (new js/Map #js[#js["a", 1], #js["b", 2]]) :args '[...]}]
    [js/Set {:sym 'Set :demo (new js/Set #js[1 2]) :args '[...]}]
    [js/WeakMap {:sym  'WeakMap
                 :demo (let [wm (js/WeakMap.)
                              o  #js{:a 1}]
                         (.set wm o 100))
                 :args '[...]}]
    ;; TODO - fix this WeakSet demo
    [js/WeakSet {:sym 'WeakSet :demo (new js/Set #js[1 2]) :args '[...]}]
    ]

   "Indexed collections"
   [[js/Array {:sym 'Array :demo (new js/Array 1 2 3) :args '[...]}]
    [js/Int8Array {:sym 'Int8Array :demo (new js/Int8Array #js[1 2 3]) :args '[...]}]
    [js/Uint8Array {:sym 'Uint8Array :demo (new js/Uint8Array #js[1 2 3]) :args '[...]}]
    [js/Uint8ClampedArray {:sym  'Uint8ClampedArray :demo (new js/Uint8ClampedArray #js[1 2 3]) :args '[...]}]
    [js/Int16Array {:sym 'Int16Array :demo (new js/Int16Array #js[1 2 3]) :args '[...]}]
    [js/Uint16Array {:sym 'Uint16Array :demo (new js/Uint16Array #js[1 2 3]) :args '[...]}]
    [js/Int32Array {:sym 'Int32Array :demo (new js/Int32Array #js[1 2 3]) :args '[...]}]
    [js/Uint32Array {:sym 'Uint32Array :demo (new js/Uint32Array #js[1 2 3]) :args '[...]}]
    [js/BigInt64Array {:sym 'BigInt64Array :demo (new js/BigInt64Array 3) :args '[...]}]
    [js/BigUint64Array {:sym 'BigUint64Array :demo (new js/BigUint64Array 3) :args '[...]}]
    [js/Float32Array {:sym 'Float32Array :demo (new js/Float32Array #js[1 2 3]) :args '[...]}]
    [js/Float64Array {:sym 'Float64Array :demo (new js/Float64Array #js[1 2 3]) :args '[...]}]]

   "Structured data"
   (into []
         (concat 
          [[js/ArrayBuffer {:sym                 'ArrayBuffer 
                            #_#_:demo                (new js/ArrayBuffer 8) 
                            :args                '[...]
                            :instance-properties ["byteLength"
                                                  "detached"
                                                  "maxByteLength"
                                                  "resizable"]}]
           [js/JSON {:sym                'JSON
                     :not-a-constructor? true}]
           [js/DataView {:sym                 'DataView
                         #_#_:demo                "(new js/DataView (new js/ArrayBuffer 8))"
                         :args                '[ArrayBuffer]
                         :instance-properties ["buffer"
                                               "byteLength"
                                               "byteOffset"]}]]
          (when Atomics [[Atomics {:sym                'Atomics
                                   :not-a-constructor? true}]])))

   "Internationalization"
   [[js/Intl {:sym 'Intl :not-a-constructor? true}]
    [js/Intl.Collator {:sym  'Intl.Collator
                       :demo (new js/Intl.Collator "sv")
                       :args '[...]}]
    [js/Intl.DateTimeFormat {:sym  'Intl.DateTimeFormat
                             :demo (new js/Intl.DateTimeFormat "en-US")
                             :args '[...]}]
    [js/Intl.DisplayNames {:sym 'Intl.DisplayNames
                           :demo (new js/Intl.DisplayNames #js["en"] #js {:type "region"})
                           :args '[...]}]
    [js/Intl.ListFormat {:sym 'Intl.ListFormat
                         :demo (new js/Intl.ListFormat "en-GB" #js {:style "long", :type "conjunction"})
                         :args '[...]}]
    [js/Intl.Locale {:sym 'Intl.Locale
                     :demo (new js/Intl.Locale
                                 "ko"
                                 #js{:script "Kore",
                                     :region "KR",
                                     :hourCycle "h23",
                                     :calendar "gregory"})
                     :args '[...]
                     :instance-properties ["baseName"
                                           "calendar"
                                           "calendars"
                                           "caseFirst"
                                           "collation"
                                           "collations"
                                           "hourCycle"
                                           "hourCycles"
                                           "language"
                                           "numberingSystem"
                                           "numberingSystems"
                                           "numeric"
                                           "region"
                                           "script"
                                           "textInfo"
                                           "timeZones"
                                           "weekInfo"]}]
    [js/Intl.NumberFormat {:sym 'Intl.NumberFormat 
                           :demo (new js/Intl.NumberFormat
                                       "de-DE"
                                       #js{:style "currency",
                                          :currency "EUR" })
                           :args '[...]}]
    [js/Intl.PluralRules {:sym 'Intl.PluralRules
                          :demo (new js/Intl.PluralRules "en-US") 
                          :args '[...]}]
    [js/Intl.RelativeTimeFormat {:sym 'Intl.RelativeTimeFormat
                                 :demo (new js/Intl.RelativeTimeFormat
                                             "en"
                                             #js{:style "short"})
                                 :args '[...]}]
    [js/Intl.Segmenter {:sym 'Intl.Segmenter
                        :demo (new js/Intl.Segmenter
                                    "fr"
                                    #js{:granularity "word"})
                        :args '[...]}]

    ;; experimental, Safari-only
    ;; [js/Intl.DurationFormat {:sym 'DurationFormat :args '[...]}]
    ]

   "Managing memory"
   [[js/WeakRef {:sym 'WeakRef
                 #_#_:demo "(new js/WeakRef (fn [x]))" :args '[f]}]
    [js/FinalizationRegistry {:sym 'FinalizationRegistry
                              #_#_:demo "(new js/FinalizationRegistry (fn [x]))"
                              :args '[f]}]]    

   "Reflection"
   [[js/Reflect {:sym 'Reflect :not-a-constructor? true}]
    [js/Proxy {:sym 'Proxy
               #_#_:demo "(new js/Proxy #js {:a 1} (fn [x]))"
               :args '[...]}]]
   ))
