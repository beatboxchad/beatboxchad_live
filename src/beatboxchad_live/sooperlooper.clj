(ns beatboxchad-live.sooperlooper
 [:require [overtone.core :refer :all]
           [clojure.java.shell :as shell]
           [beatboxchad-live.core]
           ] )

; FIXME figure out how not to block
;(shell/sh "alsa_out" "-d" "hw:PCH" "-j" "monitor" "&")

(for [n (range 8)]
      (shell/sh "jack_connect"
                (format "sooperlooper:loop%s_out_1" n)
                (format "SuperCollider:in_%s" (+ 3 n))
                )
      )

;; SL OSC docs: http://essej.net/sooperlooper/doc_osc.html

(def loop-storage-dir "/home/chad/beatboxchad_live/resources/sounds")
(def engine (osc-client "127.0.0.1" 9951))


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

(defn loop-op [loop-index op op-type]
  (osc-send engine 
            (format "/sl/%s/%s" loop-index op-type) 
            op)
  )


(defn ping-sooperlooper []
  ; send a ping 
  (osc-send engine "/ping"  "localhost:9960" "/ping-response")
  )


;; ensure there are >= 8 loops using loopcount in sl's ping response
(osc-handle beatboxchad-live.core/overtone-osc "/ping-response" 
            (fn [response]
              (let [loopcount (last (get response :args))]
                (if (< loopcount 8)
                  (do 
                    (add-mono-loop)
                    (ping-sooperlooper)
                    )
                  )
                )
              )
            )

(defn loop-setting [loop-index setting value]
  (osc-send engine 
            (format "/sl/%s/set" loop-index) 
            setting
            value
            )
  
  )

(defn setup-sooperlooper []
  (do
    (ping-sooperlooper)
    ;; FIXME I bet I could create an OSC handler to set each loop when it's created
    (for [loop-index (range 8)]
      (for [setting (keys default-loop-settings)]
        (loop-setting loop-index setting (get default-loop-settings setting)) 
        )
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

(setup-sooperlooper)
