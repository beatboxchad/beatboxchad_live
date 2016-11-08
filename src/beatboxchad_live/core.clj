(ns beatboxchad-live.core
 [:require [overtone.core :refer :all]]
)

(defn init []

  ;
  )
(def control-node-group 3)
(def inst-group 19)
(def fx-group-1 6)
(def fx-group-2 7)
(def fx-group-3 8)


;(defsynth pitch-control [


(defn inst-node [nname]
  )

(defn fx-node [nname]
  )

(defn controller-node [nname]

  ;; need a bus, need a group
  (node "percusReson1" 
        {:in guitar-signal-bus1
         :out 0
         :controlbus1 delay-time-bus} 
        {:target 4})
  )
;; then need to return something that can be freed


;;examples
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
