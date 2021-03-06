package edu.artic.db

/**
 * This file contains the constant values used in the models.
 * @author Sameer Dhakal (Fuzz)
 */

/**
 * [INVALID_FLOOR] represents the invalid floor number.
 * Since negative numbers are valid number for underground floors we are using Int.MIN_VALUE.
 */
const val INVALID_FLOOR: Int = Int.MIN_VALUE

/**
 * Dummy id to represent the intro tour stop.
 */
const val INTRO_TOUR_STOP_OBJECT_ID: String = "INTRO"