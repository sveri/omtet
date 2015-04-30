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

;(defn set-grid [x y t]
;  (swap! grid-state assoc-in [x y] t))
;
;(defn init-grid [w h]
;  (reset! grid-state (mapv #(into [] %) (into [] (take 20 (partition 10 (iterate identity 0)))))))

(defn generate-grid [h w]
  (mapv #(into [] %) (into [] (take h (partition w (iterate identity 0))))))

;(defn init-grid [h w]
;  (reset! grid-state (generate-grid w h)))

;(mapv #(into [] %) your-partitioned-data) , instead of your outer (into [])
(def t-grid [[0 0 1] [ 1 1 1] [ 1 1 1] [1 0 0] ])


(defn r [v]
  (let [clean-v (filter #(some #{0} (into #{} %)) v)]
    (println (count (concat clean-v (generate-grid (- 20 (count clean-v)) 3))))))

[[0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2 2]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 6]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 6 6]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 7 7 6]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 7 7]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 4 4]
 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 4 4]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]
 [0 0 0 0 0 0 0 0 0 0]]