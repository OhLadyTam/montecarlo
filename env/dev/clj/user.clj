(ns user
  (:require [mount.core :as mount]
            montecarlo.core))

(defn start []
  (mount/start-without #'montecarlo.core/repl-server))

(defn stop []
  (mount/stop-except #'montecarlo.core/repl-server))

(defn restart []
  (stop)
  (start))


