(ns de.sveri.omtet.tetris.subs
  (:require [re-frame.core :as re-frame :refer [register-sub]]
            [de.sveri.omtet.tetris.tetriminios :as minios])
  (:require-macros [reagent.ratom :refer [reaction]]))

;(register-sub
;  :grid-changed
;  (fn [app-state _]
;    (when (:grid-state @app-state) (minios/draw-grid (:grid-state @app-state) (:ctx @app-state)))))
;
;(let [source (re-frame/subscribe [:grid-changed])]
;  (reagent.ratom/run! (println "foo" @source)))

(register-sub :initalized? (fn [db _] (reaction (:initalized? @db))))

;(register-sub :running? (fn [db _] (reaction (and (not (:paused? @db)) (:running? @db)))))
(register-sub :started? (fn [db _] (reaction (:started? @db))))

(register-sub :paused? (fn [db _] (reaction (:paused? @db))))