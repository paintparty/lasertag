
(ns lasertag.cached)

(def infs
  #?(:cljs
     {##Inf {:tag       :number
             :type      js/Number
             :all-tags  #{:infinite :infinity :int :number :scalar}
             :classname "js/Number"}
      ##-Inf {:tag       :number
              :type      js/Number
              :all-tags  #{:infinite :-infinity :int :number :scalar}
              :classname "js/Number"}}
     :clj
     {##Inf  {:tag       :number
              :type      java.lang.Double
              :all-tags  #{:infinity :infinite :number :double :scalar}
              :classname "java.lang.Double"}
      ##-Inf {:tag       :number
              :type      java.lang.Double
              :all-tags  #{:-infinity :infinite :number :double :scalar}
              :classname "java.lang.Double"}}))

(def NaN
  #?(:cljs
     {:tag       :nan
      :type      js/Number
      :all-tags  #{:nan :number :scalar}
      :classname "js/Number"}
     :clj
     {:tag       :nan
      :type      java.lang.Double
      :all-tags  #{:nan :number :double :scalar}
      :classname "java.lang.Double"}))

(def numbers
  #?(:cljs
     {js/Number {:tag       :number
                 :type      js/Number
                 :all-tags  #{:int :number :scalar}
                 :classname "js/Number"}}

     :clj
     {java.lang.Long   {:tag       :number
                        :type      java.lang.Long
                        :all-tags  #{:int :long :number :scalar}
                        :classname "java.lang.Long"}
      java.lang.Short  {:tag       :number
                        :type      java.lang.Short
                        :all-tags  #{:int
                                     :short
                                     :scalar
                                     :number}
                        :classname "java.lang.Short"}
      java.lang.Float  {:tag       :number
                        :type      java.lang.Float
                        :all-tags  #{:number
                                     :scalar
                                     :float}
                        :classname "java.lang.Float"}
      java.lang.Double {:tag       :number
                        :type      java.lang.Double
                        :all-tags  #{:double
                                     :scalar
                                     :number}
                        :classname "java.lang.Double"}}))


