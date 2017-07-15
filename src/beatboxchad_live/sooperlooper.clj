(ns beatboxchad-live.sooperlooper
 [:require
  [overtone.core :refer :all]
  [mount.core :as mount]
  [me.raynes.conch.low-level :as shell]
  [beatboxchad-live.util :as util]
  [beatboxchad-live.osc :refer :all]
  ]
 )


(def loop-count (* (util/num-hw-inputs) 4))
(def loop-storage-dir "/home/chad/code/beatboxchad_live/resources/sounds")
(def engine (osc-client "127.0.0.1" 9951))

;; SL OSC docs: http://essej.net/sooperlooper/doc_osc.html
(def default-loop-settings {"sync" 1
                            "playback_sync" 1
                            "tempo_stretch" 1
                            "relative_sync" 1
                            "quantize" 2
                            "mute_quantized" 1
                            "overdub_quantized" 1
                            "use_common_outs" 0
                            }
  )


(defn add-mono-loop []
  (osc-send engine "/loop_add" 1 40)
  )

(defn loop-op
  [loop-index op op-type]
  "Perform an operation on a loop over OSC. Loop-index is which loop, op is the
  operation, type is either 'hit', 'up', or 'down'"
  (osc-send engine
            (format "/sl/%s/%s" loop-index op-type)
            op)
  )


; TODO support host specification
(defn ping
  [port returl]
  "Ping Sooperlooper on localhost at given port, asking it to respond at given
  return url"
  (osc-send engine "/ping"  (str "localhost:" port) returl)
  )


(defn loop-setting [loop-index setting value]
  (osc-send engine
            (format "/sl/%s/set" loop-index)
            setting
            value
            )

  )

(osc-handle overtone-osc "/loop-count"
            (fn [response]
              (let [cur-loopcount (last (get response :args))]
                (if (< cur-loopcount loop-count)
                  (add-mono-loop)
                  )
                )
              )
            )

(defn init-loops []
    (osc-send engine "/loop_del" -1)
    (Thread/sleep 500) ;FIXME I really wanna coordinate this better
    (for [loop-index (range loop-count)]
      (for [setting (keys default-loop-settings)]
        (loop-setting loop-index setting (get default-loop-settings setting))
        )
      )
  )


(defn save-loop [loop-index]
  (osc-send engine
            (format "/sl/%s/save_loop" loop-index)
            (format "%s/loop-%s.wav" loop-storage-dir loop-index)
            "nil"
            "nil"
            "nil"
            "nil"
            )
  )

(defn sooperlooper-up []
  (shell/proc "sooperlooper")
  (Thread/sleep 500)
  (osc-send engine "/register" "localhost:9960" "/loop-count")
  (Thread/sleep 500)
  (init-loops)
  )

(defn sooperlooper-down []
  (osc-send engine "/quit")
  )
(mount/defstate sooperlooper :start (sooperlooper-up) :stop (sooperlooper-down))
