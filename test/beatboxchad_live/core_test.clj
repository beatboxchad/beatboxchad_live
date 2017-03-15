(ns beatboxchad-live.core-test
  (:require [clojure.test :refer :all]
            [overtone.core :refer :all]
            [mount.core :as mount]
            [beatboxchad-live.core :refer :all]
            [beatboxchad-live.sooperlooper :refer :all]
            [beatboxchad-live.osc :refer :all]
            )
  )

(defn sooperlooper-fixture [f]
  (osc-send engine "/register" "localhost:9960" "/loop-count")
  (def sooperlooper-response (atom {}))
  (mount/start)
  (f)
  (mount/stop)
  )

(use-fixtures :once sooperlooper-fixture)

(deftest state_test
  (testing "Starting sooperlooper results in a Sooperlooper process that can be pinged"
    (osc-recv overtone-osc "/sl-up-test" 
                (fn [msg]
                  (swap! sooperlooper-response assoc :result msg))
                1)
      (ping-sooperlooper  9960 "/sl-up-test")
      (Thread/sleep 1000) ; FIXME I really wanna find a clojure native way to
                          ; wait for the OSC callback fn to do its thing. This is hacky.
      (is (= "/sl-up-test" (:path (:result @sooperlooper-response))))
    )
    
  (testing "setting up sooperlooper results in a number of mono loops matching the number of JACK hardware inputs"
    ;(setup-sooperlooper)
    ; then ping sooperlooper and examine the loopcount
    )

  (testing "setting up sooperlooper results in a number of mono loops matching the number of JACK hardware inputs"
    ()
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
