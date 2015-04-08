(ns kolmogorov-music.song
  (:require [overtone.live :refer :all]
            [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [leipzig.chord :as chord]
            [leipzig.temperament :as temperament]
            [kolmogorov-music.champernowne :as champernowne]))

(def master-volume 0.03)

(definst buzz [freq 110 vol 1.0 dur 1.0]
  (-> (saw freq)
      (rlpf (* 4 440) (line:kr 1/10 1/20 dur))
      (* (env-gen (perc 0.01 0.5) :action FREE))
      (* vol master-volume)))

(definst sing [freq 110 dur 1.0 vol 1.0]
  (-> (sin-osc freq)
      (+ (sin-osc (* 3.01 freq)))
      (+ (* 1/4 (sin-osc (* 2.01 freq))))
      (+ (* 1/3 (sin-osc (* 1/2 freq))))
      (rlpf (line:kr 3000 500 dur) 1/50)
      (clip2 0.3)
      (* (env-gen (adsr 0.05 (* dur 1/3) 0.2) (line:kr 1 0 dur) :action FREE))
      (* vol master-volume)))

; Arrangement
(defmethod live/play-note :dux [{hertz :pitch seconds :duration}]
  (when hertz (buzz hertz seconds)))
(defmethod live/play-note :comes [{hertz :pitch seconds :duration}]
  (when hertz (sing hertz seconds)))
(defmethod live/play-note :bass [{hertz :pitch seconds :duration}]
  (when hertz (sing (* 1/2 hertz) seconds)))

(defn construct [time duration pitch part]
  {:time time 
   :pitch pitch
   :duration duration
   :part part})

(defn most-behind [m]
  (first (apply min-key second m)))

(defn synchronise [m]
  (zipmap (keys m) (repeat (apply max (vals m)))) )

(defn encode
  ([state [a b c d & digits]]
   (if (zero? (* a b))
     (encode (synchronise state) digits)
     (let [part (most-behind state)
           duration (/ a b)
           pitch (+ c d)
           time (part state)]
       (cons (construct time duration pitch part)
             (lazy-seq (->> digits
                            (encode (update-in state [part] (partial + duration))))))))))

(def track
  (->>
    (champernowne/word 10
                       7207120772071207720712077207120772031203720312037203120312551255125412541253125312521232
                       )
    ; (encode {:dux 0 :comes 0 :bass 0})
    (encode {:dux 0})
    (wherever :pitch, :pitch (comp temperament/equal scale/A scale/minor scale/lower))
    (where :time (bpm 120))
    (where :duration (bpm 120))))

(comment
            
   ; Loop the track, allowing live editing.
  (live/jam (var track))
  (fx-reverb)
  (fx-chorus)
  (fx-distortion)
  
  )
