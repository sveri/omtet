(ns de.sveri.omtet.cljc.helper)


(defn in?
  "true if seq contains elm"
  [seq elm]
  (some #(= elm %) seq))

(defn not-in?
  "true if seq not contains elm"
  [seq elm]
  (not (in? seq elm)))  