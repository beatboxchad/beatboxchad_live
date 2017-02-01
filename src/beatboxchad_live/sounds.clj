(ns beatboxchad-live.sounds
 [:require 
  [overtone.core :refer :all]
  ] 
 )


;; spit the pitch of an audio bus out to a control bus
(defsynth pitch1 [bus 0 outbus 0]
  (out:kr outbus (pitch:kr (in:ar bus)))
  )

;; ditto with amplitude
(defsynth amplitude1 [bus 0 outbus 0]
  (out:kr outbus (amplitude:kr (in:ar bus)))
  )

(definst pitchf1 [databus 1]
  (let [in (sound-in databus)
        freq (/ (pitch:kr in) 2)
        amp (amplitude:kr in)]
    (* (mix (var-saw:ar (map #(* % freq ) [0.5 1 2]) 0 (lf-noise1:kr [0.3 0.1 0.1]) )) amp)
    )
  )
(pitchf1)
(stop)
(inst-fx! pitchf1 fx-freeverb)
(clear-fx pitchf1)

(definst voice1 [bus 0]
  (let [sig (sound-in:ar bus)]
    sig
  )
  )

(stop)
(voice1)
(ctl voice1 :bus 1)
(clear-fx voice1)
(def bork (inst-fx! voice1 delay-oneshot))
(ctl bork :gate 1)

;; if I really want the wacky noises I'm after, I'll need to use a buffer-based
;; delay. This one doesn't register the speed changes until the next repetition
;;s starts. It's not too bad if I start with a short delay time. 

;; scratch that, my problem is understanding how the trigger and envelopes
;; work. Got closer at one point, but you can hear it returning and there are
;; some other undesired artifacts. Might need to instantiate a fresh synth and
;; :action 2 and all that instead of hacking it with triggers

(defsynth delay-oneshot 
  "when triggered, applies a delay. Intended to be controlled by amplitude or something else clever"
  ;; FIXME play with the envelopes till this sounds super lovely, or figure out
  ;; how to fix up the thing
  [bus 0 
   gate [1 :tr]
   ]
  (let [in (in:ar bus)
        playback-env (env-gen (env-lin) gate)
        dtime-env (env-gen (envelope [0.01 0.9 0.9 0.05] [0.01 0.01 3] [-8]) gate)
        processed (allpass-n:ar in 5 dtime-env 10) 
        sig (+ in (* processed playback-env))
        ]
    (out:ar bus sig)
    )
  )

(clear-fx pitchf1)
(def meow (inst-fx! pitchf1 delay-oneshot))

(ctl meow :gate 1)
