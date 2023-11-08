package com.github.ajsnarr98.linknotes.desktop.util

/**
 * Append the given value to a set for the given key. Sets start empty.
 */
fun <K, V> MutableMap<K, Set<V>>.appendToSet(key: K, value: V) {
    this[key] = (this[key] ?: emptySet()) + value
}

/**
 * Removes the given value from a set for the given key. If set will become
 * empty, sets it to be empty.
 */
fun <K, V> MutableMap<K, Set<V>>.removeFromSet(key: K, value: V) {
    this[key] = (this[key] ?: emptySet()) - value
}