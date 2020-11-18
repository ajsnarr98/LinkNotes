package com.ajsnarr.linknotes.util

import android.app.AlertDialog
import android.content.Context
import android.widget.SpinnerAdapter
import android.content.DialogInterface
import android.widget.ArrayAdapter
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import java.util.*

/**
 * Spinner with mutliple selection enabled.
 *
 * Adapted from https://teamtreehouse.com/community/spinner-as-multi-select-drop-down-list
 */
class MultiSelectSpinner : AppCompatSpinner, OnMultiChoiceClickListener {
    private var mItems: Array<String> = arrayOf()
    private var mSelection: Array<Boolean> = arrayOf()

    private val mProxyAdapter: ArrayAdapter<String>

    val selectedStrings: List<String>
        get() {
            val selection = LinkedList<String>()
            for (i in mItems.indices) {
                if (mSelection[i]) {
                    selection.add(mItems[i])
                }
            }
            return selection
        }

    val selectedIndicies: List<Int>
        get() {
            val selection = LinkedList<Int>()
            for (i in mItems.indices) {
                if (mSelection[i]) {
                    selection.add(i)
                }
            }
            return selection
        }

    constructor(context: Context) : super(context) {
        mProxyAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(mProxyAdapter)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mProxyAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(mProxyAdapter)
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        if (which < mSelection.size) {
            mSelection[which] = isChecked

            mProxyAdapter.clear()
            mProxyAdapter.add(buildSelectedItemString())
            setSelection(0)
        } else {
            throw IllegalArgumentException("Argument 'which' is out of bounds.")
        }
    }

    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(getContext())
        builder.setMultiChoiceItems(mItems, mSelection?.toBooleanArray(), this)
        builder.setPositiveButton("Submit", DialogInterface.OnClickListener { dialog, which -> })
        builder.show()
        return true
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException("setAdapter is not supported by MultiSelectSpinner.")
    }


    fun setItemsChecked(items: Array<String>, areChecked: Boolean) {
        mItems = items

        // true defaults to checked, false defaults to unchecked
        mSelection = Array(mItems.size, { init: Int -> areChecked })

        mProxyAdapter.clear()
        mProxyAdapter.add(buildSelectedItemString())
    }

    fun setItemsChecked(items: List<String>, areChecked: Boolean) {
        setItemsChecked(items.toTypedArray(), areChecked)
    }

    fun setSelection(selection: Array<String>) {
        for (sel in selection) {
            for (j in mItems.indices) {
                if (mItems[j] == sel) {
                    mSelection[j] = true
                }
            }
        }
    }

    fun setSelection(selection: List<String>) {
        for (sel in selection) {
            for (j in mItems.indices) {
                if (mItems[j] == sel) {
                    mSelection[j] = true
                }
            }
        }
    }

    fun setSelection(selectedIndicies: IntArray) {
        for (index in selectedIndicies) {
            if (index >= 0 && index < mSelection.size) {
                mSelection[index] = true
            } else {
                throw IllegalArgumentException("Index $index is out of bounds.")
            }
        }
    }

    private fun buildSelectedItemString(): String {
        val sb = StringBuilder()
        var foundOne = false

        for (i in mItems.indices) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ")
                }
                foundOne = true

                sb.append(mItems[i])
            }
        }

        return sb.toString()
    }
}