(def by-type-common
  #?(:cljs
     {cljs.core/Keyword            {:tag       :keyword
                                    :type      cljs.core/Keyword
                                    :all-tags  #{:keyword :scalar}
                                    :classname "cljs.core/Keyword"}
      cljs.core/LazySeq            {:tag       :seq
                                    :type      cljs.core/LazySeq
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/LazySeq"}
      cljs.core/List               {:tag       :list
                                    :type      cljs.core/List
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :list
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/List"}
      cljs.core/PersistentArrayMap {:tag       :map
                                    :type      cljs.core/PersistentArrayMap
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :array-map
                                                 :coll-like
                                                 :map-like
                                                 :map
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentArrayMap"}

      cljs.core/PersistentVector   {:tag       :vector
                                    :type      cljs.core/PersistentVector
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-like
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentVector"}
      cljs.core/Symbol             {:tag       :symbol
                                    :type      cljs.core/Symbol
                                    :all-tags  #{:symbol :carries-meta :scalar}
                                    :classname "cljs.core/Symbol"}
      js/Boolean                   {:tag       :boolean
                                    :type      js/Boolean
                                    :all-tags  #{:boolean :scalar}
                                    :classname "js/Boolean"}
      js/Date                      {:tag       :inst
                                    :type      js/Date
                                    :all-tags  #{:inst :literal}
                                    :classname "Date"}
      js/String                    {:tag       :string
                                    :type      js/String
                                    :all-tags  #{:iterable :string :scalar}
                                    :classname "js/String"}}

     :clj
     {clojure.lang.Keyword            {:tag       :keyword
                                       :type      clojure.lang.Keyword
                                       :all-tags  #{:keyword :scalar}
                                       :classname "clojure.lang.Keyword"}


      clojure.lang.Symbol             {:tag       :symbol
                                       :type      clojure.lang.Symbol
                                       :all-tags  #{:symbol :scalar :carries-meta}
                                       :classname "clojure.lang.Symbol"}
      java.lang.String                {:tag       :string
                                       :type      java.lang.String
                                       :all-tags  #{:string :scalar}
                                       :classname "java.lang.String"}

      nil                                {:tag       :nil
                                          :type      nil
                                          :all-tags  #{:nil}
                                          :classname nil}

      java.lang.Boolean               {:tag       :boolean
                                       :type      java.lang.Boolean
                                       :all-tags  #{:boolean :scalar}
                                       :classname "java.lang.Boolean"}

      clojure.lang.PersistentArrayMap {:tag       :map
                                       :type      clojure.lang.PersistentArrayMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :array-map
                                                    :coll-like
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentArrayMap"}

      clojure.lang.PersistentVector   {:tag       :vector
                                       :type      clojure.lang.PersistentVector
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :vector
                                                    :coll-like
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentVector"}
      clojure.lang.PersistentHashMap  {:tag       :map
                                       :type      clojure.lang.PersistentHashMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-like
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentHashMap"}

      clojure.lang.LazySeq            {:tag       :seq
                                       :type      clojure.lang.LazySeq
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-like
                                                    :seq
                                                    :lazy
                                                    :deferred
                                                    :carries-meta}
                                       :classname "clojure.lang.LazySeq"}}))
     

(def by-type
  #?(:cljs
     {cljs.core/Atom               {:tag       :atom
                                    :type      cljs.core/Atom
                                    :all-tags  #{:atom
                                                 :reference}
                                    :classname "cljs.core/Atom"}
      cljs.core/Cons               {:tag       :seq
                                    :type      cljs.core/Cons
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/Cons"}
      cljs.core/IntegerRange       {:tag       :seq
                                    :type      cljs.core/IntegerRange
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/IntegerRange"}
      cljs.core/PersistentQueue    {:tag       :seq
                                    :type      cljs.core/PersistentQueue
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-like
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentQueue"}
      cljs.core/Repeat             {:tag       :seq
                                    :type      cljs.core/Repeat
                                    :all-tags  #{:coll
                                                 :coll-like
                                                 :seq
                                                 :lazy
                                                 :deferred
                                                 :carries-meta}
                                    :classname "cljs.core/Repeat"}
      cljs.core/Subvec             {:tag       :vector
                                    :type      cljs.core/Subvec
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-like
                                                 :carries-meta}
                                    :classname "cljs.core/Subvec"}
      cljs.core/TransientVector    {:tag       :vector
                                    :type      cljs.core/TransientVector
                                    :all-tags  #{:coll
                                                 :vector
                                                 :transient
                                                 :coll-like}
                                    :classname "cljs.core/TransientVector"}
      }
     :clj
     {clojure.lang.Atom              {:tag       :atom
                                      :type      clojure.lang.Atom
                                      :all-tags  #{:atom :reference}
                                      :classname "clojure.lang.Atom"}

      clojure.lang.Cons              {:tag       :seq
                                      :type      clojure.lang.Cons
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.Cons" }



      clojure.lang.LongRange         {:tag       :seq
                                      :type      clojure.lang.LongRange
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :lazy
                                                   :deferred
                                                   :carries-meta}
                                      :classname "clojure.lang.LongRange" }


      clojure.lang.PersistentHashSet {:tag       :set
                                      :type      clojure.lang.PersistentHashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentHashSet" }

      clojure.lang.PersistentList    {:tag       :list
                                      :type      clojure.lang.PersistentList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :list
                                                   :coll-like
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentList" }

      clojure.lang.PersistentTreeMap {:tag       :map
                                      :type      clojure.lang.PersistentTreeMap
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :map-like
                                                   :map
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentTreeMap" }

      clojure.lang.PersistentTreeSet {:tag       :set
                                      :type      clojure.lang.PersistentTreeSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentTreeSet" }


      clojure.lang.Repeat            {:tag       :seq
                                      :type      clojure.lang.Repeat
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :seq
                                                   :lazy
                                                   :deferred
                                                   :carries-meta}
                                      :classname "clojure.lang.Repeat" }

      clojure.lang.Volatile          {:tag       :volatile
                                      :type      clojure.lang.Volatile
                                      :all-tags  #{:volatile :reference}
                                      :classname "clojure.lang.Volatile"}

      java.lang.Character            {:tag       :char
                                      :type      java.lang.Character
                                      :all-tags  #{:char :scalar}
                                      :classname "java.lang.Character"}


      java.util.Date                 {:tag       :inst
                                      :type      java.util.Date
                                      :all-tags  #{:inst :literal}
                                      :classname "java.util.Date"}

      java.util.HashMap              {:tag       :map
                                      :type      java.util.HashMap
                                      :all-tags  #{:coll
                                                   :coll-like
                                                   :map-like
                                                   :map}
                                      :classname "java.util.HashMap" }


      java.util.ArrayList            {:tag       :array
                                      :type      java.util.ArrayList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :array
                                                   :coll-like}
                                      :classname "java.util.ArrayList" }


      java.util.HashSet              {:tag       :set
                                      :type      java.util.HashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-like
                                                   :set
                                                   :set-like}
                                      :classname "java.util.HashSet"}}))


 

;; Maybe these should be syntaxed like below, then use macro to create
;; maps like above
#?(:clj
   [[java.lang.Long
     :number
     #{:int :long}]

    [java.lang.Short
     :number
     #{:int :short}]

    [java.lang.Float
     :number
     #{:number :float}]

    [java.lang.Double
     :number
     #{:double}]])

