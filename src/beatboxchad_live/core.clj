(ns beatboxchad-live.core
 [:require [overtone.core :refer :all]]
)

; group 3 is for monitoring hardware inputs to generate control signals for synths

; group 19 is for main sound sources, whether hardware inputs or synths

; group 5 is for effects

;group 19 is for sound sources to which effects are applied


(defonce delay-feedback-bus (control-bus))
(defonce delay-time-bus (control-bus))
(defonce freq-bus (control-bus))
(defonce guitar-signal-bus (audio-bus))
(defonce guitar-signal-bus1 (audio-bus))

;; I am doing it this way because synthdefs in overtone aren't sounding the
;; same as in sclang and I don't care to troubleshoot at this time. So I'm
;; writing all my synths and fx in SC and then just calling them from here.

;; create a synth node on the server with the synthdef named. 

(ctl guitar-in :outbus guitar-signal-bus1)
(def guitar-delay (node "funDelay1" {:dtimeBus delay-time-bus
                                     :feedbackBus delay-feedback-bus
                                     :in 0
                                     :out 0} {:target 9}))
(node-free guitar-delay)
(ctl guitar-delay :in 1)

(def vocoder (node "vocoder1" {:modulator 1 :carrier 1 :out 0} {:target 5}))
(node-free vocoder)

(def guitar-reverb (node "reverb1" 
                        {:inBus guitar-signal-bus1
                         :outBus 0} 
                        {:target 5}))
(ctl guitar-reverb :inBus 0)

(def guitar-delay1 (node "percusReson1" 
                        {:in guitar-signal-bus1
                         :out 0
                         :controlbus1 delay-time-bus} 
                        {:target 4}))

(node-free guitar-reverb)

