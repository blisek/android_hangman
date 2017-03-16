package com.blisek.wisielec

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val unknownChar: Char = '_'
    private val hangmans: IntArray
    private val endState: Int
    private var currentState: Int
    private var gameOn: Boolean
    private var uncoveredLettersLeft: Int = -1

    private lateinit var selectedWord: String
    private lateinit var enteredWords: Array<Char>
    private lateinit var hangmanView: ImageView
    private lateinit var searchedWord: TextView
    private lateinit var headerTextView: TextView
    private lateinit var footerTextView: TextView

    init {
        hangmans = intArrayOf(R.drawable.hangman0, R.drawable.hangman1, R.drawable.hangman2,
                R.drawable.hangman3, R.drawable.hangman4, R.drawable.hangman5, R.drawable.hangman6)
        currentState = -1
        endState = hangmans.lastIndex
        gameOn = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hangmanView = findViewById(R.id.hangmanImageView) as ImageView
        searchedWord = findViewById(R.id.searchedWordTextView) as TextView
        headerTextView = findViewById(R.id.headerTextView) as TextView
        footerTextView = findViewById(R.id.footerTextView) as TextView
        initWordVariables()
        createLetterButton()
        updateEnteredWord()
        updateLeftGuessTries()
    }

    private fun initWordVariables() {
        val stringArray: Array<String> = resources.getStringArray(R.array.words);
        val rnd = Random()
        selectedWord = stringArray[rnd.nextInt(stringArray.size)]
        enteredWords = Array(selectedWord.length, { unknownChar })
        uncoveredLettersLeft = enteredWords.size
        headerTextView.text = resources.getString(R.string.gameStartMessage)
    }

    private fun createLetterButton() {
        val alphabetLettersLayout = findViewById(R.id.alphabetLettersLayout) as LinearLayout
        for(letter in 'a'..'z') {
            val btn = Button(this)
            btn.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            btn.text = letter.toString()
            btn.tag = letter
            btn.setOnClickListener { onLetterButtonClick(it as Button) }

            alphabetLettersLayout.addView(btn)
        }
    }

    private fun onLetterButtonClick(btn: Button) {
        if(!gameOn) return
        btn.isEnabled = false
        testLetter(btn.tag as Char)
    }

    private fun testLetter(letter: Char) {
        if(!(letter in selectedWord)) {
            advanceHangman()
            return
        }

        var insertedLetters = 0
        for(i in selectedWord.indices) {
            if(selectedWord[i] == letter) {
                enteredWords[i] = letter
                ++insertedLetters
                --uncoveredLettersLeft
            }
        }

        if(insertedLetters > 0) {
            updateEnteredWord()
            checkWinConditions()
        }
    }

    private fun checkWinConditions() {
        if(uncoveredLettersLeft <= 0)
            gameOverWin()
    }

    private fun updateEnteredWord() {
        searchedWord.text = enteredWords.joinToString(separator = " ")
    }

    private fun advanceHangman() {
        if(++currentState == endState) {
            gameOverLost()
            return
        }

        hangmanView.setImageDrawable(resources.getDrawable(hangmans[currentState]))
        updateLeftGuessTries()
    }

    private fun updateLeftGuessTries() {
        footerTextView.text = String.format("Pozostało prób: %d", endState - currentState)
    }

    private fun gameOverLost() {
        headerTextView.text = resources.getString(R.string.gameLost)
        gameOn = false
    }

    private fun gameOverWin() {
        headerTextView.text = resources.getString(R.string.gameWin)
        gameOn = false
    }
}
