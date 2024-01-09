package com.laterball.server.api.model

import com.google.gson.annotations.SerializedName

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    @SerializedName("Ball Possession")
    val BallPossession: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Blocked Shots")
    val BlockedShots: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Corner Kicks")
    val CornerKicks: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Fouls")
    val Fouls: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Goalkeeper Saves")
    val GoalkeeperSaves: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Offsides")
    val Offsides: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Passes %")
    val PassesPercent: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Passes accurate")
    val PassesAccurate: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Red Cards")
    val RedCards: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Shots insidebox")
    val ShotsInsidebox: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Shots off Goal")
    val ShotsOffGoal: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Shots on Goal")
    val ShotsOnGoal: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Shots outsidebox")
    val ShotsOutsidebox: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Total Shots")
    val TotalShots: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Total passes")
    val TotalPasses: HomeAwayStat = HomeAwayStat(),
    @SerializedName("Yellow Cards")
    val YellowCards: HomeAwayStat = HomeAwayStat()
)