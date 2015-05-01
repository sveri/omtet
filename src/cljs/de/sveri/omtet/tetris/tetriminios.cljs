(ns de.sveri.omtet.tetris.tetriminios)

;(defonce cyan 180)
;(defonce blue 240)
;(defonce orange 40)
;(defonce yellow 60)
;(defonce green 120)
;(defonce purple 280)
;(defonce red 0)

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


(defn set-grid [x y t]
  (if (and (<= 0 x) (< x 10) (<= 0 y) (< y 20))
    (cond
      (< t 0) (= 0 (get-in @grid-state [x y]))
      :else (do (swap! grid-state assoc-in [x y] t) true))
    false))

(defn generate-grid [h w]
  (mapv #(into [] %) (into [] (take h (partition w (iterate identity 0))))))

(defn init-grid [h w]
  (reset! grid-state (generate-grid w h)))
  ;(reset! grid-state (mapv #(into [] %) (into [] (take w (partition h (iterate identity 0)))))))

(defn draw-grid [grid ctx]
  (.clearRect ctx 0 0 200 400)
  (doseq [x (range 10)
          y (range 20)]
    (let [t (get-in grid [x y])]
      (when-not (= 0 t))
      (draw-block x y (get color-map t) ctx))))

; orientation 0 :Top 1 :Right 2 :Bottom 3 :Left
(defmulti draw-tetrimino (fn [_ _ t o _] [t o]))

(defmethod draw-tetrimino [1 0] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid (+ x 2) y (* t d)))]
    valid))

(defmethod draw-tetrimino [1 1] [x y t _ d]
  (let [valid (set-grid (+ x 1) (+ 1 y) (* t d))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 2) (* t d)))]
    valid))

(defmethod draw-tetrimino [1 2] [x y t _ d]
  (let [valid (set-grid (- x 1) (- y 1) (* t d))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 2) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [1 3] [x y t _ d]
  (let [valid (set-grid x (+ 1 y) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid x (- y 2) (* t d)))] valid))

(defmethod draw-tetrimino [2 0] [x y t _ d]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d))
        valid (and valid (set-grid (- x 1) y (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [2 1] [x y t _ d]
  (let [valid (set-grid (+ x 1) (+ y 1) (* t d))
        valid (and valid (set-grid x (+ y 1) (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [2 2] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [2 3] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (- x 1) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [3 0] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid (+ x 1) (+ y 1) (* t d)))] valid))

(defmethod draw-tetrimino [3 1] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [3 2] [x y t _ d]
  (let [valid (set-grid (- x 1) (- y 1) (* t d))
        valid (and valid (set-grid (- x 1) y (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [3 3] [x y t _ d]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d))
        valid (and valid (set-grid x (+ y 1) (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [4 0] [x y t _ d]
  (let [valid (set-grid x y (* t d))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))] valid))
(defmethod draw-tetrimino [4 1] [x y t _ d] (draw-tetrimino x y t 0 d))
(defmethod draw-tetrimino [4 2] [x y t _ d] (draw-tetrimino x y t 0 d))
(defmethod draw-tetrimino [4 3] [x y t _ d] (draw-tetrimino x y t 0 d))

(defmethod draw-tetrimino [5 0] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (+ y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (+ y 1) (* t d)))] valid))

(defmethod draw-tetrimino [5 1] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [5 2] [x y t _ d]
  (let [valid (set-grid (- x 1) (- y 1) (* t d))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [5 3] [x y t _ d]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d))
        valid (and valid (set-grid (- x 1) y (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [6 0] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid x (+ y 1) (* t d)))] valid))

(defmethod draw-tetrimino [6 1] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [6 2] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [6 3] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (- x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [7 0] [x y t _ d]
  (let [valid (set-grid (- x 1) (+ y 1) (* t d))
        valid (and valid (set-grid x (+ y 1) (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (+ x 1) y (* t d)))] valid))

(defmethod draw-tetrimino [7 1] [x y t _ d]
  (let [valid (set-grid (+ x 1) (+ y 1) (* t d))
        valid (and valid (set-grid (+ x 1) y (* t d)))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [7 2] [x y t _ d]
  (let [valid (set-grid (- x 1) y (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid x (- y 1) (* t d)))
        valid (and valid (set-grid (+ x 1) (- y 1) (* t d)))] valid))

(defmethod draw-tetrimino [7 3] [x y t _ d]
  (let [valid (set-grid x (+ y 1) (* t d))
        valid (and valid (set-grid x y (* t d)))
        valid (and valid (set-grid (- x 1) y (* t d)))
        valid (and valid (set-grid (- x 1) (- y 1) (* t d)))] valid))