(ns de.sveri.omtet.routes.tetris
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.omtet.layout :as layout]))

(defroutes
  tetris-routes
  (GET "/tetris" [] (layout/render "tetris/index.html")))
