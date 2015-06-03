(ns de.sveri.omtet.routes.tetris
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.omtet.layout :as layout]))

(defroutes
  tetris-routes
  (GET "/single" [] (layout/render "tetris/single.html"))
  (GET "/1vs1" [] (layout/render "tetris/1vs1.html")))
