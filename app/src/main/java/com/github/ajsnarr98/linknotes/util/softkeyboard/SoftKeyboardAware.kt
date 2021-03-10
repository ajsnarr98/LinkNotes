package com.github.ajsnarr98.linknotes.util.softkeyboard

interface SoftKeyboardAware {
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

    /**
     * Amount to scroll (based on height of a new view above keyboard, for example).
     * Positive values go up, negative values go down.
     */
    fun scrollVerticallyBy(dy: Int)
}