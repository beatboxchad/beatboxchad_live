(defproject beatboxchad_live "0.2"
  :description "Actual music code I intend to use live"
  :url "http://www.beatboxchad.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [overtone "0.10.1"]
                 [org.clojure/core.async "0.2.395"]
                 [mount "0.1.11"]
                 [me.raynes/conch "0.8.0"]
                 ]
  :jvm-opts ^:replace ["-Xss2m" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]
  ;:test-selectors {:sooperlooper :sooperlooper}
  )
