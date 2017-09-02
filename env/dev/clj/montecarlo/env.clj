(ns montecarlo.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [montecarlo.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[montecarlo started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[montecarlo has shut down successfully]=-"))
   :middleware wrap-dev})
