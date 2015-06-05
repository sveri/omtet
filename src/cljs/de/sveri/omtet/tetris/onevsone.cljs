(ns de.sveri.omtet.tetris.onevsone
  (:require [de.sveri.omtet.helper :as h]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn core []
  [:div "ae"]
  (let [tet-width "200px"
        ;initalized? (rf/subscribe [:initalized?])
        score (rf/subscribe [:score])
        ]
    ;(if initalized?
      [:div.row
       [:div.col-md-3
        [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
        ;[:div#score {:style {:background-color "#CCCCCC" :width tet-width}} (str "Score: " @score)]
        ]]
  ;     [:div.col-md-3
  ;      (let [started? @(rf/subscribe [:started?])
  ;            paused? @(rf/subscribe [:paused?])]
  ;        [:div
  ;         [:button.btn.btn-primary
  ;          {:on-click (cond (not started?) #(rf/dispatch [:start-game])
  ;                           paused? #(rf/dispatch [:unpause-game])
  ;                           (not paused?) #(rf/dispatch [:pause-game]))}
  ;          (cond (not started?) "Start" paused? "Continue" (not paused?) "Pause")]
  ;         [:br]
  ;         [:button.btn.btn-primary {:on-click #(rf/dispatch [:restart-game])} "Restart"]])]]
  ;
  ;    [:h4 "Loading..."]
      ;)
    )
  )


(defn ^:export main []
  ;(rf/dispatch [:initialise-db])
  (reagent/render-component (fn [] [core]) (h/get-elem "tetris-main")))
