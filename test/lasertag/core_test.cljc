(ns lasertag.core-test
  (:require [lasertag.core :refer [tag tag-map]]
            #?(:cljs [cljs.test :refer [deftest is]])
            #?(:clj [clojure.test :refer :all])))

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

(deftest alternate-tag-format
  (is (= "int" (tag 1 {:format :string})))
  (is (= 'int (tag 1 {:format :symbol}))))


#?(:cljs
   (deftest cljs-function-types
     (is (= :function (tag #(inc %))))
     (is (= :function (tag MyType)))
     (is (= :function (tag MyRecordType)))
     (is (= :lasertag.core-test/MyType (tag my-data-type)))
     (is (= :lasertag.core-test/MyRecordType (tag my-record-type)))
     (is (= :defmulti (tag different-behavior))))
   :clj
   (deftest clj-function-types
     (is (= :function (tag #(inc %))))
     (is (= :java.lang.Class (tag MyType)))
     (is (= :java.lang.Class (tag MyRecordType)))
     (is (= :lasertag.core_test.MyType (tag my-data-type)))
     (is (= :MyRecordType (tag my-record-type)))
     (is (= :defmulti (tag different-behavior)))))


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
          {:tag          :function,
           :all-tags     #{:function},
           :lamda?       true
           :coll-type?   false
           :map-like?    false
           :number-type? false
           :fn-args      '[%1]})))
   :clj
   (deftest clj-function-types-map
     (is (= 
          (dissoc (tag-map #(inc %)) :type)
          {:tag          :function,
           :all-tags     #{:function},
           :lamda?       true
           :coll-type?   false
           :map-like?    false
           :number-type? false
           :fn-ns        "lasertag.core-test"
           :fn-args      :lasertag/unknown-function-signature-on-clj-function}))))

#?(:cljs
   (deftest cljs-elide-function-info
     (is (= 
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag          :function,
           :all-tags     #{:function},
           :coll-type?   false
           :map-like?    false
           :number-type? false})))
   :clj
   (deftest clj-elide-function-info
     (is (= 
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag          :function,
           :all-tags     #{:function},
           :coll-type?   false
           :map-like?    false
           :number-type? false}))))

(deftest cljc-collection-types
 (is (= :vector (tag [1 2 3])))
 (is (= :set (tag #{1 2 3})))
 (is (= :map (tag {:a 2 :b 3})))
 (is (= :map (tag {:a 2 :b 3 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 :i 9 :j 10 :k 11 :l 12})))
 (is (= :seq (tag (map inc (range 5)))))
 (is (= :seq (tag (range 10))))
 (is (= :list (tag '(:a :b :c))))
 (is (= :list (tag (list :a :b :c)))))

#?(:cljs
   (deftest cljs-collection-types-map
     (is (= 
          (tag-map '(:a :b :c))
          {:tag          :list,
           :all-tags     #{:list :js/Iterable :coll},
           :coll-type?   true,
           :number-type? false,
           :map-like?    false,
           :coll-size    3,
           :type         cljs.core/List})))
   :clj
   (deftest clj-collection-types-map
     (is (= 
          (tag-map       '(:a :b :c))
          {:tag          :list,
           :all-tags     #{:list :coll},
           :coll-type?   true,
           :map-like?    false
           :coll-size    3
           :number-type? false,
           :type         clojure.lang.PersistentList}))))

#?(:cljs
   (deftest cljs-elide-all-tagtypes
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false})
          {:tag  :list,
           :type cljs.core/List})))
   :clj
   (deftest clj-elide-all-tagtypes 
     (is (= 
          (tag-map       '(:a :b :c) {:include-all-tags? false})
          {:tag  :list,
           :type clojure.lang.PersistentList}))))

#?(:cljs
   (deftest cljs-elide-all-tagtypes+alternate-tag-type
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false :format :string})
          {:tag  "list",
           :type cljs.core/List}))
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false :format :symbol})
          {:tag  'list,
           :type cljs.core/List})))
   :clj
   (deftest clj-elide-all-tagtypes+alternate-tag-type
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false :format :string})
          {:tag  "list",
           :type clojure.lang.PersistentList}))
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false :format :symbol})
          {:tag  'list,
           :type clojure.lang.PersistentList}))))


(deftest cljc-number-types 
  (is (= :Infinity (tag (/ 1.6 0.0))))
  (is (= :-Infinity (tag (/ -1.0 0.0))))
  (is (= :NaN (tag (/ 0.0 0.0)))))

#?(:cljs
   (deftest cljs-number-types 
     (is (= :int (tag 1)))
     (is (= :float (tag 1.50)))
     (is (= :js/BigInt (tag (js/BigInt 171))))
     (is (= :js/Date (tag (new js/Date))))
     (is (= :function (tag js/Date))))
   :clj
   (deftest clj-number-types 
     (is (= :ratio (tag 1/3)))
     (is (= :int (tag (byte 0))))
     (is (= :int (tag (short 3))))
     (is (= :double (tag (double 23.44))))
     (is (= :decimal (tag 1M)))
     (is (= :int (tag 1)))
     (is (= :float (tag (float 1.50))))
     (is (= :char (tag (char 97))))
     (is (= :java.math.BigInteger (tag (java.math.BigInteger. "171"))))
     (is (= :java.util.Date (tag (java.util.Date.))))
     (is (= :java.lang.Class (tag java.util.Date)))))



