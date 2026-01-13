;; Give all Intls a resolver fn?
;; The tag-maps are cached results for instances of these built-ins
;; The built-ins
;; Add "resolvedOptions" at key `:resolver-method`
;; 


(def js-array-all-tags #{:object :js :instance-of-built-in :typed-array :array :coll-like})
(def js-array-instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"])
(def js-array-instance-properties ["buffer" "byteLength" "byteOffset" "length"])

{
 ;; FUNDAMENTAL OBJECTS -----------------------------------------------------
 
 js/Object                  {:sym              'Object
                             :args             ['obj]
                             :category         "Fundamental objects"
                             :example          "(new js/Object)"
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
                             :args             ['& 'args]
                             :category         "Fundamental objects"
                             :example          "(js/Function. \"a\" \"b\" \"return a + b\")"
                             :example*         (new js/Function "a" "b" "return a + b")
                             :instance-methods ["apply" "bind" "call" "toString"]
                             :tag-map          {:tag       :object
                                                :type      js/Function
                                                :all-tags  #{:object :js :instance-of-built-in :function}
                                                :classname "Function"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['& args]
                                                :fn-name   "Function"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Boolean                 {:sym              'Boolean
                             :args             ['val]
                             :category         "Fundamental objects"
                             :example          "(new js/Boolean true)"
                             :example*         (new js/Boolean true)
                             :instance-methods ["toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Boolean
                                                :all-tags  #{:object :js :instance-of-built-in :primitive}
                                                :classname "Boolean"}
                             :self-tag-map     {:tag       :function
                                                :type      js/Function
                                                :fn-args   ['val]
                                                :fn-name   "Boolean"
                                                :all-tags  #{:js :function :built-in}
                                                :classname "Function"}}

 js/Symbol                  {:sym                'Symbol
                             :example            "(js/Symbol \"my-sym\")"
                             :example*           (js/Symbol "my-sym")
                             :not-a-constructor? true
                             :args               ['s]
                             :category           "Fundamental objects"
                             :instance-methods   ["toString" "valueOf"]
                             :tag-map            {:tag       :object
                                                  :type      js/Symbol
                                                  :all-tags  #{:object :js :instance-of-built-in}
                                                  :classname "Symbol"}
                             :self-tag-map       {:tag       :function
                                                  :type      js/Function
                                                  :fn-args   ['s]
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
                             :example          "(new js/Number \"3\")"
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
                             :example            "(js/BigInt 9007199254740991)"
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
                             :example          "(new js/Date 2024 0 1)"
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
                             :example      "js/NaN"
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
                             :example      "js/Infinity"
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
                             :example      "js/globalThis"
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
                             :example          "(new js/Promise (fn [resolve reject] (resolve 42)))"
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
                             :example      "(new js/AggregateError #js[(js/Error. \"err1\")] \"Multiple errors\")"
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
                             :example      "(new js/EvalError \"eval error occurred\")"
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
                             :example      "(new js/RangeError \"value out of range\")"
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
                             :example      "(new js/ReferenceError \"variable not found\")"
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
                             :example      "(new js/SyntaxError \"syntax error in code\")"
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
                             :example      "(new js/TypeError \"wrong type provided\")"
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
                             :example      "(new js/URIError \"invalid URI\")"
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
                             :example      "(new js/Error \"something went wrong\")"
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
                             :example          "(new js/String \"hi\")"
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
                             :example          "(new js/RegExp \"^hi$\")"
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
                             :example      "(js/eval \"1 + 1\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['code]
                                            :fn-name   "eval"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/isFinite                {:sym          'isFinite
                             :args         ['n]
                             :category     "Function properties"
                             :example      "(js/isFinite 42)"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['n]
                                            :fn-name   "isFinite"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/isNaN                   {:sym          'isNaN
                             :args         ['n]
                             :category     "Function properties"
                             :example      "(js/isNaN js/NaN)"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['n]
                                            :fn-name   "isNaN"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/parseFloat              {:sym          'parseFloat
                             :args         ['s]
                             :category     "Function properties"
                             :example      "(js/parseFloat \"3.14\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "parseFloat"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/parseInt                {:sym          'parseInt
                             :args         ['s]
                             :category     "Function properties"
                             :example      "(js/parseInt \"42\" 10)"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "parseInt"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/decodeURI               {:sym          'decodeURI
                             :args         ['uri]
                             :category     "Function properties"
                             :example      "(js/decodeURI \"https%3A%2F%2Fexample.com\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "decodeURI"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/decodeURIComponent      {:sym          'decodeURIComponent
                             :args         ['uri]
                             :category     "Function properties"
                             :example      "(js/decodeURIComponent \"hello%20world\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "decodeURIComponent"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/encodeURI               {:sym          'encodeURI
                             :args         ['uri]
                             :category     "Function properties"
                             :example      "(js/encodeURI \"https://example.com/path with spaces\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "encodeURI"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/encodeURIComponent      {:sym          'encodeURIComponent
                             :args         ['uri]
                             :category     "Function properties"
                             :example      "(js/encodeURIComponent \"hello world\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['uri]
                                            :fn-name   "encodeURIComponent"
                                            :all-tags  #{:function :js :built-in}
                                            :classname "Function"}}

 js/escape                  {:sym          'escape
                             :args         ['s]
                             :category     "Function properties"
                             :example      "(js/escape \"hello world\")"
                             :self-tag-map {:tag       :function
                                            :type      js/Function
                                            :fn-args   ['s]
                                            :fn-name   "escape"
                                            :all-tags  #{:function :js :built-in :deprecated}
                                            :classname "Function"}}

 js/unescape                {:sym          'unescape
                             :args         ['s]
                             :category     "Function properties"
                             :example      "(js/unescape \"hello%20world\")"
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
                             :example          "(new js/Map #js[#js[\"key1\" \"value1\"] #js[\"key2\" \"value2\"]])"
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
                             :example          "(new js/Set #js[1 2 3 2 1])"
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
                             :example          "(let [obj #js{}] (new js/WeakMap #js[#js[obj \"value\"]]))"
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
                             :example          "(let [obj #js{}] (new js/WeakSet #js[obj]))"
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
                             :example          "(new js/Array 1 2 3)"
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
                             :example          "(new js/Int8Array #js[1 2 3])"
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
                             :example          "(new js/Uint8Array #js[1 2 3])"
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
                             :example          "(new js/Uint8ClampedArray #js[1 2 3])"
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
                             :example          "(new js/Int16Array #js[1 2 3])"
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
                             :example          "(new js/Uint16Array #js[1 2 3])"
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
                             :example          "(new js/Int32Array #js[1 2 3])"
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
                             :example          "(new js/Uint32Array #js[1 2 3])"
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
                             :example          "(new js/BigInt64Array 8)"
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
                             :example          "(new js/BigUint64Array 8)"
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
                             :example          "(new js/Float32Array #js[1 2 3])"
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
                             :example          "(new js/Float64Array #js[1 2 3])"
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
                             :example             "(new js/ArrayBuffer 16)"
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
                             :example            "(js/JSON.stringify #js{:a 1})"
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
                             :example             "(new js/DataView (js/ArrayBuffer. 16))"
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
                             :example            "js/Intl"
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
                             :example          "(new 'js/Intl.Collator \"en-US\")"
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
                             :example          "(new 'js/Intl.DateTimeFormat \"en-US\" #js{:dateStyle \"full\"})"
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
                             :example          "(new 'js/Intl.DisplayNames #js[\"en\"] #js {:type \"region\"})"
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
                             :example          "(new 'js/Intl.ListFormat \"en-GB\" #js {:style \"long\", :type \"conjunction\"})"
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
                             :example             "(new 'js/Intl.Locale \"en-US\")"
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
                             :example          "(new 'js/Intl.NumberFormat \"de-DE\" #js{:style \"currency\", :currency \"EUR\" })"
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
                             :example          "(new 'js/Intl.PluralRules \"en\" #js{:type \"ordinal\"})"
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
                             :example          "(new 'js/Intl.RelativeTimeFormat \"en\" #js{:numeric \"auto\"})"
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
                             :example          "(new 'js/Intl.Segmenter \"en\" #js{:granularity \"word\"})"
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
                             :example          "(let [obj #js{}] (new js/WeakRef obj))"
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
                             :example          "(new 'js/FinalizationRegistry (fn [val] ('js/console.log \"cleaned\" val)))"
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
                             :example            "('js/Reflect.has #js{:a 1} \"a\")"
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
                             :example          "(new js/Proxy #js{} #js{:get (fn [target prop] (aget target prop))})"
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
