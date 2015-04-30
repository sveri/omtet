(ns de.sveri.omtet.session
  (:require [noir.session :refer [clear-expired-sessions]]
            [cronj.core :refer [cronj]]))

(def cleanup-job
  (cronj
    :entries
    [{:id "session-cleanup"
      :handler (fn [_ _] (clear-expired-sessions))
      :schedule "* /30 * * * * *"
      :opts {}}]))


(def grid-state (atom [[] []]))

(defn set-grid [x y t]
  (swap! grid-state assoc-in [x y] t))

(defn init-grid [w h]
  (reset! grid-state (mapv #(into [] %) (into [] (take 20 (partition 10 (iterate identity 0)))))))

;(mapv #(into [] %) your-partitioned-data) , instead of your outer (into [])
(def t-grid [[0 0 1] [ 1 1 1] [ 1 1 1] [1 0 0] ])


(defn r [v]
  (let [clean-v (filter #(some #{0} (into #{} %)) v)]
    (println (count (concat clean-v (generate-grid (- 20 (count clean-v)) 3))))))