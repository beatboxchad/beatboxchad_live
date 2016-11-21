(ns beatboxchad-live.server-fx
 [:require [overtone.core :refer :all]
           [clojure.java.shell :as shell]
           ] )

(def amplitude-control-bus-1 (control-bus))


(defn fx-fun-delay-1 [location & args]
  (let [fxbus (:id (first (rest args)))
        position (first location)
        target (:id (first (rest location)))
        ]
    (node "funDelay1" {:bus fxbus} {:position position :target target})
    )
  )

(defn fx-amp-boink [location & args]
  (let [fxbus (:id (first (rest args)))
        position (first location)
        target (:id (first (rest location)))
        ]
    (node "ampBoink" {:bus fxbus} {:position position :target target})
    )
  )
