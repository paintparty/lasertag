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

;; For getting instance properties dynamically
#_(defn categorize-properties [obj]
  (loop [current obj
         props (hash-set)
         meths (hash-set)]
    (if (or (nil? current) (identical? current (.-prototype js/Object)))
      {:instance-properties (vec props) :instance-methods (vec meths)}
      (let [prop-names (js/Object.getOwnPropertyNames current)
            [new-props new-meths]
            (reduce
             (fn [[ps ms] prop]
               (if (= prop "constructor")
                 [ps ms]
                 (let [descriptor (js/Object.getOwnPropertyDescriptor current prop)
                       value (.-value descriptor)
                       getter (.-get descriptor)
                       is-method? (or (= "function" (type value))
                                      (some? getter))]
                   (if is-method?
                     [ps (conj ms prop)]
                     [(conj ps prop) ms]))))
             [props meths]
             prop-names)]
        (recur (js/Object.getPrototypeOf current) new-props new-meths)))))

(defonce Atomics
  (try js/Atomics
       (catch js/Object e nil)))


(def js-array-all-tags #{:object :js :instance-of-built-in :typed-array :array :coll-like})
(def js-array-instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"])
(def js-array-instance-properties ["buffer" "byteLength" "byteOffset" "length"])
(defonce built-ins
  (cond->
   {
 ;; FUNDAMENTAL OBJECTS -----------------------------------------------------
 
 js/Object                  {:sym              'Object
                             :args             ['obj]
                             :category         "Fundamental objects"
                             :example          '(new js/Object)
                             :example*          (new js/Object)
                             :instance-methods ["hasOwnProperty" "isPrototypeOf" "propertyIsEnumerable" "toLocaleString" "toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Object"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['obj]
                                                :fn-name   "Object"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Function                {:sym              'Function
                             :args             ['body]
                             :category         "Fundamental objects"
                             :example          '(js/Function. "a" "b" "return a + b")
                             :example*         (new js/Function "a" "b" "return a + b")
                             :instance-methods ["apply" "bind" "call" "toString"]
                             :tag-map          {:tag       :object
                                                :type      js/Function
                                                :all-tags  #{:object :js :instance-of-built-in :function}
                                                :classname "Function"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['body]
                                                :fn-name   "Function"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Boolean                 {:sym              'Boolean
                             :args             ['val]
                             :category         "Fundamental objects"
                             :example          '(new js/Boolean true)
                             :example*         (new js/Boolean true)
                             :instance-methods ["toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Boolean
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Boolean"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['val]
                                                :fn-name   "Boolean"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Symbol                  {:sym                'Symbol
                             :example            '(js/Symbol "my-sym")
                             :example*           (js/Symbol "my-sym")
                             :not-a-constructor? true
                             :args               ['desc]
                             :category           "Fundamental objects"
                             :instance-methods   ["toString" "valueOf"]
                             :tag-map            {:tag       :object
                                                  :type      js/Symbol
                                                  :all-tags  #{:object :js :instance-of-built-in}
                                                  :classname "Symbol"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   ['desc]
                                                  :fn-name   "Symbol"
                                                  :all-tags  #{:js :function :built-in}
                                                  :classname "Function"}}


 ;; NUMBERS AND DATES -------------------------------------------------------
 
 js/Math                    {:sym                'Math
                             :not-a-constructor? true
                             :category           "Numbers and dates"
                             :example            "js/Math.PI"
                             :example*            js/Math.PI
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :built-in}
                                                  :classname "Math"}
                             :self-tag-map       {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:js :object :built-in}
                                                  :classname "Math"}}

 js/Number                  {:sym              'Number
                             :example          '(new js/Number "3")
                             :example*         (new js/Number "3")
                             :args             ['n]
                             :category         "Numbers and dates"
                             :instance-methods ["toExponential" "toFixed" "toLocaleString" "toPrecision" "toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Number
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Number"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['n]
                                                :fn-name   "Number"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/BigInt                  {:sym                'BigInt
                             :not-a-constructor? true
                             :args               ['n]
                             :category           "Numbers and dates"
                             :example            '(js/BigInt 9007199254740991)
                             :example*           (js/BigInt 9007199254740991)
                             :instance-methods   ["toLocaleString" "toString" "valueOf"]
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :instance-of-built-in}
                                                  :classname "BigInt"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   ['n]
                                                  :fn-name   "BigInt"
                                                  :all-tags  #{:js :function :built-in}
                                                  :classname "Function"}}

 js/Date                    {:sym              'Date
                             :args             ['val]
                             :category         "Numbers and dates"
                             :example          '(new js/Date 2024 0 1)
                             :example*         (new js/Date 2024 0 1)
                             :instance-methods ["getDate" "getDay" "getFullYear" "getHours" "getMilliseconds" "getMinutes" "getMonth" "getSeconds" "getTime" "getTimezoneOffset" "getUTCDate" "getUTCDay" "getUTCFullYear" "getUTCHours" "getUTCMilliseconds" "getUTCMinutes" "getUTCMonth" "getUTCSeconds" "setDate" "setFullYear" "setHours" "setMilliseconds" "setMinutes" "setMonth" "setSeconds" "setTime" "setUTCDate" "setUTCFullYear" "setUTCHours" "setUTCMilliseconds" "setUTCMinutes" "setUTCMonth" "setUTCSeconds" "toDateString" "toISOString" "toJSON" "toLocaleDateString" "toLocaleString" "toLocaleTimeString" "toString" "toTimeString" "toUTCString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Date
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Date"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['val]
                                                :fn-name   "Date"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 ;; VALUE PROPERTIES --------------------------------------------------------
 
 js/NaN                     {:sym          'NaN
                             :category     "Value properties"
                             :example      'js/NaN
                             :example*     js/NaN
                             :tag-map      {:tag       :number
                                            :type      js/Number
                                            :all-tags  #{:nan :js :number :primitive}
                                            :classname "Number"}
                             :self-tag-map {:tag       :number
                                            :type      js/Number
                                            :all-tags  #{:nan :js :number :primitive}
                                            :classname "Number"}}

 js/Infinity                {:sym          'Infinity
                             :category     "Value properties"
                             :example      'js/Infinity
                             :example*      js/Infinity
                             :tag-map      {:tag       :number
                                            :type      js/Number
                                            :all-tags  #{:infinity :infinite :js :number :primitive}
                                            :classname "Number"}
                             :self-tag-map {:tag       :number
                                            :type      js/Number
                                            :all-tags  #{:infinity :infinite :js :number :primitive}
                                            :classname "Number"}}

 js/globalThis              {:sym          'globalThis
                             :category     "Value properties"
                             :example      'js/globalThis
                             :example*      js/globalThis
                             :tag-map      {:tag       :object
                                            :type      js/Object
                                            :all-tags  #{:object :js :global}
                                            :classname "Object"}
                             :self-tag-map {:tag       :object
                                            :type      js/Object
                                            :all-tags  #{:object :js :global}
                                            :classname "Object"}}


 ;; CONTROL ABSTRACTION OBJECTS ---------------------------------------------
 
 js/Promise                 {:sym              'Promise
                             :args             ['executor]
                             :category         "Control abstraction objects"
                             :example          '(new js/Promise (fn [resolve reject] (resolve 42)))
                             :example*         (new js/Promise (fn [resolve reject] (resolve 42)))
                             :instance-methods ["catch" "finally" "then"]
                             :tag-map          {:tag       :object
                                                :type      js/Promise
                                                :all-tags  #{:object :js :instance-of-built-in :async}
                                                :classname "Promise"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['executor]
                                                :fn-name   "Promise"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}


 ;; ERROR OBJECTS -----------------------------------------------------------
 
 js/AggregateError          {:sym          'AggregateError
                             :args         ['errors]
                             :category     "Error objects"
                             :example      '(new js/AggregateError #js[(js/Error. "err1")] "Multiple errors")
                             :example*     (new js/AggregateError #js[(js/Error. "err1")] "Multiple errors")
                             :tag-map      {:tag                 :throwable
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "AggregateError"
                                            :instance-properties ["errors" "message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['errors]
                                            :fn-name   "AggregateError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/EvalError               {:sym          'EvalError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/EvalError "eval error occurred")
                             :example*      (new js/EvalError "eval error occurred")
                             :tag-map      {:tag                 :throwable
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "EvalError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "EvalError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/RangeError              {:sym          'RangeError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/RangeError "value out of range")
                             :example*     (new js/RangeError "value out of range")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "RangeError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "RangeError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/ReferenceError          {:sym          'ReferenceError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/ReferenceError "variable not found")
                             :example*     (new js/ReferenceError "variable not found")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "ReferenceError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "ReferenceError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/SyntaxError             {:sym          'SyntaxError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/SyntaxError "syntax error in code")
                             :example*     (new js/SyntaxError "syntax error in code")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "SyntaxError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "SyntaxError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/TypeError               {:sym          'TypeError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/TypeError "wrong type provided")
                             :example*     (new js/TypeError "wrong type provided")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "TypeError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "TypeError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/URIError                {:sym          'URIError
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/URIError "invalid URI")
                             :example*     (new js/URIError "invalid URI")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "URIError"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "URIError"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 js/Error                   {:sym          'Error
                             :args         ['msg]
                             :category     "Error objects"
                             :example      '(new js/Error "something went wrong")
                             :example*      (new js/Error "something went wrong")
                             :tag-map      {:tag                 :throwable 
                                            :type                js/Error
                                            :all-tags            #{:throwable :object :js :instance-of-built-in :error}
                                            :classname           "Error"
                                            :instance-properties ["message" "name" "stack"]}
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['msg]
                                            :fn-name   "Error"
                                            :all-tags  #{:js :function :built-in}
                                            :classname "Function"}}

 ;; TEXT PROCESSING ---------------------------------------------------------
 
 js/String                  {:sym              'String
                             :example          '(new js/String "hi")
                             :example*         (new js/String "hi")
                             :args             ['s]
                             :category         "Text processing"
                             :instance-methods ["at" "charAt" "charCodeAt" "codePointAt" "concat" "endsWith" "includes" "indexOf" "isWellFormed" "lastIndexOf" "localeCompare" "match" "matchAll" "normalize" "padEnd" "padStart" "repeat" "replace" "replaceAll" "search" "slice" "split" "startsWith" "substring" "toLocaleLowerCase" "toLocaleUpperCase" "toLowerCase" "toString" "toUpperCase" "toWellFormed" "trim" "trimEnd" "trimStart" "valueOf"]
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['s]
                                                :fn-name   "String"
                                                :all-tags  #{:function :js :built-in}
                                                :classname "Function"}
                             :tag-map          {:tag                 :object
                                                :type                js/String
                                                :all-tags            #{:object :js :instance-of-built-in :string}
                                                :classname           "String"
                                                :instance-properties ["length"]}}

 js/RegExp                  {:sym              'RegExp
                             :example          '(new js/RegExp "^hi$")
                             :example*         (new js/RegExp "^hi$")
                             :args             ['pattern]
                             :category         "Text processing"
                             :instance-methods ["compile" "exec" "test" "toString"]
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['pattern]
                                                :fn-name   "RegExp"
                                                :all-tags  #{:function :js :built-in}
                                                :classname "Function"}
                             :tag-map          {:tag                 :object
                                                :type                js/RegExp
                                                :all-tags            #{:object :js :instance-of-built-in :regex}
                                                :classname           "RegExp"
                                                :instance-properties ["flags" "global" "ignoreCase" "lastIndex" "multiline" "source" "sticky" "unicode"]}}


 ;; FUNCTION PROPERTIES -----------------------------------------------------
 
 js/eval                    {:sym          'eval
                             :args         ['code]
                             :category     "Function properties"
                             :example      '(js/eval "1 + 1")
                             :example*     (js/eval "1 + 1")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['code]
                                            :fn-name   "eval"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/isFinite                {:sym          'isFinite
                             :args         ['n]
                             :category     "Function properties"
                             :example      '(js/isFinite 42)
                             :example*     (js/isFinite 42)
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['n]
                                            :fn-name   "isFinite"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/isNaN                   {:sym          'isNaN
                             :args         ['n]
                             :category     "Function properties"
                             :example      '(js/isNaN js/NaN)
                             :example*     (js/isNaN js/NaN)
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['n]
                                            :fn-name   "isNaN"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/parseFloat              {:sym          'parseFloat
                             :args         ['s]
                             :category     "Function properties"
                             :example      '(js/parseFloat "3.14")
                             :example*     (js/parseFloat "3.14")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "parseFloat"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/parseInt                {:sym          'parseInt
                             :args         ['s]
                             :category     "Function properties"
                             :example      '(js/parseInt "42" 10)
                             :example*     (js/parseInt "42" 10)
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "parseInt"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/decodeURI               {:sym          'decodeURI
                             :args         ['uri]
                             :category     "Function properties"
                             :example      '(js/decodeURI "https%3A%2F%2Fexample.com")
                             :example*     (js/decodeURI "https%3A%2F%2Fexample.com")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "decodeURI"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/decodeURIComponent      {:sym          'decodeURIComponent
                             :args         ['uri]
                             :category     "Function properties"
                             :example      '(js/decodeURIComponent "hello%20world")
                             :example*     (js/decodeURIComponent "hello%20world")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "decodeURIComponent"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/encodeURI               {:sym          'encodeURI
                             :args         ['uri]
                             :category     "Function properties"
                             :example      '(js/encodeURI "https://example.com/path with spaces")
                             :example*     (js/encodeURI "https://example.com/path with spaces")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "encodeURI"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/encodeURIComponent      {:sym          'encodeURIComponent
                             :args         ['uri]
                             :category     "Function properties"
                             :example      '(js/encodeURIComponent "hello world")
                             :example*     (js/encodeURIComponent "hello world")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "encodeURIComponent"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/escape                  {:sym          'escape
                             :args         ['s]
                             :category     "Function properties"
                             :example      '(js/escape "hello world")
                             :example*     (js/escape "hello world")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "escape"
                                            :all-tags  #{:function :js :built-in :deprecated}
                                            :classname "Function"}}

 js/unescape                {:sym          'unescape
                             :args         ['s]
                             :category     "Function properties"
                             :example      '(js/unescape "hello%20world")
                             :example*     (js/unescape "hello%20world")
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "unescape"
                                            :all-tags  #{:function :js :built-in :deprecated}
                                            :classname "Function"}}

 ;; KEYED COLLECTIONS -------------------------------------------------------
 
 js/Map                     {:sym              'Map
                             :args             ['iterable]
                             :category         "Keyed collections"
                             :example          '(new js/Map #js[#js["key1" "value1"] #js["key2" "value2"]])
                             :example*         (new js/Map #js[#js["key1" "value1"] #js["key2" "value2"]])
                             :instance-methods ["clear" "delete" "entries" "forEach" "get" "has" "keys" "set" "values"]
                             :tag-map          {:tag                 :object
                                                :type                js/Map
                                                :all-tags            #{:object :js :instance-of-built-in :map-like :coll-like}
                                                :classname           "Map"
                                                :instance-properties ["size"]}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['iterable]
                                                :fn-name   "Map"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Set                     {:sym              'Set
                             :args             ['iterable]
                             :category         "Keyed collections"
                             :example          '(new js/Set #js[1 2 3 2 1])
                             :example*         (new js/Set #js[1 2 3 2 1])
                             :instance-methods ["add" "clear" "delete" "difference" "entries" "forEach" "has" "intersection" "isDisjointFrom" "isSubsetOf" "isSupersetOf" "keys" "symmetricDifference" "union" "values"]
                             :tag-map          {:tag                 :object
                                                :type                js/Set
                                                :all-tags            #{:object :js :instance-of-built-in :set-like :coll-like}
                                                :classname           "Set"
                                                :instance-properties ["size"]}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['iterable]
                                                :fn-name   "Set"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/WeakMap                 {:sym              'WeakMap
                             :args             ['iterable]
                             :category         "Keyed collections"
                             :example          '(let [obj #js{}] (new js/WeakMap #js[#js[obj "value"]]))
                             :example*         (let [obj #js{}] (new js/WeakMap #js[#js[obj "value"]]))
                             :instance-methods ["delete" "get" "has" "set"]
                             :tag-map          {:tag       :object
                                                :type      js/WeakMap
                                                :all-tags  #{:object :js :instance-of-built-in :map-like :weak}
                                                :classname "WeakMap"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['iterable]
                                                :fn-name   "WeakMap"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/WeakSet                 {:sym              'WeakSet
                             :args             ['iterable]
                             :category         "Keyed collections"
                             :example          '(let [obj #js{}] (new js/WeakSet #js[obj]))
                             :example*         (let [obj #js{}] (new js/WeakSet #js[obj]))
                             :instance-methods ["add" "delete" "has"]
                             :tag-map          {:tag       :object
                                                :type      js/WeakSet
                                                :all-tags  #{:object :js :instance-of-built-in :set-like :weak}
                                                :classname "WeakSet"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['iterable]
                                                :fn-name   "WeakSet"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}


 ;; INDEXED COLLECTIONS -----------------------------------------------------
 js/Array                   {:sym              'Array
                             :example          '(new js/Array 1 2 3)
                             :example*         (new js/Array 1 2 3)
                             :args             ['elements]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Array
                                                :all-tags            #{:js
                                                                       :array
                                                                       :coll-like
                                                                       :instance-of-built-in
                                                                       :object}
                                                :classname           "Array"
                                                :instance-properties ["length"]}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['elements]
                                                :fn-name   "Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Int8Array               {:sym              'Int8Array
                             :example          '(new js/Int8Array #js[1 2 3])
                             :example*         (new js/Int8Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Int8Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Int8Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Int8Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Uint8Array              {:sym              'Uint8Array
                             :example          '(new js/Uint8Array #js[1 2 3])
                             :example*         (new js/Uint8Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Uint8Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Uint8Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Uint8Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Uint8ClampedArray       {:sym              'Uint8ClampedArray
                             :example          '(new js/Uint8ClampedArray #js[1 2 3])
                             :example*         (new js/Uint8ClampedArray #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Uint8ClampedArray
                                                :all-tags            js-array-all-tags
                                                :classname           "Uint8ClampedArray"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Uint8ClampedArray"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Int16Array              {:sym              'Int16Array
                             :example          '(new js/Int16Array #js[1 2 3])
                             :example*         (new js/Int16Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Int16Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Int16Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Int16Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Uint16Array             {:sym              'Uint16Array
                             :example          '(new js/Uint16Array #js[1 2 3])
                             :example*         (new js/Uint16Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Uint16Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Uint16Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Uint16Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Int32Array              {:sym              'Int32Array
                             :example          '(new js/Int32Array #js[1 2 3])
                             :example*         (new js/Int32Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Int32Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Int32Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Int32Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Uint32Array             {:sym              'Uint32Array
                             :example          '(new js/Uint32Array #js[1 2 3])
                             :example*         (new js/Uint32Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Uint32Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Uint32Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Uint32Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/BigInt64Array           {:sym              'BigInt64Array
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :example          '(new js/BigInt64Array 8)
                             :example*         (new js/BigInt64Array 8)
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/BigInt64Array
                                                :all-tags            js-array-all-tags
                                                :classname           "BigInt64Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "BigInt64Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/BigUint64Array          {:sym              'BigUint64Array
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :example          '(new js/BigUint64Array 8)
                             :example*         (new js/BigUint64Array 8)
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/BigUint64Array
                                                :all-tags            js-array-all-tags
                                                :classname           "BigUint64Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "BigUint64Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Float32Array            {:sym              'Float32Array
                             :example          '(new js/Float32Array #js[1 2 3])
                             :example*         (new js/Float32Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Float32Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Float32Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Float32Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Float64Array            {:sym              'Float64Array
                             :example          '(new js/Float64Array #js[1 2 3])
                             :example*         (new js/Float64Array #js[1 2 3])
                             :args             ['length-or-source]
                             :category         "Indexed collections"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag                 :object
                                                :type                js/Float64Array
                                                :all-tags            js-array-all-tags
                                                :classname           "Float64Array"
                                                :instance-properties js-array-instance-properties}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['length-or-source]
                                                :fn-name   "Float64Array"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 ;; STRUCTURED DATA ---------------------------------------------------------
 
 js/ArrayBuffer             {:sym                 'ArrayBuffer
                             :args                ['length]
                             :instance-properties ["byteLength"
                                                   "detached"
                                                   "maxByteLength"
                                                   "resizable"]
                             :category            "Structured data"
                             :example             '(new js/ArrayBuffer 16)
                             :example*            (new js/ArrayBuffer 16)
                             :instance-methods    js-array-instance-methods
                             :tag-map             {:tag                 :object
                                                   :type                js/ArrayBuffer
                                                   :all-tags            #{:js
                                                                          :coll-like
                                                                          :instance-of-built-in
                                                                          :map-like
                                                                          :object}
                                                   :classname           "ArrayBuffer"
                                                   :instance-properties ["byteLength"
                                                                         "maxByteLength"
                                                                         "resizable"
                                                                         "detached"]}
                             :self-tag-map        {:tag       :function
                                                   :type      js/Function
                                                   :fn-args   ['length]
                                                   :fn-name   "ArrayBuffer"
                                                   :all-tags  #{:js :function :built-in}
                                                   :classname "Function"}}
 js/JSON                    {:sym                'JSON
                             :not-a-constructor? true
                             :category           "Structured data"
                             :example            '(js/JSON.stringify #js{:a 1})
                             :example*           (js/JSON.stringify #js{:a 1})
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:js :built-in :object}
                                                  :classname "JSON"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   []
                                                  :fn-name   "JSON"
                                                  :all-tags  #{:js :function :built-in}
                                                  :classname "Function"}
                             :instance-methods   js-array-instance-methods}
 js/DataView                {:sym                 'DataView
                             :args                ['buffer 'byteOffset 'byteLength]
                             :instance-properties ["buffer" "byteLength" "byteOffset"]
                             :category            "Structured data"
                             :example             '(new js/DataView (js/ArrayBuffer. 16))
                             :example*            (new js/DataView (js/ArrayBuffer. 16))
                             :instance-methods    js-array-instance-methods
                             :tag-map             {:tag                 :object
                                                   :type                js/DataView
                                                   :all-tags            #{:js
                                                                          :instance-of-built-in
                                                                          :object}
                                                   :classname           "DataView"
                                                   :instance-properties ["buffer" "byteLength" "byteOffset"]}
                             :self-tag-map        {:tag       :function
                                                   :type      js/Function
                                                   :fn-args   ['buffer 'byteOffset 'byteLength]
                                                   :fn-name   "DataView"
                                                   :all-tags  #{:js :function :built-in}
                                                   :classname "Function"}}
 js/Intl                    {:sym                'Intl
                             :not-a-constructor? true
                             :category           "Structured data"
                             :example            'js/Intl
                             :example*           js/Intl
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:js :built-in :object}
                                                  :classname "Intl"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   []
                                                  :fn-name   "Intl"
                                                  :all-tags  #{:js :function :built-in}
                                                  :classname "Function"}
                             :instance-methods   js-array-instance-methods}
 js/Intl.Collator           {:sym              'Intl.Collator
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :example          '(new 'js/Intl.Collator "en-US")
                             :example*         (new js/Intl.Collator "en-US")
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "Collator"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.Collator"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.DateTimeFormat     {:sym              'Intl.DateTimeFormat
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :example          '(new 'js/Intl.DateTimeFormat "en-US" #js{:dateStyle "full"})
                             :example*         (new js/Intl.DateTimeFormat "en-US" #js{:dateStyle "full"})
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "DateTimeFormat"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.DateTimeFormat"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.DisplayNames       {:sym              'Intl.DisplayNames
                             :example          '(new 'js/Intl.DisplayNames #js["en"] #js {:type "region"})
                             :example*         (new js/Intl.DisplayNames #js["en"] #js {:type "region"})
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "DisplayNames"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.DisplayNames"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.ListFormat         {:sym              'Intl.ListFormat
                             :example          '(new 'js/Intl.ListFormat "en-GB" #js {:style "long", :type "conjunction"})
                             :example*         (new js/Intl.ListFormat "en-GB" #js {:style "long", :type "conjunction"})
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "ListFormat"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.ListFormat"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.Locale             {:sym                 'Intl.Locale
                             :args                ['tag 'options]
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
                                                   "weekInfo"]
                             :category            "Structured data"
                             :example             '(new 'js/Intl.Locale "en-US")
                             :example*            (new js/Intl.Locale "en-US")
                             :instance-methods    js-array-instance-methods
                             :tag-map             {:tag                 :object
                                                   :type                js/Object
                                                   :all-tags            #{:js
                                                                          :intl
                                                                          :instance-of-built-in
                                                                          :object}
                                                   :classname           "Locale"
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
                                                                         "weekInfo"]}
                             :self-tag-map        {:tag       :function
                                                   :type      js/Function
                                                   :fn-args   ['tag 'options]
                                                   :fn-name   "Intl.Locale"
                                                   :all-tags  #{:js :function :built-in}
                                                   :classname "Function"}}
 js/Intl.NumberFormat       {:sym              'Intl.NumberFormat
                             :example          '(new 'js/Intl.NumberFormat "de-DE" #js{:style "currency", :currency "EUR" })
                             :example*         (new js/Intl.NumberFormat "de-DE" #js{:style "currency", :currency "EUR" })
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "NumberFormat"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.NumberFormat"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.PluralRules        {:sym              'Intl.PluralRules
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :example          '(new 'js/Intl.PluralRules "en" #js{:type "ordinal"})
                             :example*         (new js/Intl.PluralRules "en" #js{:type "ordinal"})
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "PluralRules"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.PluralRules"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.RelativeTimeFormat {:sym              'Intl.RelativeTimeFormat
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :example          '(new 'js/Intl.RelativeTimeFormat "en" #js{:numeric "auto"})
                             :example*         (new js/Intl.RelativeTimeFormat "en" #js{:numeric "auto"})
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "RelativeTimeFormat"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.RelativeTimeFormat"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Intl.Segmenter          {:sym              'Intl.Segmenter
                             :args             ['locales 'options]
                             :category         "Structured data"
                             :example          '(new 'js/Intl.Segmenter "en" #js{:granularity "word"})
                             :example*         (new js/Intl.Segmenter "en" #js{:granularity "word"})
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :intl
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "Segmenter"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['locales 'options]
                                                :fn-name   "Intl.Segmenter"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 ;; MANAGING MEMORY ---------------------------------------------------------
 
 js/WeakRef                 {:sym              'WeakRef
                             :args             ['target]
                             :category         "Managing memory"
                             :example          '(let [obj #js{}] (new js/WeakRef obj))
                             :example*         (let [obj #js{}] (new js/WeakRef obj))
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/WeakRef
                                                :all-tags  #{:js
                                                             :weak
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "WeakRef"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['target]
                                                :fn-name   "WeakRef"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/FinalizationRegistry    {:sym              'FinalizationRegistry
                             :args             ['cleanupCallback]
                             :category         "Managing memory"
                             :example          '(new 'js/FinalizationRegistry (fn [val] ('js/console.log "cleaned" val)))
                             :example*         (new js/FinalizationRegistry (fn [val] ('js/console.log "cleaned" val)))
                             :instance-methods js-array-instance-methods
                             :tag-map          {:tag       :object
                                                :type      js/FinalizationRegistry
                                                :all-tags  #{:js
                                                             :weak
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "FinalizationRegistry"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['cleanupCallback]
                                                :fn-name   "FinalizationRegistry"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}
 js/Reflect                 {:sym                'Reflect
                             :not-a-constructor? true
                             :example            '('js/Reflect.has #js{:a 1} "a")
                             :example*           ('js/Reflect.has #js{:a 1} "a")
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:js :built-in :object}
                                                  :classname "Reflect"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   []
                                                  :fn-name   "Reflect"
                                                  :all-tags  #{:js :function :built-in}
                                                  :classname "Function"}
                             :instance-methods   js-array-instance-methods}
 js/Proxy                   {:sym              'Proxy
                             :args             ['target 'handler]
                             :category         "Reflection"
                             :example          '(new js/Proxy #js{} #js{:get (fn [target prop] (aget target prop))})
                             :example*         (new js/Proxy #js{} #js{:get (fn [target prop] (aget target prop))})
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:js
                                                             :proxy
                                                             :instance-of-built-in
                                                             :object}
                                                :classname "Object"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['target 'handler]
                                                :fn-name   "Proxy"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}
                             :instance-methods js-array-instance-methods}}
    
    ;; js/Atomic is in the STRUCTURED DATA category, but we are associng it in
    ;; conditionally to avoid errors from browsers that do not support it.
    Atomics (assoc Atomics
                   {:sym                'Atomics
                    :not-a-constructor? true
                    :category           "Structured data"
                    :tag-map            {:tag       :object
                                         :type      js/Object
                                         :all-tags  #{:object
                                                      :js
                                                      :built-in
                                                      :non-constructor}
                                         :fn-name   "Atomics"
                                         :fn-args   nil
                                         :classname "Object"}}
                   )))





(defonce js-built-ins-by-category
  (array-map 
   "Fundamental objects"
   [[js/Object {:sym 'Object :args '[...] :category "Fundamental objects"}]
    [js/Function {:sym 'Function :args '[...] :category "Fundamental objects"}]
    [js/Boolean {:sym 'Boolean :args '[...] :category "Fundamental objects"}]
    [js/Symbol {:sym 'Symbol
                :demo "(js/Symbol \"my-sym\")"
                :not-a-constructor? true
                :args '[s] :category "Fundamental objects"}]]

   "Numbers and dates"
   [[js/Math {:sym 'Math :not-a-constructor? true :category "Numbers and dates"}]
    [js/Number {:sym 'Number :demo "(new js/Number \"3\")" :args '[v] :category "Numbers and dates"}]
    [js/BigInt {:sym 'BigInt :demo "(js/BigInt \"999999999999\")" :not-a-constructor? true :args '[v] :category "Numbers and dates"}]
    [js/Date {:sym 'Date :demo "(new js/Date)" :args '[...] :category "Numbers and dates"}]]

   "Value properties" 
   [[js/NaN {:sym 'NaN :category "Value properties"}] 
    [js/Infinity {:sym 'Infinity :category "Value properties"}] 
    [js/globalThis {:sym 'globalThis :category "Value properties"}]]

   "Control abstraction objects"
   [[js/Promise {:sym 'Promise #_#_:demo "(new js/Promise (fn [x] x))" :args '[f] :category "Control abstraction objects"}]]

   "Error objects"
   [[js/AggregateError {:sym 'AggregateError :demo "(new js/AggregateError #js[(new js/Error \"some error\")] \"Hello\")" :args '[array] :category "Error objects"}]
    [js/EvalError {:sym 'EvalError :demo "(new js/EvalError)" :args '[] :category "Error objects"}]
    [js/RangeError {:sym 'RangeError #_#_:demo "(new js/RangeError)" :args '[] :category "Error objects"}]
    [js/ReferenceError {:sym 'ReferenceError :demo "(new js/ReferenceError)" :args '[] :category "Error objects"}]
    [js/SyntaxError {:sym 'SyntaxError :demo "(new js/SyntaxError)" :args '[] :category "Error objects"}]
    [js/TypeError {:sym 'TypeError :demo "(new js/TypeError)" :args '[] :category "Error objects"}]
    [js/URIError {:sym 'URIError :demo "(new js/URIError)" :args '[] :category "Error objects"}]
    [js/Error {:sym 'Error :demo "(new js/Error)" :args '[] :category "Error objects"}]]

   "Text processing"
   [[js/String {:sym 'String :demo "(new js/String \"hi\")" :args '[s] :category "Text processing"}]
    [js/RegExp {:sym 'RegExp :demo "(new js/RegExp \"^hi$\")" :args '[s] :category "Text processing"}]]

   "Function properties"
   [[js/eval {:sym 'eval :args '[script] :category "Function properties"}] 
    [js/isFinite {:sym 'isFinite :args '[v] :category "Function properties"}] 
    [js/isNaN {:sym 'isNaN :args '[v] :category "Function properties"}] 
    [js/parseFloat {:sym 'parseFloat :args '[s]  :category "Function properties"}] 
    [js/parseInt {:sym 'parseInt :args '[...]  :category "Function properties"}] 
    [js/decodeURI {:sym 'decodeURI :args '[uri] :category "Function properties"}] 
    [js/decodeURIComponent {:sym 'decodeURIComponent :args '[encodedUri] :category "Function properties"}] 
    [js/encodeURI {:sym 'encodeURI :args '[uri] :category "Function properties"}] 
    [js/encodeURIComponent {:sym 'encodeURIComponent :args '[uriComponent] :category "Function properties"}] 
    [js/escape {:sym 'escape :args '[str] :category "Function properties"}] 
    [js/unescape {:sym 'unescape :args '[str] :category "Function properties"}]]

   "Keyed collections"
   [[js/Map {:sym 'Map :demo "(new js/Map #js[#js[\"a\", 1], #js[\"b\", 2]])" :args '[...] :category "Keyed collections"}]
    [js/Set {:sym 'Set :demo "(new js/Set #js[1 2])" :args '[...] :category "Keyed collections"}]
    [js/WeakMap {:sym  'WeakMap #_#_:demo "(let [wm (js/WeakMap.) o  #js{:a 1}] (.set wm o 100))" :args '[...] :category "Keyed collections"}]
    ;; TODO - fix this WeakSet demo
    [js/WeakSet {:sym 'WeakSet :demo "(new js/Set #js[1 2])" :args '[...] :category "Keyed collections"}]
    ]

   "Indexed collections"
   [[js/Array {:sym 'Array :demo "(new js/Array 1 2 3)" :args '[...] :category "Indexed collections"}]
    [js/Int8Array {:sym 'Int8Array :demo "(new js/Int8Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Uint8Array {:sym 'Uint8Array :demo "(new js/Uint8Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Uint8ClampedArray {:sym  'Uint8ClampedArray :demo "(new js/Uint8ClampedArray #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Int16Array {:sym 'Int16Array :demo "(new js/Int16Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Uint16Array {:sym 'Uint16Array :demo "(new js/Uint16Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Int32Array {:sym 'Int32Array :demo "(new js/Int32Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Uint32Array {:sym 'Uint32Array :demo "(new js/Uint32Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/BigInt64Array {:sym 'BigInt64Array #_#_:demo "(new js/BigInt64Array 3)" :args '[...] :category "Indexed collections"}]
    [js/BigUint64Array {:sym 'BigUint64Array #_#_:demo "(new js/BigUint64Array 3)" :args '[...] :category "Indexed collections"}]
    [js/Float32Array {:sym 'Float32Array :demo "(new js/Float32Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]
    [js/Float64Array {:sym 'Float64Array :demo "(new js/Float64Array #js[1 2 3])" :args '[...] :category "Indexed collections"}]]

   "Structured data"
   (into []
         (concat 
          [[js/ArrayBuffer {:sym                 'ArrayBuffer 
                            #_#_:demo                (new js/ArrayBuffer 8) 
                            :args                '[...]
                            :instance-properties ["byteLength"
                                                  "detached"
                                                  "maxByteLength"
                                                  "resizable"]
                            :category "Structured data"}]
           [js/JSON {:sym                'JSON
                     :not-a-constructor? true
                     :category "Structured data"}]
           [js/DataView {:sym                 'DataView
                         #_#_:demo                "(new js/DataView (new js/ArrayBuffer 8))"
                         :args                '[ArrayBuffer]
                         :instance-properties ["buffer"
                                               "byteLength"
                                               "byteOffset"]
                         :category "Structured data"}]]
          (when Atomics [[Atomics {:sym                'Atomics
                                   :not-a-constructor? true
                                   :category "Structured data"}]])))

   "Internationalization"
   [[js/Intl {:sym 'Intl :not-a-constructor? true :category "Structured data"}]
    [js/Intl.Collator {:sym 'Intl.Collator
                       :demo "(new js/Intl.Collator \"sv\")"
                       :args '[...]
                       :category "Structured data"}]
    [js/Intl.DateTimeFormat {:sym 'Intl.DateTimeFormat
                             :demo "(new js/Intl.DateTimeFormat \"en-US\")"
                             :args '[...]
                             :category "Structured data"}]
    [js/Intl.DisplayNames {:sym 'Intl.DisplayNames
                           :demo "(new js/Intl.DisplayNames #js[\"en\"] #js {:type \"region\"})"
                           :args '[...]
                           :category "Structured data"}]
    [js/Intl.ListFormat {:sym 'Intl.ListFormat
                         :demo "(new js/Intl.ListFormat \"en-GB\" #js {:style \"long\", :type \"conjunction\"})"
                         :args '[...]
                         :category "Structured data"}]
    [js/Intl.Locale {:sym 'Intl.Locale
                     #_#_:demo "(new js/Intl.Locale
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
                                           "weekInfo"]
                     :category "Structured data"}]
    [js/Intl.NumberFormat {:sym 'Intl.NumberFormat 
                           :demo "(new js/Intl.NumberFormat \"de-DE\" #js{:style \"currency\", :currency \"EUR\" })"
                           :args '[...]
                           :category "Structured data"}]
    [js/Intl.PluralRules {:sym 'Intl.PluralRules
                          :demo "(new js/Intl.PluralRules \"en-US\")" 
                          :args '[...]
                          :category "Structured data"}]
    [js/Intl.RelativeTimeFormat {:sym 'Intl.RelativeTimeFormat
                                 :demo "(new js/Intl.RelativeTimeFormat \"en\" #js{:style \"short\"})" 
                                 :args '[...]
                                 :category "Structured data"}]
    [js/Intl.Segmenter {:sym 'Intl.Segmenter
                        :demo "(new js/Intl.Segmenter \"fr\" #js{:granularity \"word\"})"
                        :args '[...]
                        :category "Structured data"}]

    ;; experimental, Safari-only
    ;; [js/Intl.DurationFormat {:sym 'DurationFormat :args '[...]}]
    ]

   "Managing memory"
   [[js/WeakRef {:sym 'WeakRef
                 #_#_:demo "(new js/WeakRef (fn [x]))" :args '[f] :category "Managing memory"}]
    [js/FinalizationRegistry {:sym 'FinalizationRegistry
                              #_#_:demo "(new js/FinalizationRegistry (fn [x]))"
                              :args '[f]
                              :category "Managing memory"}]]    

   "Reflection"
   [[js/Reflect {:sym 'Reflect :not-a-constructor? true}]
    [js/Proxy {:sym 'Proxy
               #_#_:demo "(new js/Proxy #js {:a 1} (fn [x]))"
               :args '[...]
               :category "Reflection"}]]
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

(def js-built-ins-cached-tag-maps
  (apply array-map
         (reduce 
          (fn [vc [built-in v]]
            (conj vc 
                  built-in 
                  (assoc v
                         :tag-map 
                         (let [tag        (if (object? built-in) :object :function)
                               extra-tags (when (:not-a-constructor? v)
                                            [:non-constructor])]
                           {:tag       tag
                            :type      (type built-in)
                            :all-tags  (into #{tag :js :built-in} extra-tags)
                            :fn-name   (str (:sym v))
                            :fn-args   (if (= (:args v) '[...]) [] (:args v))
                            :classname (if (= tag :object) "Object" "Function")}))))
          [] 
          js-built-ins-by-built-in*)))


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
  