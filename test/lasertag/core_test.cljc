(ns lasertag.core-test
  (:require [lasertag.core :refer [tag tag-map]]
            #?(:cljs [cljs.test :refer [deftest is testing]])
            #?(:clj [clojure.test :refer :all]))
  #?(:clj
     (:import (clojure.lang PersistentVector$TransientVector
                            PersistentHashSet$TransientHashSet
                            PersistentArrayMap$TransientArrayMap
                            PersistentHashMap$TransientHashMap))))


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
          {:tag       :function,
           :lamda?    true,
           :fn-args   '[%1],
           :all-tags  #{:function},
           :classname "Function"})))
   :clj
   (deftest clj-function-types-map
     ;; TODO - Address :classname dissoc
     (is (= 
          (dissoc (dissoc (tag-map #(inc %)) :type)
                  :classname)
          {:tag :function,
           :lamda? true,
           :fn-ns "lasertag.core-test",
           :fn-args
           :lasertag/unknown-function-signature-on-clj-function,
           :all-tags #{:function :carries-meta}}))))

#?(:cljs
   (deftest cljs-elide-function-info
     (is (= 
          (dissoc (tag-map xy {:include-function-info? false}) :type)
          {:tag       :function
           :all-tags  #{:function}
           :classname "Function"})))
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
     (is (= 
          (tag-map '(:a :b :c))
          {:tag       :seq,
           :type      cljs.core/List,
           :all-tags  #{:seq :js/Iterable :coll :list :coll-type :carries-meta},
           :classname "cljs.core/List",
           :coll-size 3})))
   :clj
   (deftest clj-collection-types-map
     (testing "clojure.lang.PersistentList"
       (is (= 
            (tag-map       '(:a :b :c))
            {:tag       :seq,
             :type      clojure.lang.PersistentList,
             :all-tags  #{:coll :list :coll-type :seq :carries-meta},
             :classname "clojure.lang.PersistentList",
             :coll-size 3})))
     (testing "java.util.HashMap"
       (is (= 
            (tag-map       (java.util.HashMap. {"a" 1
                                                "b" 2}))
            {:tag       :map,
             :type      java.util.HashMap,
             :all-tags  #{:coll :java-util-class :coll-type :map-like :map},
             :classname "java.util.HashMap",
             :coll-size 2})))
     (testing "java.util.HashSet"
       (is (= 
            (tag-map       (java.util.HashSet. #{"a" 1
                                                 "b" 2}))
            {:tag       :set,
             :type      java.util.HashSet,
             :all-tags  #{:coll :java-util-class :coll-type :set :set-like},
             :classname "java.util.HashSet",
             :coll-size 4})))
     (testing "java.util.ArrayList"
       (is (= 
            (tag-map       (java.util.ArrayList. [1 2 3]))
            {:tag       :array,
             :type      java.util.ArrayList,
             :all-tags  #{:coll :array :java-util-class :coll-type},
             :classname "java.util.ArrayList",
             :coll-size 3})))

     ))


#?(:cljs
   (deftest clj-transient-collections-map
     (testing "cljs.core/TransientHashSet"
       (is (= 
            (tag-map    (transient #{1 2 3}))
            {:tag       :set,
             :type      cljs.core/TransientHashSet,
             :all-tags  #{:coll :transient :coll-type :set :set-like},
             :classname "cljs.core/TransientHashSet",
             :coll-size 3})))
     (testing "cljs.core/TransientArrayMap"
       (is (= 
            (tag-map    (transient {:a 2 :b 4}))
            {:tag       :map,
             :type      cljs.core/TransientArrayMap,
             :all-tags  #{:coll :transient :coll-type :map :map-like},
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
             :all-tags  #{:map :coll :transient :coll-type :map-like},
             :classname "cljs.core/TransientHashMap",
             :coll-size 10}
            ))))
   :clj
   (deftest clj-transient-collections-map
     (testing "clojure.lang.PersistentHashSet$TransientHashSet"
       (is (= 
            (tag-map    (transient #{1 2 3}))
            {:tag       :set,
             :type      clojure.lang.PersistentHashSet$TransientHashSet,
             :all-tags  #{:coll :transient :coll-type :set :set-like},
             :classname "clojure.lang.PersistentHashSet$TransientHashSet",
             :coll-size 3})))
     (testing "clojure.lang.PersistentArrayMap$TransientArrayMap"
       (is (= 
            (tag-map    (transient {:a 2 :b 4}))
            {:tag       :map,
             :type      clojure.lang.PersistentArrayMap$TransientArrayMap,
             :all-tags  #{:coll :transient :coll-type :map :map-like},
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
             :all-tags  #{:coll :transient :coll-type :map :map-like},
             :classname "clojure.lang.PersistentHashMap$TransientHashMap",
             :coll-size 10})))))




#?(:cljs
   (deftest cljs-elide-all-tagtypes
     (is (= (tag-map '(:a :b :c) {:include-all-tags? false})
            {:tag           :seq,
             :type          cljs.core/List})))
   :clj
   (deftest clj-elide-all-tagtypes 
     (is (= (tag-map       '(:a :b :c) {:include-all-tags? false})
            {:tag           :seq,
             :type          clojure.lang.PersistentList}))))

#?(:cljs
   (deftest cljs-elide-all-tagtypes+alternate-tag-type
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false
                                :format            :string})
          {:tag           "seq",
           :type          cljs.core/List}))
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false
                                :format            :symbol})
          {:tag           'seq,
           :type          cljs.core/List})))
   :clj
   (deftest clj-elide-all-tagtypes+alternate-tag-type
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false
                                :format            :string})
          {:tag           "seq",
           :type          clojure.lang.PersistentList}))
     (is (= 
          (tag-map '(:a :b :c) {:include-all-tags? false
                                :format            :symbol})
          {:tag           'seq,
           :type          clojure.lang.PersistentList}))))


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



