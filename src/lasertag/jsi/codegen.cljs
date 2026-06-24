(ns lasertag.jsi.codegen
  (:require
   ["fs"             :as fs]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [lasertag.cached :as cached]
   [lasertag.core :as lt]
   [lasertag.jsi.native]
   [cljs.reader :as reader]
   [clojure.string :as str])
  (:require-macros [lasertag.jsi.codegen :refer [with-datafied-demos]]))


;; -----------------------------------------------------------------------------
;; Toggle this to regenerate the data structure that serves most of the results
;; for `lasertag.core/tag` & `lasertag.core/tag-map` in cljs.
;; Generated ns is `lasertag.jsi.classes`

;; (def ^:private write-classes? false)
(def ^:private write-classes? true)


;; Toggle this to regenerate the cljs tests
;; Generated ns is `lasertag.generated`

;; (def ^:private write-tests? false)
(def ^:private write-tests? true)
;; -----------------------------------------------------------------------------


(def Atomics
  (try js/Atomics
       (catch js/Object e nil)))

(def by-class*
  (with-datafied-demos
    {

     ;; ---------------------------------------------------------------------
     ;; Primatives and ClojureScript classes
     ;; ---------------------------------------------------------------------
     ["cljs.core/Keyword" :keyword]           ":foo"
     ["js/String" :string]                    "\"foo\""
     ;; ["cljs.core/Symbol" :symbol]           "(symbol foo)"
     ["js/Boolean" :boolean]                  "true"
     ["js/Number" :number]                    "21"
     ["nil" :nil]                             "nil"
     ["cljs.core/UUID" :uuid ]                "(uuid \"4fe5d828-6444-11e8-8222-720007e40350\")"
     ["js/RegExp" :regex]                     "(js/RegExp. \"hello\")"
     ["js/Date" :datetime]                    "(js/Date.)"
     ["cljs.core/PersistentArrayMap" :map]    "{1 2 3 4}"
     ["cljs.core/PersistentHashMap" :map]     "(hash-map 1 2 3 4)"
     ["cljs.core/LazySeq" :seq]               "(map inc [1 2 3])"
     ["cljs.core/PersistentVector" :vector]   "[1 2 3]"
     ["cljs.core/PersistentHashSet" :set]     "#{1 2 3}"
     ["cljs.core/Subvec" :vector]             "(subvec [1 2 3 4 5] 1 3)"
     ["cljs.core/Cons" :seq]                  "(cons 1 (list 2 3))"
     ["cljs.core/IntegerRange" :seq]          "(range 3)"
     ["cljs.core/Range" :seq]                 "(range 0 1.0 0.1)"
     ["cljs.core/Repeat" :seq]                "(repeat 2 :a)"
     ["cljs.core/List" :list]                 "(list 1 2 3)"
     ["cljs.core/EmptyList" :list]            "(list)"
     ["cljs.core/PersistentTreeMap" :map]     "(sorted-map 1 2 3 4)"
     ["cljs.core/PersistentTreeSet" :set]     "(sorted-set 1 2 3)"
     ["cljs.core/TransientArrayMap" :map]     "(transient (array-map 1 2 3 4))"
     ["cljs.core/TransientHashMap" :map]      "(transient (hash-map 1 2 3 4))"
     ["cljs.core/TransientVector" :vector]    "(transient [1 2 3])"
     ["cljs.core/TransientHashSet" :set]      "(transient #{1 2 3})"
     ["cljs.core/PersistentQueue" :queue]     "cljs.core.PersistentQueue.EMPTY"
     ["cljs.core/MapEntry" :vector]           "(-> {:a 1} first)"
     ["cljs.core/MultiFn" :function]          "(do (defmulti different-behavior (fn [x] (:x-type x))) different-behavior)"
     

     ;; TODO - Check and add
     ;;  ["cljs.core/ArrayNodeSeq" :seq]          "(seq (apply hash-map (interleave (range 100) (range 100))))"
     ;;  ["cljs.core/ChunkedCons" :seq]           "(chunk-cons (chunk (doto (chunk-buffer 1) (chunk-append 0))) nil)"
     ;;  ["cljs.core/ChunkedSeq" :seq]            "(seq (vec (range 33)))"
     ;;  ["cljs.core/Cycle" :seq]                 "(cycle [1 2 3])"
     ;;  ["cljs.core/ES6IteratorSeq" :seq]        "(es6-iterator-seq (.values #js [1 2 3]))"
     ;;  ["cljs.core/IndexedSeq" :seq]            "(seq [1 2 3])"
     ;;  ["cljs.core/IntegerRangeChunk" :chunk]   "(chunk-first (seq (range 32)))"
     ;;  ["cljs.core/Iterate" :seq]               "(iterate inc 0)"
     ;;  ["cljs.core/KeySeq" :seq]                "(keys {:a 1 :b 2})"
     ;;  ["cljs.core/NodeSeq" :seq]               "(seq (apply hash-map (range 20)))"
     ;;  ["cljs.core/PersistentArrayMapSeq" :seq] "(seq (array-map :a 1 :b 2))"
     ;;  ["cljs.core/PersistentQueueSeq" :seq]    "(seq (conj cljs.core.PersistentQueue.EMPTY 1 2 3))"
     ;;  ["cljs.core/PersistentTreeMapSeq" :seq]  "(seq (sorted-map :a 1 :b 2))"
     ;;  ["cljs.core/RSeq" :seq]                  "(rseq [1 2 3])"
     ;;  ["cljs.core/ValSeq" :seq]                "(vals {:a 1 :b 2})"
     

     ;; ---------------------------------------------------------------------
     ;; JS Built-ins
     ;; ---------------------------------------------------------------------
     
     ;; Reference types
     ["js/Promise" :promise]                  "(new js/Promise (fn [x] x))"

     ;; Error objects
     ["js/AggregateError" :throwable]         "(new js/AggregateError (array (new js/Error \"some error\")) \"Hello\")",
     ["js/EvalError" :throwable]              "(new js/EvalError)",
     ["js/RangeError" :throwable]             "(new js/RangeError)",
     ["js/ReferenceError" :throwable]         "(new js/ReferenceError)",
     ["js/SyntaxError" :throwable]            "(new js/SyntaxError)",
     ["js/TypeError" :throwable]              "(new js/TypeError)",
     ["js/URIError" :throwable]               "(new js/URIError)",
     ["js/Error" :throwable]                  "(new js/Error)"

     ;; Keyed collections 
     ["js/Map" :map]                          "(new js/Map (array (array \"a\", 1), (array \"b\", 2)))",
     ["js/Set" :set]                          "(new js/Set (array 1 2))",
     ["js/WeakMap" :map]                      "(let [wm (js/WeakMap.) o (js-obj :a 1)] (.set wm o 100))",
     ["js/WeakSet" :set]                      "(js/WeakSet. (array (js-obj :id 1)) (js-obj :id 2))"

     ;; Indexed collections 
     ["js/BigInt64Array" :array]              "(new js/BigInt64Array 3)",
     ["js/BigUint64Array" :array]             "(new js/BigUint64Array 3)",
     ["js/Int32Array" :array]                 "(new js/Int32Array (array 1 2 3))",
     ["js/Uint16Array" :array]                "(new js/Uint16Array (array 1 2 3))",
     ["js/Int8Array" :array]                  "(new js/Int8Array (array 1 2 3))",
     ["js/Uint32Array" :array]                "(new js/Uint32Array (array 1 2 3))",
     ["js/Array" :array]                      "(new js/Array 1 2 3)",
     ["js/Uint8Array" :array]                 "(new js/Uint8Array (array 1 2 3))",
     ["js/Int16Array" :array]                 "(new js/Int16Array (array 1 2 3))",
     ["js/Uint8ClampedArray" :array]          "(new js/Uint8ClampedArray (array 1 2 3))",
     ["js/Float32Array" :array]               "(new js/Float32Array (array 1 2 3))",
     ["js/Float64Array" :array]               "(new js/Float64Array (array 1 2 3))"

     ;; Internalization
     ["js/Intl.DateTimeFormat" :intl]         "(new js/Intl.DateTimeFormat \"en-US\")",
     ["js/Intl.RelativeTimeFormat" :intl]     "(new js/Intl.RelativeTimeFormat \"en\" (js-obj :style \"short\"))",
     ["js/Intl.Locale" :intl]                 "(new js/Intl.Locale \"ko\" (js-obj :script \"Kore\", :region \"KR\", :hourCycle \"h23\", :calendar \"gregory\"))",
     ["js/Intl.Collator" :intl]               "(new js/Intl.Collator \"sv\")",
     ["js/Intl.DisplayNames" :intl]           "(new js/Intl.DisplayNames (clj->js [\"en\"]) (js-obj \"type\" \"region\"))",
     ["js/Intl.PluralRules" :intl]            "(new js/Intl.PluralRules \"en-US\")",
     ;; ["js/Intl" :intl]                       nil,
     ["js/Intl.NumberFormat" :intl]           "(new js/Intl.NumberFormat \"de-DE\" (js-obj :style \"currency\", :currency \"EUR\"))",
     ["js/Intl.Segmenter" :intl]              "(new js/Intl.Segmenter \"fr\" (js-obj :granularity \"word\"))",
     ["js/Intl.ListFormat" :intl]             "(new js/Intl.ListFormat \"en-GB\" (js-obj :style \"long\", :type \"conjunction\"))"
     }))

