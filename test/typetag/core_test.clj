(ns typetag.core-test
  (:require [clojure.test :refer :all]
            [typetag.core :refer [tag tag-map]]))

;; Samples
(deftype MyType [a b])
(def my-data-type (->MyType 2 3))
(defrecord MyRecordType [a b c d])
(def my-record-type (->MyRecordType 4 8 4 5))
(defmulti different-behavior (fn [x] (:x-type x)))
(defmethod different-behavior :wolf
  [x]
  (str (:name x) " will have a specific behavior"))
(defn xy [x y] (+ x y))

(deftest clj-scalar-types
 (is (= :string (tag "hi")))
 (is (= :keyword (tag :hi)))
 (is (= :regex (tag #"^xxo$")))
 (is (= :boolean (tag true)))
 (is (= :symbol (tag 'mysym)))
 (is (= :nil (tag nil))))

(deftest clj-function-types
 (is (= :function (tag #(inc %))))
 (is (= :java.lang.Class (tag MyType)))
 (is (= :java.lang.Class (tag MyRecordType)))
 (is (= :typetag.core_test.MyType (tag my-data-type)))
 (is (= :typetag.core_test.MyRecordType (tag my-record-type)))
 (is (= :defmulti (tag different-behavior))))

(deftest clj-function-types-map
  (is (= 
       (dissoc (tag-map #(inc %)) :type)
       {:typetag      :function,
        :all-typetags [:function],
        :lamda?       true
        :fn-ns        "typetag.core_test"}))) 

(deftest clj-collection-types
 (is (= :vector (tag [1 2 3])))
 (is (= :set (tag #{1 2 3})))
 (is (= :map (tag {:a 2 :b 3})))
 (is (= :map (tag {:a 2 :b 3 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 :i 9 :j 10 :k 11 :l 12})))
 (is (= :seq (tag (map inc (range 5)))))
 (is (= :seq (tag (range 10))))
 (is (= :list (tag '(:a :b :c))))
 (is (= :list (tag (list :a :b :c)))))

(deftest clj-collection-types-map
  (is (= 
       (tag-map '(:a :b :c))
       {:typetag      :list,
        :all-typetags [:list :coll],
        :type         clojure.lang.PersistentList})))

(deftest clj-number-types 
 (is (= :Infinity (tag (/ 1.6 0.0))))
 (is (= :-Infinity (tag (/ -1.0 0.0))))
 (is (= :NaN (tag (/ 0.0 0.0))))
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
 (is (= :java.lang.Class (tag java.util.Date))))
