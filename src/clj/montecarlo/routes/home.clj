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

(defn get-dates-header [] (take 21  (p/periodic-seq (t/now) (t/days 1))))
(get-dates-header)

;(defn render-dates-in-header [] (selmer.parser/render-file "montecarlosimulation.html" {:dates (get-dates-header)}))

(defn simulate-page [ticker] (str ticker))

(defroutes home-routes (GET "/" [] (home-page)) (GET "/about" [] (about-page)) (GET "/montecarlosimulation" [] (montecarlosimulation-page)) (POST "/simulate" [ticker] (simulate-page ticker)))

