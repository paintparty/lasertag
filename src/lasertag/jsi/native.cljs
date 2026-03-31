(ns lasertag.jsi.native)


(defonce Atomics
  (try js/Atomics
       (catch js/Object e nil)))

(defonce js-built-ins-by-category
  (array-map 
   "Fundamental objects"
   [[js/Object {:sym 'Object :args '[...]}]
    [js/Function {:sym 'Function :args '[...]}]
    [js/Boolean {:sym 'Boolean :args '[...]}]
    [js/Symbol {:sym 'Symbol
                :demo "(js/Symbol \"my-sym\")"
                :not-a-constructor? true
                :args '[s]}]]

   "Numbers and dates"
   [[js/Math {:sym 'Math :not-a-constructor? true}]
    [js/Number {:sym 'Number :demo "(new js/Number \"3\")" :args '[v]}]
    [js/BigInt {:sym 'BigInt
                :demo "(js/BigInt \"999999999999\")"
                :not-a-constructor? true
                :args '[v]}]
    [js/Date {:sym 'Date :demo "(new js/Date)" :args '[...]}]]

   "Value properties" 
   [[js/NaN {:sym 'NaN}] 
    [js/Infinity {:sym 'Infinity}] 
    [js/globalThis {:sym 'globalThis}]]

   "Control abstraction objects"
   [[js/Promise {:sym 'Promise #_#_:demo "(new js/Promise (fn [x] x))" :args '[f]}]]

   "Error objects"
   [[js/AggregateError {:sym 'AggregateError
                        :demo "(new js/AggregateError
                                   #js[(new js/Error \"some error\")] \"Hello\")"
                        :args '[array]}]
    [js/EvalError {:sym 'EvalError :demo "(new js/EvalError)" :args '[]}]
    [js/RangeError {:sym 'RangeError :demo "(new js/RangeError)" :args '[]}]
    [js/ReferenceError {:sym 'ReferenceError :demo "(new js/ReferenceError)" :args '[]}]
    [js/SyntaxError {:sym 'SyntaxError :demo "(new js/SyntaxError)" :args '[]}]
    [js/TypeError {:sym 'TypeError :demo "(new js/TypeError)" :args '[]}]
    [js/URIError {:sym 'URIError :demo "(new js/URIError)" :args '[]}]
    [js/Error {:sym 'Error :demo "(new js/Error)" :args '[]}]]

   "Text processing"
   [[js/String {:sym 'String :demo "(new js/String \"hi\")" :args '[s]}]
    [js/RegExp {:sym 'RegExp :demo "(new js/RegExp \"^hi$\")" :args '[s]}]]

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
   [[js/Map {:sym 'Map :demo "(new js/Map #js[#js[\"a\", 1], #js[\"b\", 2]])" :args '[...]}]
    [js/Set {:sym 'Set :demo "(new js/Set #js[1 2])" :args '[...]}]
    [js/WeakMap {:sym  'WeakMap
                 :demo "(let [wm (js/WeakMap.)
                              o  #js{:a 1}]
                         (.set wm o 100))"
                 :args '[...]}]
    ;; TODO - fix this WeakSet demo
    [js/WeakSet {:sym 'WeakSet :demo "(new js/Set #js[1 2])" :args '[...]}]
    ]

   "Indexed collections"
   [[js/Array {:sym 'Array :demo "(new js/Array 1 2 3)" :args '[...]}]
    [js/Int8Array {:sym 'Int8Array :demo "(new js/Int8Array #js[1 2 3])" :args '[...]}]
    [js/Uint8Array {:sym 'Uint8Array :demo "(new js/Uint8Array #js[1 2 3])" :args '[...]}]
    [js/Uint8ClampedArray {:sym  'Uint8ClampedArray :demo "(new js/Uint8ClampedArray #js[1 2 3])" :args '[...]}]
    [js/Int16Array {:sym 'Int16Array :demo "(new js/Int16Array #js[1 2 3])" :args '[...]}]
    [js/Uint16Array {:sym 'Uint16Array :demo "(new js/Uint16Array #js[1 2 3])" :args '[...]}]
    [js/Int32Array {:sym 'Int32Array :demo "(new js/Int32Array #js[1 2 3])" :args '[...]}]
    [js/Uint32Array {:sym 'Uint32Array :demo "(new js/Uint32Array #js[1 2 3])" :args '[...]}]
    [js/BigInt64Array {:sym 'BigInt64Array :demo "(new js/BigInt64Array 3)" :args '[...]}]
    [js/BigUint64Array {:sym 'BigUint64Array :demo "(new js/BigUint64Array 3)" :args '[...]}]
    [js/Float32Array {:sym 'Float32Array :demo "(new js/Float32Array #js[1 2 3])" :args '[...]}]
    [js/Float64Array {:sym 'Float64Array :demo "(new js/Float64Array #js[1 2 3])" :args '[...]}]]

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
    [js/Intl.Collator {:sym 'Intl.Collator
                       :demo "(new js/Intl.Collator \"sv\")"
                       :args '[...]}]
    [js/Intl.DateTimeFormat {:sym 'Intl.DateTimeFormat
                             :demo "(new js/Intl.DateTimeFormat
                                         \"en-US\")"
                             :args '[...]}]
    [js/Intl.DisplayNames {:sym 'Intl.DisplayNames
                           :demo "(new js/Intl.DisplayNames
                                       #js[\"en\"]
                                       #js {:type \"region\"})"
                           :args '[...]}]
    [js/Intl.ListFormat {:sym 'Intl.ListFormat
                         :demo "(new js/Intl.ListFormat 
                                     \"en-GB\"
                                     #js {:style \"long\",
                                          :type \"conjunction\"})"
                         :args '[...]}]
    [js/Intl.Locale {:sym 'Intl.Locale
                     :demo "(new js/Intl.Locale
                                 \"ko\"
                                 #js{:script \"Kore\",
                                     :region \"KR\",
                                     :hourCycle \"h23\",
                                     :calendar \"gregory\"})"
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
                           :demo "(new js/Intl.NumberFormat
                                       \"de-DE\"
                                       #js{:style \"currency\",
                                          :currency \"EUR\" })"
                           :args '[...]}]
    [js/Intl.PluralRules {:sym 'Intl.PluralRules
                          :demo "(new js/Intl.PluralRules \"en-US\")" 
                          :args '[...]}]
    [js/Intl.RelativeTimeFormat {:sym 'Intl.RelativeTimeFormat
                                 :demo "(new js/Intl.RelativeTimeFormat
                                             \"en\"
                                             #js{:style \"short\"})" 
                                 :args '[...]}]
    [js/Intl.Segmenter {:sym 'Intl.Segmenter
                        :demo "(new js/Intl.Segmenter
                                    \"fr\"
                                    #js{:granularity \"word\"})"
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

(defonce js-built-ins-by-built-in*
  (apply concat (vals js-built-ins-by-category)))

(defonce js-built-ins-by-built-in
  (into {} js-built-ins-by-built-in*))

(defonce js-built-ins-which-are-iterables-by-built-in*
  (apply concat
         (vals (select-keys js-built-ins-by-category 
                            ["Indexed collections" "Keyed collections"]))))

(defonce js-built-ins-which-are-iterables
  (disj (into #{} (keys (into {} js-built-ins-which-are-iterables-by-built-in*)))
        js/WeakMap
        js/WeakSet))

(defonce js-built-in-intl-by-object*
  (get js-built-ins-by-category "Internationalization"))

(defonce js-built-in-intl-by-object
  (into {} js-built-in-intl-by-object*))

(defonce js-built-in-errors-by-object*
  (get js-built-ins-by-category "Error objects"))

(defonce js-built-in-errors-by-object
  (into {} js-built-in-errors-by-object*))

(defonce js-built-in-indexed-collections-by-object*
  (get js-built-ins-by-category "Indexed collections"))

(defonce js-built-in-indexed-collections-by-object
  (into {} js-built-in-indexed-collections-by-object*))


;; for cached
;; Error objects       :throwable, maybe js-error? :js
;; Indexed collections :array, maybe :typed-array, :js
;; Internationalization :intl
;; Managing memory      :object :memory


;; These are the built-ins you cannot access directly, e.g. `js/Iterator`
;; Iterator 
;; AsyncIterator
;; GeneratorFunction 
;; AsyncGeneratorFunction 
;; Generator 
;; AsyncGenerator 
;; AsyncFunction 
;; Errors
