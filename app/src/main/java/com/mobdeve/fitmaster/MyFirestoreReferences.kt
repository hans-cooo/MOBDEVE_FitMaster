package com.mobdeve.fitmaster

object MyFirestoreReferences {
    // Collection References
    const val USERS_COLLECTION = "Users"
    const val USER_PROGRESS_COLLECTION = "UserProgress"
    const val USER_WORKOUT_COLLECTION = "UserWorkout"

    // User Fields
    const val USERNAME_FIELD = "username"
    const val BIRTHDAY_FIELD = "birthday"
    const val EMAIL_FIELD = "email"
    const val PASSWORD_FIELD = "password"
    const val WEIGHT_FIELD = "weight"
    const val HEIGHT_FIELD = "height"
    const val LEVEL_FIELD = "level"
    const val GOAL_FIELD = "goal"
    const val GOAL_WEIGHT_FIELD = "goalWeight"

    // UserProgress Fields
    const val DAY_1_FIELD = "Day1"
    const val DAY_2_FIELD = "Day2"
    const val DAY_3_FIELD = "Day3"
    const val DAY_4_FIELD = "Day4"
    const val DAY_5_FIELD = "Day5"
    const val DAY_6_FIELD = "Day6"
    const val DAY_7_FIELD = "Day7"
    const val LAST_DAY_FIELD = "LastDay"

    // UserWorkout Fields
    const val MONDAY_FIELD = "dayMon"
    const val TUESDAY_FIELD = "dayTue"
    const val WEDNESDAY_FIELD = "dayWed"
    const val THURSDAY_FIELD = "dayThu"
    const val FRIDAY_FIELD = "dayFri"
    const val SATURDAY_FIELD = "daySat"
    const val SUNDAY_FIELD = "daySun"

    const val JUMPING_JACKS_FIELD = "JumpingJacks"
    const val SQUATS_FIELD = "squat"
    const val ROWS_FIELD = "rows"
    const val PUSHUPS_FIELD = "Pushups"
    const val BICYCLE_CRUNCHES = "BicycleCrunches"
    const val BURPEES_FIELD = "Burpees"
    const val DUMBELL_BENCH_PRESS_FIELD = "DumbbellBenchPress"
    const val HIGH_KNEES_FIELD = "HighKnees"
    const val INCLINED_BENCH_PRESS_FIELD = "InclinedBenchPress"
    const val DEADLIFT_FIELD = "deadlift"
    const val JOG_MINUTES = "jogMinutes"

    val EXERCISE_LIST = listOf(
        JUMPING_JACKS_FIELD,
        SQUATS_FIELD,
        ROWS_FIELD,
        PUSHUPS_FIELD,
        BICYCLE_CRUNCHES,
        BURPEES_FIELD,
        DUMBELL_BENCH_PRESS_FIELD,
        HIGH_KNEES_FIELD,
        INCLINED_BENCH_PRESS_FIELD,
        DEADLIFT_FIELD
        )

    val EXERCISE_LIST_LOSE_WEIGHT = listOf(
        JUMPING_JACKS_FIELD,
        PUSHUPS_FIELD,
        BICYCLE_CRUNCHES,
        BURPEES_FIELD,
        HIGH_KNEES_FIELD
    )

    val EXERCISE_LIST_GAIN_MUSCLE = listOf(
        SQUATS_FIELD,
        ROWS_FIELD,
        DUMBELL_BENCH_PRESS_FIELD,
        INCLINED_BENCH_PRESS_FIELD,
        DEADLIFT_FIELD
    )

    fun getName(field: String) :String{
        return when (field){
            JUMPING_JACKS_FIELD -> "Jumping Jacks"
            SQUATS_FIELD -> "Squats"
            ROWS_FIELD -> "Rows"
            PUSHUPS_FIELD -> "Push Ups"
            BICYCLE_CRUNCHES -> "Bicycle Crunches"
            BURPEES_FIELD -> "Burpees"
            DUMBELL_BENCH_PRESS_FIELD -> "Dumbbell Bench Press"
            HIGH_KNEES_FIELD -> "High Knees"
            INCLINED_BENCH_PRESS_FIELD -> "Inclined Bench Press"
            DEADLIFT_FIELD -> "Deadlift"
            else -> ""

        }
    }
}