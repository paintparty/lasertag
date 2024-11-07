;; TODO - Should demos be functions that create new Errors and instances ?
;;        Or maybe just quoted lists?

(ns lasertag.cljs-interop
  (:require 
   ;; keep these commented out unless you are doing code gen with fns at end of this namespace
  ;;  [clojure.pprint :refer [pprint]]
  ;;  [clojure.set :as set]
   ))


;; These are the built-ins you cannot access directly, e.g. `js/Iterator`
;; Iterator 
;; AsyncIterator
;; GeneratorFunction 
;; AsyncGeneratorFunction 
;; Generator 
;; AsyncGenerator 
;; AsyncFunction 
;; Errors

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
                :demo (js/Symbol "my-sym")
                :not-a-constructor? true
                :args '[s]}]]

   "Numbers and dates"
   [[js/Math {:sym 'Math :not-a-constructor? true}]
    [js/Number {:sym 'Number :demo (new js/Number "3") :args '[v]}]
    [js/BigInt {:sym 'BigInt
                :demo (js/BigInt "999999999999")
                :not-a-constructor? true
                :args '[v]}]
    [js/Date {:sym 'Date :demo (new js/Date) :args '[...]}]]

   "Value properties" 
   [[js/NaN {:sym 'NaN}] 
    [js/Infinity {:sym 'Infinity}] 
    [js/globalThis {:sym 'globalThis}]]

   "Control abstraction objects"
   [[js/Promise {:sym 'Promise :demo (new js/Promise (fn [x] x)) :args '[f]}]]

   "Error objects"
   [[js/AggregateError {:sym 'AggregateError
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
    [js/WeakSet {:sym 'WeakSet :demo (new js/Set #js[1 2]) :args '[...]}]
    ]

   "Indexed collections"
   [[js/Array {:sym 'Array :demo (new js/Array #js[1 2 3]) :args '[...]}]
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
                            :demo                (new js/ArrayBuffer 8) 
                            :args                '[...]
                            :instance-properties ["byteLength"
                                                  "detached"
                                                  "maxByteLength"
                                                  "resizable"]}]
           [js/JSON {:sym                'JSON
                     :not-a-constructor? true}]
           [js/DataView {:sym                 'DataView
                         :demo                (new js/DataView (new js/ArrayBuffer 8))
                         :args                '[ArrayBuffer]
                         :instance-properties ["buffer"
                                               "byteLength"
                                               "byteOffset"]}]]
          (when Atomics [[Atomics {:sym                'Atomics
                                   :not-a-constructor? true}]])))

   "Internationalization"
   [[js/Intl {:sym 'Intl :not-a-constructor? true}]
    [js/Intl.Collator {:sym 'Intl.Collator
                       :demo (new js/Intl.Collator "sv")
                       :args '[...]}]
    [js/Intl.DateTimeFormat {:sym 'Intl.DateTimeFormat
                             :demo (new js/Intl.DateTimeFormat
                                        "en-US")
                             :args '[...]}]
    [js/Intl.DisplayNames {:sym 'Intl.DisplayNames
                           :demo (new js/Intl.DisplayNames
                                      #js["en"]
                                      #js {:type "region"})
                           :args '[...]}]
    [js/Intl.ListFormat {:sym 'Intl.ListFormat
                         :demo (new js/Intl.ListFormat 
                                    "en-GB"
                                    #js {:style "long",
                                         :type "conjunction"})
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
                 :demo (new js/WeakRef (fn [x])) :args '[f]}]
    [js/FinalizationRegistry {:sym 'FinalizationRegistry
                              :demo (new js/FinalizationRegistry (fn [x])) :args '[f]}]]    

   "Reflection"
   [[js/Reflect {:sym 'Reflect :not-a-constructor? true}]
    [js/Proxy {:sym 'Proxy
               :demo (new js/Proxy #js {:a 1} (fn [x])) :args '[...]}]]
   ))

(defonce js-built-ins-by-built-in*
  (apply concat (vals js-built-ins-by-category)))

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
                 "Reflection"]))))

(defonce js-built-ins-by-built-in
  (into {} js-built-ins-by-built-in*))

(defonce js-built-ins-which-are-not-functions-or-constructors
  (if Atomics 
    ;; js/Atomics support
    #{js/Math
      js/JSON
      Atomics
      js/Intl
      js/Reflect}
    ;; No js/Atomics support
    #{js/Math
      js/JSON
      js/Intl
      js/Reflect} ))

(defonce js-built-ins-which-are-iterables-by-built-in*
  (apply concat
         (vals (select-keys js-built-ins-by-category 
                            ["Indexed collections" "Keyed collections"]))))


(defonce js-built-ins-which-are-iterables
  (disj (into #{} (keys (into {} js-built-ins-which-are-iterables-by-built-in*)))
        js/WeakMap
        js/WeakSet))


;;;; define built-in values -------------------
(defonce js-built-in-global-values-by-value
  (get js-built-ins-by-category "Value properties"))

(defonce js-built-in-global-values-by-value-map
  (into {} js-built-in-global-values-by-value))

(defonce js-built-in-global-values
  (into #{} (keys js-built-in-global-values-by-value-map)))


;;;; define built-in functions -------------------

(defonce js-built-in-functions-by-function*
  (get js-built-ins-by-category "Function properties"))

(defonce js-built-in-functions-by-function
  (into {} js-built-in-functions-by-function*))

(defonce js-built-in-functions
  (into #{} (keys js-built-in-functions-by-function)))


;;;; define built-in Internationalization objects ---

(defonce js-built-in-intl-by-object*
  (get js-built-ins-by-category "Internationalization"))

(defonce js-built-in-intl-by-object
  (into {} js-built-in-intl-by-object*))

(defonce js-built-in-intl
  (into #{} (keys js-built-in-intl-by-object)))


;;;; define built-in objects ---------------------

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
                 (get js-built-ins-by-category k)))))

(defonce js-built-in-objects-by-object-map
  (into {} js-built-in-objects-by-object))

(defonce js-built-in-objects
  (into #{} (keys js-built-in-objects-by-object-map)))


;; This def is generated AOT at repl from above,
;; to speed up startup time of script
(def objects-by-method-name 
  {"message"                  [js/Error
                               js/AggregateError
                               js/EvalError
                               js/RangeError
                               js/ReferenceError
                               js/SyntaxError
                               js/TypeError
                               js/URIError],
   "values"                   [js/Map js/Object js/Set js/Array],
   "includes"                 [js/String js/Array],
   "forEach"                  [js/Map js/Set js/Array],
   "defineProperty"           [js/Reflect js/Object],
   "valueOf"                  [js/String
                               js/Object
                               js/Boolean
                               js/Symbol
                               js/Number
                               js/BigInt
                               js/Date],
   "keys"                     [js/Map js/Object js/Set js/Array],
   "getPrototypeOf"           [js/Reflect js/Object],
   "toLocaleString"           [js/Object js/Array js/Number js/BigInt js/Date],
   "of"                       [js/Intl.DisplayNames js/Array],
  ;;  "sub"                      [js/Atomics js/String],
   "sub"                      [js/String],
   "byteLength"               [js/DataView js/ArrayBuffer],
   "delete"                   [js/Map js/Set js/WeakMap js/WeakSet],
   "formatRange"              [js/Intl.DateTimeFormat js/Intl.NumberFormat],
   "preventExtensions"        [js/Reflect js/Object],
   "BYTES_PER_ELEMENT"        [js/Int8Array
                               js/Uint8Array
                               js/Uint8ClampedArray
                               js/Int16Array
                               js/Uint16Array
                               js/Int32Array
                               js/Uint32Array
                               js/BigInt64Array
                               js/BigUint64Array
                               js/Float32Array
                               js/Float64Array],
   "toString"                 [js/Error
                               js/String
                               js/RegExp
                               js/Intl.Locale
                               js/Object
                               js/Function
                               js/Boolean
                               js/Symbol
                               js/Array
                               js/Number
                               js/BigInt
                               js/Date],
   "getOwnPropertyDescriptor" [js/Reflect js/Object],
   "supportedLocalesOf"       [js/Intl.Collator
                               js/Intl.DateTimeFormat
                               js/Intl.DisplayNames
                               js/Intl.ListFormat
                               js/Intl.NumberFormat
                               js/Intl.PluralRules
                               js/Intl.RelativeTimeFormat
                               js/Intl.Segmenter],
   "apply"                    [js/Reflect js/Function],
   "name"                     [js/Error
                               js/String
                               js/DataView
                               js/RegExp
                               js/Intl.Collator
                               js/Intl.DateTimeFormat
                               js/Intl.DisplayNames
                               js/Intl.ListFormat
                               js/Intl.Locale
                               js/Intl.NumberFormat
                               js/Intl.PluralRules
                               js/Intl.RelativeTimeFormat
                               js/Intl.Segmenter
                               js/WeakRef
                               js/FinalizationRegistry
                               js/Map
                               js/Object
                               js/Set
                               js/Proxy
                               js/Function
                               js/WeakMap
                               js/Boolean
                               js/WeakSet
                               js/Symbol
                               js/Array
                               js/Int8Array
                               js/Number
                               js/Uint8Array
                               js/BigInt
                               js/Uint8ClampedArray
                               js/Date
                               js/Int16Array
                               js/Uint16Array
                               js/Promise
                               js/Int32Array
                               js/AggregateError
                               js/Uint32Array
                               js/EvalError
                               js/BigInt64Array
                               js/RangeError
                               js/BigUint64Array
                               js/ReferenceError
                               js/Float32Array
                               js/SyntaxError
                               js/Float64Array
                               js/TypeError
                               js/ArrayBuffer
                               js/URIError],
   "formatRangeToParts"       [js/Intl.DateTimeFormat js/Intl.NumberFormat],
   "replace"                  [js/String js/Symbol],
   "lastIndexOf"              [js/String js/Array],
   "formatToParts"            [js/Intl.DateTimeFormat
                               js/Intl.ListFormat
                               js/Intl.NumberFormat
                               js/Intl.RelativeTimeFormat],
   "split"                    [js/String js/Symbol],
   "isExtensible"             [js/Reflect js/Object],
   "concat"                   [js/String js/Array],
   "setPrototypeOf"           [js/Reflect js/Object],
   "length"                   [js/Error
                               js/String
                               js/DataView
                               js/RegExp
                               js/Intl.Collator
                               js/Intl.DateTimeFormat
                               js/Intl.DisplayNames
                               js/Intl.ListFormat
                               js/Intl.Locale
                               js/Intl.NumberFormat
                               js/Intl.PluralRules
                               js/Intl.RelativeTimeFormat
                               js/Intl.Segmenter
                               js/WeakRef
                               js/FinalizationRegistry
                               js/Map
                               js/Object
                               js/Set
                               js/Proxy
                               js/Function
                               js/WeakMap
                               js/Boolean
                               js/WeakSet
                               js/Symbol
                               js/Array
                               js/Int8Array
                               js/Number
                               js/Uint8Array
                               js/BigInt
                               js/Uint8ClampedArray
                               js/Date
                               js/Int16Array
                               js/Uint16Array
                               js/Promise
                               js/Int32Array
                               js/AggregateError
                               js/Uint32Array
                               js/EvalError
                               js/BigInt64Array
                               js/RangeError
                               js/BigUint64Array
                               js/ReferenceError
                               js/Float32Array
                               js/SyntaxError
                               js/Float64Array
                               js/TypeError
                               js/ArrayBuffer
                               js/URIError],
   "entries"                  [js/Map js/Object js/Set js/Array],
   "groupBy"                  [js/Map js/Object],
   "match"                    [js/String js/Symbol],
   "slice"                    [js/String js/Array js/ArrayBuffer],
   "parse"                    [js/Date js/JSON],
  ;;  "add"                      [js/Atomics js/Set js/WeakSet],
   "add"                      [js/Set js/WeakSet],
   "set"                      [js/Map js/Reflect js/WeakMap],
   "matchAll"                 [js/String js/Symbol],
   "size"                     [js/Map js/Set],
   "has"                      [js/Map js/Reflect js/Set js/WeakMap js/WeakSet],
   "clear"                    [js/Map js/Set],
   "indexOf"                  [js/String js/Array],
   "search"                   [js/String js/Symbol],
   "get"                      [js/Map js/Reflect js/WeakMap],
   "at"                       [js/String js/Array],
   "resolvedOptions"          [js/Intl.Collator
                               js/Intl.DateTimeFormat
                               js/Intl.DisplayNames
                               js/Intl.ListFormat
                               js/Intl.NumberFormat
                               js/Intl.PluralRules
                               js/Intl.RelativeTimeFormat
                               js/Intl.Segmenter],
   "format"                   [js/Intl.DateTimeFormat
                               js/Intl.ListFormat 
                               js/Intl.NumberFormat 
                               js/Intl.RelativeTimeFormat]})

(def objects-by-unique-method-name
  {"NaN"                       js/Number,
   "toSpliced"                 js/Array,
   "toUTCString"               js/Date,
   "copyWithin"                js/Array,
   "floor"                     js/Math,
   "map"                       js/Array,
   "log1p"                     js/Math,
   "ceil"                      js/Math,
   "message"                   js/URIError,
   "toWellFormed"              js/String,
   "compare"                   js/Intl.Collator,
   "trimRight"                 js/String,
   "getUTCMinutes"             js/Date,
   "padStart"                  js/String,
   "LOG10E"                    js/Math,
   "defineProperties"          js/Object,
   "getTimezoneOffset"         js/Date,
   "random"                    js/Math,
   "setMonth"                  js/Date,
   "setUint16"                 js/DataView,
   "cosh"                      js/Math,
   "PI"                        js/Math,
   "values"                    js/Array,
   "sup"                       js/String,
   "unicodeSets"               js/RegExp,
   "byteOffset"                js/DataView,
   "min"                       js/Math,
   "isRawJSON"                 js/JSON,
   "trim"                      js/String,
   "toLocaleTimeString"        js/Date,
   "includes"                  js/Array,
   "getFloat32"                js/DataView,
   "toLowerCase"               js/String,
   "getUint32"                 js/DataView,
   "unscopables"               js/Symbol,
   "NumberFormat"              js/Intl,
   "numeric"                   js/Intl.Locale,
   "fromCodePoint"             js/String,
   "toLocaleDateString"        js/Date,
   "Collator"                  js/Intl,
   "getBigUint64"              js/DataView,
   "lastMatch"                 js/RegExp,
   "assign"                    js/Object,
   "findLastIndex"             js/Array,
   "setSeconds"                js/Date,
   "calendar"                  js/Intl.Locale,
   "getUTCFullYear"            js/Date,
   "fround"                    js/Math,
   "species"                   js/Symbol,
   "normalize"                 js/String,
   "forEach"                   js/Array,
   "getUint16"                 js/DataView,
   "defineProperty"            js/Object,
   "freeze"                    js/Object,
   "setTime"                   js/Date,
   "valueOf"                   js/Date,
   "keys"                      js/Array,
   "trimStart"                 js/String,
   "getPrototypeOf"            js/Object,
   "replaceAll"                js/String,
   "resolve"                   js/Promise,
   "LN2"                       js/Math,
   "call"                      js/Function,
   "setInt16"                  js/DataView,
   "toLocaleString"            js/Date,
   "unregister"                js/FinalizationRegistry,
   "of"                        js/Array,
   "setDate"                   js/Date,
   "PluralRules"               js/Intl,
   "Locale"                    js/Intl,
   "fromCharCode"              js/String,
   "lastParen"                 js/RegExp,
   "sub"                       js/String,
   "numberingSystem"           js/Intl.Locale,
   "setUTCHours"               js/Date,
   "v8BreakIterator"           js/Intl,
   "setUTCMonth"               js/Date,
   "asUintN"                   js/BigInt,
   "getUTCMilliseconds"        js/Date,
   "find"                      js/Array,
   "getFloat64"                js/DataView,
   "toFixed"                   js/Number,
   "byteLength"                js/ArrayBuffer,
   "supportedValuesOf"         js/Intl,
   "isArray"                   js/Array,
   "race"                      js/Promise,
   "DisplayNames"              js/Intl,
   "toGMTString"               js/Date,
   "setUint32"                 js/DataView,
   "getInt16"                  js/DataView,
   "setUint8"                  js/DataView,
   "collations"                js/Intl.Locale,
   "getCanonicalLocales"       js/Intl,
   "weekInfo"                  js/Intl.Locale,
   "delete"                    js/WeakSet,
   "stackTraceLimit"           js/Error,
   "formatRange"               js/Intl.NumberFormat,
  ;;  "compareExchange"           js/Atomics,
   "every"                     js/Array,
   "leftContext"               js/RegExp,
   "atan2"                     js/Math,
   "getUTCSeconds"             js/Date,
   "multiline"                 js/RegExp,
   "getYear"                   js/Date,
   "hasOwnProperty"            js/Object,
   "segment"                   js/Intl.Segmenter,
  ;;  "load"                      js/Atomics,
   "hourCycle"                 js/Intl.Locale,
   "pop"                       js/Array,
   "padEnd"                    js/String,
   "reverse"                   js/Array,
   "setBigInt64"               js/DataView,
   "bind"                      js/Function,
   "cbrt"                      js/Math,
   "is"                        js/Object,
   "asIntN"                    js/BigInt,
   "minimize"                  js/Intl.Locale,
   "repeat"                    js/String,
   "endsWith"                  js/String,
   "localeCompare"             js/String,
   "setInt8"                   js/DataView,
   "textInfo"                  js/Intl.Locale,
   "toLocaleLowerCase"         js/String,
   "ownKeys"                   js/Reflect,
  ;;  "waitAsync"                 js/Atomics,
   "flat"                      js/Array,
   "max"                       js/Math,
   "preventExtensions"         js/Object,
   "BYTES_PER_ELEMENT"         js/Float64Array,
   "strike"                    js/String,
   "getHours"                  js/Date,
   "toLocaleUpperCase"         js/String,
   "withResolvers"             js/Promise,
   "asyncIterator"             js/Symbol,
   "parseFloat"                js/Number,
   "captureStackTrace"         js/Error,
   "toStringTag"               js/Symbol,
   "pow"                       js/Math,
   "toPrimitive"               js/Symbol,
   "small"                     js/String,
   "resizable"                 js/ArrayBuffer,
   "setHours"                  js/Date,
   "flags"                     js/RegExp,
   "toString"                  js/Date,
   "for"                       js/Symbol,
   "collation"                 js/Intl.Locale,
   "getMilliseconds"           js/Date,
   "getOwnPropertyDescriptor"  js/Object,
   "catch"                     js/Promise,
   "push"                      js/Array,
   "timeZones"                 js/Intl.Locale,
   "POSITIVE_INFINITY"         js/Number,
   "supportedLocalesOf"        js/Intl.Segmenter,
   "unicode"                   js/RegExp,
   "fixed"                     js/String,
   "fontsize"                  js/String,
   "atan"                      js/Math,
   "revocable"                 js/Proxy,
   "MAX_VALUE"                 js/Number,
   "detached"                  js/ArrayBuffer,
   "sort"                      js/Array,
   "iterator"                  js/Symbol,
   "exec"                      js/RegExp,
   "log"                       js/Math,
   "findIndex"                 js/Array,
   "apply"                     js/Function,
   "acosh"                     js/Math,
   "anchor"                    js/String,
   "buffer"                    js/DataView,
   "getDate"                   js/Date,
   "setFullYear"               js/Date,
   "splice"                    js/Array,
   "unshift"                   js/Array,
   "any"                       js/Promise,
   "substring"                 js/String,
   "MIN_SAFE_INTEGER"          js/Number,
   "toTimeString"              js/Date,
   "name"                      js/URIError,
   "selectRange"               js/Intl.PluralRules,
   "formatRangeToParts"        js/Intl.NumberFormat,
   "sin"                       js/Math,
   "replace"                   js/Symbol,
   "maximize"                  js/Intl.Locale,
   "construct"                 js/Reflect,
   "dotAll"                    js/RegExp,
   "allSettled"                js/Promise,
   "toPrecision"               js/Number,
   "hypot"                     js/Math,
   "E"                         js/Math,
   "caseFirst"                 js/Intl.Locale,
   "keyFor"                    js/Symbol,
   "script"                    js/Intl.Locale,
   "flatMap"                   js/Array,
   "caller"                    js/Function,
   "lastIndexOf"               js/Array,
   "charAt"                    js/String,
   "shift"                     js/Array,
   "exp"                       js/Math,
   "toISOString"               js/Date,
   "getUint8"                  js/DataView,
  ;;  "wait"                      js/Atomics,
   "imul"                      js/Math,
   "select"                    js/Intl.PluralRules,
   "formatToParts"             js/Intl.RelativeTimeFormat,
  ;;  "or"                        js/Atomics,
   "LN10"                      js/Math,
   "setMilliseconds"           js/Date,
   "substr"                    js/String,
   "DateTimeFormat"            js/Intl,
   "region"                    js/Intl.Locale,
   "fill"                      js/Array,
   "split"                     js/Symbol,
   "getFullYear"               js/Date,
   "getUTCDay"                 js/Date,
   "isExtensible"              js/Object,
   "log10"                     js/Math,
   "toUpperCase"               js/String,
   "setUTCMinutes"             js/Date,
   "isInteger"                 js/Number,
   "trimLeft"                  js/String,
   "toDateString"              js/Date,
   "sqrt"                      js/Math,
   "concat"                    js/Array,
   "setMinutes"                js/Date,
   "filter"                    js/Array,
   "RelativeTimeFormat"        js/Intl,
   "ignoreCase"                js/RegExp,
   "findLast"                  js/Array,
   "register"                  js/FinalizationRegistry,
   "setPrototypeOf"            js/Object,
   "transferToFixedLength"     js/ArrayBuffer,
   "length"                    js/URIError,
   "getUTCDate"                js/Date,
   "tan"                       js/Math,
   "compile"                   js/RegExp,
   "charCodeAt"                js/String,
   "cos"                       js/Math,
   "transfer"                  js/ArrayBuffer,
   "input"                     js/RegExp,
   "trunc"                     js/Math,
  ;;  "and"                       js/Atomics,
   "SQRT1_2"                   js/Math,
   "deref"                     js/WeakRef,
   "italics"                   js/String,
   "entries"                   js/Array,
   "link"                      js/String,
   "isSealed"                  js/Object,
   "hourCycles"                js/Intl.Locale,
   "source"                    js/RegExp,
   "propertyIsEnumerable"      js/Object,
   "UTC"                       js/Date,
   "isSafeInteger"             js/Number,
   "isConcatSpreadable"        js/Symbol,
   "toSorted"                  js/Array,
   "getUTCHours"               js/Date,
   "groupBy"                   js/Object,
   "abs"                       js/Math,
   "getDay"                    js/Date,
   "hasIndices"                js/RegExp,
   "isNaN"                     js/Number,
   "stringify"                 js/JSON,
   "baseName"                  js/Intl.Locale,
   "startsWith"                js/String,
   "setBigUint64"              js/DataView,
   "match"                     js/Symbol,
   "expm1"                     js/Math,
   "big"                       js/String,
   "getSeconds"                js/Date,
   "setFloat64"                js/DataView,
   "from"                      js/Array,
   "join"                      js/Array,
   "getOwnPropertyDescriptors" js/Object,
   "toExponential"             js/Number,
   "isWellFormed"              js/String,
   "slice"                     js/ArrayBuffer,
   "LOG2E"                     js/Math,
   "numberingSystems"          js/Intl.Locale,
  ;;  "exchange"                  js/Atomics,
   "ListFormat"                js/Intl,
   "blink"                     js/String,
   "NEGATIVE_INFINITY"         js/Number,
   "isFinite"                  js/Number,
   "setInt32"                  js/DataView,
   "create"                    js/Object,
   "parse"                     js/JSON,
   "seal"                      js/Object,
   "deleteProperty"            js/Reflect,
   "arguments"                 js/Function,
   "with"                      js/Array,
   "trimEnd"                   js/String,
   "add"                       js/WeakSet,
   "getInt32"                  js/DataView,
   "now"                       js/Date,
   "set"                       js/WeakMap,
   "getUTCMonth"               js/Date,
   "some"                      js/Array,
   "setUTCFullYear"            js/Date,
   "hasInstance"               js/Symbol,
   "matchAll"                  js/Symbol,
   "all"                       js/Promise,
   "MAX_SAFE_INTEGER"          js/Number,
   "then"                      js/Promise,
   "reduceRight"               js/Array,
   "atanh"                     js/Math,
   "rawJSON"                   js/JSON,
   "clz32"                     js/Math,
   "resize"                    js/ArrayBuffer,
   "size"                      js/Set,
   "setUTCDate"                js/Date,
   "has"                       js/WeakSet,
  ;;  "notify"                    js/Atomics,
   "isView"                    js/ArrayBuffer,
   "reduce"                    js/Array,
   "clear"                     js/Set,
   "EPSILON"                   js/Number,
   "isFrozen"                  js/Object,
   "MIN_VALUE"                 js/Number,
   "global"                    js/RegExp,
   "indexOf"                   js/Array,
   "getMonth"                  js/Date,
   "isPrototypeOf"             js/Object,
   "getInt8"                   js/DataView,
   "raw"                       js/String,
   "language"                  js/Intl.Locale,
   "sinh"                      js/Math,
   "getOwnPropertySymbols"     js/Object,
   "reject"                    js/Promise,
   "setYear"                   js/Date,
   "sticky"                    js/RegExp,
   "asinh"                     js/Math,
   "setUTCSeconds"             js/Date,
   "calendars"                 js/Intl.Locale,
  ;;  "isLockFree"                js/Atomics,
   "fromEntries"               js/Object,
   "SQRT2"                     js/Math,
   "getTime"                   js/Date,
   "Segmenter"                 js/Intl,
   "round"                     js/Math,
   "toReversed"                js/Array,
   "search"                    js/Symbol,
   "get"                       js/WeakMap,
   "hasOwn"                    js/Object,
   "bold"                      js/String,
   "asin"                      js/Math,
   "getMinutes"                js/Date,
   "sign"                      js/Math,
   "rightContext"              js/RegExp,
   "getBigInt64"               js/DataView,
  ;;  "store"                     js/Atomics,
  ;;  "xor"                       js/Atomics,
   "tanh"                      js/Math,
   "at"                        js/Array,
   "setUTCMilliseconds"        js/Date,
   "resolvedOptions"           js/Intl.Segmenter,
   "description"               js/Symbol,
   "finally"                   js/Promise,
   "log2"                      js/Math,
   "maxByteLength"             js/ArrayBuffer,
   "fontcolor"                 js/String,
   "test"                      js/RegExp,
   "setFloat32"                js/DataView,
   "format"                    js/Intl.RelativeTimeFormat,
   "codePointAt"               js/String,
   "getOwnPropertyNames"       js/Object,
   "toJSON"                    js/Date, 
   "acos"                      js/Math, 
   "parseInt"                  js/Number})



;; TODO - incorporate test for Atomics support in codegen below
;; Code below is for generating 
;; lasertag.interop/objects-by-unique-method-name
;; lasertag.interop/objects-by-method-name


;;;; helper fns ---------------------

;; (defn- freq [coll op]
;;   (->> coll 
;;        (frequencies)
;;        (filter (fn [[_ v]] (op v 1)))
;;        (keys)) )

;; (defn- method-names-by-object [objects]
;;   (into {}
;;         (map (fn [o]
;;                (let [statics   (js->clj (js/Object.getOwnPropertyNames o))
;;                      instances (some-> o
;;                                        .-prototype
;;                                        js/Object.getOwnPropertyNames
;;                                        js->clj)]
;;                  [o (filter #(not (re-find #"^closure_uid|^\$|^__|^com\$|^transit\$|^portal\$|^prototype$|^constructor$|^cljs$|^clojure$" %))
;;                             (concat statics instances))])) 
;;              objects)))


;; (defn- objects-by-method-name*
;;   [method-names-by-object repeats]
;;   (into {}
;;         (map (fn [s]
;;                (let [built-ins (reduce (fn [acc [k v]]
;;                                          (if (contains? (into #{} v) s)
;;                                            (conj acc k)
;;                                            acc))
;;                                        [] 
;;                                        method-names-by-object)]
;;                  [s built-ins]))
;;              repeats)))

;; ;; (defn- x
;; ;;   [m]
;; ;;   (->> m
;; ;;        (map (fn [[k v]]
;; ;;               (map (fn [method-name]
;; ;;                      [method-name k])
;; ;;                    v)))
;; ;;        (apply concat)
;; ;;        (into {})))


;; ;; Define different servings of the data only for use when generating data aot from repl
;; (defonce js-built-in-methods
;;   (do 
;;     (println "def'ing js-built-in-methods")
;;     (let [
;;           ;; TODO - add the rest of the relevant built-ins from js
;;           objects-ordered
;;           (mapv (fn [[o _]] o) js-built-in-objects-by-object)

;;           object-names-by-object
;;           (into {} (map (fn [[o m]] [o (:sym m)]) js-built-in-objects-by-object-map)) 

;;           object-names
;;           (into #{} (vals object-names-by-object))

;;           method-names-by-object 
;;           (method-names-by-object js-built-in-objects)

;;           ;; should be #{"prototype" "name" "length" "constructor"}
;;           shared-names
;;           (do 
;;             (apply set/intersection '(#{"name"} #{"name"}))
;;             (apply set/intersection (map #(into #{} %) (vals method-names-by-object))))

;;           all-names
;;           (apply concat (vals method-names-by-object))

;;           freqs
;;           (freq all-names >)

;;           freqs-set (into #{} freqs)

;;           uniques
;;           (freq all-names =)

;;           unique-method-names-by-object
;;           (into {}
;;                 (map (fn [[o v]]
;;                        [o (disj (into #{} v) freqs-set)])
;;                      method-names-by-object))

;;           objects-by-unique-method-name
;;           (objects-by-unique-method-names* unique-method-names-by-object)
          

;;           repeats
;;           (apply disj freqs-set shared-names)
          
;;           objects-by-method-name
;;           (objects-by-method-name* method-names-by-object repeats)]

;;       {:js-built-in-objects           js-built-in-objects
;;        :objects-ordered               objects-ordered
;;        :object-names-by-object        object-names-by-object
;;        :object-names                  object-names
;;        :unique-method-names-by-object unique-method-names-by-object
;;        :objects-by-unique-method-name objects-by-unique-method-name
;;        :method-names-by-object        method-names-by-object
;;        :shared-names                  shared-names
;;        :all-names                     all-names
;;        :freqs                         freqs
;;        :uniques                       uniques
;;        :repeats                       repeats
;;        :objects-by-method-name        objects-by-method-name})))




;; (def intl-classes
;;   #{"Collator"
;;     "DateTimeFormat"
;;     "DisplayNames"
;;     "ListFormat"
;;     "Locale"
;;     "NumberFormat"
;;     "PluralRules"
;;     "RelativeTimeFormat"
;;     "Segmenter"})

;; (defn symbolize-js-built-in [x]
;;   (let [s (cond 
;;             (= x js/Math)    "js/Math"
;;             (= x js/JSON)    "js/JSON"
;;             (= x js/Intl)    "js/Intl"
;;             (= x js/Atomics) "js/Atomics"
;;             (= x js/Reflect) "js/Reflect"
;;             :else
;;             (let [[_ cn] (re-find #"\#object\[(.+)\]" (with-out-str (print x)))]
;;               (str "js/" (when (contains? intl-classes cn) "Intl.") cn)))]
;;     (symbol s)))

;; (defn print-objects-by-method-name-aot
;;   "For building maps aot from repl"
;;   [m] 
;;   (pprint (reduce (fn [acc [k v]]
;;                     (assoc acc k (mapv symbolize-js-built-in v)))
;;                   {}
;;                   m))) 

;; (defn print-objects-by-unique-method-name-aot
;;   "For building maps aot from repl"
;;   [m] 
;;   (pprint (reduce (fn [acc [k v]]
;;                     (if (re-find #"^clojure\$|^cljs\$" k) 
;;                       acc
;;                       (assoc acc
;;                              k 
;;                              (symbolize-js-built-in v))))
;;                   {}
;;                   m)))

;; ;; (print-objects-by-method-name-aot (:objects-by-method-name js-built-in-methods))
;; ;; (print-objects-by-unique-method-name-aot (:objects-by-unique-method-name js-built-in-methods))
  
  
