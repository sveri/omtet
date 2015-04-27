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

(defn fix-current-add-new []
  (let [cur (:current @tetriminios-state)
        fixed (:fixed @tetriminios-state)
        new-fixed (conj fixed cur)]
    (reset! tetriminios-state {:current (minios/get-rand-tetriminio)
                               :fixed   new-fixed})))

(defn keydown [e]
  (condp = (.-keyCode e)
    40 (do (swap! tetriminios-state update-in [:current :y] + 1)
           (.preventDefault e))
    38 (do (swap! tetriminios-state update-in [:current :orientation] (fn [old] (mod (+ 1 old) 4)))
           (.preventDefault e))
    37 (do (swap! tetriminios-state update-in [:current :x] - 1)
           (.preventDefault e))
    39 (do (swap! tetriminios-state update-in [:current :x] + 1)
           (.preventDefault e))
    (fix-current-add-new)))

(defn init-tetris []
  (let [ctx (->canv-ctx tet-id)]
    (add-watch tetriminios-state :tetri-state
               (fn [_ _ _ nv]
                 (.clearRect ctx 0 0 200 400)
                 (doseq [tet (:fixed nv)] (minios/draw-tetriminio-map tet ctx))
                 (minios/draw-tetriminio-map (:current nv) ctx)))
    (reset! tetriminios-state {:current (minios/get-rand-tetriminio)})


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
