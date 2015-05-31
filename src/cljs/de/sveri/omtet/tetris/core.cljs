(ns de.sveri.omtet.tetris.core
  (:require de.sveri.omtet.tetris.handlers
            de.sveri.omtet.tetris.subs
            [cljs.core.async :refer [chan close! put!]]
            [reagent.core :as reagent]
            [de.sveri.omtet.helper :as h]
            [goog.Timer]
            [re-frame.core :as rf])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

(defn core []
  (let [tet-width "200px"]
    [:div.row
     [:div.col-md-3
      [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
      [:div#score {:style {:background-color "#CCCCCC" :width tet-width}} "Score: "]]
     [:div.col-md-3
      [:div
       [:button.btn.btn-primary
        {:on-click #(rf/dispatch [:start-game])} "Start"]
       [:br]
       [:button.btn.btn-primary {:on-click #(rf/dispatch [:restart-game])} "Restart"]]]]))

(defn ^:export main []
  (rf/dispatch [:initialise-db])
  (reagent/render-component (fn [] [core]) (h/get-elem "tetris-main")))
