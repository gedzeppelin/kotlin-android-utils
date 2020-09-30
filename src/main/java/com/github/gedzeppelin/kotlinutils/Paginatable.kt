package com.github.gedzeppelin.kotlinutils

interface Paginatable<T: Any> {
    val haveNext: Boolean
    val havePrevious: Boolean
    val results: List<T>
    val totalCount: Int
}