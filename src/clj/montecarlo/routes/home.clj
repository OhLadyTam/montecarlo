(ns montecarlo.routes.home
  (:require incanter.stats
            incanter.core
            incanter.charts
            incanter.pdf
            incanter.datasets
            incanter.excel)
  (:require [montecarlo.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [montecarlo.montecarlo-simulation :as mcsim]
            [selmer.parser :as selmer]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.format :as f])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))


(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn montecarlosimulation-page [] (layout/render "montecarlosimulation.html"))

(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

;sort dates of trade - there are 21 days in a month of trade
(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))
(defn get-millis [coll] (loop [i 0 rs []] (if (< i 3) (recur (inc i) (conj rs (c/to-long (nth coll i)))) rs)))
(defn get-dates-header [] (unparse-dates (get-dates)))

;get simulation results in a table
(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:resultset coll :dates (get-dates-header)}))
(defn simulate-page [ticker] (get-simulation-results (mcsim/start-simulations ticker)))


(def vcs-rs-vec [[1 2 3] [4 5 6] [7 8 9] [0 9 8] [7 6 5] [4 3 2]])

;draw chart with simulation results
(defn draw [] (let [tr-mcs (incanter.core/add-column :dates (get-millis (get-dates)) (incanter.core/to-dataset (incanter.core/trans (incanter.core/to-matrix (incanter.core/to-dataset vcs-rs-vec)))))] (doto (incanter.charts/time-series-plot :dates :col-0 :data tr-mcs) incanter.core/view)))

;save chart.png
(defn save-png [] (incanter.core/save (draw) "resources/public/img/chart.png"))

;render chart
(defn draw-res [ticker1] (selmer.parser/render-file "montecarlosimulation.html" (draw)))

;defined routes
(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)) (POST "/getchart" [ticker1] (draw-res ticker1)))

