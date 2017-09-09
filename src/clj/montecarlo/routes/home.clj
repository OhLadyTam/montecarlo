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
(defn montecarlochart-page [] (layout/render "montecarlochart.html"))

(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

;sort dates of trade - there are 21 days in a month of trade
(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))
(defn get-millis [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (c/to-long (nth coll i)))) rs)))
(defn get-dates-header [] (unparse-dates (get-dates)))

;get simulation results in a table
(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:resultset coll :dates (get-dates-header)}))
(defn simulate-page [ticker] (get-simulation-results (mcsim/start-simulations ticker)))

;draw chart with simulation results
(defn draw [ticker1] (let [tr-mcs (incanter.core/add-column :dates (get-millis (get-dates)) (incanter.core/to-dataset (incanter.core/trans (incanter.core/to-matrix (incanter.core/to-dataset  (montecarlo.montecarlo-simulation/start-simulations ticker1))))))] (doto (incanter.charts/time-series-plot :dates :col-0 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-1 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-2 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-3 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-4 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-5 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-6 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-7 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-8 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-9 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-10 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-11 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-12 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-13 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-14 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-15 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-16 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-17 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-18 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-19 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-20 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-21 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-22 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-23 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-24 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-25 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-26 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-27 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-28 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-29 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-30 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-31 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-32 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-33 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-34 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-35 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-36 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-37 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-38 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-39 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-40 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-41 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-42 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-43 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-44 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-45 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-46 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-47 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-48 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-49 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-50 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-51 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-52 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-53 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-54 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-55 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-56 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-57 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-58 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-59 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-60 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-61 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-62 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-63 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-64 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-65 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-66 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-67 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-68 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-69 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-70 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-71 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-72 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-73 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-74 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-75 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-76 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-77 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-78 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-79 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-80 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-81 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-82 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-83 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-84 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-85 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-86 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-87 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-88 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-89 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-90 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-91 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-92 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-93 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-94 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-95 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-96 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-97 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-98 :data tr-mcs)
                                                                                                                                                                                                                                                                    (incanter.charts/add-lines :dates :col-99 :data tr-mcs)
                                                                                                                                                                                                                                                                    incanter.core/view)))
;render chart
(defn draw-res [ticker1] (selmer.parser/render-file "montecarlochart.html" (draw ticker1)))

;defined routes
(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (GET "/montecarlochart" [] (montecarlochart-page) ) (POST "/simulate" [ticker] (simulate-page ticker)) (POST "/getchart" [ticker1] (draw-res ticker1)))

