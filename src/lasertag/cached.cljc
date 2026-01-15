(ns lasertag.cached)

(def infs
  #?(:cljs
     {##Inf {:tag       :number
             :type      js/Number
             :all-tags  #{:number-type :int :number}
             :classname "js/Number"}
      ##-Inf {:tag       :number
              :type      js/Number
              :all-tags  #{:number-type :int :number}
              :classname "js/Number"}}
     :clj
     {##Inf  {:tag       :infinity
              :type      java.lang.Double
              :all-tags  #{:infinity :java-lang-class}
              :classname "java.lang.Double"}
      ##-Inf {:tag       :-infinity
              :type      java.lang.Double
              :all-tags  #{:-infinity :java-lang-class}
              :classname "java.lang.Double"}}))

(def NaN
  #?(:cljs
     {:tag       :nan
      :type      js/Number
      :all-tags  #{:nan :number :number-type}
      :classname "js/Number"}
     :clj
     {:tag       :nan
      :type      java.lang.Double
      :all-tags  #{:nan :java-lang-class :number-typess}
      :classname "java.lang.Double"}))

(def numbers
  #?(:cljs
     {js/Number {:tag       :number
                  :type      js/Number
                  :all-tags  #{:number-type :int :number}
                  :classname "js/Number"}}

     :clj
     {java.lang.Long   {:tag       :number
                        :type      java.lang.Long
                        :all-tags  #{:number-type
                                     :int
                                     :number
                                     :java-lang-class}
                        :classname "java.lang.Long"}
      java.lang.Short  {:tag       :number
                        :type      java.lang.Short
                        :all-tags  #{:number-type
                                     :int
                                     :number
                                     :java-lang-class}
                        :classname "java.lang.Short"}
      java.lang.Float  {:tag       :number
                        :type      java.lang.Float
                        :all-tags  #{:number-type
                                     :number
                                     :float
                                     :java-lang-class}
                        :classname "java.lang.Float"}
      java.lang.Double {:tag       :number
                        :type      java.lang.Double
                        :all-tags  #{:number-type
                                     :double
                                     :number
                                     :java-lang-class}
                        :classname "java.lang.Double"}}))


