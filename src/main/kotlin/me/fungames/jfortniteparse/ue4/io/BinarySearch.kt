package me.fungames.jfortniteparse.ue4.io

/**
 * Performs binary search, resulting in position of the first element > value using predicate.
 * The list must be already sorted by sortPredicate.
 *
 * @param value Value to look for
 * @param sortPredicate Predicate for sort comparison, defaults to <
 *
 * @returns Position of the first element > value, may be past end of range
 */
fun <T> List<T>.upperBound(value: T, sortPredicate: (a: T, b: T) -> Boolean): Int {
    // Current start of sequence to check
    var start = 0
    // Size of sequence to check
    var size = size

    // With this method, if size is even it will do one more comparison than necessary, but because size can be predicted by the CPU it is faster in practice
    while (size > 0) {
        val leftoverSize = size % 2
        size /= 2

        val checkIndex = start + size
        val startIfLess = checkIndex + leftoverSize

        val checkValue = get(checkIndex)
        start = if (!sortPredicate(value, checkValue)) startIfLess else start
    }

    return start
}