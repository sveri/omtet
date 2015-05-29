(ns de.sveri.omtet.tetris.handlers
  (:require [goog.Timer]
            [goog.events :as ev]
            [re-frame.core :as rf :refer [register-handler, dispatch]]
            [de.sveri.omtet.helper :as h]
            [de.sveri.omtet.tetris.tetriminios :as minios]))

(defn grid-changed-mw [app-state]
  (minios/draw-grid (:grid-state app-state) (:ctx app-state)))

(defn move-on-keypress [app-state move-fn]
  (let [cur-active (:cur-active app-state)
        cur-grid (:grid-state app-state)
        remove-cur-grid (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid)]
    (if (minios/is-move-allowed? (move-fn) cur-active cur-grid minios/tet-recipe)
      (assoc app-state :cur-active (move-fn) :grid-state (minios/draw-tet (move-fn) minios/tet-recipe 1 remove-cur-grid))
      app-state)))

(defn stop-timer [timer]
  (. timer (stop))
  (.disposeInternal timer))

(defn new-timer [timestep]
  (let [timer (goog.Timer. timestep)]
    (ev/listen timer goog.Timer/TICK #(dispatch [:game-sec-tick]))
    timer))

(register-handler
  :move-one-down
  (rf/after grid-changed-mw)
  (fn [app-state _]
    (when (minios/is-move-allowed? (update-in (:cur-active app-state) [:y] + 2) (:cur-active app-state)
                                   (:grid-state app-state) minios/tet-recipe)
      (dispatch [:move-one-down]))
    (move-on-keypress app-state #(update-in (:cur-active app-state) [:y] + 1))))

(register-handler
  :keypressed
  (rf/after grid-changed-mw)
  (fn [app-state [_ e]]
    (condp = (.-keyCode e)
      37 (move-on-keypress app-state #(update-in (:cur-active app-state) [:x] - 1))
      38 (move-on-keypress app-state #(update-in (:cur-active app-state) [:o] (fn [old] (mod (+ 1 old) 4))))
      39 (move-on-keypress app-state #(update-in (:cur-active app-state) [:x] + 1))
      40 (move-on-keypress app-state #(update-in (:cur-active app-state) [:y] + 1))
      32 (do (dispatch [:move-one-down]) app-state)
      80 (if (:paused? app-state) (dispatch [:unpause-game]) (dispatch [:pause-game]))
      app-state)))

(defn keydown [e]
  (when (h/in? [32 37 38 39 40] (.-keyCode e)) (.preventDefault e))
  (dispatch [:keypressed e]))

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

(register-handler
  :start-game
  (rf/after grid-changed-mw)
  (fn [app-state _]
    (let [rand-tet (minios/get-rand-tetriminio)
          empty-grid (mapv #(into [] %) (into [] (take (:grid-w app-state)
                                                       (partition (:grid-h app-state) (iterate identity 0)))))
          one-move-grid (minios/draw-tet rand-tet minios/tet-recipe 1 empty-grid)
          timestep (:start-timestep app-state)
          timer (new-timer timestep)]
      (. timer (start))
      (minios/draw-grid one-move-grid (:ctx app-state))
      (assoc app-state :grid-state one-move-grid :started? true :timer timer :cur-active rand-tet :paused? false
                       :score 0 :lvl 1 :timestep timestep))))

(register-handler
  :restart-game
  (fn [app-state _]
    (stop-timer (:timer app-state))
    (dispatch [:start-game])
    (get-clean-db-state)))

(register-handler
  :pause-game
  (fn [db _]
    (. (:timer db) (stop))
    (assoc db :paused? true)))

(register-handler
  :unpause-game
  (fn [db _]
    (. (:timer db) (start))
    (assoc db :paused? false)))

(register-handler
  :stop-game
  (fn [app-state _]
    (stop-timer (:timer app-state))
    (assoc app-state :started? false)))

(register-handler
  :game-over
  (fn [app-state]
    (dispatch [:stop-game])
    app-state))

(register-handler
  :next-lvl
  (fn [app-state _]
    (let [new-lvl (+ 1 (:lvl app-state))
          new-timestep (- (:timestep app-state) (* (:timestep-dim app-state) (:timestep app-state)))
          timer-new (new-timer new-timestep)]
      (stop-timer (:timer app-state))
      (. timer-new (start))
      (assoc app-state :timer timer-new :lvl new-lvl :timestep new-timestep))))

(defn- move-tick [app-state]
  (let [cur-active (:cur-active app-state)
        cur-grid (:grid-state app-state)
        remove-cur-grid (minios/draw-tet cur-active minios/tet-recipe 0 cur-grid)]

    (if (minios/is-move-allowed? (update-in cur-active [:y] + 1) cur-active remove-cur-grid minios/tet-recipe)
      (let [moved-active (update-in cur-active [:y] + 1)
            moved-grid (minios/draw-tet moved-active minios/tet-recipe 1 remove-cur-grid)]
        (assoc app-state :grid-state moved-grid :cur-active moved-active))
      (let [new-act (minios/get-rand-tetriminio)]
        (if (minios/is-move-allowed? (update-in new-act [:y] + 1) cur-active cur-grid minios/tet-recipe)
          (assoc app-state :cur-active new-act)
          (do (dispatch [:game-over]) app-state))))))

(defn check-lvl [app-state new-points]
  (when (< (* 10 (:lvl app-state)) (+ new-points (:score app-state))) (dispatch [:next-lvl])))

(register-handler
  :game-sec-tick
  (rf/after grid-changed-mw)
  (fn [app-state]
    (let [moved-app-state (move-tick app-state)
          new-points (minios/count-points (:grid-state moved-app-state))]
      (check-lvl moved-app-state new-points)
      (update-in
        (assoc moved-app-state :grid-state (minios/remove-full-lines (:grid-state moved-app-state)))
        [:score] + new-points))))