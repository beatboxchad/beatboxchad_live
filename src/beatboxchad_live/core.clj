(ns beatboxchad-live.core
  [:require 
   [overtone.core :refer :all]
   [mount.core :as mount]
   [me.raynes.conch.low-level :as shell]
   [beatboxchad-live.sooperlooper :as sooperlooper]
   [beatboxchad-live.util :as util]
   ]
  )

(def total-sc-ins (+ (util/num-hw-inputs) sooperlooper/loop-count))

(mount/defstate sc-up :start (boot-external-server 44100 [:max-input-bus total-sc-ins]) :stop (kill-server))


;(defn- connect-jack-ports
;  "Connect all the sooperlooper ports"
;  ([] (connect-jack-ports 2))
;  ([n-channels]
;   (let [port-list      (shell/stream-to-string (shell/proc "jack_lsp") :out)
;
;           sc-ins         (re-seq #"SuperCollider.*:in_[0-9]*" port-list)
;           sc-outs        (re-seq #"SuperCollider.*:out_[0-9]*" port-list)
;           system-ins     (re-seq #"system:capture_[0-9]*" port-list)
;           system-outs    (re-seq #"system:playback_[0-9]*" port-list)
;           interface-ins  (re-seq #"system:AC[0-9]*_dev[0-9]*_.*In.*" port-list)
;           interface-outs (re-seq #"system:AP[0-9]*_dev[0-9]*_LineOut.*" port-list)
;           connections    (partition 2 (concat
;                                        (interleave sc-outs system-outs)
;                                        (interleave sc-outs interface-outs)
;                                        (interleave system-ins sc-ins)
;                                        (interleave interface-ins sc-ins)))]
;       (doseq [[src dest] connections]
;         (println src dest)
;         (shell/proc "jack_connect" src dest)
;         ))))
;;(connect-jack-ports)
;
;(defn connect-sooperlooper-jack []
;  (do 
;    ;;FIXME hardcoding
;    (for [n (range 8)]
;      (shell/proc "jack_connect"
;                (format "sooperlooper:loop%s_out_1" n)
;                (format "SuperCollider:in_%s" (+ 3 n))
;                )
;      )
;    ;;FIXME hack. Use a clojure async thing
;    (Thread/sleep 1000)
;    (for [n (range 4)]
;      (shell/proc "jack_connect"
;                (format "SuperCollider:out_%s" (+ 5 n))
;                (format "sooperlooper:loop%s_in_1"(+ 4 n))
;                )
;      )
;    )
;  )
;

;(mount/defstate jack-connections :start (connect-sooperlooper-jack) :stop ())
