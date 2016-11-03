(ns beatboxchad-live.midi
 [:require [overtone.core :refer :all]] )

;; get the value from expression pedal on MIDI controller and write it to the bus
(on-event [:midi :control-change]
          (fn [e]
              (let [control-number (:data1 e)
                    value (:data2 e)
                    ]
                (do
                (if (= control-number 27)
                  (do
                    ;; we need to out it to a control bus and then set the
                    ;; effect to be listening to that control bus
                    (control-bus-set! delay-time-bus value) 
                    )
                  )
                (if (= control-number 7)
                     (do
                       ;; we need to out it to a control bus and then set the
                       ;; effect to be listening to that control bus
                       (control-bus-set! delay-feedback-bus value) 
                     )
              )
                )
                   ))
           ::delay-pedal-handler
          )
