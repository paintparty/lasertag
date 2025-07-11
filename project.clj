(defproject io.github.paintparty/lasertag "0.11.4"
  :description "Clojure(Script) utility for discerning types of values."
  :url "https://github.com/paintparty/lasertag"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :repl-options {:init-ns lasertag.core}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :sign-releases false}]])
