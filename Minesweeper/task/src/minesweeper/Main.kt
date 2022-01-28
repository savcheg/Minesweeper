package minesweeper

import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

class Minesweeper {
    private var mineField: MutableList<MutableList<Char>>
    private var displayableMineField: MutableList<MutableList<Char>>
    private var isSetted = false
    private val minesSymbol = 'X'
    private val safeCell = '.'
    private val markedCell = '*'
    private val spaceSymbol = '/'
    private var mines: Int = 0
    private val rows = 9
    private val columns = 9
    private var minesCount = 0

    init {
        mineField = (0 until rows).map {
            (0 until columns).map {
                safeCell
            }.toMutableList()
        }.toMutableList()
        displayableMineField = (0 until rows).map {
            (0 until columns).map {
                safeCell
            }.toMutableList()
        }.toMutableList()
    }

    private fun scanMines() {
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (mineField[row][col] != minesSymbol) {
                    val count = countMines(row, col)
                    if (count > 0) {
                        mineField[row][col] = count.toString().single()

                    }
                }
            }
        }
    }

    private fun countMines(row: Int, col: Int): Int {
        var count = 0

        if (isValidCoordinate(row - 1, col)) {
            if (isMine(row - 1, col))
                count++
        }

        if (isValidCoordinate(row + 1, col)) {
            if (isMine(row + 1, col))
                count++
        }

        if (isValidCoordinate(row, col - 1)) {
            if (isMine(row, col - 1))
                count++
        }

        if (isValidCoordinate(row, col + 1)) {
            if (isMine(row, col + 1))
                count++
        }

        if (isValidCoordinate(row - 1, col - 1)) {
            if (isMine(row - 1, col - 1))
                count++
        }

        if (isValidCoordinate(row + 1, col - 1)) {
            if (isMine(row + 1, col - 1))
                count++
        }

        if (isValidCoordinate(row - 1, col + 1)) {
            if (isMine(row - 1, col + 1))
                count++
        }

        if (isValidCoordinate(row + 1, col + 1)) {
            if (isMine(row + 1, col + 1))
                count++
        }

        return count
    }

    private fun randomMines(xInd: Int, yInd: Int) {
        var list = IntArray(81) { it }
        list.shuffle()
        var i = 0
        var a: Int
        var column: Int
        var row: Int
        while (mines > 0) {
            a = list[i]
            column = a / 9
            row = a % 9
            if (column != xInd || row != yInd) {
                mineField[column][row] = minesSymbol
                mines--
            }
            i++
        }
        isSetted = true
    }

    private fun display() {
        print(
            """
             │123456789│
            —│—————————│
            
        """.trimIndent()
        )
        for (row in 0 until rows) {
            print("${row + 1}|")
            for (column in 0 until columns) {
                print(displayableMineField[row][column])
//                print(mineField[row][column])
            }
            println("|")
        }
        println("—│—————————│")
    }

    private fun selectItem() {
        if (minesCount == 0) {
            println("Congratulations! You found all the mines!")
            exitProcess(1)
        }
        print("Set/delete mine marks (x and y coordinates): ")
        val coords = readLine()!!.removePrefix("> ").split(" ")
        val xInd = coords[1].toInt() - 1
        val yInd = coords[0].toInt() - 1
        val type = coords[2]

        when (type) {
            "free" -> setFree(xInd, yInd)
            "mine" -> setStar(xInd, yInd)
        }
        display()
        selectItem()
    }

    private fun setFree(xInd: Int, yInd: Int) {
        if (isSetted == false) {
            randomMines(xInd, yInd)
            scanMines()
        }
        floodFill(xInd, yInd)
    }


    private fun setStar(xInd: Int, yInd: Int) {
        when {
            displayableMineField[xInd][yInd] == safeCell -> {
                displayableMineField[xInd][yInd] = markedCell
                if (mineField[xInd][yInd] == minesSymbol) minesCount--
            }
            displayableMineField[xInd][yInd] == markedCell -> {
                displayableMineField[xInd][yInd] = safeCell
                if (mineField[xInd][yInd] == minesSymbol) minesCount++
            }
            displayableMineField[xInd][yInd].isDigit() -> {
                starOnDigit()
            }
        }
    }

    private fun starOnDigit() {
        println("There is a number here!")
        selectItem()
    }

    private fun floodFill(xInd: Int, yInd: Int) {
        var prevChar = mineField[xInd][yInd]
        floodFillUtil(xInd, yInd, prevChar)
    }

    private fun floodFillUtil(xInd: Int, yInd: Int, prevChar: Char) {
        if (xInd < 0 || xInd >= columns || yInd < 0 || yInd >= rows) return
        if (isMine(xInd, yInd)) freeOnMine()
        if (!prevChar.isDigit()) {
            if (mineField[xInd][yInd] != prevChar) {
                if (mineField[xInd][yInd].isDigit()) displayableMineField[xInd][yInd] =
                    mineField[xInd][yInd]
                return
            }
            displayableMineField[xInd][yInd] = spaceSymbol
            mineField[xInd][yInd] = spaceSymbol
        } else {
            if (mineField[xInd][yInd] != prevChar) {
                displayableMineField[xInd][yInd] = spaceSymbol
                mineField[xInd][yInd] = spaceSymbol
            }
            displayableMineField[xInd][yInd] = mineField[xInd][yInd]
            return
        }

        floodFillUtil(xInd + 1, yInd, prevChar)
        floodFillUtil(xInd - 1, yInd, prevChar)
        floodFillUtil(xInd, yInd + 1, prevChar)
        floodFillUtil(xInd, yInd - 1, prevChar)
        floodFillUtil(xInd + 1, yInd + 1, prevChar)
        floodFillUtil(xInd - 1, yInd - 1, prevChar)
        floodFillUtil(xInd - 1, yInd + 1, prevChar)
        floodFillUtil(xInd + 1, yInd - 1, prevChar)
    }

    private fun freeOnMine() {
        println("You stepped on a mine and failed!")
        exitProcess(1)
    }

    private fun isValidCoordinate(row: Int, col: Int) = (row in 0 until rows && col in 0 until columns)

    private fun isMine(row: Int, col: Int) = mineField[row][col] == minesSymbol

    fun defineTheMinesNumber() {
        print("How many mines do you want on the field? ")
        val number = readLine()!!.removePrefix("> ").toInt()
        minesCount = if (number > 80) 80 else number
        mines = minesCount
        display()
        selectItem()
    }
}

fun main() {
    val minesweeper = Minesweeper()
    minesweeper.defineTheMinesNumber()
}