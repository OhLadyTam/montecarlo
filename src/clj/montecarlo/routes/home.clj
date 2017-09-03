(ns montecarlo.routes.home
  (:require [montecarlo.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [montecarlo.montecarlo-simulation :as mcsim]
            [selmer.parser :as selmer]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.format :as f]))


(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn montecarlosimulation-page [] (layout/render "montecarlosimulation.html"))

(defn unparse-date [date] (f/unparse (f/formatter "dd/MM/yy") date))

(defn get-dates [] (take 21 (p/periodic-seq (t/now) (t/days 1))))
(defn unparse-dates [coll] (loop [i 0 rs []] (if (< i 20) (recur (inc i) (conj rs (unparse-date (nth coll i)))) rs)))

(defn get-dates-header [] (unparse-dates (get-dates)))


(defn render-dates-in-header [] (selmer.parser/render-file "montecarlosimulation.html" {:dates (get-dates-header)}))




(defn get-simulation-results [coll] (selmer.parser/render-file "montecarlosimulation.html" {:prices coll}))


(defn simulate-page [ticker] ((render-dates-in-header) (get-simulation-results (mcsim/start-simulation ticker))))

(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)))

