(ns lasertag.number-instances-test
  (:require [clojure.test :refer [deftest testing is]]
            [lasertag.core :as lasertag])
  #?(:jolt
     (:import [])
     :bb
     (:import [])
     :clj
     (:import [java.util.concurrent.atomic AtomicInteger AtomicLong
               DoubleAccumulator DoubleAdder LongAccumulator LongAdder]
              [java.math BigDecimal BigInteger]
              [java.util.function DoubleBinaryOperator LongBinaryOperator])) )

#?(:jolt
   nil
   :bb
   nil
   :clj
   (deftest number-subclass-instances-tag-as-number-test
     (testing "All java.lang.Number subclasses/implementations tag as :number"
       (let [instances
             {"AtomicInteger"     (AtomicInteger.)
              "AtomicLong"        (AtomicLong.)
              "BigDecimal"        (BigDecimal. "1.0")
              "BigInteger"        (BigInteger. "1")
              "Byte"              (Byte/valueOf (byte 1))
              "Double"            (Double/valueOf 1.0)
              "DoubleAccumulator" (DoubleAccumulator.
                                   (reify DoubleBinaryOperator
                                     (applyAsDouble [_ a b] (+ a b)))
                                   0.0)
              "DoubleAdder"       (DoubleAdder.)
              "Float"             (Float/valueOf (float 1.0))
              "Integer"           (Integer/valueOf 1)
              "Long"              (Long/valueOf 1)
              "LongAccumulator"   (LongAccumulator.
                                   (reify LongBinaryOperator
                                     (applyAsLong [_ a b] (+ a b)))
                                   0)
              "LongAdder"         (LongAdder.)
              "Short"             (Short/valueOf (short 1))}]
         (doseq [[class-name instance] instances]
           (testing class-name
             (is (= :number (lasertag/tag instance))
                 (str class-name " instance should tag as :number, got class "
                      (.getName (class instance))))))))))

