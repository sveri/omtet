(ns de.sveri.omtet.user
  (:require
    [reloaded.repl :refer [go]]
            ;[de.sveri.omtet.dev :refer [start-figwheel]]
            [de.sveri.omtet.components.components :refer [dev-system]])
  )

(defn start-dev-system []
  ;(start-figwheel)
  (go))

(reloaded.repl/set-init! dev-system)