(def by-class
  (reduce-kv 
   (fn [m [classname tag :as k] [stringified-demo demo]]
     (assoc m
            (symbol (str classname))
            (with-meta {:type      (symbol (str classname))
                        :classname (str classname)
                        :tag       tag
                        :all-tags  (conj (cached/all-tags* demo) tag)
                        :demo      stringified-demo}
              (meta k))))
   {}
   by-class*))

(defn write-file-sync!
  "Write `content` to `filepath`, creating or overwriting it."
  ([filepath content]
   (write-file-sync! filepath content nil))
  ([filepath content opts]
   (.writeFileSync fs filepath content opts)
   (println "Wrote" filepath)))

(defn autogen-file-header-comment 
  "This is used as the comment header of the generated file"
  [& lines]
  (str 
   ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"
   ";;\n"
   ";;\n"
   (string/join
    "\n;;\n"
    (mapv #(str ";;   " %) lines))
   "\n"
   ";;\n"
   ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"))


(def header-comment
  (autogen-file-header-comment
   "This namespace is automatically generated in lasertag.jsi.codegen.cljs."
   "Do not manually add anything to this namespace."
   "To regenerate, following instructions in readme \"Developing\" section."))

(defn header-comment+namespace-form [s form]
  (str
   header-comment
   "\n\n"
   (with-out-str (clojure.pprint/pprint form ))
   "\n\n"))

(def test-path "./test/lasertag/core_test.cljs")

(defn write-tests! []
  (write-file-sync! test-path
                    (header-comment+namespace-form
                     header-comment
                     '(ns lasertag.core-test
                        (:require
                         [clojure.test :refer [deftest is]]
                         [lasertag.core :refer [tag-map]]
                         [clojure.string :as string]))))
  

  (doseq [[sym {:keys [classname demo]
                :as   m}] by-class] 
    (write-file-sync! 
     test-path
     (str
      "\n\n"
      (with-out-str
        (clojure.pprint/pprint
         (list 
          'deftest (symbol (-> (str classname "-test")
                               (str/replace #"^js/|^cljs.core/" "")
                               (str/replace #"\." "_")))
          (list 'is
                (list '=
                      (list 'tag-map (reader/read-string demo))
                      (dissoc m :demo)))))))
     ;; flag for appending
     #js{:flag "a"})))

(defn write-classes! []
  (write-file-sync! 
   "./src/lasertag/jsi/classes.cljs"
   (str (header-comment+namespace-form
         header-comment
         '(ns lasertag.jsi.classes)
         
         )
        (with-out-str
          (clojure.pprint/pprint
           (list 'def
                 'by-class
                 (reduce-kv
                  (fn [m k v]
                    (assoc m k (dissoc v :demo)))
                  {}
                  by-class)))))))

(defn main []
  (when write-tests? (write-tests!))
  (when write-classes? (write-classes!)))
