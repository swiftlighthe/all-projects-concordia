;;; SOEN6441 CourseWork
;;; 
;;; KENKEN Puzzle (http://www.kenken.com/) developed with Common LISP.
;;; 
;;; This file is created by Yuan Tao (ewan.msn@gmail.com)
;;; Licensed under GNU GPL v3

;;; $Author$
;;; $Date$
;;; $Rev$
;;; $HeadURL$
;;;

(defparameter *games* 
  (list              ; list of games
   (list 'game1 1 (list (list 1 '= 'a1)))
   
   (list             ; list of elements of a game 
    'game2           ; game name or number
    2                ; game size
    (list            ; list of cages.
     (list           ; list of elements of a cage
      2 '=           ; value & operator of the cage.  
      'a1)           ; cells of the cage
     (list 
      2 '*
      'a2 'b1 'b2)
     ))
   
   (list 'game3 3 (list (list 2 '- 'a1 'a2) (list 12 '* 'a3 'b2 'b3) (list 3 '+ 'b1 'c1)
                        (list 3 '/ 'c2 'c3)))
   (list 'game4 4 (list (list 2 '= 'a1) (list 7 '+ 'a2 'a3) (list 2 '- 'a4 'b4)
                        (list 12 '* 'b1 'c1) (list 3 '+ 'b2 'c2) (list 1 '= 'b3)
                        (list 3 '- 'd1 'd2) (list 1 '- 'c3 'd3) (list 2 '/ 'c4 'd4)))
   (list 'game5 5 (list (list 2 '/ 'a1 'a2) (list 1 '- 'a3 'b3) (list 15 '* 'a4 'a5)
                        (list 9 '+ 'b1 'b2) (list 4 '+ 'b4 'c4) (list 3 '- 'b5 'c5)
                        (list 4 '+ 'c1 'd1) (list 6 '* 'c2 'd2) (list 1 '- 'c3 'd3)
                        (list 3 '- 'd4 'd5) (list 4 '- 'e1 'e2) (list 9 '+ 'e3 'e4 'e5)))
   (list 'game6 6 (list (list 5 '- 'a1 'a2) (list 9 '* 'a3 'b3 'b4) (list 120 '* 'a4 'a5 'b5) (list 2 '/ 'a6 'b6)
                        (list 14 '+ 'b1 'b2 'c1 'd1) (list 6 '+ 'c2 'd2) (list 9 '+ 'c3 'd3) (list 11 '+ 'c4 'c5 'd5)
                        (list 5 '- 'c6 'd6) (list 48 '* 'e1 'f1 'f2) (list 1 '- 'd4 'e4) (list 3 '- 'e2 'e3)
                        (list 3 '- 'f4 'f3) (list 5 '+ 'e5 'f5) (list 2 '- 'e6 'f6)))
   ))

;(defparameter *cur-game* (car *games*))
(defparameter *cur-values* (list (list 'a1 0)))

; ((GAME1 2 ((= 2 A1) (* 2 A2 B1 B2))))
; the format of a game layout:
;
;        1       2               ; first line of layout
;    +-------+-------+           ; 2th
; A  |2=     |2*     |           ; 3th
;    |   1   |       |           ; 4th
;    +-------+       +           ; 2th
; B  |               |
;    |               |
;    +-------+-------+           ; last line
(defun print-game (game)
  (format t "Game Name: ~D~%" (car game))
  (let ((size (cadr game)))
    ; print the first line
    (format t "    ")         
    (dotimes (i size)          
      (format t "    ~D   " (+ 1 i)))
    (format t "~%")
    
    (loop for i from 1 to size do           ; line of cells
          ; 2th line
          (format t "    ")
          
          ; first column of cell
          (loop for j from 1 to size do 
                (if (= i 1)
                    (format t "+-------")
                  (if (if-2cells-connected (- i 1) j i j)
                      (format t "+       ")
                    (format t "+-------"))))		
          
          (format t "+~%")
          
          ; 3th line
          (format t " ~D  " (get-letter-from-num i))
          
          ; first column of cells
          (let ((opv (get-op-and-value i 1)))
            (if (= i 1)
                (if (if-first-cell-of-cage i 1)
                    (print-cage-info (car opv) (cadr opv))
                  (format t "|       "))
              ; if current cell is connceted with above cell
              (if (if-2cells-connected (- i 1) 1 i 1)
                  (format t "|       ")
                (if (if-first-cell-of-cage i 1)
                    (print-cage-info (car opv) (cadr opv))
                  (format t "|       ")))))
          
          ; other columns of cells
          (loop for j from 2 to size do 
                (let ((opv (get-op-and-value i j)))
                  ; if current cell is connceted with left cell or above cell
                  (if (if-2cells-connected i (- j 1) i j)
                      (format t "        ")
                    (if (if-first-cell-of-cage i j)
                        (print-cage-info (car opv) (cadr opv))
                      (format t "|       ")))))
          
          ; last edge of the table
          (format t "|~%")
          
          ; 4th line
          (format t "    ")
          
          ; first column
          (let ((v (get-value-of-cell i 1)))
            (if (null v)
                (format t "|       ")
              (format t "|   ~D   " v)))
          
          ; other columns
          (loop for j from 2 to size do 
                (let ((v (get-value-of-cell i j)))
                  ; if current cell is connceted with left cell
                  (if (if-2cells-connected i (- j 1) i j)
                      (if (null v)
                          (format t "        ")
                        (format t "    ~D   " v))
                    (if (null v)
                        (format t "|       ")
                      (format t "|   ~D   " v)))))
          (format t "|~%"))
    
    ; last line
    (format t "    ")
    (dotimes (i size)          
      (format t "+-------"))
    (format t "+~%")
    ))
	  
(defun print-games ()
  (dolist (e *games*)
    (defparameter *cur-game* e)
	(print-game e)))

(defun get-letter-from-num (num)
  (cond ((= num 1) 'A)
        ((= num 2) 'B)
        ((= num 3) 'C)
        ((= num 4) 'D)
        ((= num 5) 'E)
        ((= num 6) 'F)))

; get a list containing the operator and the value of the element (i j)
; e.g ; ((GAME1 2 ((= 2 A1) (* 2 A2 B1 B2))))
; CG-USER(51): (GET-OP-AND-VALUE 2 1)
; (* 2 A2 B1 B2)
(defun get-op-and-value (i j)
  (dolist (e (caddr *cur-game*))
    (dolist (k (cddr e))
      (if (string-equal (get-cell-name i j) (write-to-string k))
          (return-from get-op-and-value e)
        nil))))

(defun get-cell-name (i j)
  (concatenate 'string (write-to-string (get-letter-from-num i))
    (write-to-string j)))

; check if two cells are connected or they are in the same cage
(defun if-2cells-connected (i1 j1 i2 j2)
  (equal (get-op-and-value i1 j1) (get-op-and-value i2 j2)))

; check if the name of cell is the smallest one of the cage
(defun if-first-cell-of-cage (i j)
  (let ((c (get-cell-name i j)))
    (dolist (e (cddr (get-op-and-value i j)))
      (if (string< e c)
          (return-from if-first-cell-of-cage nil)))
    t))

; get value of the cell from the values list
; e.g. ((A1 1) (A2 2))
(defun get-value-of-cell (i j)
  (let ((c (get-cell-name i j)))
    (dolist (e *cur-values*)
      (if (string-equal c (car e))
          (if (null (cadr e))
              (return-from get-value-of-cell nil)
            (if (or (< (cadr e) 1) (> (cadr e) (cadr *cur-game*)))
                (return-from get-value-of-cell nil)
              (return-from get-value-of-cell (cadr e))))
        nil))))

; print the value and operator of the cage
(defun print-cage-info (num op)
  (if (> num 100)
      (format t "|~D~D   " num op)
    (if (> num 10)
        (format t "|~D~D    " num op)
      (format t "|~D~D     " num op))))

; start to play the game
; 1. select a game
; 2. keep inputing cell values and check the values
; 3. go back to 1
(defun play-game ()
  (loop 
    (print-games)
    (if (null (select-a-game))
        (return-from play-game nil)
      (if (null (check-cell-values))
          (return-from play-game nil)
        (if (prompt-play-again)
            (return-from play-game nil)
          t)))))

; recusively asking user to select a game
(defun select-a-game ()
  (loop 
    (let ((game (prompt-select-game)))
      (if (or (string-equal game "q") (string-equal game "quit"))
          (return-from select-a-game nil)              ; exit
        (dolist (e *games*)
          (if (string-equal game (car e))
              (progn (defparameter *cur-game* e)
                (return-from select-a-game e))
            nil)))
        (format t "Invalid game name! ~%"))))

(defun prompt-select-game ()
  (format t "Please select a game by name (type q to exit game):~%")
  (read-line *query-io*))

; check cell values. if all cells have a value, check the solution.
; otherwise, continue asking user to input cell values.
(defun check-cell-values ()
  (loop
    ; keep inputing cell values
    (let ((cvs (prompt-cell-values)))
      (if (or (string-equal cvs "q") (string-equal cvs "quit"))
          (return-from check-cell-values nil)          ; exit
        (parse-cell-values cvs))
      ;(print *cur-values*)
      )
    
    ; if all cells of the game have a value, check if the values are right
    (if (= (length *cur-values*) (* (cadr *cur-game*) (cadr *cur-game*)))
      (if (if-valid-solution)
          (progn
            (format t "Congratulations!~%")
            (return-from check-cell-values t))         ; OK
          (progn
            (format t "Invalid solution, please check cell values.~%")))
      nil)))
      
(defun prompt-cell-values ()
  (print-game *cur-game*)    
  (format t "Please set values of cells (e.g. a1=1 b2=2 a1=2) (type q to exit game):~%")
  (read-line *query-io*))

(defun prompt-play-again ()
  (y-or-n-p "Play again? [y/n]: "))

; parse and store cell values from user input
; format: a1=1 b2=2 a1=2 ...
(defun parse-cell-values (cvs)
  (dolist (e (split-by-one-space cvs))
    (store-cell-value (get-key-value-pair e))))

; Returns a list of substrings of string divided by ONE space each.
; Note: Two consecutive spaces will be seen as if there were an empty string between them."
; This function is copied from http://cl-cookbook.sourceforge.net/strings.html
(defun split-by-one-space (string)
    (loop for i = 0 then (1+ j)
          as j = (position #\Space string :start i)
          collect (subseq string i j)
        while j))

; returns a list with two elements: key and value, from "key=value"
(defun get-key-value-pair (string)
  (if (find #\= string :test #'equal)
      (list (subseq string 0 2) (parse-integer (subseq string 3)))
    nil))

; replace an element (which is a list) of a list
; lst: (("a1" 1) ("a2" 2) ("b1" 2))
; ele-lst: ("a2" 1)
(defun replace-a-value (lst ele)
  (if (null ele)
      lst
    (if (null lst)
        (list ele)
      (if (string-equal (caar lst) (car ele))
          (cons ele (cdr lst))
        (cons (car lst) (replace-a-value (cdr lst) ele))))))
  
(defun store-cell-value (lst)
    (setf *cur-values* (replace-a-value *cur-values* lst)))
            
(defun if-valid-solution ()
  (print-game *cur-game*) 
  t)



  
