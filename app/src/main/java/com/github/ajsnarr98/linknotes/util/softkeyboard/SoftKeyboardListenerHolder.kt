package com.github.ajsnarr98.linknotes.util.softkeyboard

interface SoftKeyboardListenerHolder {
    /**
     * Add a listener that will be called when the soft keybaord is shown or
     * hidden.
     *
     * @return whether operation was successful or not
     */
    fun addSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean

    /**
     * Remove a listener that will be called when the soft keybaord is shown or
     * hidden.
     *
     * @return whether operation was successful or not
     */
    fun removeSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean
}