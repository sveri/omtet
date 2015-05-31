(ns de.sveri.omtet.tetris.handlers
  (:require [goog.Timer]
            [goog.events :as ev]
            [re-frame.core :as rf :refer [register-handler, dispatch]]
            [de.sveri.omtet.helper :as h]
            [de.sveri.omtet.tetris.tetriminios :as minios]))

(defn get-clean-db-state []
  {:ctx            (.getContext (h/get-elem "tetris-canv") "2d")
   :grid-state     [[] []]
   :initialized?   true
   :started?       false
   :grid-h         20
   :grid-w         10
   :start-timestep 1000
   :timestep-dim   0.1})

(register-handler
  :initialise-db
  (rf/after grid-changed-mw)
  (fn [_ _]
    (set! (.-onkeydown js/document) keydown)
    (get-clean-db-state)))