package edu.artic.base.utils

import android.annotation.SuppressLint
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat
import android.view.MenuItem



/**
 * This hack is required for displaying title below icon.
 * There is an api available to achieve this in support library 28-alpha1 (labelVisibilityMode mode).
 * Once support library goes stable we can remove this hack.
 *
 * @see https://stackoverflow.com/a/47407229/4253091
 * @param colorList ColorStateList for icon and text
 */
fun BottomNavigationView.disableShiftMode(colorList: Int = 0) {
    val menuView = getChildAt(0) as BottomNavigationMenuView

    menuView.javaClass.getDeclaredField("mShiftingMode").apply {
        isAccessible = true
        setBoolean(menuView, false)
        isAccessible = false
    }

    @SuppressLint("RestrictedApi")
    for (i in 0 until menuView.childCount) {
        (menuView.getChildAt(i) as BottomNavigationItemView).apply {
            setShiftingMode(false)
            setChecked(false)
            if (colorList > 0) {
                setTextColor(ContextCompat.getColorStateList(this.context, colorList))
            }
        }
    }
}

/**
 * Special implementation of [BottomNavigationView.OnNavigationItemReselectedListener]
 * with absolutely no state and no action. Set it on a view with [preventReselection].
 */
private object IgnoreReselection : BottomNavigationView.OnNavigationItemReselectedListener {
    // No need to do anything in the method body.
    override fun onNavigationItemReselected(item: MenuItem) = Unit
}

/**
 * Disable on-click events for highlighted items.
 *
 * See [BottomNavigationView.setOnNavigationItemReselectedListener] for
 * details.
 */
fun BottomNavigationView.preventReselection() {
    setOnNavigationItemReselectedListener(IgnoreReselection)
}