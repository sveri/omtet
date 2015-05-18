(ns de.sveri.omtet.tetris.tetriminios)

(defonce color-map {1 180 2 240 3 40 4 60 5 120 6 280 7 0})

(def grid-state (atom [[] []]))
(def global-var (atom {:x 1 :y 1 :o 1 :t 4}))

(defn set-rand-tetriminio []
  (reset! global-var {:x 1 :y 1 :o 0 :t (+ 1 (Math/floor (* 7 (Math/random))))}))

(defn draw-block-top [x y ctx]
  (.beginPath ctx)
  (.moveTo ctx x y)
  (.lineTo ctx (+ 20 x) y)
  (.lineTo ctx (+ 18 x) (+ 2 y))
  (.lineTo ctx (+ 2 x) (+ 2 y))
  (.fill ctx))

(defn draw-block-left [x y ctx]
  (.beginPath ctx)
  (.moveTo ctx x y)
  (.lineTo ctx x (+ 20 y))
  (.lineTo ctx (+ 2 x) (+ 18 y))
  (.lineTo ctx (+ 2 x) (+ 2 y))
  (.fill ctx))

(defn draw-block-right [x y ctx]
  (.beginPath ctx)
  (.moveTo ctx (+ 20 x) y)
  (.lineTo ctx (+ 20 x) (+ 20 y))
  (.lineTo ctx (+ 18 x) (+ 18 y))
  (.lineTo ctx (+ 18 x) (+ 2 y))
  (.fill ctx))

(defn draw-block-bottom [x y ctx]
  (.beginPath ctx)
  (.moveTo ctx x (+ 20 y))
  (.lineTo ctx (+ 20 x) (+ 20 y))
  (.lineTo ctx (+ 18 x) (+ 18 y))
  (.lineTo ctx (+ 2 x) (+ 18 y))
  (.fill ctx))



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


(defn set-grid [x y t grid]
  (if (and (<= 0 x) (< x 10) (<= 0 y) (< y 20))
    (cond
      (< t 0) (= 0 (get-in grid [x y]))
      :else (assoc-in grid [x y] t))
    ;(< t 0) (= 0 (get-in @grid-state [x y]))
    ;:else (do (swap! grid-state assoc-in [x y] t) true))
    false))

;(defn init-grid [w h]
;  (reset! grid-state (mapv #(into [] %) (into [] (take w (partition h (iterate identity 0)))))))

(defn draw-grid [grid ctx]
  (.clearRect ctx 0 0 200 400)
  (doseq [x (range 10)
          y (range 20)]
    (let [t (get-in grid [x y])]
      (when-not (= 0 t)
        (draw-block x y (get color-map t) ctx)))))

; orientation 0 :Top 1 :Right 2 :Bottom 3 :Left
(defmulti draw-tetrimino (fn [{:keys [t o]} _ _] [t o]))

(defmethod draw-tetrimino [1 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid (+ x 2) y (* t d) grid))]
    valid))

(defmethod draw-tetrimino [1 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid (+ x 1) (+ 1 y) (* t d) grid)
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 2) (* t d) grid))]
    valid))

(defmethod draw-tetrimino [1 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (- y 1) (* t d) grid)
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 2) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [1 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ 1 y) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid x (- y 2) (* t d) grid))] valid))

(defmethod draw-tetrimino [2 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid (- x 1) y (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [2 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid (+ x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid x (+ y 1) (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [2 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [2 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (- x 1) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [3 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid (+ x 1) (+ y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [3 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [3 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (- y 1) (* t d) grid)
        valid (and valid (set-grid (- x 1) y (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [3 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid x (+ y 1) (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [4 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid x y (* t d) grid)
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))] valid))
(defmethod draw-tetrimino [4 1] [tet d grid] (draw-tetrimino (assoc tet :o 0) d grid))
(defmethod draw-tetrimino [4 2] [tet d grid] (draw-tetrimino (assoc tet :o 0) d grid))
(defmethod draw-tetrimino [4 3] [tet d grid] (draw-tetrimino (assoc tet :o 0) d grid))

(defmethod draw-tetrimino [5 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (+ y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (+ y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [5 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [5 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (- y 1) (* t d) grid)
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [5 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid (- x 1) y (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [6 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid x (+ y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [6 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [6 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [6 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (- x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [7 0] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid x (+ y 1) (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (+ x 1) y (* t d) grid))] valid))

(defmethod draw-tetrimino [7 1] [{:keys [x y t]} d grid]
  (let [valid (set-grid (+ x 1) (+ y 1) (* t d) grid)
        valid (and valid (set-grid (+ x 1) y (* t d) grid))
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [7 2] [{:keys [x y t]} d grid]
  (let [valid (set-grid (- x 1) y (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid x (- y 1) (* t d) grid))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d) grid))] valid))

(defmethod draw-tetrimino [7 3] [{:keys [x y t]} d grid]
  (let [valid (set-grid x (+ y 1) (* t d) grid)
        valid (and valid (set-grid x y (* t d) grid))
        valid (and valid (set-grid (- x 1) y (* t d) grid))
        valid (and valid (set-grid (- x 1) (- y 1) (* t d) grid))] valid))


; removing full lines
(defn transpose [xs] (apply map vector xs))

(defn wanted? [xs] (some zero? xs))

(defn lpad [n xs] (vec (concat (repeat (- n (count xs)) 0) xs)))

(defn remove-full-lines [xs]
  (let [n (count (first xs))]
    (vec (map (partial lpad n) (transpose (filter wanted? (transpose xs)))))))