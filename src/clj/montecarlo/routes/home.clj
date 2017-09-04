(ns montecarlo.routes.home
  (:require [montecarlo.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.http response]
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

(defn montecarlosimulation-page1 [] (layout/render "montecarlosimulation.html"))

(defn save-chart [] (incanter.core/save (incanter.charts/histogram (incanter.stats/sample-normal 1000)) "chart.png")

  (defn montecarlosimulation-page [] (selmer.parser/render-file "montecarlosimulation.html" {:image  (save-chart)})))
(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))

(defn get-dates-header [] (unparse-dates (get-dates)))

(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:resultset coll :dates (get-dates-header)}))

(defn simulate-page [ticker] (get-simulation-results (mcsim/start-simulations ticker)))

(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)))


(defn gen-samp-hist-png
  [request size-str mean-str sd-str]
  (let [size (if (nil? size-str)
               1000
               (Integer/parseInt size-str))
        m (if (nil? mean-str)
            0
            (Double/parseDouble mean-str))
        s (if (nil? sd-str)
            1
            (Double/parseDouble sd-str))
        samp (incanter.stats/sample-normal size
                            :mean m
                            :sd s)
        chart (incanter.charts/histogram
                samp
                :title "Normal Sample"
                :x-label (str "sample-size = " size
                              ", mean = " m
                              ", sd = " s))
        out-stream (ByteArrayOutputStream.)
        in-stream (do
                    (incanter.core/save chart out-stream)
                    (ByteArrayInputStream.
                      (.toByteArray out-stream)))
        header {:status 200
                :headers {"Content-Type" "image/png"}}]
    (response/update-response request
                     header
                     in-stream)))
