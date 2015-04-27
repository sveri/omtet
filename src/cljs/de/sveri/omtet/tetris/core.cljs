(ns de.sveri.omtet.tetris.core
  (:require [cljs.core.async :refer [chan close! put!]]
            [reagent.core :as reagent]
            [de.sveri.omtet.tetris.tetriminios :as minios]
            [de.sveri.omtet.helper :as h]
            [goog.events :as ev]
            [goog.dom :as dom])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

;timer function
;(events/listen timer goog.Timer/TICK #(game timer state surface))

(def tet-width "200px")
(def tet-id "tetris-canv")

(defn ->canv-ctx [id]
  (.getContext (h/get-elem id) "2d"))

(defn erase-tetriminio []
  (minios/draw-tetrimino (:x @minios/global-var) (:y @minios/global-var)
                         (:t @minios/global-var) (:o @minios/global-var) 0))

(defn draw-state-tetriminio []
  (minios/draw-tetrimino (:x @minios/global-var) (:y @minios/global-var)
                         (:t @minios/global-var) (:o @minios/global-var) 1))

(defn keydown [e]
  (condp = (.-keyCode e)
    37 (do (erase-tetriminio )
           (swap! minios/global-var update-in [:x] - 1)
           (.preventDefault e))
    38 (do (erase-tetriminio )
           (swap! minios/global-var update-in [:o] (fn [old] (mod (+ 1 old) 4)))
           (.preventDefault e))
    39 (do (erase-tetriminio )
           (swap! minios/global-var update-in [:x] + 1)
           (.preventDefault e))
    40 (do (erase-tetriminio )
           (swap! minios/global-var update-in [:y] + 1)
           (.preventDefault e))
    e)
  (draw-state-tetriminio))

(defn init-tetris []
  (let [ctx (->canv-ctx tet-id)]

    (add-watch minios/grid-state :grid-state
               (fn [_ _ _ nv]
                 (minios/draw-grid nv ctx)))

      (minios/init-grid 10 20)
    (draw-state-tetriminio)
    (set! (.-onkeydown js/document) keydown)

    ;might be better
    ;(defn listen [el type]  (let [out (chan)] (events/listen el type (fn [e] (put! out e))) out))
    ))

(defn core []
  [:div
   [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
   [:div#score {:style {:background-color "#CCCCCC" :width tet-width}} "Score: 0"]])

(defn init-core []
  (reagent/create-class
    {:render              (fn [] [core])
     :component-did-mount #(init-tetris)}))

(defn ^:export main []
  (reagent/render-component (fn [] [init-core]) (h/get-elem "tetris-main")))
