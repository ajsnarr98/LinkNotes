package com.ajsnarr.peoplenotes.search

import android.view.MenuItem
import android.widget.Checkable

/**
 * @property onSelectionChange called with the id of the menu item that was just clicked
 */
class SingleSelectMenuActionGroup(
    menuItems: List<MenuItem>,
    defaultSelection: MenuItem = menuItems.first(),
    val onSelectionChange: (selection: Int) -> Unit,
) : MenuActionGroup(menuItems, setOf(defaultSelection)) {

    init {
        // update based on current (default) selection
        val menuItem = defaultSelections.first()
        onMenuItemClick(menuItem)
        onActionViewClick(getActionView(menuItem))
    }

    override fun onActionViewClick(actionView: Checkable) {
        if (actionView.isChecked) {
            uncheckOthers(actionView)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem) = onSelectionChange(menuItem.itemId)

    /**
     * Uncheck all other action views.
     */
    private fun uncheckOthers(actionView: Checkable)  {
        menuItems
            .filter { it.actionView != actionView }
            .forEach { menuItem ->
                getActionView(menuItem).isChecked = false
            }
    }
}