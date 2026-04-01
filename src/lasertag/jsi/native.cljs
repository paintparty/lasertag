(ns lasertag.jsi.native)


(defonce Atomics
  (try js/Atomics
       (catch js/Object e nil)))

(defonce js-built-ins-by-category
  (array-map 
   "Fundamental objects"
   [[js/Object {:sym 'Object }]
    [js/Function {:sym 'Function }]
    [js/Boolean {:sym 'Boolean }]
    [js/Symbol {:sym 'Symbol
                :not-a-constructor? true
                :args '[s]}]]

   "Numbers and dates"
   [[js/Math {:sym 'Math :not-a-constructor? true}]
    [js/Number {:sym 'Number}]
    [js/BigInt {:sym                'BigInt
                :not-a-constructor? true}]
    [js/Date {:sym 'Date}]]

   "Value properties" 
   [[js/NaN {:sym 'NaN}] 
    [js/Infinity {:sym 'Infinity}] 
    [js/globalThis {:sym 'globalThis}]]

   "Control abstraction objects"
   [[js/Promise {:sym 'Promise}]]

   "Error objects"
   [[js/AggregateError {:sym 'AggregateError}]
    [js/EvalError {:sym 'EvalError}]
    [js/RangeError {:sym 'RangeError}]
    [js/ReferenceError {:sym 'ReferenceError}]
    [js/SyntaxError {:sym 'SyntaxError}]
    [js/TypeError {:sym 'TypeError}]
    [js/URIError {:sym 'URIError}]
    [js/Error {:sym 'Error}]]

   "Text processing"
   [[js/String {:sym 'String}]
    [js/RegExp {:sym 'RegExp}]]

   "Function properties"
   [[js/eval {:sym 'eval}] 
    [js/isFinite {:sym 'isFinite}] 
    [js/isNaN {:sym 'isNaN}] 
    [js/parseFloat {:sym 'parseFloat}] 
    [js/parseInt {:sym 'parseInt  }] 
    [js/decodeURI {:sym 'decodeURI}] 
    [js/decodeURIComponent {:sym 'decodeURIComponent}] 
    [js/encodeURI {:sym 'encodeURI}] 
    [js/encodeURIComponent {:sym 'encodeURIComponent}] 
    [js/escape {:sym 'escape}] 
    [js/unescape {:sym 'unescape}]]

   "Keyed collections"
   [[js/Map {:sym 'Map}]
    [js/Set {:sym 'Set}]
    [js/WeakMap {:sym  'WeakMap}]
    [js/WeakSet {:sym 'WeakSet}]
    ]

   "Indexed collections"
   [[js/Array {:sym 'Array}]
    [js/Int8Array {:sym 'Int8Array}]
    [js/Uint8Array {:sym 'Uint8Array}]
    [js/Uint8ClampedArray {:sym  'Uint8ClampedArray}]
    [js/Int16Array {:sym 'Int16Array}]
    [js/Uint16Array {:sym 'Uint16Array}]
    [js/Int32Array {:sym 'Int32Array}]
    [js/Uint32Array {:sym 'Uint32Array}]
    [js/BigInt64Array {:sym 'BigInt64Array}]
    [js/BigUint64Array {:sym 'BigUint64Array}]
    [js/Float32Array {:sym 'Float32Array}]
    [js/Float64Array {:sym 'Float64Array}]]

   "Structured data"
   (into []
         (concat 
          [[js/ArrayBuffer {:sym                 'ArrayBuffer 
                            :instance-properties ["byteLength"
                                                  "detached"
                                                  "maxByteLength"
                                                  "resizable"]}]
           [js/JSON {:sym                'JSON
                     :not-a-constructor? true}]
           [js/DataView {:sym                 'DataView
                         :instance-properties ["buffer"
                                               "byteLength"
                                               "byteOffset"]}]]
          (when Atomics [[Atomics {:sym                'Atomics
                                   :not-a-constructor? true}]])))

   "Internationalization"
   [[js/Intl {:sym 'Intl :not-a-constructor? true}]
    [js/Intl.Collator {:sym 'Intl.Collator}]
    [js/Intl.DateTimeFormat {:sym 'Intl.DateTimeFormat}]
    [js/Intl.DisplayNames {:sym 'Intl.DisplayNames}]
    [js/Intl.ListFormat {:sym 'Intl.ListFormat}]
    [js/Intl.Locale {:sym                 'Intl.Locale
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
    [js/Intl.NumberFormat {:sym 'Intl.NumberFormat}]
    [js/Intl.PluralRules {:sym 'Intl.PluralRules}]
    [js/Intl.RelativeTimeFormat {:sym 'Intl.RelativeTimeFormat}]
    [js/Intl.Segmenter {:sym 'Intl.Segmenter}]

    ;; experimental, Safari-only
    ;; [js/Intl.DurationFormat {:sym 'DurationFormat }]
    ]

   "Managing memory"
   [[js/WeakRef {:sym 'WeakRef}]
    [js/FinalizationRegistry {:sym 'FinalizationRegistry}]]    

   "Reflection"
   [[js/Reflect {:sym 'Reflect :not-a-constructor? true}]
    [js/Proxy {:sym 'Proxy}]]))


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
