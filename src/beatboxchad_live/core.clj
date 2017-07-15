(ns beatboxchad-live.core
  [:require
   [overtone.core :refer :all]
   [mount.core :as mount]
   [me.raynes.conch.low-level :as shell]
   [beatboxchad-live.sooperlooper :as sooperlooper]
  ; [beatboxchad-live.midi :refer :all]

   [beatboxchad-live.util :as util]
   ]
  )

(def total-sc-ins (+ (util/num-hw-inputs) sooperlooper/loop-count))


(mount/defstate sc-up :start (boot-external-server 44100 [:max-input-bus total-sc-ins]) :stop (kill-server))



(mount/stop)
(mount/start)
