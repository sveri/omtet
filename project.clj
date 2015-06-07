(defproject
  omtet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/clojurescript "0.0-3308"]

                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]

                 [ring "1.3.2"]
                 [lib-noir "0.9.9"]
                 [ring-server "0.4.0"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [compojure "1.3.4"]
                 [reagent "0.5.0"]
                 [environ "1.0.0"]
                 [leiningen "2.5.1"]
                 [http-kit "2.1.19"]
                 [selmer "0.8.2"]
                 [prone "0.8.2"]
                 [im.chit/cronj "1.4.3"]
                 [com.taoensso/timbre "3.4.0"]
                 [noir-exception "0.2.5"]

                 [buddy/buddy-auth "0.5.3"]
                 [buddy/buddy-hashers "0.4.2"]

                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [org.clojure/java.jdbc "0.3.7"]
                 [korma "0.4.1"]
                 [com.h2database/h2 "1.4.187"]
                 [joplin.core "0.2.9"]

                 [com.draines/postal "1.11.3"]

                 [jarohen/nomad "0.7.1"]

                 [de.sveri/clojure-commons "0.2.0"]

                 [clojure-miniprofiler "0.4.0"]

                 [org.danielsz/system "0.1.8"]

                 [datascript "0.11.3"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]

                 [re-frame "0.4.1"]]

  :plugins [[de.sveri/closp-crud "0.1.0"]
            [lein-cljsbuild "1.0.5"]
            [ragtime/ragtime.lein "0.3.8"]]

  ;database migrations
  :joplin {:migrators {:sql-mig "joplin/migrators/sql"}}

  :closp-crud {:jdbc-url               "jdbc:h2:mem:test_mem"
               :migrations-output-path "./resources/migrators/sql"
               :clj-src                "src/clj"
               :ns-db                  "de.sveri.omtet.db"
               :ns-routes              "de.sveri.omtet.routes"
               :ns-layout              "de.sveri.omtet.layout"
               :templates              "resources/templates"}

  :min-lein-version "2.5.0"

  :uberjar-name "omtet.jar"

  ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
  ;:hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds
   {:dev {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
          :figwheel {:css-dirs ["resources/public/css"]             ;; watch and update CSS
                     :on-jsload "omtet.dev/main"}
          :compiler     {:main                 omtet.dev
                         :asset-path           "/js/out"
                         :output-to            "resources/public/js/app.js"
                         :output-dir           "resources/public/js/out"
                         :optimizations  :none
                         :cache-analysis true
                         :pretty-print   true
                         :source-map-timestamp true}}
    :adv {:source-paths ["src/cljs" "src/cljc"]
          :compiler     {
                         :output-to     "resources/public/js/app.js"
                         ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
                         ;:jar           true
                         :optimizations :advanced
                         :pretty-print  false}}}}

  :profiles {:dev     {:repl-options {:init-ns de.sveri.omtet.user}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.3.3"]
                                      [joplin.lein "0.2.9"]]

                       :dependencies [[ring-mock "0.1.5"]
                                      [ring/ring-devel "1.3.2"]
                                      [pjstadig/humane-test-output "0.7.0"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]

                       :joplin       {:databases    {:sql-dev {:type :sql, :url "jdbc:h2:./db/korma.db"}}
                                      :environments {:sql-dev-env [{:db :sql-dev, :migrator :sql-mig}]}}}

             :uberjar {:auto-clean  false                   ; not sure about this one
                       :omit-source true
                       :aot         :all}}

  :main de.sveri.omtet.core

  :aliases {"rel-jar" ["do" "clean," "cljsbuild" "once" "adv," "uberjar"]})
