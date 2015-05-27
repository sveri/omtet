(ns de.sveri.omtet.tetris.tetriminios
  (:require [de.sveri.omtet.helper :as h]))

;(defn get-rand-tetriminio [] {:x 1 :y 1 :o 0 :t 5})
(defn get-rand-tetriminio [] {:x 1 :y 1 :o 0 :t (+ 1 (Math/floor (* 7 (Math/random))))})

(defonce color-map {1 180 2 240 3 40 4 60 5 120 6 280 7 0})

;; drawing related
(defn draw-block-x [ctx & [line-to-fns]]
  (.beginPath ctx)
  (doseq [f line-to-fns] (f))
  (.fill ctx))

(defn draw-block-top [x y ctx]
  (draw-block-x ctx [#(.moveTo ctx x y)
                         #(.lineTo ctx (+ 20 x) y)
                         #(.lineTo ctx (+ 18 x) (+ 2 y))
                         #(.lineTo ctx (+ 2 x) (+ 2 y))]))

(defn draw-block-left [x y ctx]
  (draw-block-x ctx [#(.moveTo ctx x y)
                     #(.lineTo ctx x (+ 20 y))
                     #(.lineTo ctx (+ 2 x) (+ 18 y))
                     #(.lineTo ctx (+ 2 x) (+ 2 y))]))

(defn draw-block-right [x y ctx]
  (draw-block-x ctx [#(.moveTo ctx (+ 20 x) y)
                     #(.lineTo ctx (+ 20 x) (+ 20 y))
                     #(.lineTo ctx (+ 18 x) (+ 18 y))
                     #(.lineTo ctx (+ 18 x) (+ 2 y))]))

(defn draw-block-bottom [x y ctx]
  (draw-block-x ctx [#(.moveTo ctx x (+ 20 y))
                     #(.lineTo ctx (+ 20 x) (+ 20 y))
                     #(.lineTo ctx (+ 18 x) (+ 18 y))
                     #(.lineTo ctx (+ 2 x) (+ 18 y))]))


(defn draw-block [x y h ctx]
  (let [x' (* x 20)
        y' (* y 20)]                                        ; (* 20 (- 19 y))
    (aset ctx "fillStyle" (str "hsl(" h ",100%,50%)"))
    (.fillRect ctx (+ 2 x') (+ 2 y') 16 16)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,70%)"))
    (draw-block-top x' y' ctx)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,40%)"))
    (draw-block-left x' y' ctx)

    (draw-block-right x' y' ctx)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,30%)"))
    (draw-block-bottom x' y' ctx)))

(defn draw-grid [grid ctx]
  (.clearRect ctx 0 0 200 400)
  (doseq [x (range 10)
          y (range 20)]
    (let [t (get-in grid [x y])]
      (when-not (= 0 t)
        (draw-block x y (get color-map t) ctx)))))


;; moving related
(defn is-moving-part-allowed? [x y grid]
  (and (<= 0 x) (< x 10) (<= 0 y) (< y 20) (= 0 (get-in grid [x y]))))

(defn move-x-y [x y rec]
  (let [x' (if (get rec :x) ((get rec :x) x) x)
        y' (if (get rec :y) ((get rec :y) y) y)]
    {:x x' :y y'}))

(defn realize-move [x y t grid] (assoc-in grid [x y] t))

(defn draw-tet [{:keys [x y t o] :as cur-tet} tet-recipe d grid]
  (let [new-position (map #(move-x-y x y %) (get-in tet-recipe [t o]))]
      (reduce (fn [a b] (realize-move (:x b) (:y b) (* t d) a)) grid new-position)))

(defn is-move-allowed? [{:keys [x y t o]} cur-active grid tet-recipe]
  (let [new-position (map #(move-x-y x y %) (get-in tet-recipe [t o]))
        removed-grid (draw-tet cur-active tet-recipe 0 grid)]
    (h/not-in? (map #(is-moving-part-allowed? (:x %) (:y %) removed-grid) new-position) false)))

;1 I 2 L 3 J 4 O 5 S 6 T 7 S
(def tet-recipe
  {1 {0 [{:x #(- % 1)} {} {:x #(+ % 1)} {:x #(+ % 2)}]
      1 [{:x #(+ % 1) :y #(+ % 1)} {:x #(+ % 1)} {:x #(+ % 1) :y #(- % 1)} {:x #(+ % 1) :y #(- % 2)}]
      2 [{:x #(- % 1) :y #(- % 1)} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)} {:x #(+ % 2) :y #(- % 1)}]
      3 [{:y #(+ % 1)} {} {:y #(- % 1)} {:y #(- % 2)}]}
   2 {0 [{:x #(- % 1) :y #(+ % 1)} {:x #(- % 1)} {} {:x #(+ % 1)}]
      1 [{:x #(+ % 1) :y #(+ % 1)} {:y #(+ % 1)} {} {:y #(- % 1)}]
      2 [{:x #(- % 1)} {} {:x #(+ % 1)} {:x #(+ % 1) :y #(- % 1)}]
      3 [{:y #(+ % 1)} {} {:y #(- % 1)} {:x #(- % 1) :y #(- % 1)}]}
   3 {0 [{:x #(- % 1)} {} {:x #(+ % 1)} {:x #(+ % 1) :y #(+ % 1)}]
      1 [{:y #(+ % 1)} {} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]
      2 [{:x #(- % 1) :y #(- % 1)} {:x #(- % 1)} {} {:x #(+ % 1)}]
      3 [{:x #(- % 1) :y #(+ % 1)} {:y #(+ % 1)} {} {:y #(- % 1)}]}
   4 {0 [{} {:x #(+ % 1)} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]
      1 [{} {:x #(+ % 1)} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]
      2 [{} {:x #(+ % 1)} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]
      3 [{} {:x #(+ % 1)} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]}
   5 {0 [{:x #(- % 1)} {} {:y #(+ % 1)} {:x #(+ % 1) :y #(+ % 1)}]
      1 [{:y #(+ % 1)} {} {:x #(+ % 1)} {:x #(+ % 1) :y #(- % 1)}]
      2 [{:x #(- % 1) :y #(- % 1)} {:y #(- % 1)} {} {:x #(+ % 1)}]
      3 [{:x #(- % 1) :y #(+ % 1)} {:x #(- % 1)} {} {:y #(- % 1)}]}
   6 {0 [{:x #(- % 1)} {} {:x #(+ % 1)} {:y #(+ % 1)}]
      1 [{:y #(+ % 1)} {} {:y #(- % 1)} {:x #(+ % 1)}]
      2 [{:x #(- % 1)} {} {:x #(+ % 1)} {} {:y #(- % 1)}]
      3 [{:y #(+ % 1)} {} {:y #(- % 1)} {:x #(- % 1)}]}
   7 {0 [{:x #(- % 1) :y #(+ % 1)} {:y #(+ % 1)} {} {:x #(+ % 1)}]
      1 [{:x #(+ % 1) :y #(+ % 1)} {:x #(+ % 1)} {} {:y #(- % 1)}]
      2 [{:x #(- % 1)} {} {:y #(- % 1)} {:x #(+ % 1) :y #(- % 1)}]
      3 [{:y #(+ % 1)} {} {:x #(- % 1)} {:x #(- % 1) :y #(- % 1)}]}})

; removing full lines
(defn transpose [xs] (apply map vector xs))

(defn wanted? [xs] (some zero? xs))

(defn lpad [n xs] (vec (concat (repeat (- n (count xs)) 0) xs)))

(defn remove-full-lines [xs]
  (let [n (count (first xs))]
    (vec (map (partial lpad n) (transpose (filter wanted? (transpose xs)))))))

(defn count-points [xs]
  (count (filter #(not (some zero? %)) (transpose xs))))