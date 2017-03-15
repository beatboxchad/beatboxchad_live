(ns beatboxchad-live.sounds
 [:require 
  [overtone.core :refer :all]
  [beatboxchad-live.core :refer :all]
  ] 
 )



(definst guitar []
  (sound-in 0)
  )
(guitar)
(voice1)
(clear-fx voice1)

(definst voice1 []
  (let [sig (sound-in:ar 1)]
    sig
    )
  )
(inst-fx! guitar fx-distortion-tubescreamer)
(inst-fx! guitar fx-echo)
(clear-fx guitar)
(voice1)
(inst-fx! voice1 fx-freeverb)
(inst-volume! voice1 10)
(kill voice1)

(definst pitchf1 [databus 1]
  (let [in (sound-in databus)
        freq (/ (pitch:kr in) 1)
        amp (amplitude:kr in)]
    (* (mix (var-saw:ar (map #(* % freq ) [0.5 1 2]) 0 (lf-noise1:kr [0.3 0.1 0.1]) )) amp)
    )
  )

(pitchf1)
(inst-fx! pitchf1 fx-feedback)
(inst-fx! pitchf1 fx-freeverb)
(kill pitchf1)
(definst ding
  [note 60 velocity 100 gate 1 bend 0.0]
  (let [freq (+ (midicps note) bend)
        env (env-gen (adsr 0.001 0.1 0.6 0.3) gate :action FREE)
        amp (/ velocity 127.0)
        snd (sin-osc freq)
        ]
    (* amp env snd)))

(stop)
(def dinger (midi-poly-player ding (:overtone.studio.midi/full-device-key korg) :none ))
(on-event [:midi :pitch-bend]
          (fn [event]
            (let [bend (:velocity event)]
              (if (> 64 bend)
                (ctl ding :bend bend)
                (ctl ding :bend (- bend 127))
                )
              )
            )
          :ding-bender)

(remove-event-handler :ding-bender)
(midi-player-stop dinger)
(inst-fx! guitar fx-freeverb)
(inst-fx! voice1 fx-freeverb)
(inst-fx! voice1 fx-distortion)
(inst-fx! ding fx-rlpf)
(inst-fx! ding fx-echo)
(inst-fx! ding fx-freeverb)
(inst-fx! ding fx-distortion)
(clear-fx voice1)
(clear-fx ding)

(inst-fx! voice1 fx-echo)
(inst-fx! voice1 fx-distortion)

(definst harmonize-input
  [note 60 velocity 80 gate 1 bend 0.0 bus 1]
  (let [in (sound-in bus)
        pitch (pitch:kr in 
                        :exec-freq 500 
                        :down-sample 0.1)
        amp (/ velocity 43)
        env (env-gen (adsr 0.01 0.1 1 0.3) gate :action FREE)
        orig-midinote (cpsmidi pitch)
        interval (- note orig-midinote)
        ratio (+ bend (midiratio interval))
        sig (pitch-shift:ar in :pitch-ratio ratio)
        ]
      (* env (* amp [sig sig]))
    )
  )

(harmonize-input)
(inst-fx! harmonize-input fx-echo)
(inst-fx! harmonize-input fx-distortion)
(clear-fx harmonize-input)
(kill harmonize-input)

(def harmonizer (midi-poly-plaYer harmonize-input (:overtone.studio.midi/full-device-key korg) :none ))

(on-event [:midi :pitch-bend]
          (fn [event]
            (let [bend (:data2-f event)]
              (if (> 0.5 bend)
                (ctl harmonize-input :bend bend)
                (ctl harmonize-input :bend (- bend 1))
                )
              )
            )
          :harmonize-bender)

(midi-player-stop harmonizer)
(remove-event-handler :harmonize-bender)

;; there'll be a synth that monitors the input. The synth will send-trig or
;; something to this synth on the same bus
;(definst delay-oneshot 
;  [note 60 velocity 80 gate [0 :tr] bus 1]
;  (let [in (sound-in:ar bus)
;        playback-env (x-line:kr 1.0 0.1 1 2)
;        processed (allpass-c:ar in 0.5 (env-gen (adsr 0.001 0.5 1 0.5 0.5) gate) 0.6)
;        sig (* processed playback-env)
;        ]
;    sig
;    )
;  )
;(def delayer (midi-poly-player delay-oneshot))
;(voice1)
(def shit (freesound 222593))
;(def horn (freesound 248229))
;(demo (shit))
;(:buf-num (buffer-info horn))
;(:id horn )



(definst shitbells 
  "The word 'shit' sung beautifully. Some improvements to come"
  [note 60 velocity 100 gate 1 bend 0]
  (let [sample (freesound 222593)
        ratio (midiratio (- note 77)) 
        env (env-gen (adsr 0.001 0.1 0.6 0.3) gate :action FREE)
        ]
    (* env 
       (play-buf 
         2 
         sample 
         (* ratio (buf-rate-scale (:id sample))) 
         :start-pos (/ 617076 2) 
         :trigger gate
         :action FREE 
         )
       )
    )
  )


;(def shitter (midi-poly-player shitbells (:overtone.studio.midi/full-device-key (first (midi-connected-devices))) :none ))
;(midi-player-stop shitter)
;(stop)
;(voice1)
(defsynth bufdelay 
  "A buffer-based delay as an experiment, in progress"
  [bus 0
   dtime 0.2]
  (let [buf (local-buf (* (server-sample-rate) 6))
        in (sound-in bus)
        ]
    (replace-out:ar bus (+ in (buf-delay-c buf in dtime)))
    )
  )
;(def what (inst-fx! voice1 bufdelay))
;(ctl what :dtime 0.5)
;(ctl what :bus 0.5)

;(clear-fx voice1)

;; so how would a x-faded sustained thingy go? 
;; well, first of all, this all needs to be controlled by a single gate, just
;; like a regular envelope basically at the end of the attack, it would xfade
;; to the first of two sustain parts.  it woudl continuously xfade between the
;; two sustain parts until gait is released, at which pint it would xfade to
;; the release. 

;; this technique, like what I've been doing on guitar for years, is called
;; 'windowing' according to some guy on the interent

;; but also check into granular synthesis. grain-in

;; how on earth do I write that?

;; these momentary effects are instruments, and I'll use the standard api for midi isntruments too. The note can be the speed.

;(voice1)
;(inst-fx! voice1 delay-oneshot)
;(clear-fx pitchf1)
;(def meow (inst-fx! pitchf1 delay-oneshot))

;(ctl meow :gate 1)
