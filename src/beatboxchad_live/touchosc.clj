(ns beatboxchad-live.touchosc
  [:require [overtone.core :refer :all] 
            [beatboxchad-live.core]
            [beatboxchad-live.server-fx :refer :all]
  ]
  )

;FIXME de-hardcode this shit
(def touchosc (osc-client "192.168.0.25" 9000))

; This is an example of how to toggle things back to touch-osc. Write code that
; updates the buttons when a node is freed. 
; (osc-send touchosc "/2/multitoggle3/1/3" 0)


(osc-listen beatboxchad-live.core/overtone-osc (fn [msg] (println msg)) :debug)
(osc-rm-listener beatboxchad-live.core/overtone-osc :debug)

(def insts
  { 
   "1"  beatboxchad-live.core/audio-in-0 
   "2"  beatboxchad-live.core/audio-in-1 
   "3"  beatboxchad-live.core/sl-in-0
   "4"  beatboxchad-live.core/sl-in-1
   "5"  beatboxchad-live.core/sl-in-2
   "6"  beatboxchad-live.core/sl-in-3
   "7"  beatboxchad-live.core/sl-in-4
   "8"  beatboxchad-live.core/sl-in-5
   "9"  beatboxchad-live.core/sl-in-6
   "10" beatboxchad-live.core/sl-in-7

   }
  )

(def effects
  { 
   "1"  fx-freeverb
   "2"  fx-fun-delay-1
   "3"  fx-compressor
   "4"  fx-amp-boink
   }
  )
(def active-effects {
  "1" {
       :reverb (atom {})
       "2" (atom {})
       "3" (atom {})
       }
  "2" {
       :reverb (atom {})
       "0" (atom {})
       "1" (atom {})
       "2" (atom {})
       "3" (atom {})
       }
  "3" {
       :reverb (atom {})
       "0" (atom {})
       "1" (atom {})
       "2" (atom {})
       "3" (atom {})
       }
  }
  )

(defn toggle-loop [osc-msg]
  (let [inst-index (str (last (:path osc-msg))) toggler (first (:args osc-msg))]
    (if (= 0.0 toggler)
      (swap! (get (get active-effects inst-index))
      ((get insts inst-index))
      )
    )
  )

  (defn toggle-reverb [osc-msg]
    (let [inst-index (str (last (:path osc-msg))) toggler (first (:args osc-msg))]
      (if (= 0.0 toggler)
        (swap! (:reverb (get active-effects inst-index)) kill)
        (reset! (:reverb (get active-effects inst-index) (inst-fx! (get insts inst-index) fx-freeverb)))
        )
      )
    )

(reset! (:reverb (get active-effects "2")) (inst-fx! (get insts "2") fx-amp-boink))
(swap! (:reverb (get active-effects "2")) kill)
(clear-fx beatboxchad-live.core/audio-in-1)


(for [n (range 10)]
  (let [osc-path (str "/inst/toggle/1/"  (inc n))]
    (osc-handle beatboxchad-live.core/overtone-osc osc-path
                (fn [msg] (toggle-loop msg))
                )
    )
  )

(for [n (range 10)]
  (let [osc-path (str "/inst-fx/reverb/toggle/1/"  (inc n))]
    (osc-handle beatboxchad-live.core/overtone-osc osc-path
                (fn [msg] (toggle-reverb msg))
                )
    )
  )
