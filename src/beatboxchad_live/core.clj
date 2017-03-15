(ns beatboxchad-live.core
  [:require 
   [overtone.core :refer :all]
   [mount.core :as mount]
   [me.raynes.conch.low-level :as shell]
   [beatboxchad-live.sooperlooper :as sooperlooper]
   [beatboxchad-live.osc :as osc]
   ]
  )

(defn num-hw-inputs 
  "how many inputs do we have on the interface?"
  []
  (let [port-list      (shell/stream-to-string (shell/proc "jack_lsp") :out)
        system-ins     (re-seq #"system:capture_[0-9]*" port-list)
        ]
    (count system-ins)
    )
  )

(mount/defstate sc-up :start (boot-external-server) :stop (kill-server))
