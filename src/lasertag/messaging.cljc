(ns lasertag.messaging
  (:require [clojure.string :as string]))

(defn- yellow [s]
  (str #?(:cljs nil :clj "\033[38;5;136m") s #?(:cljs nil :clj "\033[0;m")))

(defn- orange [s]
  (str #?(:cljs nil :clj"\033[38;5;208;1m") s #?(:cljs nil :clj "\033[0;m")))

(defn- italic [s]
  (str #?(:cljs nil :clj "\033[3m") s #?(:cljs nil :clj "\033[0;m")))

(defn- bold [s]
  (str #?(:cljs nil :clj "\033[1m") s #?(:cljs nil :clj "\033[0;m")))


(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn- regex? [v]
  #?(:clj  (-> v type str (= "class java.util.regex.Pattern"))
     :cljs (-> v type str (= "#object[RegExp]"))))

(defn- surround-with-quotes [x]
  (str "\"" x "\""))

(defn shortened
  "Stringifies a value and truncates the result with ellipsis 
   so that it fits on one line."
  [v limit]
  (let [as-str         (str v)
        regex?         (regex? v)
        double-quotes? (or (string? v) regex?)
        regex-pound    #?(:cljs nil :clj (when regex? "#"))]
    (if (> limit (count as-str))
      (if double-quotes?
        (str regex-pound (surround-with-quotes as-str))
        as-str)
      (let [ret* (-> as-str
                     (string/split #"\n")
                     first)
            ret  (if (< limit (count ret*))
                   (let [ret (->> ret*
                                  (take limit)
                                  string/join)]
                     (str (if double-quotes?
                            (str regex-pound (surround-with-quotes ret))
                            ret)
                          (when-not double-quotes? " ")
                          "..."))
                   ret*)]
        ret))))

(defn- supplied-value-section [x kw s]
  (when-not (= x kw)
    (str "\n\n\n"
         (italic s) "\n\n"
         "  "
         (cond
           (nil? x) 
           "nil"
           (string? x)
           (str "\"" x "\"")
           :else
           x))))

(defn ^:public ^:no-doc print-warning! 
  [e {:keys [fqns-fn desc supplied-value supplied-opts]
      :or   {supplied-value :lasertag.messaging/supplied-value-missing
             supplied-opts :lasertag.messaging/supplied-opts-missing}}]
  (println 
   (str (orange "══")
        (bold (str " Exception (Caught): " fqns-fn " "))
        (orange "════════════════") 
        "\n\n"
        (italic "Error Description:") "\n\n"
        "  "
        desc
        (supplied-value-section supplied-value
                                :lasertag.messaging/supplied-value-missing
                                "Supplied value:")
        (supplied-value-section supplied-opts
                                :lasertag.messaging/supplied-opts-missing
                                "Supplied opts:")
        "\n\n\n"
        (italic "Error Message, from Clojure:") "\n\n"
        "  "
        #?(:cljs (.-message e) :clj (.getMessage e)) 
        "\n\n\n"
        (italic "You are most like seeing this as a result of using the API\n")
        (italic "of Fireworks, Bling, or Lasertag. Please consider submitting an issue\n")
        (italic "that includes the following:\n\n")
        "  - This error message\n"
        "  - An example (or contrived example) of the val to be printed\n"
        "  - The val's class or type, if you know it.\n"
        "  - Which context? JVM Clojure, bb, cljs browser, or cljs node?"
        "\n\n"
        (italic "https://github.com/paintparty/lasertag/issues")
        "\n\n"
        (orange "───────────────────────────────────────────────────────────────"))))


(defn ^:public ^:no-doc print-unknown-coll-size-warning! [e]
  (print-warning! e {:desc    "Problem determining the size of a collection"
                     :fqns-fn "lasertag.core/coll-size*"}))


(defn ^:public ^:no-doc mock-unknown-coll-size [x]
  #?(:cljs
     (do (println "ClojureScript :: lasertag.core/all-tags")
         (println x)
         (println "\n")
         (try (new js/Error (str :lasertag.core/unknown-coll-size))
              (catch js/Object e
                (print-unknown-coll-size-warning! e)
                :lasertag.core/unknown-coll-size))
         :lasertag.core/unknown-coll-size)
     :clj
     (do (println "JVM Clojure :: lasertag.core/all-tags")
         (println x)
         (println "\n")
         (try (throw (Throwable. (str :lasertag.core/unknown-coll-size)))
              (catch Throwable e
                (print-unknown-coll-size-warning! e)
                :lasertag.core/unknown-coll-size))
         :lasertag.core/unknown-coll-size)))
