;;--
;;
;; This is a dumping ground for synthdef designs and livecoding muckery.
;;
;; I mean, the whole project is kind of like that but shut up
;;
;;
;;
;;++


(ns beatboxchad-live.sounds
  [:require
   [overtone.core :refer :all]
   [beatboxchad-live.core :refer :all]
                                        ;[beatboxchad-live.midi :refer :all]
   [beatboxchad-live.fx   :refer :all]
   ]

  (:require [mount.core :as mount]))

;; I'm gonna want an instrument for each HW input, which means I'll probably wanna resurrect that
;; intern trick  I used before.

;; In addition,  I'll want to automatically allocate a feedback line for each delay AND be able to throw
;; fx on that feedback line. So those will have to behave like instruments too. I'll just control their busses
;;

;; Or I could actually just use a hard numbering scheme. So there are always two available feedback busses for each hw instrument, or something.
(mount/stop)
(mount/start)
(definst guitar []
  (sound-in 0)
  )



(guitar)
(kill guitar)

(definst voice1 []
  (let [sig (sound-in:ar 1)]
    sig
     )
  )
(mount/start)
(mount/stop)
(voice1)
(kill voice1)
;(clear-fx voice1)


(def guitar-echo (inst-fx! guitar fx-echo-pi))
(def guitar-wobble (inst-fx! guitar fx-wobble))
((kill guitar-wobble)
 def guitar-distortion (inst-fx! guitar fx-distortion2))

(kill guitar-echo)
(def-fx harmonizer-pitchfollow
   [sig-bus 1]
  [harm-pitch (pitch:kr sig-bus
                   :exec-freq 500
                   :down-sample 0.1)
   base-pitch (pitch:kr dry-l)

   amp (amplitude:kr sig-bus)
   interval (- base-pitch harm-pitch)
   wet-l (+ dry-l (pitch-shift:ar dry-l :pitch-ratio interval))
   wet-r (+ dry-r (pitch-shift:ar dry-r :pitch-ratio interval))

   ]

  )



(def test1 (inst-fx! guitar harmonizer-pitchfollow))
(kill test1)

(def verb (inst-fx! guitar fx-reverb))
(kill verb)

(def cow (inst-fx! guitar fx-flanger))
(def test2 (inst-fx! voice1 harmonizer-chaos))

(ctl test2 :rate 1000000)
(ctl test2 :multiplier 31)
(kill test2)
(clear-fx guitar)

;; Random glitch harmonizer. The pitch of the instrument provides a random seed for a noise generating Ugen
;; This ugen controls the interval to pitch-shift:ar. Beautiful chaos.
(def-fx harmonizer-chaos
   [sig-bus 1 rate 2000 multiplier 4]
  [


   interval (* multiplier (lfd-noise0:kr rate))
   wet-l (+ dry-l (pitch-shift:ar dry-l :pitch-ratio interval))
   wet-r (+ dry-r (pitch-shift:ar dry-r :pitch-ratio interval))

   ]

  )


;; delay with a pitch shifter in the feedback loop. The shifted pitch is automated to drift between +/-3% of
;; the base pitch by a sine wave whose frequency is a multiple of the delaytime. Also in fb loop: RHPF with cutoff frequency 200 and an 0.8 reciprocal


(def meow (inst-fx! voice1 delay-pitchshift))
(ctl meow :decay 0.98)
(ctl meow :decay 1.01)

(ctl meow :speed 0.4)

(def cow (inst-fx! voice1 fx-scope_out))
(clear-fx voice1)

(def-fx test [speed 0.2]
  [wet-l dry-l
   wet-r dry-r])

(on-event (conj v49 :control-change)
          (fn [e]
            (if (= (:note e) 20)
              (let [dtime (:velocity-f e)]
                (ctl beatboxchad-live.sounds/guitar-echo
                     :phase_slide dtime
                     :phase dtime
                     :decay_slide (* 16 dtime)
                     :decay (* 16 dtime))
                )


            ;(clojure.pprint/pprint (:note e))
            )
            )
          ::v49-delay-control
          )


(on-event (conj v49 :control-change)
          (fn [e]
            (if (= (:note e) 21)
            (ctl beatboxchad-live.sounds/guitar-distortion :amount (:velocity-f e))
            )
            )
          ::v49-distortion-control
          )

(remove-event-handler :v49-delay-control)
(remove-event-handler :v49-distortion-control)
(inst-fx! guitar fx-distortion-tubescreamer)
(inst-fx! guitar fx-echo)
(ctl guitar-echo :delay-time 0.5)
(ctl guitar-echo :decay-time 5.5)
(clear-fx guitar)
(voice1)
(inst-fx! voice1 fx-freeverb)
(inst-volume! voice1 10)
(voice1)

(kill guitar)
;;
(definst pitchf1 [databus 1]
  (let [in (sound-in databus)
        freq (/ (pitch:kr in) 1)

        amp (amplitude:kr in)]
    (* (mix (sin-osc:ar (map #(* % freq ) [4 1 2]) 0 (lf-noise1:kr [0.3 0.1 0.1]) )) amp)
    )
  )

(kill pitchf1)
(pitchf1)
(ctl pitchf1 :databus 0)
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
  "harmonizes the bus based on the note. Control it with a keyboard"
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

(definst harmonize-input-normalized
  "like harmonize-input, but restricts the harmonized notes to the nearest octave for a less glitchy chipmunk sound"
  [note 60 velocity 80 gate 1 bend 0.0 bus 1]
  (let [in (sound-in bus)
        pitch (pitch:kr in
                        :exec-freq 500
                        :down-sample 0.1)
        amp (/ velocity 43)
        env (env-gen (adsr 0.01 0.1 1 0.3) gate :action FREE)
        orig-midinote (cpsmidi pitch)
        interval (- note orig-midinote)
        ;;FIXME round that interval down
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

(def harmonizer (midi-poly-player harmonize-input (:overtone.studio.midi/full-device-key korg) :none ))

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


(def shitter (midi-poly-player shitbells v49 :none ))
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

;; these momentary effects are instruments, and I'll use the standard api for
;; midi isntruments too. The note can be the speed.

;(voice1)
;(inst-fx! voice1 delay-oneshot)
;(clear-fx pitchf1)
;(def meow (inst-fx! pitchf1 delay-oneshot))

;(ctl meow :gate 1)
