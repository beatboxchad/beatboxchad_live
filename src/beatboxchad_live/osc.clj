(ns beatboxchad-live.osc
  [:require 
   [overtone.core :refer :all]
   [mount.core :as mount]
   ]
  )

(defn osc-up []
  (def overtone-osc (osc-server 9960 "osc-overtone"))
  )

(defn osc-down []
  (osc-close overtone-osc)
  )

(mount/defstate setup-osc :start (osc-up) :stop (osc-down))
