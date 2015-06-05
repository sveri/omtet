(ns de.sveri.omtet.tetris.subs
  (:require [re-frame.core :refer [register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(register-sub :initalized? (fn [db _] (reaction (:initalized? @db))))

(register-sub :started? (fn [db _] (reaction (:started? @db))))

(register-sub :paused? (fn [db _] (reaction (:paused? @db))))

(register-sub :score (fn [db _] (reaction (:score @db))))