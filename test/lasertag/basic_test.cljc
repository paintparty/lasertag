(ns lasertag.basic-test
  (:require [lasertag.core :refer [tag tag-map]]
            [clojure.pprint :refer [pprint]]
            #?(:cljs [cljs.test :refer [deftest is testing]]
               :clj  [clojure.test :refer [deftest is testing]])
            #?(:clj  [lasertag.macros :refer [?]])
            [lasertag.cached :as cached])
  ;; This require breaks testing in clj and bb (cognitect.test-runner)
  ;; leave it comment out unless debugging
  #_(:require-macros [lasertag.macros :refer [?]]))


;; Basic experimentation
#?(:bb
   nil
   :clj
   (do
     #_(? (lasertag.core/tag-map (seq [1 2 3])))
     #_(? (lasertag.core/tag-map (hash-map :a 1)))
    ;;  (deftype CustomMap [m]
    ;;    clojure.lang.IPersistentMap
    ;;    ;;  (count [_] (count m))
    ;;    (assoc [this _ _] this))

     ;; (println (.size {:a 1}))

     ;;  (.size (->CustomMap {:a 1}) :a)

     ;;  (println #{:a 1 2 3 4 5 6 8 9 :b :C :d})
     ;;  (tag-map #{:a 1 2 3 4 5 6 8 9 :b :C :d})

     ;;  (println 'range)
     ;;  (tag-map (range 10))

     ;;  (println 'range)
     ;;  (tag-map (reverse (range 10)))

     ;;  (println '(:a 1))
     ;;  (tag-map '(:a 1))

     ;;  (println '[:a 1])
     ;;  (tag-map [:a 1])

     ;;  (println 'CustomMap) 
     ;;  (println (count (->CustomMap {:a 1 :b 3})))
     ;;  (println (tag-map (->CustomMap {:a 1 :b 3})))
     ))


;; #?(:clj
;; (do 
;;   (pprint (instance? java.lang.Iterable (java.util.ArrayList. [1 2 3])))
;;   (pprint (tag-map (java.util.ArrayList. [1 2 3])))))

;; Samples
(deftype MyType [a b])
(def my-data-type (->MyType 2 3))
(defrecord MyRecordType [a b c d])
(def my-record-type (->MyRecordType 4 8 4 5))
(defmulti different-behavior (fn [x] (:x-type x)))
(defmethod different-behavior :foo
  [x]
  (str (:name x) " will have a specific behavior"))
(defn xy [x y] (+ x y))

;; #?(:cljs
;;    (deftest cljs-function-types
;;      (is (= :function (tag #(inc %))))
;;      (is (= :function (tag MyType)))
;;      (is (= :function (tag MyRecordType)))
;;      (is (= :lasertag.core-test/MyType (tag my-data-type)))
;;      (is (= :record (tag my-record-type)))
;;      (is (= :defmulti (tag different-behavior))))
;;    :clj
;;    (do
;;      (deftest clj-function-types
;;        (is (= :function (tag #(inc %))))
;;        (is (= :class (tag MyType)))
;;        (is (= :class (tag MyRecordType)))
;;        (is (= :lasertag.core_test.MyType (tag my-data-type)))
;;        (is (= :record (tag my-record-type)))
;;        (is (= :defmulti (tag different-behavior))))))


;; (deftest cljc-scalar-types
;;   (is (= :string (tag "hi")))
;;   (is (= :keyword (tag :hi)))
;;   (is (= :regex (tag #"^xxo$")))
;;   (is (= :boolean (tag true)))
;;   (is (= :symbol (tag 'mysym)))
;;   (is (= :nil (tag nil))))

#?(:cljs
   (do 
     (deftest cljs-function-types-map
       (is (=
            (dissoc (tag-map #(inc %)) :type)
            {:tag       :function,
             :all-tags  #{:callable :lambda :function}
             :classname "Function"}))))
   :bb
   (deftest clj-function-types-map
     ;; TODO - Address :classname dissoc
     (is (=
          (dissoc (dissoc (tag-map #(inc %)) :type)
                  :classname)
          {:tag      :function,
           :all-tags #{:function :carries-meta :callable}})))
   :clj
   (deftest clj-function-types-map
     ;; TODO - Address :classname dissoc
     (is (=
          (dissoc (dissoc (tag-map #(inc %)) :type)
                     :classname)
          {:tag      :function,
           :all-tags #{:callable :lambda :function :carries-meta}}))))

#?(:cljs
   (deftest cljs-elide-function-info
     (is (=
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag       :function
           :all-tags  #{:callable :function}
           :classname "Function"})))
   :bb
   (deftest clj-elide-function-info
     (testing "custom xy fn, in bb"
       (is (=
            (dissoc (tag-map xy {:include-function-info? false}) :type)
            {:tag       :function,
             :all-tags  #{:function :carries-meta :callable},
             :classname "sci.impl.fns"}))))
   :clj
   (deftest clj-elide-function-info
     (is (=
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag       :function,
           :all-tags  #{:callable :function :carries-meta},
           :classname "lasertag.basic_test$xy"}))))


;; #?(:clj
;;    (pprint (tag-map  (java.util.ArrayList. [1 2 3]))))


;;------------------------------------------------------------------------------
;; Random Cruft Below
;;------------------------------------------------------------------------------

;;  (? :no-file lasertag/js-built-ins-by-built-in)
;;  (? :no-file (lasertag/tag-map (aget "hi" "concat")))
;;  (? :no-file (lasertag/tag-map (aget #js [1 2 3] "concat")))
;;  (? :no-file (lasertag/tag-map js/String))

;; This is all the cached tag-maps you need for js/built-ins
;; but more in all-tags like :js :built-in :non-constructor
;;  (!? (lasertag/tag-map juxt))
;;  (!? (lasertag/tag-map hi))
;;  (!? (lasertag/tag-map #()))
;;  (!? (lasertag/tag-map (fn wtf [])))

;;  (? jsi/js-built-ins-cached-tag-maps)
;;  (? (lasertag/tag-map js/RangeError))

;;  (? {:print-length 300 :scalar-max-length 300} jsi/js-built-ins-cached-tag-maps)

;;  (? (lasertag/tag-map js/Math))

;;  #_(-> (? :data {:print-length 300 :scalar-max-length 300} jsi/js-built-ins-cached-tag-maps)
;;      :formatted
;;      :string
;;      println)


;; #_(-> (? {:truncate? false} jsi/js-built-ins-cached-tag-maps))


;; const littleEndian = (() => {
;;   const buffer = new ArrayBuffer(2);
;;   new DataView(buffer).setInt16(0, 256, true /* littleEndian */);
;;   // Int16Array uses the platform's endianness.
;;   return new Int16Array(buffer)[0] === 256;
;; })();
;; console.log(littleEndian); // true or false

;; (? :log js/Atomics)

;; Run this in codegen phase to produce array of prop-names and attach to :resolver-fn or :instance-properties

#?(:cljs
   (do
    ;;  (def sgm (new js/Intl.Segmenter "fr" #js{:granularity "word"} "fr"))

     ;; (def m (tag-map sgm))
     ;; (def arrBuff (new js/ArrayBuffer 2))
     
     #_(? [(some-> m :resolver (apply [sgm]))])
     #_(? [sgm])
     ;; (? [arrBuff])
     ;; (? (aget sgm js/Symbol.toStringTag))
     ;; (? (= (js/Boolean nil) false))
     ;; (? (= (js/Boolean 1) true))
     
     ;; (? (type js/Error))
     ;; (? (lasertag.core/tag-map js/Intl.Locale))
     ;; (? (lasertag.core/tag-map js/Array.concat))
     ;; (? (lasertag.core/tag-map #(inc %)))
     ;; (? (lasertag.core/tag-map (aget "string" "concat")))
     

    ;;  (def korean (new js/Intl.Locale "ko" #js{:script    "Kore" 
    ;;                                           :region    "KR" 
    ;;                                           :hourCycle "h23" 
    ;;                                           :calendar  "gregory"}))
     ;; (? [#js{:one 1 :two 2 :three 3}])
     ;; (? [korean])
     

    ;;  (defn built-in? [obj]
    ;;    (if-let [ctor (.-constructor obj)]
    ;;      (let [ctor-str (js/Function.prototype.toString.call ctor)]
    ;;        (string/includes? ctor-str " [native code] "))
    ;;      false))


    ;;  (defclass HelloWorld
    ;;    (extends js/HTMLElement)

    ;;    (constructor [this]
    ;;                 (super))

    ;;    Object
    ;;    (connectedCallback [this]
    ;;                       (js/console.log "hey from CLJS" this)))

    ;;  #_(js/window.customElements.define "hello-world" HelloWorld)


    ;;  (defn foo [x] nil)

     ;; (? (contains? jsi/built-ins HelloWorld))
     
     ;; (? (built-in? foo))
     
     ;; (? (aget foo js/Symbol.toStringTag))
     ;; (? (aget sgm js/Symbol.toStringTag))
     
     ;; Try this to suss out js-built-in situation
     ;; (? (aget (new js/Int8Array #js[1 2 3]) js/Symbol.toStringTag))
     ;; (? :js (new js/Int8Array #js[1 2 3]))
     ;; (? :js ( (js/Function. "a" "b" "return a + b")  1 2))
     ;; (? :js #(inc %))
     ;; (? {:display-metadata? true} (with-meta (symbol "hi") {:bling.theme/token :js-object-key}))
     
     ;; (? [(.resolvedOptions (new js/Intl.Segmenter "fr" #js{:granularity "word"} "fr"))])
     ;; (? [#js{:a 1}])
     

     ;; (doseq [m (-> jsi/built-ins vals)]
     ;;   (let [example (some-> m :example*)
     ;;         string-tag (boolean (some-> example (aget js/Symbol.toStringTag)))]
     ;;     (when example
     ;;       (!? (object? example))
     ;;       [(:sym m) string-tag])))
     

    ;;  #_(instance? js/Object korean)
    ;;  #_(js/console.log (aget (.values #js [1 2 3]) js/Symbol.toStringTag))
     ;; (? (uuid "00000000-0000-0000-0000-000000000000"))
     
     ;; (? (lasertag.core/tag #"hey"))
     
     ;; (? (implements? IReversible [1 2 3]))
     ;; (? (implements? ICounted [1 2 3]))
     ;; (? (implements? ICollection [1 2 3]))
     ;; (? (.-name (type [1 2 3])))
     
     ;; (js/console.log (.hasOwnProperty [1 2 3] "cljs$lang$protocol_mask$partition0$"))
     
     ;; (js/console.log (.hasOwnProperty (delay (println "whhhhh") 1000) "cljs$lang$protocol_mask$partition0$"))
     
     ;; (println (delay (println "whhhhh") 1000))
     
     ;; (println (lasertag.core/tag-map (delay (println "whhhhh") 1000)))
     ;; (? :pp (lasertag.core/tag-map (atom 1000)))
     
     ;; (println (lasertag.core/tag-map [1 2 3] #_(volatile! 1000)))
     
     ;; (? (instance? cljs.core/IReversible (reverse [1 2 3])))
     ;; (? (reversible? (reverse [1 2 3])))
     ;; (? (type (reverse [1 2 3])))
     
     ;; (? (tag-map true))
     
     ;; ;; (? (tag-map 1))
     ;; ;; (? (tag-map "hey"))
     ;; (? (tag-map 'go))
     ;; (? (tag-map #"mn"))
     
     ;; #_(-> (? :data
     ;;         {:print-length                   300
     ;;          :scalar-max-length        300
     ;;          :scalar-mapkey-max-length 300}
     ;;         jsi/js-built-ins)
     ;;      :formatted
     ;;      :string
     ;;      println)
     
     ;; (? (type ##NaN))
     
     ;; (? (tag-map (js/BigInt 9007199254740991)))
     
     ;; (? (double? 42))
     ;; (? (int? 42.05))
     

     ;; (? (int? (double 42)))
     ;; (? (lasertag.core/tag-map ##NaN))
     
     ))


;; FOR CLAWED
;; [{:type     clojure.lang.PersistentList
;;   :examples [{:quoted  '(1 2 3)
;;               :literal '(1 2 3)}
;;              {:quoted  '(list 1 2 3)
;;               :literal (list 1 2 3)}]}
;;  {:type     clojure.lang.PersistentHashMap
;;   :examples [{:quoted  '{:a 1 :b 2}
;;               :literal {:a 1 :b 2}}
;;              {:quoted  '(hash-map :a 1 :b 2)
;;               :literal (hash-map :a 1 :b 2)}]}]



;;  (? (apply array-map [js/Math 1]))
;;  (? js/Math)   

;;  (? (apply array-map [js/Math 1 js/JSON 2]))


;;  Lasertag TODO

;;  Figure out how to print js/Math like js/Math{...} consistently, whether it is alone or part of a collection
;;  Maybe you should detect :js :built-in :non-constructor, and cast it as a symbol like (symbol (str "js/Atomics"))
;;           and then if there is a user's symbol that is 'js/Atomics (conflicting as key or something) make sure it gets quoted, so internally it would be (symbol (str "'js/Atomics"))

;; Problem vals:
;; js/NaN
;; js/Infinity
;; js/globalThis
;; js/Intl.Segmenter (namespace dot thing)


;; Make sure this still works!
;; You need to use the make it work if the object is not coll-like, such as below

;; (? {(new js/Intl.Segmenter "fr" #js{:granularity "word"} "fr")
;;     (new js/Intl.Segmenter "de" #js{:granularity "word"} "de")})

;;  Would be sweet to show resolved options for js objects like above such as - how hard would this be? resolver fn to def in lasertag?
;; js/Intl.Segmenter
;; {'locale': "fr", 'granularity': "word"}

;;  Solve 2 fireworks issues

;;  Go through all the interops and create sample for each one, then author cached tag-map by hand

;;  Print hashmap contents of js-built-ins like you want - Math, JSON, Atomics, Intl, will not survive truncate

;;  Just use that for js/built-in logic

;;  Move the method-of-built-in stuff to another namespace

;;  Do the same thing for java built-ins

;;  Go back to control-flow and start there

;;  Do current approach, and try to get to exhaustive test suite

;;  The maybe consider the following api surface, if you can achive parity apart from tag-map approach
;;  - lasertag.tag     ; <- just for `tag`, lightweight
;;  - lasertag.tag-map ; <- just for `tag-map`, pulls in more stuff to deal with java and js values
;;  - lasertag.reflect ; <- just for cljs `built-in-method-of`, etc.



;;  js/WeakRef
;;  {:sym      WeakRef
;;   :args     [f]
;;   :category "Managing memory"
;;   :tag-map  {:tag       :function
;;              :type      js/Function
;;              :all-tags  #{:function :js :built-in}
;;              :fn-name   "WeakRef"
;;              :fn-args   [f]
;;              :classname "Function"}}

;;  js/FinalizationRe...
;;  {:sym      FinalizationRegistry
;;   :args     [f]
;;   :category "Managing memory"
;;   :tag-map  {:tag       :function
;;              :type      js/Function
;;              :all-tags  #{:function :js :built-in}
;;              :fn-name   "FinalizationRegistry"
;;              :fn-args   [f]
;;              :classname "Function"}}

;;  js/Proxy
;;  {:sym      Proxy
;;   :args     [...]
;;   :category "Reflection"
;;   :tag-map  {:tag       :function
;;              :type      js/Function
;;              :all-tags  #{:function :js :built-in}
;;              :fn-name   "Proxy"
;;              :fn-args   []
;;              :classname "Function"}}

;;  #_(? :no-file 
;;     (reduce-kv (fn [m built-in v]
;;                  (assoc m
;;                         built-in 
;;                         (assoc v
;;                                :cached-tag-map 
;;                                (lasertag/tag-map built-in))))
;;                {} 
;;                jsi/js-built-ins-by-built-in))


;;  (? :no-file (lasertag/tag-map 10))
;;  (? :no-file (lasertag/tag-map 100))
;;  (? :no-file (lasertag/tag-map 3.14))
;;  (? :no-file (lasertag/tag-map 42))
;;  (? :no-file (lasertag/tag-map 99.99M))
;;  (? :no-file (lasertag/tag-map (js/BigInt "999999999999")))
;;  (? :no-file (lasertag/tag-map ##Inf))
;;  (? :no-file (lasertag/tag-map ##-Inf))
  