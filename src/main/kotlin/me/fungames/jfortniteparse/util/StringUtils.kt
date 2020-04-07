package me.fungames.jfortniteparse.util

import java.awt.FontMetrics
import java.util.*


/**
 * Returns an array of strings, one for each line in the string after it has
 * been wrapped to fit lines of <var>maxWidth</var>. Lines end with any of
 * cr, lf, or cr lf. A line ending at the end of the string will not output a
 * further, empty string.
 *
 *
 * This code assumes <var>str</var> is not `null`.
 *
 * @param str
 * the string to split
 * @param fm
 * needed for string width calculations
 * @param maxWidth
 * the max line width, in points
 * @return a non-empty list of strings
 */
fun String.wrap(fm: FontMetrics, maxWidth: Int): List<String> {
    val lines = this.splitIntoLines()
    if (lines.isEmpty()) return lines
    val strings = ArrayList<String>()
    val iter = lines.iterator()
    while (iter.hasNext()) {
        wrapLineInto(iter.next(), strings, fm, maxWidth)
    }
    return strings
}

/**
 * Given a line of text and font metrics information, wrap the line and add
 * the new line(s) to <var>list</var>.
 *
 * @param line
 * a line of text
 * @param list
 * an output list of strings
 * @param fm
 * font metrics
 * @param maxWidth
 * maximum width of the line(s)
 */
fun wrapLineInto(
    line: String,
    list: MutableList<String>,
    fm: FontMetrics,
    maxWidth: Int
) {
    var line = line
    var len = line.length
    var width = 0
    while (len > 0 && fm.stringWidth(line).also {
            width = it
        } > maxWidth) { // Guess where to split the line. Look for the next space before
// or after the guess.
        val guess = len * maxWidth / width
        var before = line.substring(0, guess).trim { it <= ' ' }
        width = fm.stringWidth(before)
        var pos: Int
        if (width > maxWidth) // Too long
            pos = line.findBreakBefore(guess) else { // Too short or possibly just right
            pos = line.findBreakAfter(guess)
            if (pos != -1) { // Make sure this doesn't make us too long
                before = line.substring(0, pos).trim { it <= ' ' }
                if (fm.stringWidth(before) > maxWidth) pos = line.findBreakBefore(guess)
            }
        }
        if (pos == -1) pos = guess // Split in the middle of the word
        list.add(line.substring(0, pos).trim { it <= ' ' })
        line = line.substring(pos).trim { it <= ' ' }
        len = line.length
    }
    if (len > 0) list.add(line)
}

/**
 * Returns the index of the first whitespace character or '-' in <var>line</var>
 * that is at or before <var>start</var>. Returns -1 if no such character is
 * found.
 *
 * @param start
 * where to star looking
 */
fun String.findBreakBefore(start: Int): Int {
    for (i in start downTo 0) {
        val c = this[i]
        if (Character.isWhitespace(c) || c == '-') return i
    }
    return -1
}

/**
 * Returns the index of the first whitespace character or '-' in <var>line</var>
 * that is at or after <var>start</var>. Returns -1 if no such character is
 * found.
 *
 * @param start
 * where to star looking
 */
fun String.findBreakAfter(start: Int): Int {
    val len = this.length
    for (i in start until len) {
        val c = this[i]
        if (Character.isWhitespace(c) || c == '-') return i
    }
    return -1
}

/**
 * Returns an array of strings, one for each line in the string. Lines end
 * with any of cr, lf, or cr lf. A line ending at the end of the string will
 * not output a further, empty string.
 *
 *
 * This code assumes <var>str</var> is not `null`.
 *
 * @return a non-empty list of strings
 */
fun String.splitIntoLines(): List<String> {
    val strings = ArrayList<String>()
    val len = this.length
    if (len == 0) {
        strings.add("")
        return strings
    }
    var lineStart = 0
    var i = 0
    while (i < len) {
        val c = this[i]
        if (c == '\r') {
            var newlineLength = 1
            if (i + 1 < len && this[i + 1] == '\n') newlineLength = 2
            strings.add(this.substring(lineStart, i))
            lineStart = i + newlineLength
            if (newlineLength == 2) // skip \n next time through loop
                ++i
        } else if (c == '\n') {
            strings.add(this.substring(lineStart, i))
            lineStart = i + 1
        }
        ++i
    }
    if (lineStart < len) strings.add(this.substring(lineStart))
    return strings
}