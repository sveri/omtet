(ns de.sveri.omtet.tetris.subs
  (:require [re-frame.core :as re-frame :refer [register-sub]]
            [de.sveri.omtet.tetris.tetriminios :as minios])
  (:require-macros [reagent.ratom :refer [reaction]]))

(register-sub
  :grid-changed
  (fn [app-state _]
    ;(minios/draw-grid (:grid-state app-state) (:ctx app-state))
    (when (:grid-state @app-state) (minios/draw-grid (:grid-state @app-state) (:ctx @app-state)))
    (println "changed")
    ;(println "grid changed" (:grid-state @app-state))
    ;(reaction
    ;  ;(println "grid changed" (:grid-state @app-state))
    ;  ;(minios/draw-grid (:grid-state app-state) (:ctx app-state))
    ;  ["foo"])
    ))