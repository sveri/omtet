(ns de.sveri.omtet.tetris.core
  (:require [reagent.core :as reagent]
            [de.sveri.omtet.tetris.tetriminios :as minios]
            [de.sveri.omtet.helper :as h]
            [goog.events :as ev]
            [goog.dom :as dom])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

;timer function
;(events/listen timer goog.Timer/TICK #(game timer state surface))

(def tetriminios-state (atom {}))

(def tet-width "200px")
(def tet-id "tetris-canv")

(defn ->canv-ctx [id]
  (.getContext (h/get-elem id) "2d"))

(def keycodes {27 :escape, 38 :up, 40 :down, 37 :left, 39 :right
             65 :strafel 68 :strafer, 87 :up, 83 :down})

(defn keydown [e]
  (condp = (.-keyCode e)
    40 (do (swap! tetriminios-state update-in [:y] + 1)
           (.preventDefault e))
    38 (do (swap! tetriminios-state update-in [:orientation] (fn [old] (mod (+ 1 old) 4)))
           (.preventDefault e))
    37 (do (swap! tetriminios-state update-in [:x] - 1)
           (.preventDefault e))
    39 (do (swap! tetriminios-state update-in [:x] + 1)
           (.preventDefault e))
    e))

(defn init-tetris []
  (let [ctx (->canv-ctx tet-id)]
    (add-watch tetriminios-state :tetri-state
               (fn [_ _ _ nv]
                 (.clearRect ctx 0 0 200 400)
                 (minios/draw-tetrimino (:x nv)
                                        (:y nv)
                                        (:type nv)
                                        (:orientation nv)
                                        ctx)))
    (reset! tetriminios-state {:x           1 :y 2
                               :type        3
                               ;:type        (Math/floor (* 7 (Math/random)))
                               :orientation 0})


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
