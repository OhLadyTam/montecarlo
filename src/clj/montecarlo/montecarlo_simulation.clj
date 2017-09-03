(ns montecarlo.montecarlo-simulation
  (:require incanter.stats)
  (:import yahoofinance.YahooFinance yahoofinance.Stock histquotes.HistoricalQuote quotes.stock.StockQuote))
  ;(:import (yahoofinance YahooFinance Stock)
   ;        (histquotes HistoricalQuote)
    ;       (quotes.stock StockQuote)
     ;      (incanter.stats incanter.stats)))

;(ns clj-test1.montecarlo-simulation (:import yahoofinance.YahooFinance))

;(def h (.getHistory (YahooFinance/get "DJI")))
;(def price (.getPrice (.getQuote (yahoofinance.YahooFinance/get "GOOG"))))
;(println price)


(defn mean [coll]
  (let [sum (apply + coll)
        count (count coll)]
    (if (pos? count)
      (/ (double sum) (double count))
      0)))

(defn get-close-price [x] (double (.getClose x)))

(defn get-historical-closes [f history]
  (let [historical-close (partition (count history)
                           (apply interleave history))]
    (reduce (fn [s coll] (conj s (apply f coll))) [] historical-close)))


(defn build-historical-close-vector [ticker] (get-historical-closes get-close-price (vector (.getHistory (yahoofinance.YahooFinance/get ticker)))))
;(build-historical-close-vector "MSFT")


(defn standard-deviation [coll]
  (let [avg (mean coll)
        squares (for [x coll]
                  (let [x-avg (- x avg)]
                    (* x-avg x-avg)))
        total (count coll)]
    (-> (/ (apply + squares)
           (- total 1))
        (Math/sqrt))))

(defn normalize-stddev [coll]
  (loop [iter 1 norm []]
    (if (< iter (count coll))
      (recur
        (inc iter)
        (conj norm (-
                     (/
                       (double (nth coll iter))
                       (double (nth coll (- iter 1) ))) 1)))
      (standard-deviation norm))))

(defn calc-daily-volatility [coll] (normalize-stddev coll))
(defn calc-annual-volatility [coll] (* (Math/sqrt 252) (calc-daily-volatility coll)))

(defn calc-mcs-price [prob mn price histCloses]
  (* price (+ 1
              (incanter.stats/quantile-normal prob :mean mn :sd (calc-daily-volatility histCloses)))))

(defn get-start-price [ticker] (.getPrice (.getQuote (YahooFinance/get ticker))))
;(get-start-price "MSFT")
(defn calc-mcs-prices
  [price prob histCloses]
  (loop [i 0 result-set [(calc-mcs-price prob 0 price histCloses)]]
    (if (< i 20)
      (recur (inc i) (conj result-set (calc-mcs-price prob 0 (last result-set) histCloses)))
      result-set)))

(defn start-simulations [ticker]
                   (let [start-price (get-start-price ticker) history (build-historical-close-vector ticker)]
                     (loop [i 0 result-set []] (if (< i 100) (recur (inc i) (conj result-set (calc-mcs-prices start-price (rand) history))) result-set))))


(defn start-simulation [ticker] (let [start-price (get-start-price ticker) history (build-historical-close-vector ticker)] (calc-mcs-prices start-price (rand) history)))


;(start-simulation "DAX")
