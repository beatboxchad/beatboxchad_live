(ns beatboxchad-live.core
 [:require [overtone.core :refer :all]]
)

(def overtone-osc (osc-server 9960 "osc-overtone"))



(comment IDEA: high amplitude and/or pitch triggers brief delay-hold jitter
         
         you can make all kinds of little triggered effects like this, maybe
         spawn one-shot synths with a done-action of FREE when a control bus
         fires. Environmental, hands-free control of fx. Just smack the guitar
         and bdddthdtdtdtdtttttt

         when the wicked rule 
         )
(definst audio-in-0 []
  (sound-in:ar 0)
  )

(definst audio-in-1 []
  (sound-in:ar 1)
  )

(definst sl-in-0 []
  (sound-in:ar 2)
  )

(definst sl-in-1 []
  (sound-in:ar 3)
  )


(definst sl-in-2 []
  (sound-in:ar 4)
  )

(definst sl-in-3 []
  (sound-in:ar 5)
  )

(definst sl-in-4 []
  (sound-in:ar 6)
  )

(definst sl-in-5 []
  (sound-in:ar 7)
  )

(definst sl-in-6 []
  (sound-in:ar 8)
  )

(definst sl-in-7 []
  (sound-in:ar 9)
  )

(definst sl-in-8 []
  (sound-in:ar 10)
  )
