(ns beatboxchad-live.sooperlooper
 [:require 
  [overtone.core :refer :all]
  [mount.core :as mount]
  [me.raynes.conch.low-level :as shell]
  [beatboxchad-live.osc :refer :all]
  ] 
 )


;; SL OSC docs: http://essej.net/sooperlooper/doc_osc.html
;(def loop-storage-dir "/home/chad/beatboxchad_live/resources/sounds")
(def engine (osc-client "127.0.0.1" 9951))
;(def default-loop-settings {"sync" 1
;                            "playback_sync" 1
;                            "tempo_stretch" 1
;                            "relative_sync" 1
;                            "quantize" 2
;                            "mute_quantized" 1
;                            "overdub_quantized" 1
;                            "use_common_outs" 0
;                            }
;  )
;
;
(defn start-sooperlooper []
  (shell/proc "sooperlooper")
  )

(defn stop-sooperlooper []
  (osc-send engine "/quit")
  )

(mount/defstate sooperlooper :start (start-sooperlooper) :stop (stop-sooperlooper))
;
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
;(defn add-mono-loop []
;  (osc-send engine "/loop_add" 1 40)
;  )
;
;(defn loop-op [loop-index op op-type]
;  (osc-send engine 
;            (format "/sl/%s/%s" loop-index op-type) 
;            op)
;  )
;
;
(defn ping-sooperlooper [port returl]
  (osc-send engine "/ping"  (str "localhost:" port) returl)
  )
;
;;; there will be one mono loop for each hw input
;
;;; ensure there are >= 8 loops using loopcount in sl's ping response
;(osc-handle overtone-osc "/ping-response" 
;            (fn [response]
;              ;(println response)
;              ;; FIXME document my assumption
;              ;(let [loopcount (last (get response :args))]
;              ;  (if (< loopcount 8)
;              ;    (do 
;              ;      (add-mono-loop)
;              ;      (ping-sooperlooper)
;              ;      )
;              ;    )
;              ;  )
;              ;)
;            )
;            )
;
;(defn loop-setting [loop-index setting value]
;  (osc-send engine 
;            (format "/sl/%s/set" loop-index) 
;            setting
;            value
;            )
;  
;  )
;
;(defn setup-sooperlooper []
;  (do
;    (ping-sooperlooper)
;    ;; FIXME I bet I could create an OSC handler to set each loop when it's created
;    (Thread/sleep 500)
;    (for [loop-index (range 8)] ;FIXME hardcoded.
;      (for [setting (keys default-loop-settings)]
;        (loop-setting loop-index setting (get default-loop-settings setting)) 
;        )
;      )
;    )
;  )
;
;(defn save-loop [loop-index]
;  (osc-send engine 
;            (format "/sl/%s/save_loop" loop-index) 
;            (format "%s/loop-%s.wav" loop-storage-dir loop-index)
;            "nil"
;            "nil"
;            "nil"
;            "nil"
;            )
;  )
