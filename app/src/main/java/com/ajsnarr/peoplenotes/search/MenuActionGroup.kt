package com.ajsnarr.peoplenotes.search

import android.view.MenuItem
import android.widget.Checkable

/**
 * Represents a group of menu items and their action views.
 */
abstract class MenuActionGroup(val menuItems: List<MenuItem>, val defaultSelections: Set<MenuItem>) {

    /**
     * Used to figure out if menu item or the actionView was first item clicked.
     *
     * Parallel to list of menuItems and actionViews.
     */
    private val isItemClicked: MutableList<Boolean> = menuItems.map { false }.toMutableList()

    init {
        menuItems.forEachIndexed { i, menuItem ->

            // listener for menu item
            val menuItemClickedListener = { _: MenuItem ->
                if (!isItemClicked[i])  {
                    // if menu item was first item clicked
                    isItemClicked[i] = true
                    menuItem.actionView.performClick()
                }

                onMenuItemClick(menuItem)
                isItemClicked[i] = false
                true
            }
            menuItem.setOnMenuItemClickListener(menuItemClickedListener)

            // listener for action view
            menuItem.actionView.setOnClickListener {
                if (!isItemClicked[i]) {
                    // if action view was first item clicked
                    isItemClicked[i] = true
                    menuItemClickedListener(menuItem)
                }

                onActionViewClick(getActionView(menuItem))
                isItemClicked[i] = false
            }
        }

        // update based on default selections
        defaultSelections.forEach { menuItem ->
            getActionView(menuItem).isChecked = true
        }
    }

    protected fun getActionView(menuItem: MenuItem): Checkable {
        val view = menuItem.actionView
        if (view is Checkable) {
            return view
        } else {
            throw IllegalStateException("Action view must implement Checkable")
        }
    }

    /**
     * Called when a specific action layout is clicked.
     *
     * @param index of associated menuItem
     * @param actionView actionLayout that is clicked
     */
    protected abstract fun onActionViewClick(actionView: Checkable)

    protected abstract fun onMenuItemClick(menuItem: MenuItem)
}