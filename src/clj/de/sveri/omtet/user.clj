(ns de.sveri.omtet.user
  (:require [reloaded.repl :refer [go]]
            [de.sveri.omtet.components.components :refer [dev-system]]))

(defn start-dev-system []
  (go))

(reloaded.repl/set-init! dev-system)

