(ns lasertag.reflect
  "ClojureScript API namespace offering enhanced reflection for JS values"
  (:require 
   [lasertag.cljs-interop :as jsi]))

;; (declare objects-by-method-name)
;; (declare objects-by-unique-method-name)
;; (declare js-built-in-objects)
;; (declare js-built-in-functions)

(defn ^:public built-in? [x]
  (or (contains? js-built-in-objects x)
      (contains? js-built-in-functions x)))

;; (when built-in? {:js-built-in-function? true})
;; (when-not built-in? (js-built-in-method-of x s fn-nm))


(defn- js-built-in-map [o]
  (let [{:keys [sym]} (get jsi/js-built-ins-by-built-in o)]
    {:js-built-in-method-of      o
     :js-built-in-method-of-name (some-> sym name)
     :js-built-in-function?      true}))

(defn ^:public built-in-method-of [x name-prop fn-nm]
  (when 
   (and (string? name-prop)
        (re-find #"[^\$\.-]" name-prop))
    (if-let [built-in-candidates
             (get objects-by-method-name fn-nm)]
      (let [o (first (filter #(= x (aget (.-prototype %) fn-nm)) 
                             built-in-candidates))]
        (js-built-in-map o))
      (when-let [built-in (get objects-by-unique-method-name
                               fn-nm)]
        (js-built-in-map built-in)))))

;; cruft
#_(defn- cljs-fn-args [x fn-args fn-info]
  (when-not fn-args 
    (let [{:keys [_ args]} (get jsi/js-built-ins-by-built-in x)]
      (merge 
       (if args 
         {:js-built-in-function? true
          :fn-args               args}
         (when (:js-built-in-method-of fn-info)
           {:fn-args
            :lasertag/unknown-function-signature-on-js-built-in-method}))))))

