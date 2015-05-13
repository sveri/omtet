(ns de.sveri.omtet.tetris.cleaner)

(defn in?
  "true if seq contains elm"
  [seq elm]
  (some #(= elm %) seq))

(defn not-in? [seq elm] (not (in? seq elm)))

(defn all-greater-zero-at [v at]
  (not-in? (map #(if (> (nth % at) 0) true false) v) false))

(defn to-be-replaced [v width]
  (reduce (fn [a b] (if (all-greater-zero-at v b) (conj a b) a)) [] (range width)))

(defn remove-at [v at]
  (into [] (concat (subvec v 0 at) (subvec v (+ at 1) (count v)))))

(defn insert-at [v elm at]
  (into [] (concat (subvec v 0 at) elm (subvec v at (count v)))))

(defn remove-and-replace-all-at [v at]
  (map #(insert-at (remove-at % at) [0] at) v))

(defn replace-full-by-zero [v width]
  (reduce (fn [a b] (remove-and-replace-all-at a b)) v (to-be-replaced v width)))

(defn remove-zeros [v at width]
  (reduce (fn [a b] (conj a (remove-at b at))) [] v))

(defn fill-with-zeros [v width]
  (map #(into [] (concat (take (- width (count (first v))) (repeat 0)) %)) v))

(defn doit [v width height]
  (let [fbz (replace-full-by-zero v width)
        tbr (to-be-replaced v width)
        cleaned-grid (loop [acc fbz tbr tbr i 0]
                       (if (empty? tbr)
                         acc
                         (recur (remove-zeros acc (- (first tbr) i) width) (rest tbr) (inc i))))]
    (into [] (fill-with-zeros cleaned-grid height))))