(def by-type-common
  #?(:cljs
     {cljs.core/Keyword            {:tag       :keyword
                                    :type      cljs.core/Keyword
                                    :all-tags  #{:keyword}
                                    :classname "cljs.core/Keyword"}
      cljs.core/LazySeq            {:tag       :seq
                                    :type      cljs.core/LazySeq
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/LazySeq"}
      cljs.core/List               {:tag       :seq
                                    :type      cljs.core/List
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :list
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/List"}
      cljs.core/PersistentArrayMap {:tag       :map
                                    :type      cljs.core/PersistentArrayMap
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :array-map
                                                 :coll-type
                                                 :map-like
                                                 :map
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentArrayMap"}

      cljs.core/PersistentVector   {:tag       :vector
                                    :type      cljs.core/PersistentVector
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-type
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentVector"}
      cljs.core/Symbol             {:tag       :symbol
                                    :type      cljs.core/Symbol
                                    :all-tags  #{:symbol :carries-meta}
                                    :classname "cljs.core/Symbol"}
      js/Boolean                   {:tag       :boolean
                                    :type      js/Boolean
                                    :all-tags  #{:boolean}
                                    :classname "js/Boolean"}
      js/Date                      {:tag       :inst
                                    :type      js/Date
                                    :all-tags  #{:inst}
                                    :classname "Date"}
      js/String                    {:tag       :string
                                    :type      js/String
                                    :all-tags  #{:iterable :string}
                                    :classname "js/String"}}

     :clj
     {clojure.lang.Keyword            {:tag       :keyword
                                       :type      clojure.lang.Keyword
                                       :all-tags  #{:keyword}
                                       :classname "clojure.lang.Keyword"}


      clojure.lang.Symbol             {:tag       :symbol
                                       :type      clojure.lang.Symbol
                                       :all-tags  #{:symbol :carries-meta}
                                       :classname "clojure.lang.Symbol"}
      java.lang.String                {:tag       :string
                                       :type      java.lang.String
                                       :all-tags  #{:string}
                                       :classname "java.lang.String"}

      nil                                {:tag       :nil
                                          :type      nil
                                          :all-tags  #{:nil}
                                          :classname nil}

      java.lang.Boolean               {:tag       :boolean
                                       :type      java.lang.Boolean
                                       :all-tags  #{:boolean}
                                       :classname "java.lang.Boolean"}

      clojure.lang.PersistentArrayMap {:tag       :map
                                       :type      clojure.lang.PersistentArrayMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :array-map
                                                    :coll-type
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentArrayMap"}

      clojure.lang.PersistentVector   {:tag       :vector
                                       :type      clojure.lang.PersistentVector
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :vector
                                                    :coll-type
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentVector"}
      clojure.lang.PersistentHashMap  {:tag       :map
                                       :type      clojure.lang.PersistentHashMap
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-type
                                                    :map-like
                                                    :map
                                                    :carries-meta}
                                       :classname "clojure.lang.PersistentHashMap"}

      clojure.lang.LazySeq            {:tag       :seq
                                       :type      clojure.lang.LazySeq
                                       :all-tags  #{:iterable
                                                    :coll
                                                    :coll-type
                                                    :seq
                                                    :carries-meta}
                                       :classname "clojure.lang.LazySeq"}}))
     

(def by-type
  #?(:cljs
     {cljs.core/Atom               {:tag       :cljs.core/Atom
                                    :type      cljs.core/Atom
                                    :all-tags  #{:js-map-like-object
                                                 :cljs.core/Atom
                                                 :coll-type
                                                 :map-like}
                                    :classname "cljs.core/Atom"}
      cljs.core/Cons               {:tag       :seq
                                    :type      cljs.core/Cons
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/Cons"}
      cljs.core/IntegerRange       {:tag       :seq
                                    :type      cljs.core/IntegerRange
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/IntegerRange"}
      cljs.core/PersistentQueue    {:tag       :seq
                                    :type      cljs.core/PersistentQueue
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/PersistentQueue"}
      cljs.core/Repeat             {:tag       :seq
                                    :type      cljs.core/Repeat
                                    :all-tags  #{:coll
                                                 :coll-type
                                                 :seq
                                                 :carries-meta}
                                    :classname "cljs.core/Repeat"}
      cljs.core/Subvec             {:tag       :vector
                                    :type      cljs.core/Subvec
                                    :all-tags  #{:iterable
                                                 :coll
                                                 :vector
                                                 :coll-type
                                                 :carries-meta}
                                    :classname "cljs.core/Subvec"}
      cljs.core/TransientVector    {:tag       :vector
                                    :type      cljs.core/TransientVector
                                    :all-tags  #{:coll
                                                 :vector
                                                 :transient
                                                 :coll-type}
                                    :classname "cljs.core/TransientVector"}
      }
     :clj
     {clojure.lang.Atom              {:tag       :clojure.lang.Atom
                                      :type      clojure.lang.Atom
                                      :all-tags  #{:clojure.lang.Atom}
                                      :classname "clojure.lang.Atom"}

      clojure.lang.Cons              {:tag       :seq
                                      :type      clojure.lang.Cons
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.Cons" }



      clojure.lang.LongRange         {:tag       :seq
                                      :type      clojure.lang.LongRange
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.LongRange" }


      clojure.lang.PersistentHashSet {:tag       :set
                                      :type      clojure.lang.PersistentHashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentHashSet" }

      clojure.lang.PersistentList    {:tag       :seq
                                      :type      clojure.lang.PersistentList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :list
                                                   :coll-type
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentList" }

      clojure.lang.PersistentTreeMap {:tag       :map
                                      :type      clojure.lang.PersistentTreeMap
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :map-like
                                                   :map
                                                   :carries-meta}
                                      :classname "clojure.lang.PersistentTreeMap" }

      clojure.lang.PersistentTreeSet {:tag       :set
                                      :type      clojure.lang.PersistentTreeSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :set
                                                   :carries-meta
                                                   :set-like}
                                      :classname "clojure.lang.PersistentTreeSet" }


      clojure.lang.Repeat            {:tag       :seq
                                      :type      clojure.lang.Repeat
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :coll-type
                                                   :seq
                                                   :carries-meta}
                                      :classname "clojure.lang.Repeat" }

      clojure.lang.Volatile          {:tag       :clojure.lang.Volatile
                                      :type      clojure.lang.Volatile
                                      :all-tags  #{:clojure.lang.Volatile}
                                      :classname "clojure.lang.Volatile"}

      java.lang.Character            {:tag       :char
                                      :type      java.lang.Character
                                      :all-tags  #{:char}
                                      :classname "java.lang.Character"}


      java.util.Date                 {:tag       :inst
                                      :type      java.util.Date
                                      :all-tags  #{:inst :java-util-class}
                                      :classname "java.util.Date"}

      java.util.HashMap              {:tag       :map
                                      :type      java.util.HashMap
                                      :all-tags  #{:coll
                                                   :java-util-class
                                                   :coll-type
                                                   :map-like
                                                   :map}
                                      :classname "java.util.HashMap" }


      java.util.ArrayList            {:tag       :array
                                      :type      java.util.ArrayList
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :array
                                                   :java-util-class
                                                   :coll-type}
                                      :classname "java.util.ArrayList" }


      java.util.HashSet              {:tag       :set
                                      :type      java.util.HashSet
                                      :all-tags  #{:iterable
                                                   :coll
                                                   :java-util-class
                                                   :coll-type
                                                   :set
                                                   :set-like}
                                      :classname "java.util.HashSet"}}))


 

