(ns montecarlo.routes.home
  (:require incanter.stats incanter.core incanter.charts incanter.pdf incanter.datasets incanter.excel)
  (:require [montecarlo.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [montecarlo.montecarlo-simulation :as mcsim]
            [selmer.parser :as selmer]

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

;(defn save-chart [] (incanter.core/save (incanter.charts/histogram (incanter.stats/sample-normal 1000)) "chart.png")

;  (defn montecarlosimulation-page [] (selmer.parser/render-file "montecarlosimulation.html" {:image  (save-chart)})))

(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))

(defn get-dates-header [] (unparse-dates (get-dates)))

(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:resultset coll :dates (get-dates-header)}))

(defn simulate-page [ticker] (get-simulation-results (mcsim/start-simulations ticker)))

(defn get-xls [ticker] (incanter.excel/save-xls (incanter.datasets/get-dataset (mcsim/start-simulation ticker)) "D:/montecarlosimulation.xls"))

(defn create-dataset [ticker] (incanter.core/dataset ["col1"](mcsim/start-simulation ticker) :delim \space))

(def ds (incanter.core/dataset ["x1" "x2"] [[1 2] [3 4]]))
(defn create-csv [ticker] (incanter.core/save (incanter.datasets/get-dataset ds) "d.csv"))
(defn download [ticker] (selmer.parser/render-file "montecarlosimulation.html" (create-csv ticker)))

(defn open-chart1 [ticker] (incanter.core/view (incanter.charts/scatter-plot :data (incanter.datasets/get-dataset (create-dataset ticker)) :title "Montecarlo" :x-label "days" :y-label "prices")))

(defn open-chart [ticker] (selmer.parser/render-file "montecarlosimulation.html" (incanter.core/view (incanter.charts/scatter-plot :data (incanter.datasets/get-dataset (incanter.core/dataset ["x1"] [[1] [2]])   :title "Montecarlo" :x-label "days" :y-label "prices")))))

(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)) (POST "/getxls" [ticker2] (download ticker2)) (POST "/getchart" [ticker1] (open-chart ticker1)))

;(incanter.core/save)


;(incanter.core/save (mcsim/start-simulation "goog") "D:/mcs.csv")