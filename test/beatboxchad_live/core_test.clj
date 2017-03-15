(ns beatboxchad-live.core-test
  (:require [clojure.test :refer :all]
            [overtone.core :refer :all]
            [mount.core :as mount]
            [beatboxchad-live.core :refer :all]
            [beatboxchad-live.sooperlooper :as sooperlooper]
            [beatboxchad-live.osc :refer :all]
            [beatboxchad-live.util :as util]
            )
  )

(defn sooperlooper-fixture [f]
  (def sooperlooper-response (atom {}))
  (mount/start)
  (f)
  (mount/stop)
  )

(use-fixtures :once sooperlooper-fixture)

(deftest setup-test 
  (testing "Starting sooperlooper results in a Sooperlooper process that can be pinged"
    (osc-recv overtone-osc "/sl-up-test" 
                (fn [msg]
                  (swap! sooperlooper-response assoc :result msg))
                1)
      (sooperlooper/ping  9960 "/sl-up-test")
      (Thread/sleep 1000) ; FIXME I really wanna find a clojure native way to
                          ; wait for the OSC callback fn to do its thing. This is hacky.
      (is (= "/sl-up-test" (:path (:result @sooperlooper-response))))
    )
    
  (testing "setting up sooperlooper results in a number of mono loops matching the number of JACK hardware inputs"
    (sooperlooper/init-loops)
    (osc-recv overtone-osc "/loopcount-test" 
                (fn [msg]
                  (swap! sooperlooper-response assoc :result msg))
                1)
      (sooperlooper/ping  9960 "/loopcount-test")
      (Thread/sleep 1000) ; FIXME I really wanna find a clojure native way to
                          ; wait for the OSC callback fn to do its thing. This is hacky.
    ; then ping sooperlooper and examine the loopcount
    (is (= sooperlooper/loop-count (last (:args(:result @sooperlooper-response)))))
    )

  (testing "Stopping sooperlooper state results in an OSC response to /quit and no sooperlooper process"
    ()
    )

  (testing "Starting external server results in a pingable scsynth process"
    ()
    )

  (testing "Stopping scsynth state results in an OSC response and no sooperlooper process"
    ()
    )
  )
