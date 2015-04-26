(ns de.sveri.omtet.tetris.core
  (:require [reagent.core :as reagent]
            [de.sveri.omtet.tetris.tetriminios :as minios]
            [de.sveri.omtet.helper :as h]))

(def tet-width "200px")
(def tet-id "tetris-canv")

(defn ->canv-ctx [id]
  (.getContext (h/get-elem id) "2d"))

(defn init-tetris []
  (let [ctx (->canv-ctx tet-id)]
    (minios/draw-tetrimino 1 2 :J :T ctx)
    (minios/draw-tetrimino 1 2 :J :R ctx)
    (minios/draw-tetrimino 1 2 :J :B ctx)
    (minios/draw-tetrimino 1 2 :J :L ctx)
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
