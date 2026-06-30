(ns lasertag.jsi.codegen
  (:require [clojure.edn :as edn]))


(defmacro with-datafied-demos [m]
  (reduce-kv
     (fn [m k v]
       (assoc m 
              k
              [v (edn/read-string v)]))
     {}
     m))
