(ns de.sveri.omtet.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.omtet.layout :as layout]))

(defn home-page []
  (layout/render "home/index.html"))

(defn contact-page []
  (layout/render "home/contact.html"))

(defn tos-page []
  (layout/render "home/tos.html"))

(defn cookies-page []
  (layout/render "home/cookies.html"))

(defn example-page []
  (layout/render "home/example.html"))

(defroutes home-routes
           (GET "/contact" [] (contact-page))
           (GET "/tos" [] (tos-page))
           (GET "/cookies" [] (cookies-page))
           (GET "/" [] (home-page))
           (GET "/example" [] (example-page)))
