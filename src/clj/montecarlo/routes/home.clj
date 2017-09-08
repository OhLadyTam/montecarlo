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

;(defn save-chart [] (incanter.core/save (incanter.charts/histogram (incanter.stats/sample-normal 1000)) "chart.png")

;  (defn montecarlosimulation-page [] (selmer.parser/render-file "montecarlosimulation.html" {:image  (save-chart)})))

(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))

(defn get-millis [coll] (loop [i 0 rs []] (if (< i 3) (recur (inc i) (conj rs (c/to-long (nth coll i)))) rs)))


(defn get-dates-header [] (unparse-dates (get-dates)))

(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:resultset coll :dates (get-dates-header)}))

(defn simulate-page [ticker] (get-simulation-results (mcsim/start-simulations ticker)))

(defn get-xls [ticker] (incanter.excel/save-xls (incanter.datasets/get-dataset (mcsim/start-simulation ticker)) "D:/montecarlosimulation.xls"))

(defn create-dataset [ticker] (incanter.core/dataset ["col1"] (mcsim/start-simulation ticker) :delim \space))

(def ds (incanter.core/dataset ["x1" "x2"] [[1 2] [3 4]]))
(defn create-csv [ticker] (incanter.core/save (incanter.datasets/get-dataset ds) "d.csv"))
(defn download [ticker] (selmer.parser/render-file "montecarlosimulation.html" (create-csv ticker)))

(defn open-chart1 [ticker] (incanter.core/view (incanter.charts/scatter-plot :data (incanter.datasets/get-dataset (create-dataset ticker)) :title "Montecarlo" :x-label "days" :y-label "prices")))

(defn open-chart [ticker] (selmer.parser/render-file "montecarlosimulation.html" (incanter.core/view (incanter.charts/scatter-plot :data (incanter.datasets/get-dataset (incanter.core/dataset ["x1"] [[1] [2]]) :title "Montecarlo" :x-label "days" :y-label "prices")))))

(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)) (POST "/getxls" [ticker2] (download ticker2)) (POST "/getchart" [ticker1] (open-chart ticker1)))

;(montecarlo.db.core/create-user { :first_name "John" :last_name "Doe"})
;https://incanter.files.wordpress.com/2009/06/9781782162643_chapter-6.pdf
;(def matrix-set-2 (incanter.core/dataset [:a :b :c] [[1 2 3] [2 3 4]]))
;(incanter.core/view matrix-set-2)
;(incanter.core/view (incanter.charts/scatter-plot :a :b :data matrix-set-2))
;(incanter.core/col-names matrix-set-2)

;(def mcs (montecarlo.montecarlo-simulation/start-simulations "GOOG"))
;(def mcs-dataset (incanter.core/to-dataset mcs))
;(incanter.core/col-names mcs-dataset)
;(println mcs-dataset)

;(incanter.core/with-data (incanter.datasets/get-dataset mcs-dataset) (incanter.core/view (incanter.charts/xy-plot :col-0 :col-1)))
;(incanter.core/view (incanter.charts/scatter-plot :col-0 :col-1 :data mcs-dataset))

;(incanter.core/with-data (incanter.datasets/get-dataset mcs-dataset (doto (incanter.charts/add-lines :col-0))))


;(def mcs (montecarlo.montecarlo-simulation/start-simulations "GOOG"))




(def vcs-rs-vec [[1 2 3] [4 5 6] [7 8 9] [0 9 8] [7 6 5] [4 3 2]])
(def mcs-dataset (incanter.core/to-dataset vcs-rs-vec))
(def mcs-dataset-with-dates (incanter.core/add-column :dates (get-millis (get-dates)) mcs-dataset))

(def mcs-final (incanter.core/add-column :dates (get-millis (get-dates)) (incanter.core/to-dataset vcs-rs-vec)))
(def trans-mcs (incanter.core/add-column :dates (get-millis (get-dates)) (incanter.core/to-dataset (incanter.core/trans (incanter.core/to-matrix (incanter.core/to-dataset vcs-rs-vec))))))



(doto (incanter.charts/time-series-plot :dates :col-0 :data trans-mcs) incanter.core/view)

;crtanje jedne timeseries - RADI!!! :D
(defn draw [] (let [tr-mcs (incanter.core/add-column :dates (get-millis (get-dates)) (incanter.core/to-dataset (incanter.core/trans (incanter.core/to-matrix (incanter.core/to-dataset vcs-rs-vec)))))] (doto (incanter.charts/time-series-plot :dates :col-0 :data tr-mcs) incanter.core/view)))
(draw)
;(def lm (incanter.stats/linear-model :dates :col-1))

(incanter.core/view trans-mcs)

;(incanter.core/with-data (incanter.datasets/get-dataset trans-mcs) (doto (incanter.charts/xy-plot :col-0 :col-3)(incanter.charts/add-lines :col-2 :col-3) incanter.core/view))

;Prikazuje sve tri linije :D
(doto (incanter.charts/xy-plot :dates :col-0 :data trans-mcs) (incanter.charts/add-lines :dates :col-1 :data trans-mcs) (incanter.charts/add-lines :dates :col-2 :data trans-mcs) incanter.core/view)

;(let [] (loop [i 0] (if (< i 5) (recur (inc i) ()))))
;(nth (incanter.core/col-names trans-mcs) 2)
;(incanter.charts/add-lines :dates :col-1 :data trans-mcs)
;(defn draw-with-cols [col] (doto (incanter.charts/xy-plot :dates :col-0 :data trans-mcs) (loop [i 0 s[] ] (if (< i 5) (recur (inc i) (incanter.charts/add-lines :dates (nth (incanter.core/col-names trans-mcs) i) :data trans-mcs)  ) ) incanter.core/view)))


;ovaj loop radi!

(loop [x 5] (when (> x -1) (incanter.charts/add-lines :dates (nth (incanter.core/col-names trans-mcs) x) :data trans-mcs) (recur (dec x)) ))

(doto (incanter.charts/xy-plot :dates :col-0 :data trans-mcs) (let [x 5 trans-mcs [] trans-mcs] (loop [x 5] (when (> x -1) (incanter.charts/add-lines :dates (nth (incanter.core/col-names trans-mcs) x) :data trans-mcs) (recur (dec x)) )) ) incanter.core/view)


;(loop [x 5] (when (> x 0)) (incanter.charts/add-lines :dates (nth (incanter.core/col-names trans-mcs) x) :data trans-mcs) (recur (- x 1)))



;(doto (incanter.charts/xy-plot :dates :col-0 :data trans-mcs) (loop [x 5] (when (> x 0)) (incanter.charts/add-lines :dates (nth (incanter.core/col-names trans-mcs) x) :data trans-mcs) (recur (- x 1))) incanter.core/view)

;(doto (incanter.charts/xy-plot :dates :col-0 :data trans-mcs) (incanter.charts/add-lines :dates (incanter.core/col-names trans-mcs) :data trans-mcs) incanter.core/view)
