(ns de.sveri.omtet.tetris.core
  (:require [cljs.core.async :refer [chan close! put!]]
            [reagent.core :as reagent]
            [de.sveri.omtet.tetris.tetriminios :as minios]
            [de.sveri.omtet.helper :as h]
            [goog.events :as ev]
            [goog.dom :as dom]
            [de.sveri.omtet.tetris.cleaner :as cleaner]
            [goog.Timer])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

(defonce tet-width "200px")
(defonce tet-id "tetris-canv")

(defonce timer (goog.Timer. 1000))

(defn ->canv-ctx [id]
  (.getContext (h/get-elem id) "2d"))

(defn draw-or-erase-tetriminio [draw-erase]
  (minios/draw-tetrimino (:x @minios/global-var) (:y @minios/global-var)
                         (:t @minios/global-var) (:o @minios/global-var) draw-erase))

(defn add-new-tetriminio []
  (minios/set-rand-tetriminio)
  (draw-or-erase-tetriminio 1))

(defn act-on-keycode [f e]
  (draw-or-erase-tetriminio 0)
  (f)
  (.preventDefault e))

(defn keydown [e]
  (condp = (.-keyCode e)
    37 (act-on-keycode
         #(when (minios/draw-tetrimino (- (:x @minios/global-var) 1) (:y @minios/global-var) (:t @minios/global-var)
                                       (:o @minios/global-var) -1)
           (swap! minios/global-var update-in [:x] - 1))
         e)
    38 (act-on-keycode
         #(when (minios/draw-tetrimino (:x @minios/global-var) (:y @minios/global-var) (:t @minios/global-var)
                                       (mod (+ 1 (:o @minios/global-var)) 4) -1)
           (swap! minios/global-var update-in [:o] (fn [old] (mod (+ 1 old) 4))))
         e)
    39 (act-on-keycode
         #(when (minios/draw-tetrimino (+ (:x @minios/global-var) 1) (:y @minios/global-var) (:t @minios/global-var)
                                       (:o @minios/global-var) -1)
           (swap! minios/global-var update-in [:x] + 1))
         e)
    40 (act-on-keycode
         #(when (minios/draw-tetrimino (:x @minios/global-var) (+ (:y @minios/global-var) 1) (:t @minios/global-var)
                                       (:o @minios/global-var) -1)
           (swap! minios/global-var update-in [:y] + 1))
         e)
    32 (act-on-keycode
         #(while (minios/draw-tetrimino (:x @minios/global-var) (+ (:y @minios/global-var) 1) (:t @minios/global-var)
                                        (:o @minios/global-var) -1)
           (swap! minios/global-var update-in [:y] + 1))
         e)
    (add-new-tetriminio))
  (draw-or-erase-tetriminio 1))

(defn tick []
  (draw-or-erase-tetriminio 0)
  (if (minios/draw-tetrimino (:x @minios/global-var) (+ (:y @minios/global-var) 1) (:t @minios/global-var)
                             (:o @minios/global-var) -1)
    (do (swap! minios/global-var update-in [:y] + 1)
        (draw-or-erase-tetriminio 1))
    (do
      (draw-or-erase-tetriminio 1)
      (reset! minios/grid-state (cleaner/doit @minios/grid-state 20 20))
      (minios/set-rand-tetriminio)
      (if (minios/draw-tetrimino (:x @minios/global-var) (:y @minios/global-var) (:t @minios/global-var)
                                 (:o @minios/global-var) -1)
        (draw-or-erase-tetriminio 1)
        (do (. timer (stop))
            (js/alert "Game Over!"))))))

(defn init-tetris []
  (let [ctx (->canv-ctx tet-id)]
    (add-watch minios/grid-state :grid-state
               (fn [_ _ _ nv]
                 (println "draw grid")
                 (minios/draw-grid nv ctx)))

    (set! (.-onkeydown js/document) keydown)


    ;might be better
    ;(defn listen [el type]  (let [out (chan)] (events/listen el type (fn [e] (put! out e))) out))
    ))

(defn start-game []
  (. timer (stop))
  (minios/init-grid 10 20)
  ;(draw-or-erase-tetriminio 1)
  (minios/set-rand-tetriminio)
  (ev/listen timer goog.Timer/TICK tick)
  (. timer (start))
  )

(defn core []
  [:div.row
   [:div.col-md-3
    [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
    [:div#score {:style {:background-color "#CCCCCC" :width tet-width}} "Score: 0"]]
   [:div.col-md-3
    [:button.btn.btn-primary {:on-click start-game} "Start Game"]
    [:button.btn.btn-primary {:on-click #(. timer (stop))} "Stop Game"]]])

(defn init-core []
  (reagent/create-class
    {:render              (fn [] [core])
     :component-did-mount #(init-tetris)}))

(defn ^:export main []
  (reagent/render-component (fn [] [init-core]) (h/get-elem "tetris-main")))
