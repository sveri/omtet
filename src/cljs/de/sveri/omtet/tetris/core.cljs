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
  (let [tet-width "200px"
        initalized? (rf/subscribe [:initalized?])]
    (if initalized?
      [:div.row
       [:div.col-md-3
        [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
        [:div#score {:style {:background-color "#CCCCCC" :width tet-width}} "Score: 0"]]
       [:div.col-md-3
        (let [started? @(rf/subscribe [:started?])
              paused? @(rf/subscribe [:paused?])]
          [:button.btn.btn-primary
           {:on-click
            (cond (not started?) #(rf/dispatch [:start-game])
                  paused? #(rf/dispatch [:unpause-game])
                  (not paused?) #(rf/dispatch [:pause-game]))}
           (cond (not started?) "Start Game" paused? "Continue Game" (not paused?) "Pause Game")])
        ;[:button.btn.btn-primary {:on-click #(rf/dispatch [:stop-game])} "Stop Game"]
        ]]
      [:h4 "Loading..."])))

;(defn init-core []
;  (reagent/create-class
;    {:render              (fn [] [core])
;     ;:component-did-mount #(init-tetris)
;      }))

(defn ^:export main []
  (rf/dispatch [:initialise-db])
  (reagent/render-component (fn [] [core]) (h/get-elem "tetris-main")))
