(ns kolmogorov-music.champernowne
  (:import [java.lang Math]
           [java.util Collections]))

(defn decompose [n base]
  (let [[remainder quotient] ((juxt mod quot) n base)]
    (if (zero? quotient)
      [remainder]
      (conj (decompose quotient base) remainder))))

(defn word
  ([base from]
   (->> (range)
        (map (partial + from))
        (mapcat #(decompose % base))))
  ([base]
   (word base 0)))

(defn rightshift [n distance base]
  (/ n (int (Math/pow base distance))))

(defn expand [base digits]
  (->> digits
       (map #(rightshift %2 %1 base) (range))
       (apply +)))

(defn constant
  [precision base]
  (->> (word base)
       (take precision)
       (expand base)))

(defn prefix? [sub super]
  (= sub (take (count sub) super)))

(defn windows [super]
  (map (fn [i] [i (drop i super)]) (range)))

(defn index [sub base]
  (->> (word base)
       windows
       (some (fn [[i super]] (when (prefix? sub super) i)))))