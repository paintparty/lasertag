;; Give all Intls a resolver fn?
;; The tag-maps are cached results for instances of these built-ins
;; The built-ins
;; Add "resolvedOptions" at key `:resolver-method`
;; 


{;; FUNDAMENTAL OBJECTS -----------------------------------------------------

 js/Object                  {:sym              'Object
                             :args             ['...]
                             :category         "Fundamental objects"
                             :example          "(new js/Object)"
                             :instance-methods ["hasOwnProperty" "isPrototypeOf" "propertyIsEnumerable" "toLocaleString" "toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Object"}}

 js/Function                {:sym              'Function
                             :args             ['...]
                             :category         "Fundamental objects"
                             :example          "(js/Function. \"a\" \"b\" \"return a + b\")"
                             :instance-methods ["apply" "bind" "call" "toString"]
                             :tag-map          {:tag       :object
                                                :type      js/Function
                                                :all-tags  #{:object :js :instance-of-built-in :function}
                                                :classname "Function"}}

 js/Boolean                 {:sym              'Boolean
                             :args             ['...]
                             :category         "Fundamental objects"
                             :example          "(new js/Boolean true)"
                             :instance-methods ["toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Boolean
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Boolean"}}

 js/Symbol                  {:sym                'Symbol
                             :example            "(js/Symbol \"my-sym\")"
                             :not-a-constructor? true
                             :args               ['s]
                             :category           "Fundamental objects"
                             :instance-methods   ["toString" "valueOf"]
                             :tag-map            {:tag       :object
                                                  :type      js/Symbol
                                                  :all-tags  #{:object :js :instance-of-built-in}
                                                  :classname "Symbol"}}


 ;; NUMBERS AND DATES -------------------------------------------------------

 js/Math                    {:sym                'Math
                             :not-a-constructor? true
                             :category           "Numbers and dates"
                             :example            "js/Math.PI"
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :built-in}
                                                  :classname "Math"}}

 js/Number                  {:sym              'Number
                             :example          "(new js/Number \"3\")"
                             :args             ['v]
                             :category         "Numbers and dates"
                             :instance-methods ["toExponential" "toFixed" "toLocaleString" "toPrecision" "toString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Number
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Number"}}

 js/BigInt                  {:sym                'BigInt
                             :not-a-constructor? true
                             :args               ['v]
                             :category           "Numbers and dates"
                             :example            "(js/BigInt 9007199254740991)"
                             :instance-methods   ["toLocaleString" "toString" "valueOf"]
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :instance-of-built-in}
                                                  :classname "BigInt"}}

 js/Date                    {:sym              'Date
                             :args             ['...]
                             :category         "Numbers and dates"
                             :example          "(new js/Date 2024 0 1)"
                             :instance-methods ["getDate" "getDay" "getFullYear" "getHours" "getMilliseconds" "getMinutes" "getMonth" "getSeconds" "getTime" "getTimezoneOffset" "getUTCDate" "getUTCDay" "getUTCFullYear" "getUTCHours" "getUTCMilliseconds" "getUTCMinutes" "getUTCMonth" "getUTCSeconds" "setDate" "setFullYear" "setHours" "setMilliseconds" "setMinutes" "setMonth" "setSeconds" "setTime" "setUTCDate" "setUTCFullYear" "setUTCHours" "setUTCMilliseconds" "setUTCMinutes" "setUTCMonth" "setUTCSeconds" "toDateString" "toISOString" "toJSON" "toLocaleDateString" "toLocaleString" "toLocaleTimeString" "toString" "toTimeString" "toUTCString" "valueOf"]
                             :tag-map          {:tag       :object
                                                :type      js/Date
                                                :all-tags  #{:object :js :instance-of-built-in}
                                                :classname "Date"}}

 ;; VALUE PROPERTIES --------------------------------------------------------

 js/NaN                     {:sym      'NaN
                             :category "Value properties"
                             :example  "js/NaN"
                             :tag-map  {:tag       :object
                                        :type      js/Number
                                        :all-tags  #{:nan :js :number :primitive}
                                        :classname "Number"}}

 js/Infinity                {:sym      'Infinity
                             :category "Value properties"
                             :example  "js/Infinity"
                             :tag-map  {:tag       :object
                                        :type      js/Number
                                        :all-tags  #{:infinity :infinite :js :number :primitive}
                                        :classname "Number"}}

 js/globalThis              {:sym      'globalThis
                             :category "Value properties"
                             :example  "js/globalThis"
                             :tag-map  {:tag       :object
                                        :type      js/Object
                                        :all-tags  #{:object :js :global}
                                        :classname "Object"}}


 ;; CONTROL ABSTRACTION OBJECTS ---------------------------------------------

 js/Promise                 {:sym              'Promise
                             :args             ['f]
                             :category         "Control abstraction objects"
                             :example          "(new js/Promise (fn [resolve reject] (resolve 42)))"
                             :instance-methods ["catch" "finally" "then"]
                             :tag-map          {:tag       :object
                                                :type      js/Promise
                                                :all-tags  #{:object :js :instance-of-built-in :async}
                                                :classname "Promise"}}


 ;; ERROR OBJECTS -----------------------------------------------------------

 js/AggregateError          {:sym      'AggregateError
                             :args     ['array]
                             :category "Error objects"
                             :example  "(new js/AggregateError #js[(js/Error. \"err1\")] \"Multiple errors\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "AggregateError"
                                        :instance-properties ["errors" "message" "name" "stack"]}}

 js/EvalError               {:sym      'EvalError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/EvalError \"eval error occurred\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "EvalError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/RangeError              {:sym      'RangeError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/RangeError \"value out of range\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "RangeError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/ReferenceError          {:sym      'ReferenceError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/ReferenceError \"variable not found\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "ReferenceError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/SyntaxError             {:sym      'SyntaxError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/SyntaxError \"syntax error in code\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "SyntaxError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/TypeError               {:sym      'TypeError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/TypeError \"wrong type provided\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "TypeError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/URIError                {:sym      'URIError
                             :args     []
                             :category "Error objects"
                             :example  "(new js/URIError \"invalid URI\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "URIError"
                                        :instance-properties ["message" "name" "stack"]}}

 js/Error                   {:sym      'Error
                             :args     []
                             :category "Error objects"
                             :example  "(new js/Error \"something went wrong\")"
                             :tag-map  {:tag                 :object
                                        :type                js/Error
                                        :all-tags            #{:object :js :instance-of-built-in :error}
                                        :classname           "Error"
                                        :instance-properties ["message" "name" "stack"]}}


 ;; TEXT PROCESSING ---------------------------------------------------------

 js/String                  {:sym              'String
                             :example          "(new js/String \"hi\")"
                             :args             ['s]
                             :category         "Text processing"
                             :instance-methods ["at" "charAt" "charCodeAt" "codePointAt" "concat" "endsWith" "includes" "indexOf" "isWellFormed" "lastIndexOf" "localeCompare" "match" "matchAll" "normalize" "padEnd" "padStart" "repeat" "replace" "replaceAll" "search" "slice" "split" "startsWith" "substring" "toLocaleLowerCase" "toLocaleUpperCase" "toLowerCase" "toString" "toUpperCase" "toWellFormed" "trim" "trimEnd" "trimStart" "valueOf"]
                             :tag-map          {:tag                 :object
                                                :type                js/String
                                                :all-tags            #{:object :js :instance-of-built-in :string}
                                                :classname           "String"
                                                :instance-properties ["length"]}}

 js/RegExp                  {:sym              'RegExp
                             :example          "(new js/RegExp \"^hi$\")"
                             :args             ['s]
                             :category         "Text processing"
                             :instance-methods ["compile" "exec" "test" "toString"]
                             :tag-map          {:tag                 :object
                                                :type                js/RegExp
                                                :all-tags            #{:object :js :instance-of-built-in :regex}
                                                :classname           "RegExp"
                                                :instance-properties ["flags" "global" "ignoreCase" "lastIndex" "multiline" "source" "sticky" "unicode"]}}


 ;; FUNCTION PROPERTIES -----------------------------------------------------

 js/eval                    {:sym      'eval
                             :args     ['script]
                             :category "Function properties"
                             :example  "(js/eval \"1 + 1\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/isFinite                {:sym      'isFinite
                             :args     ['v]
                             :category "Function properties"
                             :example  "(js/isFinite 42)"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/isNaN                   {:sym      'isNaN
                             :args     ['v]
                             :category "Function properties"
                             :example  "(js/isNaN js/NaN)"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/parseFloat              {:sym      'parseFloat
                             :args     ['s]
                             :category "Function properties"
                             :example  "(js/parseFloat \"3.14\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/parseInt                {:sym      'parseInt
                             :args     ['...]
                             :category "Function properties"
                             :example  "(js/parseInt \"42\" 10)"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/decodeURI               {:sym      'decodeURI
                             :args     ['uri]
                             :category "Function properties"
                             :example  "(js/decodeURI \"https%3A%2F%2Fexample.com\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/decodeURIComponent      {:sym      'decodeURIComponent
                             :args     ['encodedUri]
                             :category "Function properties"
                             :example  "(js/decodeURIComponent \"hello%20world\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/encodeURI               {:sym      'encodeURI
                             :args     ['uri]
                             :category "Function properties"
                             :example  "(js/encodeURI \"https://example.com/path with spaces\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/encodeURIComponent      {:sym      'encodeURIComponent
                             :args     ['uriComponent]
                             :category "Function properties"
                             :example  "(js/encodeURIComponent \"hello world\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in}
                                        :classname "Function"}}

 js/escape                  {:sym      'escape
                             :args     ['str]
                             :category "Function properties"
                             :example  "(js/escape \"hello world\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in :deprecated}
                                        :classname "Function"}}

 js/unescape                {:sym      'unescape
                             :args     ['str]
                             :category "Function properties"
                             :example  "(js/unescape \"hello%20world\")"
                             :tag-map  {:tag       :object
                                        :type      js/Function
                                        :all-tags  #{:function :js :built-in :deprecated}
                                        :classname "Function"}}

 ;; KEYED COLLECTIONS -------------------------------------------------------

 js/Map                     {:sym              'Map
                             :args             ['...]
                             :category         "Keyed collections"
                             :example          "(new js/Map #js[#js[\"key1\" \"value1\"] #js[\"key2\" \"value2\"]])"
                             :instance-methods ["clear" "delete" "entries" "forEach" "get" "has" "keys" "set" "values"]
                             :tag-map          {:tag                 :object
                                                :type                js/Map
                                                :all-tags            #{:object :js :instance-of-built-in :map-like :coll-like}
                                                :classname           "Map"
                                                :instance-properties ["size"]}}

 js/Set                     {:sym              'Set
                             :args             ['...]
                             :category         "Keyed collections"
                             :example          "(new js/Set #js[1 2 3 2 1])"
                             :instance-methods ["add" "clear" "delete" "difference" "entries" "forEach" "has" "intersection" "isDisjointFrom" "isSubsetOf" "isSupersetOf" "keys" "symmetricDifference" "union" "values"]
                             :tag-map          {:tag                 :object
                                                :type                js/Set
                                                :all-tags            #{:object :js :instance-of-built-in :set-like :coll-like}
                                                :classname           "Set"
                                                :instance-properties ["size"]}}

 js/WeakMap                 {:sym              'WeakMap
                             :args             ['...]
                             :category         "Keyed collections"
                             :example          "(let [obj #js{}] (new js/WeakMap #js[#js[obj \"value\"]]))"
                             :instance-methods ["delete" "get" "has" "set"]
                             :tag-map          {:tag       :object
                                                :type      js/WeakMap
                                                :all-tags  #{:object :js :instance-of-built-in :map-like :weak}
                                                :classname "WeakMap"}}

 js/WeakSet                 {:sym              'WeakSet
                             :args             ['...]
                             :category         "Keyed collections"
                             :example          "(let [obj #js{}] (new js/WeakSet #js[obj]))"
                             :instance-methods ["add" "delete" "has"]
                             :tag-map          {:tag       :object
                                                :type      js/WeakSet
                                                :all-tags  #{:object :js :instance-of-built-in :set-like :weak}
                                                :classname "WeakSet"}}

 ;; INDEXED COLLECTIONS -----------------------------------------------------

 js/Array                   {:sym              'Array
                             :example          "(new js/Array 1 2 3)"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "concat" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "flat" "flatMap" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "pop" "push" "reduce" "reduceRight" "reverse" "shift" "slice" "some" "sort" "splice" "toLocaleString" "toReversed" "toSorted" "toSpliced" "toString" "unshift" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Array
                                                :all-tags            #{:object :js :instance-of-built-in :array :coll-like}
                                                :classname           "Array"
                                                :instance-properties ["length"]}}

 js/Int8Array               {:sym              'Int8Array
                             :example          "(new js/Int8Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Int8Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Int8Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Uint8Array              {:sym              'Uint8Array
                             :example          "(new js/Uint8Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Uint8Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Uint8Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Uint8ClampedArray       {:sym              'Uint8ClampedArray
                             :example          "(new js/Uint8ClampedArray #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Uint8ClampedArray
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Uint8ClampedArray"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Int16Array              {:sym              'Int16Array
                             :example          "(new js/Int16Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Int16Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Int16Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Uint16Array             {:sym              'Uint16Array
                             :example          "(new js/Uint16Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Uint16Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Uint16Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Int32Array              {:sym              'Int32Array
                             :example          "(new js/Int32Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Int32Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Int32Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Uint32Array             {:sym              'Uint32Array
                             :example          "(new js/Uint32Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Uint32Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Uint32Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/BigInt64Array           {:sym              'BigInt64Array
                             :args             ['...]
                             :category         "Indexed collections"
                             :example          "(new js/BigInt64Array 8)"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/BigInt64Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "BigInt64Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/BigUint64Array          {:sym              'BigUint64Array
                             :args             ['...]
                             :category         "Indexed collections"
                             :example          "(new js/BigUint64Array 8)"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/BigUint64Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "BigUint64Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Float32Array            {:sym              'Float32Array
                             :example          "(new js/Float32Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Float32Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Float32Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 js/Float64Array            {:sym              'Float64Array
                             :example          "(new js/Float64Array #js[1 2 3])"
                             :args             ['...]
                             :category         "Indexed collections"
                             :instance-methods ["at" "copyWithin" "entries" "every" "fill" "filter" "find" "findIndex" "findLast" "findLastIndex" "forEach" "includes" "indexOf" "join" "keys" "lastIndexOf" "map" "reduce" "reduceRight" "reverse" "set" "slice" "some" "sort" "subarray" "toLocaleString" "toReversed" "toSorted" "toString" "values" "with"]
                             :tag-map          {:tag                 :object
                                                :type                js/Float64Array
                                                :all-tags            #{:object :js :instance-of-built-in :typed-array :array :coll-like}
                                                :classname           "Float64Array"
                                                :instance-properties ["buffer" "byteLength" "byteOffset" "length"]}}

 ;; STRUCTURED DATA ---------------------------------------------------------

 js/ArrayBuffer             {:sym                 'ArrayBuffer
                             :args                ['...]
                             :instance-properties ["byteLength"
                                                   "detached"
                                                   "maxByteLength"
                                                   "resizable"]
                             :category            "Structured data"
                             :example             "(new js/ArrayBuffer 16)"
                             :instance-methods    ["resize" "slice" "transfer" "transferToFixedLength"]
                             :tag-map             {:tag                 :object
                                                   :type                js/ArrayBuffer
                                                   :all-tags            #{:object :js :instance-of-built-in :map-like :coll-like}
                                                   :classname           "ArrayBuffer"
                                                   :instance-properties ["byteLength" "maxByteLength" "resizable" "detached"]}}

 js/JSON                    {:sym                'JSON
                             :not-a-constructor? true
                             :category           "Structured data"
                             :example            "(js/JSON.stringify #js{:a 1})"
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :built-in}
                                                  :classname "JSON"}}

 js/DataView                {:sym                 'DataView
                             :args                ['ArrayBuffer]
                             :instance-properties ["buffer" "byteLength" "byteOffset"]
                             :category            "Structured data"
                             :example             "(new js/DataView (js/ArrayBuffer. 16))"
                             :instance-methods    ["getBigInt64" "getBigUint64" "getFloat32" "getFloat64" "getInt8" "getInt16" "getInt32" "getUint8" "getUint16" "getUint32" "setBigInt64" "setBigUint64" "setFloat32" "setFloat64" "setInt8" "setInt16" "setInt32" "setUint8" "setUint16" "setUint32"]
                             :tag-map             {:tag                 :object
                                                   :type                js/DataView
                                                   :all-tags            #{:object :js :instance-of-built-in}
                                                   :classname           "DataView"
                                                   :instance-properties ["buffer" "byteLength" "byteOffset"]}}


 js/Intl                    {:sym                'Intl
                             :not-a-constructor? true
                             :category           "Structured data"
                             :example            "js/Intl"
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :built-in}
                                                  :classname "Intl"}}

 js/Intl.Collator           {:sym              'Intl.Collator
                             :args             ['...]
                             :category         "Structured data"
                             :example          "(new js/Intl.Collator \"en-US\")"
                             :instance-methods ["compare" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "Collator"}}

 js/Intl.DateTimeFormat     {:sym              'Intl.DateTimeFormat
                             :args             ['...]
                             :category         "Structured data"
                             :example          "(new js/Intl.DateTimeFormat \"en-US\" #js{:dateStyle \"full\"})"
                             :instance-methods ["format" "formatRange" "formatRangeToParts" "formatToParts" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "DateTimeFormat"}}

 js/Intl.DisplayNames       {:sym              'Intl.DisplayNames
                             :example          "(new js/Intl.DisplayNames #js[\"en\"] #js {:type \"region\"})"
                             :args             ['...]
                             :category         "Structured data"
                             :instance-methods ["of" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "DisplayNames"}}

 js/Intl.ListFormat         {:sym              'Intl.ListFormat
                             :example          "(new js/Intl.ListFormat \"en-GB\" #js {:style \"long\", :type \"conjunction\"})"
                             :args             ['...]
                             :category         "Structured data"
                             :instance-methods ["format" "formatToParts" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "ListFormat"}}

 js/Intl.Locale             {:sym                 'Intl.Locale
                             :args                ['...]
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
                             :example             "(new js/Intl.Locale \"en-US\")"
                             :instance-methods    ["getCalendars" "getCollations" "getHourCycles" "getNumberingSystems" "getTextInfo" "getTimeZones" "getWeekInfo" "maximize" "minimize" "toString"]
                             :tag-map             {:tag                 :object
                                                   :type                js/Object
                                                   :all-tags            #{:object :js :instance-of-built-in :intl}
                                                   :classname           "Locale"
                                                   :instance-properties ["baseName" "calendar" "calendars" "caseFirst" "collation" "collations" "hourCycle" "hourCycles" "language" "numberingSystem" "numberingSystems" "numeric" "region" "script" "textInfo" "timeZones" "weekInfo"]}}

 js/Intl.NumberFormat       {:sym              'Intl.NumberFormat
                             :example          "(new js/Intl.NumberFormat \"de-DE\" #js{:style \"currency\", :currency \"EUR\" })"
                             :args             ['...]
                             :category         "Structured data"
                             :instance-methods ["format" "formatRange" "formatRangeToParts" "formatToParts" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "NumberFormat"}}

 js/Intl.PluralRules        {:sym              'Intl.PluralRules
                             :args             ['...]
                             :category         "Structured data"
                             :example          "(new js/Intl.PluralRules \"en\" #js{:type \"ordinal\"})"
                             :instance-methods ["resolvedOptions" "select" "selectRange"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "PluralRules"}}

 js/Intl.RelativeTimeFormat {:sym              'Intl.RelativeTimeFormat
                             :args             ['...]
                             :category         "Structured data"
                             :example          "(new js/Intl.RelativeTimeFormat \"en\" #js{:numeric \"auto\"})"
                             :instance-methods ["format" "formatToParts" "resolvedOptions"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "RelativeTimeFormat"}}

 js/Intl.Segmenter          {:sym              'Intl.Segmenter
                             :args             ['...]
                             :category         "Structured data"
                             :example          "(new js/Intl.Segmenter \"en\" #js{:granularity \"word\"})"
                             :instance-methods ["resolvedOptions" "segment"]
                             :tag-map          {:tag       :object
                                                :type      js/Object
                                                :all-tags  #{:object :js :instance-of-built-in :intl}
                                                :classname "Segmenter"}}

 ;; MANAGING MEMORY ---------------------------------------------------------

 js/WeakRef                 {:sym              'WeakRef
                             :args             ['f]
                             :category         "Managing memory"
                             :example          "(let [obj #js{}] (new js/WeakRef obj))"
                             :instance-methods ["deref"]
                             :tag-map          {:tag       :object
                                                :type      js/WeakRef
                                                :all-tags  #{:object :js :instance-of-built-in :weak}
                                                :classname "WeakRef"}}

 js/FinalizationRegistry    {:sym              'FinalizationRegistry
                             :args             ['f]
                             :category         "Managing memory"
                             :example          "(new js/FinalizationRegistry (fn [val] (js/console.log \"cleaned\" val)))"
                             :instance-methods ["register" "unregister"]
                             :tag-map          {:tag       :object
                                                :type      js/FinalizationRegistry
                                                :all-tags  #{:object :js :instance-of-built-in :weak}
                                                :classname "FinalizationRegistry"}}

 js/Reflect                 {:sym                'Reflect
                             :not-a-constructor? true
                             :example            "(js/Reflect.has #js{:a 1} \"a\")"
                             :tag-map            {:tag       :object
                                                  :type      js/Object
                                                  :all-tags  #{:object :js :built-in}
                                                  :classname "Reflect"}}

 js/Proxy                   {:sym      'Proxy
                             :args     ['...]
                             :category "Reflection"
                             :example  "(new js/Proxy #js{} #js{:get (fn [target prop] (aget target prop))})"
                             :tag-map  {:tag       :object
                                        :type      js/Object
                                        :all-tags  #{:object :js :instance-of-built-in :proxy}
                                        :classname "Object"}}}
