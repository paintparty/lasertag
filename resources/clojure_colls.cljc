;; Check kondo lintings on the cljs things
;; Generate some tests from this with provided examples
;; 



{"Sequential Collections"
 {"Vectors"
  [{:type        #?(:clj clojure.lang.PersistentVector :cljs cljs.core/PersistentVector)
    :classname   #?(:clj "clojure.lang.PersistentVector" :cljs "cljs.core/PersistentVector")
    :description "Standard vector"
    :example     "[1 2 3]"
    :priority?   true
    :lazy        false}
   {:type        #?(:clj clojure.lang.PersistentVector$ChunkedSeq :cljs cljs.core/ChunkedSeq)
    :classname   #?(:clj "clojure.lang.PersistentVector$ChunkedSeq" :cljs "cljs.core/ChunkedSeq")
    :description "Sequence over a vector"
    :lazy        false}
   {:type        #?(:clj clojure.lang.SubVector :cljs cljs.core/Subvec)
    :classname   #?(:clj "clojure.lang.SubVector" :cljs "cljs.core/Subvec")
    :priority?   true
    :description "Result of subvec"
    :example     "(subvec [1 2 3 4] 1 3)"
    :lazy        false}
   {:type        #?(:clj clojure.lang.MapEntry :cljs cljs.core/MapEntry)
    :priority?   true
    :classname   #?(:clj "clojure.lang.MapEntry" :cljs "cljs.core/MapEntry")
    :description "Key-value pairs (technically a vector)"
    :lazy        false}]

  "Lists"
  [{:type        #?(:clj clojure.lang.PersistentList :cljs cljs.core/List)
    :classname   #?(:clj "clojure.lang.PersistentList" :cljs "cljs.core/List")
    :description "Standard list"
    :example     "'(1 2 3)"
    :lazy        false}
   {:type        #?(:clj clojure.lang.PersistentList$EmptyList :cljs cljs.core/EmptyList)
    :classname   #?(:clj "clojure.lang.PersistentList$EmptyList" :cljs "cljs.core/EmptyList")
    :priority?   true
    :description "The empty list"
    :example     "'()"
    :lazy        false}
   {:type        #?(:clj clojure.lang.Cons :cljs cljs.core/Cons)
    :classname   #?(:clj "clojure.lang.Cons" :cljs "cljs.core/Cons")
    :description "Result of cons"
    :example     "(cons 1 '(2 3))"
    :lazy        false}]

  "Lazy Sequences"
  [{:type        #?(:clj clojure.lang.LazySeq :cljs cljs.core/LazySeq)
    :classname   #?(:clj "clojure.lang.LazySeq" :cljs "cljs.core/LazySeq")
    :description "Lazy sequences from map, filter, etc."
    :priority?   true
    :example     "(map inc [1 2 3])"
    :lazy        true}
   {:type        #?(:clj clojure.lang.Cycle :cljs cljs.core/Cycle)
    :classname   #?(:clj "clojure.lang.Cycle" :cljs "cljs.core/Cycle")
    :description "From cycle"
    :example     "(cycle [1 2 3])"
    :lazy        true}
   {:type        #?(:clj clojure.lang.Iterate :cljs cljs.core/Iterate)
    :classname   #?(:clj "clojure.lang.Iterate" :cljs "cljs.core/Iterate")
    :description "From iterate"
    :example     "(iterate inc 0)"
    :lazy        true}
   {:type        #?(:clj clojure.lang.Repeat :cljs cljs.core/Repeat)
    :classname   #?(:clj "clojure.lang.Repeat" :cljs "cljs.core/Repeat")
    :description "From repeat"
    :example     "(repeat 5 :x)"
    :lazy        true}
   {:type        #?(:clj clojure.lang.Range :cljs cljs.core/Range)
    :classname   #?(:clj "clojure.lang.Range" :cljs "cljs.core/Range")
    :description "From range"
    :example     "(range 10)"
    :priority?   true
    :lazy        true}
   {:type        #?(:clj clojure.lang.Concat :cljs cljs.core/Concat)
    :classname   #?(:clj "clojure.lang.Concat" :cljs "cljs.core/Concat")
    :description "From concat"
    :example     "(concat [1 2] [3 4])"
    :priority?   true
    :lazy        true}]

  "Other Sequences"
  [{:type        #?(:clj clojure.lang.ArraySeq :cljs cljs.core/IndexedSeq)
    :classname   #?(:clj "clojure.lang.ArraySeq" :cljs "cljs.core/IndexedSeq")
    :description "Sequence over Java arrays (CLJ) or indexed collections (CLJS)"
    :lazy        false}
   {:type        #?(:clj clojure.lang.StringSeq :cljs cljs.core/StringSeq)
    :classname   #?(:clj "clojure.lang.StringSeq" :cljs "cljs.core/StringSeq")
    :description "Sequence over strings"
    :example     "(seq \"hello\")"
    :lazy        false}
   {:type        #?(:clj clojure.lang.IteratorSeq :cljs nil)
    :classname   #?(:clj "clojure.lang.IteratorSeq" :cljs nil)
    :description "Sequence over Java iterators (CLJ only)"
    :lazy        false}
   {:type        #?(:clj clojure.lang.EnumerationSeq :cljs nil)
    :classname   #?(:clj "clojure.lang.EnumerationSeq" :cljs nil)
    :description "Sequence over Java enumerations (CLJ only)"
    :lazy        false}
   {:type        #?(:clj clojure.lang.ChunkedCons :cljs cljs.core/ChunkedCons)
    :classname   #?(:clj "clojure.lang.ChunkedCons" :cljs "cljs.core/ChunkedCons")
    :description "Chunked sequences for performance"
    :lazy        false}]}

 "Maps"
 {"Hash Maps"
  [{:type        #?(:clj clojure.lang.PersistentHashMap :cljs cljs.core/PersistentHashMap)
    :classname   #?(:clj "clojure.lang.PersistentHashMap" :cljs "cljs.core/PersistentHashMap")
    :description "Standard hash map"
    :example     "{:a 1 :b 2}"
    :priority?   true
    :transient   false}
   {:type        #?(:clj clojure.lang.PersistentHashMap$TransientHashMap :cljs cljs.core/TransientHashMap)
    :classname   #?(:clj "clojure.lang.PersistentHashMap$TransientHashMap" :cljs "cljs.core/TransientHashMap")
    :description "Transient version"
    :example     "(transient {})"
    :transient   true}
   {:type        #?(:clj clojure.lang.PersistentArrayMap :cljs cljs.core/PersistentArrayMap)
    :classname   #?(:clj "clojure.lang.PersistentArrayMap" :cljs "cljs.core/PersistentArrayMap")
    :description "Small maps (< 9 entries)"
    :priority?   true
    :transient   false}]

  "Sorted Maps"
  [{:type        #?(:clj clojure.lang.PersistentTreeMap :cljs cljs.core/PersistentTreeMap)
    :classname   #?(:clj "clojure.lang.PersistentTreeMap" :cljs "cljs.core/PersistentTreeMap")
    :description "Sorted map"
    :example     "(sorted-map :b 2 :a 1)"
    :priority?   true
    :transient   false
    :sorted      true}
   {:type        #?(:clj clojure.lang.PersistentTreeMap$TransientTreeMap :cljs cljs.core/TransientTreeMap)
    :classname   #?(:clj "clojure.lang.PersistentTreeMap$TransientTreeMap" :cljs "cljs.core/TransientTreeMap")
    :description "Transient version"
    :transient   true
    :sorted      true}]

  "Specialized Maps"
  [{:type        #?(:clj clojure.lang.PersistentStructMap :cljs nil)
    :classname   #?(:clj "clojure.lang.PersistentStructMap" :cljs nil)
    :description "Older struct maps (rarely used, CLJ only)"
    :deprecated  true}]}

 "Sets"
 {"Hash Sets"
  [{:type        #?(:clj clojure.lang.PersistentHashSet :cljs cljs.core/PersistentHashSet)
    :classname   #?(:clj "clojure.lang.PersistentHashSet" :cljs "cljs.core/PersistentHashSet")
    :description "Standard set"
    :example     "#{1 2 3}"
    :priority?   true
    :transient   false}
   {:type        #?(:clj clojure.lang.PersistentHashSet$TransientHashSet :cljs cljs.core/TransientHashSet)
    :classname   #?(:clj "clojure.lang.PersistentHashSet$TransientHashSet" :cljs "cljs.core/TransientHashSet")
    :description "Transient version"
    :example     "(transient #{})"
    :transient   true}]

  "Sorted Sets"
  [{:type        #?(:clj clojure.lang.PersistentTreeSet :cljs cljs.core/PersistentTreeSet)
    :classname   #?(:clj "clojure.lang.PersistentTreeSet" :cljs "cljs.core/PersistentTreeSet")
    :description "Sorted set"
    :example     "(sorted-set 3 1 2)"
    :priority?   true
    :transient   false
    :sorted      true}
   {:type        #?(:clj clojure.lang.PersistentTreeSet$TransientTreeSet :cljs cljs.core/TransientTreeSet)
    :classname   #?(:clj "clojure.lang.PersistentTreeSet$TransientTreeSet" :cljs "cljs.core/TransientTreeSet")
    :description "Transient version"
    :transient   true
    :sorted      true}]}

 "Queues"
 [{:type        #?(:clj clojure.lang.PersistentQueue :cljs cljs.core/PersistentQueue)
   :classname   #?(:clj "clojure.lang.PersistentQueue" :cljs "cljs.core/PersistentQueue")
   :description "Immutable queue"
   :priority?   true
   :example     "clojure.lang.PersistentQueue/EMPTY"}
  {:type        #?(:clj clojure.lang.PersistentQueue$EmptyQueue :cljs cljs.core/PersistentQueue)
   :classname   #?(:clj "clojure.lang.PersistentQueue$EmptyQueue" :cljs "cljs.core/PersistentQueue")
   :description "Empty queue"}]

 ;; What to do with these?
 "Protocols and Abstractions"
 [{:type        #?(:clj clojure.lang.Seqable :cljs cljs.core/ISeqable)
   :classname   #?(:clj "clojure.lang.Seqable" :cljs "cljs.core/ISeqable")
   :description "Protocol for things that can produce sequences"
   :kind        :protocol}
  {:type        #?(:clj clojure.lang.Reversible :cljs cljs.core/IReversible)
   :classname   #?(:clj "clojure.lang.Reversible" :cljs "cljs.core/IReversible")
   :description "For collections that support rseq"
   :kind        :protocol}
  {:type        #?(:clj clojure.lang.Indexed :cljs cljs.core/IIndexed)
   :classname   #?(:clj "clojure.lang.Indexed" :cljs "cljs.core/IIndexed")
   :description "For indexed access (vectors)"
   :kind        :protocol}
  {:type        #?(:clj clojure.lang.Associative :cljs cljs.core/IAssociative)
   :classname   #?(:clj "clojure.lang.Associative" :cljs "cljs.core/IAssociative")
   :description "For associative collections"
   :kind        :protocol}
  {:type        #?(:clj clojure.lang.Sorted :cljs cljs.core/ISorted)
   :classname   #?(:clj "clojure.lang.Sorted" :cljs "cljs.core/ISorted")
   :description "For sorted collections"
   :kind        :protocol}]

 "Java Interop"
 [{:type        #?(:clj java.util.List :cljs nil)
   :classname   #?(:clj "java.util.List" :cljs nil)
   :description "Java lists can be used as sequences (CLJ only)"
   :priority?   true
   :java        true}
  {:type        #?(:clj java.util.Map :cljs nil)
   :classname   #?(:clj "java.util.Map" :cljs nil)
   :description "Java maps can be used (CLJ only)"
   :priority?   true
   :java        true}
  {:type        #?(:clj java.util.Set :cljs nil)
   :classname   #?(:clj "java.util.Set" :cljs nil)
   :description "Java sets can be used (CLJ only)"
   :priority?   true
   :java        true}
  {:type        #?(:clj java.lang.Array :cljs js/Array)
   :classname   #?(:clj "java.lang.Array" :cljs "js/Array")
   :description "Java arrays (CLJ) or JavaScript arrays (CLJS) can be treated as sequences"
   :priority?   true
   :java        true}]

 ;; What to do with these?
 "Transient Collections"
 [{:type        #?(:clj clojure.lang.ITransientVector :cljs cljs.core/ITransientVector)
   :classname   #?(:clj "clojure.lang.ITransientVector" :cljs "cljs.core/ITransientVector")
   :description "Transient vector interface"
   :transient   true}
  {:type        #?(:clj clojure.lang.ITransientMap :cljs cljs.core/ITransientMap)
   :classname   #?(:clj "clojure.lang.ITransientMap" :cljs "cljs.core/ITransientMap")
   :description "Transient map interface"
   :transient   true}
  {:type        #?(:clj clojure.lang.ITransientSet :cljs cljs.core/ITransientSet)
   :classname   #?(:clj "clojure.lang.ITransientSet" :cljs "cljs.core/ITransientSet")
   :description "Transient set interface"
   :transient   true}]

 "Characteristics"
 {:persistent "Immutable with structural sharing"
  :transient  "Temporarily mutable for performance"
  :chunked    "Processes elements in chunks of 32 for efficiency"
  :seq        "Implements the sequence abstraction"
  :lazy       "Elements computed on demand"}

 "Most Commonly Used"
 [#?(:clj clojure.lang.PersistentVector :cljs cljs.core/PersistentVector)
  #?(:clj clojure.lang.PersistentList :cljs cljs.core/List)
  #?(:clj clojure.lang.PersistentHashMap :cljs cljs.core/PersistentHashMap)
  #?(:clj clojure.lang.PersistentHashSet :cljs cljs.core/PersistentHashSet)
  #?(:clj clojure.lang.LazySeq :cljs cljs.core/LazySeq)]}
