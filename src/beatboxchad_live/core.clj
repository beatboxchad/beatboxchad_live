(ns beatboxchad-live.core
  [:require 
   [clojure.java.shell :as shell]
   [overtone.core :refer :all]
   [overtone.libs.deps :refer :all]
   [overtone.config.log :as log]
   ;[beatboxchad-live.midi :refer :all]
   ;[beatboxchad-live.sooperlooper :refer :all]
   ;[beatboxchad-live.server-fx :refer :all]
   ;[beatboxchad-live.inst :refer :all]
   ]
  )

(def overtone-osc (osc-server 9960 "osc-overtone"))

; stolen from Overtone
(defn- logged-sh
  "Run a shell command and log any errors. Returns stdout."
  [cmd & args]
  (let [res (apply shell/sh cmd args)]
    (when-not (zero? (:exit res))
      (log/error "Subprocess error: " (:err res)))
    (:out res)))

(def num-hw-inputs #(count (re-seq #"system:capture_[0-9]*" (logged-sh "jack_lsp"))))


(defn start-processes []
  (let [threads [(Thread. #(logged-sh "alsa_out" "-d" "hw:PCH" "-j" "monitor"))
                (Thread. #(logged-sh "scide"))
                (Thread. #(logged-sh "slgui"))
                ]]
    (doseq [thread threads]
      (.setDaemon thread true)
      (.start thread)
      )
    )
  )


(defn evil-kill-shutdown 
  [] 
  "Bad bad bad! Later I'll be less lazy and actually track them processes."
  (let [pnames ["scide" "sooperlooper" "scsynth" "slgui"]]
         (map (fn [pname] (logged-sh "pkill" pname)) (map clojure.string/trim pnames))
         )
    )


;; define a bunch of instruments. Thanks to
;; http://stackoverflow.com/questions/2486752/in-clojure-how-to-define-a-variable-named-by-a-string
;; for showing me the technique.
(defn build-hw-inputs [input-count]
  (do
    (map 
      (fn [n]
        (let [instname (symbol (str "in-" n))]
          (intern 'beatboxchad-live.core instname 
                  (eval `(inst ~instname [] (sound-in:ar ~n)))
                  )
          )
        )
      (range (inc input-count))
      )
    ;(satisfy-deps :setup-live-insts)
    )
  )

(build-hw-inputs (num-hw-inputs))
(on-deps :studio-setup-completed
         ::setup-live-insts1
         (build-hw-inputs num-hw-inputs)
)

;; set them all into fx mode. Out the same channel they came in on.
(defn fx-mode [input-count]
  (map 
    (fn [n]
      (let [instname (symbol (str "beatboxchad-live.core/in-" (str n)))]
        (ctl (:mixer @(resolve instname)) :out-bus n)
        )
      )
    (range input-count)
    )
  )
(fx-mode (num-hw-inputs))

