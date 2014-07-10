;; Copyright (c) 2014, Andrey Antukh
;; Copyright (c) 2014, Alejandro Gómez
;; All rights reserved.
;;
;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions
;; are met:
;;
;; 1. Redistributions of source code must retain the above copyright
;;    notice, this list of conditions and the following disclaimer.
;; 2. Redistributions in binary form must reproduce the above copyright
;;    notice, this list of conditions and the following disclaimer in the
;;    documentation and/or other materials provided with the distribution.
;;
;; THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
;; IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
;; OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
;; IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
;; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
;; NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
;; DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
;; THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
;; THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns cats.builtin
  "Clojure(Script) builtin types extensions."
  (:require [clojure.set :as s]
            [cats.protocols :as proto]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LazySeq
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend-type #+clj  clojure.lang.LazySeq
             #+cljs cljs.core.LazySeq
  proto/Functor
  (fmap [self f] (map f self))

  proto/Applicative
  (pure [_ v] (lazy-seq [v]))
  (fapply [self av]
    (for [f self
          v av]
         (f v)))

  proto/Monad
  (bind [self f]
    (apply concat (map f self))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vector
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend-type #+clj clojure.lang.PersistentVector
             #+cljs cljs.core.PersistentVector
  proto/Functor
  (fmap [self f] (vec (map f self)))

  proto/Applicative
  (pure [_ v] [v])
  (fapply [self av]
    (vec (for [f self
               v av]
           (f v))))

  proto/Monad
  (bind [self f]
    (into []
          (apply concat (map f self))))

  proto/MonadZero
  (mzero [_] [])

  proto/MonadPlus
  (mplus [mv mv'] (into mv mv')))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Set
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend-type #+clj clojure.lang.PersistentHashSet
             #+cljs cljs.core.PersistentHashSet
  proto/Functor
  (fmap [self f]
    (set (map f self)))

  proto/Applicative
  (pure [_ v] #{v})

  (fapply [self av]
    (set (for [f self
               v av]
           (f v))))

  proto/Monad
  (bind [self f]
    (apply s/union (map f self)))

  proto/MonadZero
  (mzero [_] #{})

  proto/MonadPlus
  (mplus [mv mv']
    (s/union mv mv')))