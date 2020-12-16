package com.github.ajsnarr98.linknotes.search

import android.view.MenuItem
import android.widget.Checkable

/**
 * @property onSelectionChange called with the menu item ids of every selected item in the group
 */
class MultiSelectMenuActionGroup(
    menuItems: List<MenuItem>,
    defaultSelections: Set<MenuItem> = menuItems.toSet(),
    val onSelectionChange: (selections: Set<Int>) -> Unit,
) : MenuActionGroup(menuItems, defaultSelections) {

    init {
        // update based on current (default) selections
        val menuItem = defaultSelections.first()
        onMenuItemClick(menuItem)
        onActionViewClick(getActionView(menuItem))
    }

    /**
     * All currently 'checked' menu items
     */
    private val selectedItems: List<MenuItem>
        get() = menuItems.filter { menuItem ->
            getActionView(menuItem).isChecked
        }

    override fun onActionViewClick(actionView: Checkable) {
        // do nothing
    }

    override fun onMenuItemClick(menuItem: MenuItem) = onSelectionChange(selectedItems.map { it.itemId }.toSet())
}