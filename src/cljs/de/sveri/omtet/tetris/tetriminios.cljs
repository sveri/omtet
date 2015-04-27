(ns de.sveri.omtet.tetris.tetriminios)

(defonce cyan 180)
(defonce blue 240)
(defonce orange 40)
(defonce yellow 60)
(defonce green 120)
(defonce purple 280)
(defonce red 0)

(defn get-rand-tetriminio []
  {:x           1
   :y 2
   :type        (Math/floor (* 7 (Math/random)))
   :orientation 0})

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
    (aset ctx "fillStyle" (str "hsl(" h ",100%,50%)" ))
    (.fillRect ctx (+ 2 x') (+ 2 y') 16 16)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,70%)" ))
    (draw-block-top x' y' ctx)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,40%)" ))
    (draw-block-left x' y' ctx)

    (draw-block-right x' y' ctx)

    (aset ctx "fillStyle" (str "hsl(" h ",100%,30%)" ))
    (draw-block-bottom x' y' ctx)))

; orientation 0 :Top 1 :Right 2 :Bottom 3 :Left
(defmulti draw-tetrimino (fn [_ _ t o _] [t o]))

(defmethod draw-tetrimino [0 0] [x y _ _ ctx]
  (draw-block (- x 1) y cyan ctx)
  (draw-block x y cyan ctx)
  (draw-block (+ x 1) y cyan ctx)
  (draw-block (+ x 2) y cyan ctx))

(defmethod draw-tetrimino [0 1] [x y _ _ ctx]
  (draw-block (+ x 1) (+ 1 y) cyan ctx)
  (draw-block (+ x 1) y cyan ctx)
  (draw-block (+ x 1) (- y 1) cyan ctx)
  (draw-block (+ x 1) (- y 2) cyan ctx))

(defmethod draw-tetrimino [0 2] [x y _ _ ctx]
  (draw-block (- x 1) (- y 1) cyan ctx)
  (draw-block x (- y 1) cyan ctx)
  (draw-block (+ x 1) (- y 1) cyan ctx)
  (draw-block (+ x 2) (- y 1) cyan ctx))

(defmethod draw-tetrimino [0 3] [x y _ _ ctx]
  (draw-block x (+ 1 y) cyan ctx)
  (draw-block x y cyan ctx)
  (draw-block x (- y 1) cyan ctx)
  (draw-block x (- y 2) cyan ctx))

(defmethod draw-tetrimino [1 0] [x y _ _ ctx]
  (draw-block (- x 1) (+ y 1) blue ctx)
  (draw-block (- x 1) y blue ctx)
  (draw-block x y blue ctx)
  (draw-block (+ x 1) y blue ctx))

(defmethod draw-tetrimino [1 1] [x y _ _ ctx]
  (draw-block (+ x 1) (+ y 1) blue ctx)
  (draw-block x (+ y 1) blue ctx)
  (draw-block x y blue ctx)
  (draw-block x (- y 1) blue ctx))

(defmethod draw-tetrimino [1 2] [x y _ _ ctx]
  (draw-block (- x 1) y blue ctx)
  (draw-block x y blue ctx)
  (draw-block (+ x 1) y blue ctx)
  (draw-block (+ x 1) (- y 1) blue ctx))

(defmethod draw-tetrimino [1 3] [x y _ _ ctx]
  (draw-block x (+ y 1) blue ctx)
  (draw-block x y blue ctx)
  (draw-block x (- y 1) blue ctx)
  (draw-block (- x 1) (- y 1) blue ctx))

(defmethod draw-tetrimino [2 0] [x y _ _ ctx]
  (draw-block (- x 1) y orange ctx)
  (draw-block x y orange ctx)
  (draw-block (+ x 1) y orange ctx)
  (draw-block (+ x 1) (+ y 1) orange ctx))

(defmethod draw-tetrimino [2 1] [x y _ _ ctx]
  (draw-block x (+ y 1) orange ctx)
  (draw-block x y orange ctx)
  (draw-block x (- y 1) orange ctx)
  (draw-block (+ x 1) (- y 1) orange ctx))

(defmethod draw-tetrimino [2 2] [x y _ _ ctx]
  (draw-block (- x 1) (- y 1) orange ctx)
  (draw-block (- x 1) y orange ctx)
  (draw-block x y orange ctx)
  (draw-block (+ x 1) y orange ctx))

