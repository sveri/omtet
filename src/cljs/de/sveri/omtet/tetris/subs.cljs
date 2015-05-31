(ns de.sveri.omtet.tetris.subs
  (:require [re-frame.core :as re-frame :refer [register-sub]]
            [de.sveri.omtet.tetris.tetriminios :as minios])
  (:require-macros [reagent.ratom :refer [reaction]]))