(ns beatboxchad-live.midi
 [:require [overtone.core :refer :all]] 
 [:require [beatboxchad-live.sooperlooper]])

(def fcb (midi-mk-full-device-key (midi-find-connected-device "mio")))

(def loop-ops
  {0 {:action "record"    :hit false}
   1 {:action "overdub"   :hit false}
   2 {:action "trigger"   :hit true}
   3 {:action "pause"     :hit true}
   4 {:action "reverse"   :hit true}
   }
  )


(on-event (conj fcb :note-on)
          (fn [e]
            (let [note (:note e)]
              (let [loop-index (int (/ note 10))
                    cmd (mod note 10)
                    loop-op (if (:hit (get loop-ops cmd))
                              "hit"
                              "down")
                    ]
                (beatboxchad-live.sooperlooper/loop-op 
                  loop-index
                  (:action (get loop-ops cmd))
                  loop-op


                  )
                )
              )
            )
          ::fcb-note-on
          )

(on-event (conj fcb :note-off)
          (fn [e]
            (let [note (:note e)]
              (let [loop-index (int (/ note 10))
                    cmd (mod note 10)
                    ]
                (if-not (:hit (get loop-ops cmd))
                  (beatboxchad-live.sooperlooper/loop-op 
                    loop-index
                    (:action (get loop-ops cmd))
                    "up"

                    )
                  )

                )
              )
            )
          ::fcb-note-off
          )
(remove-event-handler ::fcb-note-on)
(remove-event-handler ::fcb-note-off)
