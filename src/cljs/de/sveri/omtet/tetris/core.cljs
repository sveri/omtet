(ns de.sveri.omtet.tetris.core
  (:require de.sveri.omtet.tetris.handlers
            de.sveri.omtet.tetris.subs
            [cljs.core.async :refer [chan close! put!]]
            [reagent.core :as reagent]
            ;[de.sveri.omtet.tetris.tetriminios :as minios]
            [de.sveri.omtet.helper :as h]
            ;[goog.events :as ev]
            [goog.Timer]
            [re-frame.core :as re-frame])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

;(defn tick []
;  (draw-or-erase-tetriminio 0)
;  (if (minios/draw-tetrimino (update-in @minios/global-var [:y] + 1) -1)
;    (do (swap! minios/global-var update-in [:y] + 1)
;        (draw-or-erase-tetriminio 1))
;    (do
;      (draw-or-erase-tetriminio 1)
;      (reset! minios/grid-state (minios/remove-full-lines @minios/grid-state))
;      (minios/set-rand-tetriminio)
;      (if (minios/draw-tetrimino @minios/global-var -1)
;        (draw-or-erase-tetriminio 1)
;        (do (. timer (stop))
;            (js/alert "Game Over!"))))))

(defn core []
  (let [tet-width "200px"
        footer-stats (re-frame/subscribe [:grid-changed])
        ]

    [:div.row
    [:div.col-md-3
     [:canvas#tetris-canv {:height "400px" :width tet-width :style {:background-color "#444444"}}]
     [:div#score {:style {:background-color "#CCCCCC" :width tet-width}} "Score: 0"]]
    [:div.col-md-3
     [:button.btn.btn-primary {:on-click #(re-frame/dispatch [:start-game])} "Start Game"]
     [:button.btn.btn-primary {:on-click #(re-frame/dispatch [:stop-game])} "Stop Game"]
     ]]))

(defn init-core []
  (reagent/create-class
    {:render              (fn [] [core])
     ;:component-did-mount #(init-tetris)
      }
    ))

(defn ^:export main []
  (re-frame/dispatch [:initialise-db])
  (reagent/render-component (fn [] [init-core]) (h/get-elem "tetris-main")))
