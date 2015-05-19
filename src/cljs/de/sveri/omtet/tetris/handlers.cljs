(ns de.sveri.omtet.tetris.handlers
  (:require [goog.Timer]
            [goog.events :as ev]
            [re-frame.core :refer [register-handler, dispatch]]
            [de.sveri.omtet.helper :as h]
            [de.sveri.omtet.tetris.tetriminios :as minios]))


(defn act-on-keycode [f e]
  ;(draw-or-erase-tetriminio 0)
  (f)
  (.preventDefault e))

(defn keydown [e]
  (println "keydown")
  (condp = (.-keyCode e)
    ;37 (act-on-keycode
    ;     #(when (minios/draw-tetrimino (update-in @minios/global-var [:x] - 1) -1)
    ;       (swap! minios/global-var update-in [:x] - 1))
    ;     e)
    ;38 (act-on-keycode
    ;     #(when (minios/draw-tetrimino (update-in @minios/global-var [:y] (fn [old] (mod (+ 1 old) 4))) -1)
    ;       (swap! minios/global-var update-in [:o] (fn [old] (mod (+ 1 old) 4))))
    ;     e)
    ;39 (act-on-keycode
    ;     #(when (minios/draw-tetrimino (update-in @minios/global-var [:x] + 1) -1)
    ;       (swap! minios/global-var update-in [:x] + 1))
    ;     e)
    ;40 (act-on-keycode
    ;     #(when (minios/draw-tetrimino (update-in @minios/global-var [:y] + 1) -1)
    ;       (swap! minios/global-var update-in [:y] + 1))
    ;     e)
    ;32 (act-on-keycode
    ;     #(while (minios/draw-tetrimino (update-in @minios/global-var [:y] + 1) -1)
    ;       (swap! minios/global-var update-in [:y] + 1))
    ;     e)
    nil)

  ;(draw-or-erase-tetriminio 1)
  )

(defn ->canv-ctx [id]
  (.getContext (h/get-elem id) "2d"))

(register-handler
  :initialise-db
  (fn
    [_ _]
    (set! (.-onkeydown js/document) keydown)
    (let [ctx (->canv-ctx "tetris-canv")
          timer (goog.Timer. 1000)]
      (ev/listen timer goog.Timer/TICK #(dispatch [:game-sec-tick]))
      {:timer      timer
       :ctx        ctx
       :grid-state [[] []]
       :cur-active {:x 3 :y 1 :o 1 :t 1}})))

(register-handler
  :start-game
  (fn [app-state _]
    (. (:timer app-state) (start))
    (assoc app-state :grid-state (mapv #(into [] %) (into [] (take 10 (partition 20 (iterate identity 0))))))))

(register-handler
  :stop-game
  (fn [app-state _]
    (. (:timer app-state) (stop))
    app-state))

;(defn draw-or-erase-tetriminio [draw-erase cur-tet grid]
;  (minios/draw-tetrimino cur-tet draw-erase grid))

(register-handler
  :game-sec-tick
  (fn [app-state]
    ;(println (:grid-state app-state))
    (let [grid (draw-or-erase-tetriminio 0 (:cur-active app-state) (:grid-state app-state))]
      (println grid))
    ;(let [cur-tet (:cur-active app-state)]
    ;  (if (minios/draw-tetrimino (update-in cur-tet [:y] + 1) -1)
    ;    (do (swap! app-state update-in [:cur-active :y] + 1)
    ;        (draw-or-erase-tetriminio 1 (:cur-active app-state) (:grid-state app-state)))
    ;    (do
    ;      (draw-or-erase-tetriminio 1 (:cur-active app-state) (:grid-state app-state))
    ;      (reset! minios/grid-state (minios/remove-full-lines @minios/grid-state))
    ;      (minios/set-rand-tetriminio)
    ;      (if (minios/draw-tetrimino @minios/global-var -1)
    ;        (draw-or-erase-tetriminio 1 (:cur-active app-state) (:grid-state app-state))
    ;        (do (. (:timer app-state) (stop))
    ;            (js/alert "Game Over!"))))))

    ;(minios/draw-grid (:))
    app-state))