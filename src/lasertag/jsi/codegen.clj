(ns lasertag.jsi.codegen
  (:require
   [clojure.walk :as walk]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [clojure.edn :as edn]))


(defmacro with-datafied-demos [m]
  (reduce-kv
     (fn [m k v]
       (assoc m 
              k
              [v (edn/read-string v)]))
     {}
     m))

(defmacro with-stringified-demos [m]
  (reduce-kv
     (fn [m k v]
       (assoc m 
              (with-meta k {:demo (str v)})  
              v))
     {}
     m))

(defmacro with-stringified-demos2 [form]
  (reduce-kv
   (fn [vc _ vcs]
     (conj
      vc
      (reduce
       (fn [m [class-sym v]]
         (assoc m
                [(str class-sym) :tag]
                (:demo v)))
       {}
       vcs)))
   [] 
   form)

  #_(walk/postwalk 
     (fn [x]
       (if-let [demo (and (map? x) (:demo x))]
         (with-meta x {:demo (str demo)})
         x))
     form))
