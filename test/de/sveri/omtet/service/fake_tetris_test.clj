(ns de.sveri.omtet.service.fake-tetris-test
  (:require [de.sveri.omtet.service.fake-tetris :as fk]
            [clojure.test :refer :all]))

(deftest two-cols
  (is (= [[0 0] [0 1] [0 3]] (fk/doit [[0 1] [1 1] [3 3]] 2 2))))
