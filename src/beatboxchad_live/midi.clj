(ns beatboxchad-live.midi
 [:require [overtone.core :refer :all]] 
 [:require [beatboxchad-live.sooperlooper]])

(def fcb (midi-mk-full-device-key (midi-find-connected-device "mio")))

(on-event (conj fcb :program-change)
          (fn [e]
            (let [note (:note e)]
              (if (< note 8 )
                (beatboxchad-live.sooperlooper/record note)
                )
              )
            )
          ::sooperlooper-record
          )
