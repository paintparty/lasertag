(ns lasertag.messaging)

(defn- yellow [s]
  (str #?(:cljs nil :clj "\033[38;5;136m") s #?(:cljs nil :clj "\033[0;m")))

(defn- orange [s]
  (str #?(:cljs nil :clj"\033[38;5;208;1m") s #?(:cljs nil :clj "\033[0;m")))

(defn- italic [s]
  (str #?(:cljs nil :clj "\033[3m") s #?(:cljs nil :clj "\033[0;m")))

(defn- bold [s]
  (str #?(:cljs nil :clj "\033[1m") s #?(:cljs nil :clj "\033[0;m")))

(defn print-unknown-coll-size-warning [e]
  (println 
   (str (orange "══")
        (bold " Exception (Caught): lasertag.core/all-tags ")
        (orange "════════════════") 
        "\n\n"
        (italic "Error Description:")"\n\n"
        "  Problem determining the size of a collection"
        "\n\n\n"
        (italic "Error Message, from Clojure:")"\n\n"
        #?(:cljs
           (.-message e)
           :clj
           (str "  " (.getMessage e))) 
        "\n\n\n"
        (italic "You are most like seeing this as a result of using the API\n")
        (italic "of Fireworks or Bling. Please consider filing a ticket\n")
        (italic "that includes the following:\n\n")
        "  - This error message\n"
        "  - An example (or contrived example) of the val to be printed\n"
        "  - The val's class or type, if you know it.\n"
        "  - Which context? JVM Clojure, bb, cljs browser, or cljs node?"
        "\n\n"
        (italic "https://github.com/paintparty/lasertag/issues")
        "\n\n"
        (orange "───────────────────────────────────────────────────────────────"))))

(defn mock-unknown-coll-size [x]
  #?(:cljs
     (do (println "ClojureScript :: lasertag.core/all-tags")
         (println x)
         (println "\n")
         (try (new js/Error (str :lasertag.core/unknown-coll-size))
              (catch js/Object e
                (print-unknown-coll-size-warning e)
                :lasertag.core/unknown-coll-size))
         :lasertag.core/unknown-coll-size)
     :clj
     (do (println "JVM Clojure :: lasertag.core/all-tags")
         (println x)
         (println "\n")
         (try (throw (Throwable. (str :lasertag.core/unknown-coll-size)))
              (catch Throwable e
                (print-unknown-coll-size-warning e)
                :lasertag.core/unknown-coll-size))
         :lasertag.core/unknown-coll-size)))