(defmethod draw-tetrimino [2 3] [x y _ _ ctx]
  (draw-block (- x 1) (+ y 1) orange ctx)
  (draw-block x (+ y 1) orange ctx)
  (draw-block x y orange ctx)
  (draw-block x (- y 1) orange ctx))

(defmethod draw-tetrimino [3 0] [x y _ _ ctx]
  (draw-block x y yellow ctx)
  (draw-block (+ x 1) y yellow ctx)
  (draw-block x (- y 1) yellow ctx)
  (draw-block (+ x 1) (- y 1) yellow ctx))
(defmethod draw-tetrimino [3 1] [x y t _ ctx] (draw-tetrimino x y t 0 ctx))
(defmethod draw-tetrimino [3 2] [x y t _ ctx] (draw-tetrimino x y t 0 ctx))
(defmethod draw-tetrimino [3 3] [x y t _ ctx] (draw-tetrimino x y t 0 ctx))

(defmethod draw-tetrimino [4 0] [x y _ _ ctx]
  (draw-block (- x 1) y green ctx)
  (draw-block x y green ctx)
  (draw-block x (+ y 1) green ctx)
  (draw-block (+ x 1) (+ y 1) green ctx))

(defmethod draw-tetrimino [4 1] [x y _ _ ctx]
  (draw-block x (+ y 1) green ctx)
  (draw-block x y green ctx)
  (draw-block (+ x 1) y green ctx)
  (draw-block (+ x 1) (- y 1) green ctx))

(defmethod draw-tetrimino [4 2] [x y _ _ ctx]
  (draw-block (- x 1) (- y 1) green ctx)
  (draw-block x (- y 1) green ctx)
  (draw-block x y green ctx)
  (draw-block (+ x 1) y green ctx))

(defmethod draw-tetrimino [4 3] [x y _ _ ctx]
  (draw-block (- x 1) (+ y 1) green ctx)
  (draw-block (- x 1) y green ctx)
  (draw-block x y green ctx)
  (draw-block x (- y 1) green ctx))

(defmethod draw-tetrimino [5 0] [x y _ _ ctx]
  (draw-block (- x 1) y purple ctx)
  (draw-block x y purple ctx)
  (draw-block (+ x 1) y purple ctx)
  (draw-block x (+ y 1) purple ctx))

(defmethod draw-tetrimino [5 1] [x y _ _ ctx]
  (draw-block x (+ y 1) purple ctx)
  (draw-block x y purple ctx)
  (draw-block x (- y 1) purple ctx)
  (draw-block (+ x 1) y purple ctx))

(defmethod draw-tetrimino [5 2] [x y _ _ ctx]
  (draw-block (- x 1) y purple ctx)
  (draw-block x y purple ctx)
  (draw-block (+ x 1) y purple ctx)
  (draw-block x (- y 1) purple ctx))

(defmethod draw-tetrimino [5 3] [x y _ _ ctx]
  (draw-block x (+ y 1) purple ctx)
  (draw-block x y purple ctx)
  (draw-block x (- y 1) purple ctx)
  (draw-block (- x 1) y purple ctx))

(defmethod draw-tetrimino [6 0] [x y _ _ ctx]
  (draw-block (- x 1) (+ y 1) red ctx)
  (draw-block x (+ y 1) red ctx)
  (draw-block x y red ctx)
  (draw-block (+ x 1) y red ctx))

(defmethod draw-tetrimino [6 1] [x y _ _ ctx]
  (draw-block (+ x 1) (+ y 1) red ctx)
  (draw-block (+ x 1) y red ctx)
  (draw-block x y red ctx)
  (draw-block x (- y 1) red ctx))

(defmethod draw-tetrimino [6 2] [x y _ _ ctx]
  (draw-block (- x 1) y red ctx)
  (draw-block x y red ctx)
  (draw-block x (- y 1) red ctx)
  (draw-block (+ x 1) (- y 1) red ctx))

(defmethod draw-tetrimino [6 3] [x y _ _ ctx]
  (draw-block x (+ y 1) red ctx)
  (draw-block x y red ctx)
  (draw-block (- x 1) y red ctx)
  (draw-block (- x 1) (- y 1) red ctx))