(ns de.sveri.omtet.tetris.handlers
  (:require [goog.Timer]
            [goog.events :as ev]
            [re-frame.core :refer [register-handler, dispatch]]
            [de.sveri.omtet.helper :as h]
            [de.sveri.omtet.tetris.tetriminios :as minios]))

(defn move-on-keypress [app-state update-fn e]
  (.preventDefault e)
  (let [cur-active (:cur-active app-state)
        cur-grid (:grid-state app-state)
        remove-cur-grid (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid)]

    (if (minios/is-move-allowed? (update-fn) cur-grid minios/tet-recipe)
      (assoc app-state :cur-active (update-fn) :grid-state remove-cur-grid)
      app-state)))

(register-handler
  :keypressed
  (fn [app-state [_ e]]
    (dispatch [:draw-grid])
    (condp = (.-keyCode e)
      37 (move-on-keypress app-state #(update-in (:cur-active app-state) [:x] - 1) e)
      38 (move-on-keypress app-state #(update-in (:cur-active app-state) [:o] (fn [old] (mod (+ 1 old) 4))) e)
      39 (move-on-keypress app-state #(update-in (:cur-active app-state) [:x] + 1) e)
      40 (move-on-keypress app-state #(update-in (:cur-active app-state) [:y] + 1) e)
      app-state)))

(defn keydown [e]
  (dispatch [:keypressed e]))

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
       :cur-active {:x 1 :y 2 :o 1 :t 1}})))

(register-handler
  :start-game
  (fn [app-state _]
    (. (:timer app-state) (start))
    (let [cur-active (:cur-active app-state)
          empty-grid (mapv #(into [] %) (into [] (take 10 (partition 20 (iterate identity 0)))))
          one-move-grid (minios/draw-tet cur-active minios/tet-recipe 1 empty-grid)]
      (dispatch [:draw-grid])
      (assoc app-state :grid-state one-move-grid))))

(register-handler
  :draw-grid
  (fn [app-state _]
    (minios/draw-grid (:grid-state app-state) (:ctx app-state))
    app-state))

(register-handler
  :stop-game
  (fn [app-state _]
    (. (:timer app-state) (stop))
    app-state))

(register-handler
  :game-sec-tick
  (fn [app-state]
    (dispatch [:draw-grid])
    (let [cur-active (:cur-active app-state)
          cur-grid (:grid-state app-state)
          remove-cur-grid (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid)]

      (if (minios/is-move-allowed? (update-in cur-active [:y] + 1) remove-cur-grid minios/tet-recipe)
        (let [moved-active (update-in cur-active [:y] + 1)
              moved-grid (minios/draw-tet moved-active minios/tet-recipe 1 remove-cur-grid)]
          (assoc app-state :grid-state moved-grid :cur-active moved-active))
        (let [new-act (minios/get-rand-tetriminio)]
              (assoc app-state :cur-active new-act)))

      ;(println (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid))

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
      ;(assoc app-state :grid-state (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid))
      )))