(ns lasertag.core-test
  (:require [lasertag.core :refer [tag tag-map]]
            [clojure.pprint :refer [pprint]]
            #?(:cljs [cljs.test :refer [deftest is testing]])
            #?(:clj [clojure.test :refer :all]))
  #?(:clj
     (:import (clojure.lang PersistentVector$TransientVector
                            PersistentHashSet$TransientHashSet
                            PersistentArrayMap$TransientArrayMap
                            PersistentHashMap$TransientHashMap))))


#?(:bb
   nil
   :clj
   (do

     (deftype CustomMap [m]
       clojure.lang.IPersistentMap
       ;;  (count [_] (count m))
       (assoc [this _ _] this))

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

#?(:cljs
   (deftest cljs-function-types
     (is (= :function (tag #(inc %))))
     (is (= :function (tag MyType)))
     (is (= :function (tag MyRecordType)))
     (is (= :lasertag.core-test/MyType (tag my-data-type)))
     (is (= :record (tag my-record-type)))
     (is (= :defmulti (tag different-behavior))))
   :clj
   (do
     (deftest clj-function-types
       (is (= :function (tag #(inc %))))
       (is (= :class (tag MyType)))
       (is (= :class (tag MyRecordType)))
       (is (= :lasertag.core_test.MyType (tag my-data-type)))
       (is (= :record (tag my-record-type)))
       (is (= :defmulti (tag different-behavior))))))


(deftest cljc-scalar-types
  (is (= :string (tag "hi")))
  (is (= :keyword (tag :hi)))
  (is (= :regex (tag #"^xxo$")))
  (is (= :boolean (tag true)))
  (is (= :symbol (tag 'mysym)))
  (is (= :nil (tag nil))))

#?(:cljs
   (deftest cljs-function-types-map
     (is (=
          (dissoc (tag-map #(inc %)) :type)
          {:tag       :function,
           :lambda?   true,
           :fn-args   '[%1],
           :all-tags  #{:function},
           :classname "Function"})))
   :bb
   (deftest clj-function-types-map
     ;; TODO - Address :classname dissoc
     (is (=
          (dissoc (dissoc (tag-map #(inc %)) :type)
                  :classname)
          {:tag      :function,
           :lambda?  true,
           :fn-ns    "sci.impl.fns",
           :fn-args  :lasertag/unknown-function-signature-on-clj-function,
           :all-tags #{:function :carries-meta}})))
   :clj
   (deftest clj-function-types-map
     ;; TODO - Address :classname dissoc
     (is (=
          (dissoc (dissoc (tag-map #(inc %)) :type)
                  :classname)
          {:tag      :function,
           :lambda?  true,
           :fn-ns    "lasertag.core-test",
           :fn-args  :lasertag/unknown-function-signature-on-clj-function,
           :all-tags #{:function :carries-meta}}))))

#?(:cljs
   (deftest cljs-elide-function-info
     (is (=
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag       :function
           :all-tags  #{:function}
           :classname "Function"})))
   :bb
   (deftest clj-elide-function-info
     (testing "custom xy fn, in bb"
       (is (=
            (dissoc (tag-map xy {:include-function-info? false}) :type)
            {:tag       :function,
             :all-tags  #{:function :carries-meta},
             :classname "sci.impl.fns"}))))
   :clj
   (deftest clj-elide-function-info
     (is (=
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag       :function,
           :all-tags  #{:function :carries-meta},
           :classname "lasertag.core_test$xy"}))))

(deftest cljc-collection-types
  (is (= :vector (tag [1 2 3])))
  (is (= :set (tag #{1 2 3})))
  (is (= :map (tag {:a 2 :b 3})))
  (is (= :map (tag {:a 2 :b 3 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 :i 9 :j 10 :k 11 :l 12})))
  (is (= :seq (tag (map inc (range 5)))))
  (is (= :seq (tag (range 10))))
  (is (= :seq (tag '(:a :b :c))))
  (is (= :seq (tag (list :a :b :c)))))

#?(:cljs
   (deftest cljs-collection-types-map
     (testing "cljs.core/List"
       (is (=
            (tag-map '(:a :b :c))
            {:tag       :seq,
             :type      cljs.core/List,
             :all-tags  #{:seq :iterable :coll :list :coll-like :carries-meta},
             :classname "cljs.core/List",
             :coll-size 3})))
     ;; TODO - why is :fn-args flipping from nil to []
     #_(testing "Built-in js object method: string.concat"
         (is (=
              (tag-map (aget "hi" "concat"))
              {:fn-name                    "concat",
               :type                       js/Function,
               :classname                  "Function",
               :js-built-in-function?      true,
               :js-built-in-method-of-name "String",
               :all-tags                   #{:function},
               :js-built-in-method-of      js/String,
               :fn-args                    nil,
               :tag                        :function})))
     (testing "Built-in js/JSON., exclude :js-built-in-object-info"
       (is (=
            (tag-map js/JSON
                     {:include-js-built-in-object-info? false})
            {:coll-size 1,
             :classname "Object",
             :all-tags  #{:object
                          :js-object
                          :js-map-like-object
                          :coll-like
                          :map-like},
             :type      js/Object,
             :tag       :object})))

     (testing "Built-in js/JSON."
       (is (= (tag-map js/JSON)
              {:tag                     :object,
               :type                    js/Object,
               :all-tags                #{:object
                                          :js-map-like-object
                                          :coll-like
                                          :map-like
                                          :js-object},
               :classname               "Object",
               :coll-size               1,
               :js-built-in-object?     true,
               :js-built-in-object-name "JSON"}))))
   :clj
   (deftest clj-collection-types-map
     (testing "clojure.lang.PersistentList"
       (is (=
            (tag-map '(:a :b :c))
            {:tag       :seq,
             :type      clojure.lang.PersistentList,
             :all-tags  #{:iterable :coll :list :coll-like :seq :carries-meta},
             :classname "clojure.lang.PersistentList",
             :coll-size 3})))

     (testing "java.util.HashMap"
       (is (=
            (tag-map       (java.util.HashMap. {"a" 1
                                                "b" 2}))
            {:tag       :map,
             :type      java.util.HashMap,
             :all-tags  #{:coll :coll-like :map-like :map},
             :classname "java.util.HashMap",
             :coll-size 2})))

     (testing "java.util.HashSet"
       (is (=
            (tag-map       (java.util.HashSet. #{"a" 1
                                                 "b" 2}))
            {:tag       :set,
             :type      java.util.HashSet,
             :all-tags  #{:iterable :coll :coll-like :set :set-like},
             :classname "java.util.HashSet",
             :coll-size 4})))

     (testing "java.util.ArrayList"
       (is (=
            (tag-map       (java.util.ArrayList. [1 2 3]))
            {:tag       :array,
             :type      java.util.ArrayList,
             :all-tags  #{:iterable :coll :array :coll-like},
             :classname "java.util.ArrayList",
             :coll-size 3})))))


#?(:cljs
   (deftest clj-transient-collections-map
     (testing "cljs.core/TransientHashSet"
       (is (=
            (tag-map    (transient #{1 2 3}))
            {:tag       :set,
             :type      cljs.core/TransientHashSet,
             :all-tags  #{:coll :transient :coll-like :set :set-like},
             :classname "cljs.core/TransientHashSet",
             :coll-size 3})))
     (testing "cljs.core/TransientArrayMap"
       (is (=
            (tag-map    (transient {:a 2 :b 4}))
            {:tag       :map,
             :type      cljs.core/TransientArrayMap,
             :all-tags  #{:coll :transient :coll-like :map :map-like},
             :classname "cljs.core/TransientArrayMap",
             :coll-size 2})))
     (testing "cljs.core/PersistentHashMap$TransientHashMap"
       (is (=
            (tag-map    (transient {:a 1
                                    :b 2
                                    :c 3
                                    :d 4
                                    :e 5
                                    :f 6
                                    :g 7
                                    :h 8
                                    :i 9
                                    :j 10}))
            {:tag       :map,
             :type      cljs.core/TransientHashMap,
             :all-tags  #{:map :coll :transient :coll-like :map-like},
             :classname "cljs.core/TransientHashMap",
             :coll-size 10}))))
   :clj
   (deftest clj-transient-collections-map
     (testing "clojure.lang.PersistentHashSet$TransientHashSet"
       (is (=
            (tag-map    (transient #{1 2 3}))
            {:tag       :set,
             :type      clojure.lang.PersistentHashSet$TransientHashSet,
             :all-tags  #{:coll :transient :coll-like :set :set-like},
             :classname "clojure.lang.PersistentHashSet$TransientHashSet",
             :coll-size 3})))
     (testing "clojure.lang.PersistentArrayMap$TransientArrayMap"
       (is (=
            (tag-map    (transient {:a 2 :b 4}))
            {:tag       :map,
             :type      clojure.lang.PersistentArrayMap$TransientArrayMap,
             :all-tags  #{:coll :transient :coll-like :map :map-like},
             :classname "clojure.lang.PersistentArrayMap$TransientArrayMap",
             :coll-size 2})))
     (testing "clojure.lang.PersistentHashMap$TransientHashMap"
       (is (=
            (tag-map    (transient {:a 1
                                    :b 2
                                    :c 3
                                    :d 4
                                    :e 5
                                    :f 6
                                    :g 7
                                    :h 8
                                    :i 9
                                    :j 10}))
            {:tag       :map,
             :type      clojure.lang.PersistentHashMap$TransientHashMap,
             :all-tags  #{:coll :transient :coll-like :map :map-like},
             :classname "clojure.lang.PersistentHashMap$TransientHashMap",
             :coll-size 10})))))



#?(:cljs
   (deftest cljs-inf-and-nan-types
     (is (= :number (tag (/ 1.6 0.0))))
     (is (= :number (tag (/ -1.0 0.0))))
     (is (= :nan (tag (/ 0.0 0.0)))))
   :bb
   nil
   :clj
   (deftest clj-inf-and-nan-types
     (is (= :number (tag (/ 1.6 0.0))))
     (is (= :number (tag (/ -1.0 0.0))))
     (is (= :nan (tag (/ 0.0 0.0))))))


#?(:cljs
   (deftest cljs-number-types
     (is (= :number (tag 1)))
     (is (= :number (tag 1.50)))
     (is (= :number (tag (js/BigInt 171))))
     (is (= :inst (tag (new js/Date))))
     (is (= :function (tag js/Date))))
   :clj
   (deftest clj-number-types
     (is (= :ratio (tag 1/3)))
     (is (= :number (tag (byte 0))))
     (is (= :number (tag (short 3))))
     (is (= :number (tag (double 23.44))))
     (is (= :number (tag 1M)))
     (is (= :number (tag 1)))
     (is (= :number (tag (float 1.50))))
     (is (= :char (tag (char 97))))
     (is (= :number (tag (java.math.BigInteger. "171"))))
     (is (= :inst (tag (java.util.Date.))))
     (is (= :class (tag java.util.Date)))))

#?(:cljs
   ()
   :clj
   (println (tag-map '(:a :b :c) {:include-all-tags? false})))


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

(def sgm (new js/Intl.Segmenter "fr" #js{:granularity "word"} "fr"))
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


(def korean (new js/Intl.Locale "ko" #js{:script "Kore" :region "KR" :hourCycle "h23" :calendar "gregory"}))
;; (? [#js{:one 1 :two 2 :three 3}])
;; (? [korean])


(defn built-in? [obj]
  (if-let [ctor (.-constructor obj)]
    (let [ctor-str (js/Function.prototype.toString.call ctor)]
      (string/includes? ctor-str " [native code] "))
    false))


(defclass HelloWorld
  (extends js/HTMLElement)

  (constructor [this]
               (super))

  Object
  (connectedCallback [this]
                     (js/console.log "hey from CLJS" this)))

#_(js/window.customElements.define "hello-world" HelloWorld)


(defn foo [x] nil)

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


(!? (instance? js/Object korean))
#_(js/console.log (aget (.values #js [1 2 3]) js/Symbol.toStringTag))
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



#_(? [{:name "Integer" :desc "ClojureScript integer literal" :example 42}])

#_(? :+ {:print-length 80
         ;; :quote-symbols? true
         }
     (reduce
      (fn [acc m]
        (assoc acc
               (type (:example m))
               (lasertag.core/tag-map (:example m))))
      {}
      [;;  {:name "BigInt"
       ;;   :desc "Big Integer (max safe integer)"
       ;;   :quoted '(clojure.lang.BigInt 9007199254740991)
       ;;   :example (clojure.lang.BigInt 9007199254740991)}

       {:name "Boolean True"
        :desc "Boolean true value"
        :example true}

       {:name "Boolean False"
        :desc "Boolean false value"
        :example false}

       {:name "Nil"
        :desc "ClojureScript nil value (represents absence of value)"
        :example nil}

       {:name "Atom"
        :desc "Mutable reference type, created with initial value"
        :quoted '(atom 1)
        :example (atom 1)}

       {:name "Positive Infinity"
        :desc "Special numeric value representing positive infinity"
        :quoted '##Inf
        :example ##Inf}

       {:name "Negative Infinity"
        :desc "Special numeric value representing negative infinity"
        :quoted '##-Inf
        :example ##-Inf}

       {:name "NaN"
        :desc "Special numeric value representing Not-a-Number"
        :quoted '##NaN
        :example ##NaN}

       {:name "String"
        :desc "ClojureScript string literal"
        :example "hello"}

       {:name "Boolean"
        :desc "ClojureScript string literal"
        :example "hello"}

       {:name "Keyword"
        :desc "ClojureScript keyword"
        :example :hello}

       {:name "Symbol"
        :desc "ClojureScript symbol, quoted"
        :quoted  'hello
        :example 'hello}

       {:name    "Range"
        :desc    "Lazy sequence of integers from 0 to n-1"
        :quoted  '(range 5)
        :example (range 5)}

       {:name    "Reverse"
        :desc    "Returns a sequence in reverse order"
        :quoted  '(reverse (range 5))
        :example (reverse (range 5))}

       {:name    "Cons"
        :desc    "Constructs a new sequence with element prepended to collection"
        :quoted  '(cons 1 '(2 3))
        :example (cons 1 '(2 3))}

       {:name    "Cons"
        :desc    "Constructs a new sequence with element prepended to collection"
        :quoted  '(cons 1 '(2 3))
        :example (cons 1 '(2 3))}

       ;; throws
       #_{:name    "Assert"
          :desc    "Throws an error if condition is false"
          :quoted  '(assert false "thrown error")
          :example (assert false "thrown error")}

       {:name    "Repeat"
        :desc    "Returns a lazy infinite sequence of x"
        :quoted  '(repeat 3)
        :example (take 10 (repeat 3))}

       {:name    "Repeat with count"
        :desc    "Returns a lazy sequence of n repetitions of x"
        :quoted  '(repeat 5 :x)
        :example (repeat 5 :x)}

       {:name    "Repeatedly"
        :desc    "Returns a lazy infinite sequence of calls to function f"
        :quoted  '(take 10 (repeatedly rand))
        :example (take 10 (repeatedly rand))}

       {:name    "Cycle"
        :desc    "Returns a lazy infinite sequence of repetitions of the items in collection"
        :quoted  '(take 10 (cycle [1 2 3]))
        :example (take 10 (cycle [1 2 3]))}

       ;;  ;; infinite
       ;;  ;; {:name "Iterate"
       ;;  ;;   :desc "Returns a lazy infinite sequence of x, (f x), (f (f x)), etc."
       ;;  ;;   :quoted '(iterate inc 0)
       ;;  ;;   :example (iterate inc 0)}

       {:name    "Take"
        :desc    "Returns a lazy sequence of the first n items"
        :quoted  '(take 3 (range 10))
        :example (take 3 (range 10))}

       {:name    "Drop"
        :desc    "Returns a lazy sequence with the first n items removed"
        :quoted  '(drop 3 (range 10))
        :example (drop 3 (range 10))}

       {:name    "List (function)"
        :desc    "Creates a list from arguments"
        :quoted  '(list 1 2 3)
        :example (list 1 2 3)}

       {:name    "Vector (function)"
        :desc    "Creates a vector from arguments"
        :quoted  '(vector 1 2 3)
        :example (vector 1 2 3)}

       {:name    "Array Map"
        :desc    "Creates a small map optimized for sequential lookup"
        :quoted  '(array-map :a 1 :b 2)
        :example (array-map :a 1 :b 2)}

       {:name    "Queue"
        :desc    "Persistent queue (FIFO)"
        :quoted  '(conj cljs.core.PersistentQueue.EMPTY 1 2 3)
        :example (conj cljs.core.PersistentQueue.EMPTY 1 2 3)}

       {:name    "Lazy Seq"
        :desc    "Creates a lazy sequence from a body of expressions"
        :quoted  '(lazy-seq (cons 1 (lazy-seq (cons 2 nil))))
        :example (lazy-seq (cons 1 (lazy-seq (cons 2 nil))))}

       {:name    "Interleave"
        :desc    "Returns a lazy seq of first item from each coll, then second, etc."
        :quoted  '(interleave [1 2 3] [:a :b :c])
        :example (interleave [1 2 3] [:a :b :c])}

       {:name    "Interpose"
        :desc    "Returns a lazy seq with separator interposed between elements"
        :quoted  '(interpose "," ["a" "b" "c"])
        :example (interpose "," ["a" "b" "c"])}

       {:name    "Concat"
        :desc    "Returns a lazy seq representing the concatenation of collections"
        :quoted  '(concat [1 2] [3 4] [5 6])
        :example (concat [1 2] [3 4] [5 6])}

       {:name    "Mapcat"
        :desc    "Returns the result of applying concat to the result of applying map"
        :quoted  '(mapcat reverse [[1 2] [3 4]])
        :example (mapcat reverse [[1 2] [3 4]])}

       {:name    "Flatten"
        :desc    "Takes any nested combination of sequential things and returns a flat sequence"
        :quoted  '(flatten [1 [2 [3 4]] 5])
        :example (flatten [1 [2 [3 4]] 5])}

       {:name    "Partition"
        :desc    "Returns a lazy sequence of lists of n items each"
        :quoted  '(partition 2 [1 2 3 4 5 6])
        :example (partition 2 [1 2 3 4 5 6])}

       {:name    "Partition-all"
        :desc    "Returns a lazy sequence of lists like partition, but includes final partial partition"
        :quoted  '(partition-all 2 [1 2 3 4 5])
        :example (partition-all 2 [1 2 3 4 5])}

       {:name    "Split-at"
        :desc    "Returns a vector of [(take n coll) (drop n coll)]"
        :quoted  '(split-at 2 [1 2 3 4 5])
        :example (split-at 2 [1 2 3 4 5])}

       {:name    "Subvec"
        :desc    "Returns a subvector of vector v from start (inclusive) to end (exclusive)"
        :quoted  '(subvec [1 2 3 4 5] 1 3)
        :example (subvec [1 2 3 4 5] 1 3)}

       {:name    "Transient"
        :desc    "Creates a transient version of a collection for efficient batch updates"
        :quoted  '(transient [1 2 3])
        :example (transient [1 2 3])}

       {:name    "Persistent!"
        :desc    "Converts a transient collection back to persistent"
        :quoted  '(persistent! (transient [1 2 3]))
        :example (persistent! (transient [1 2 3]))}

       {:name    "Character Literal"
        :desc    "Character literal notation"
        :quoted  '\h
        :example \h}

       {:name    "Newline Character"
        :desc    "Newline character literal"
        :quoted  '\n
        :example \n}

       {:name    "Tab Character"
        :desc    "Tab character literal"
        :quoted  '\tab
        :example \tab}

       {:name    "Scientific Notation"
        :desc    "Number in scientific notation"
        :quoted  (symbol "1.5e3")
        :example 1.5e3}

       {:name    "Hexadecimal"
        :desc    "Hexadecimal number literal"
        :quoted  (symbol "0xFF")
        :example 0xFF}

       {:name    "Octal"
        :desc    "Octal number literal"
        :quoted  (symbol "077")
        :example 077}

       {:name    "Binary"
        :desc    "Binary number literal"
        :quoted  (symbol "2r1010")
        :example 2r1010}

       {:name    "Arbitrary Radix"
        :desc    "Number in arbitrary base (radix)"
        :quoted  (symbol "36rZZ")
        :example 36rZZ}

       {:name    "Tagged Literal"
        :desc    "Extensible data notation tagged literal"
        :quoted  '#inst "2024-01-01"
        :example #inst "2024-01-01"}


       {:name    "Anonymous Function"
        :desc    "Anonymous function literal syntax"
        :quoted  (symbol "#(+ % 1)")
        :example #(+ % 1)}

       {:name    "Metadata"
        :desc    "Attaches metadata to an object"
        :quoted  '(with-meta [1 2 3] {:doc "My vector"})
        :example (with-meta [1 2 3] {:doc "My vector"})}

       {:name    "Deref"
        :desc    "Dereferences a reference type (atom, delay, etc.)"
        :quoted  '(deref (atom 42))
        :example (deref (atom 42))}]))






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
