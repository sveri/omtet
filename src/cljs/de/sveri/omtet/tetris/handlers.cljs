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
    (println "init")

    (set! (.-onkeydown js/document) keydown)
    (let [ctx (->canv-ctx "tetris-canv")
          timer (goog.Timer. 1000)]
      (ev/listen timer goog.Timer/TICK #(dispatch [:game-sec-tick]))
      {:timer      timer
       :ctx        ctx
       :grid-state [[] []]
       :cur-active {:x 3 :y 1 :o 1 :t 4}})))

(register-handler
  :start-game
  (fn [app-state _]
    (. (:timer app-state) (start))
    (minios/init-grid 10 20)
    (assoc app-state :grid-state )
    app-state))

(register-handler
  :stop-game
  (fn [app-state _]
    (. (:timer app-state) (stop))
    app-state))

(defn draw-or-erase-tetriminio [draw-erase]
  (minios/draw-tetrimino @minios/global-var draw-erase))

(register-handler
  :game-sec-tick
  (fn [app-state]
    (draw-or-erase-tetriminio 0)
    (let [cur-tet (:cur-active app-state)]
      (if (minios/draw-tetrimino (update-in cur-tet [:y] + 1) -1)
        (do (swap! app-state update-in [:cur-active :y] + 1)
            (draw-or-erase-tetriminio 1))
        (do
          (draw-or-erase-tetriminio 1)
          (reset! minios/grid-state (minios/remove-full-lines @minios/grid-state))
          (minios/set-rand-tetriminio)
          (if (minios/draw-tetrimino @minios/global-var -1)
            (draw-or-erase-tetriminio 1)
            (do (. (:timer app-state) (stop))
                (js/alert "Game Over!"))))))
    app-state))