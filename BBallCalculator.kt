// See https://rapidapi.com/api-sports/api/api-nba/ and https://api-sports.io/documentation/nba/v2#section/Introduction
fun calculateEntertainmentValue(gameStats: Map<String, Any>, teamStandings: Map<Int, Int>): Int {
    // Extract relevant statistics from the gameStats object
    val team1Stats = (gameStats["response"] as List<Map<String, Any>>)[0]["statistics"] as Map<String, Any>
    val team2Stats = (gameStats["response"] as List<Map<String, Any>>)[1]["statistics"] as Map<String, Any>

    // Get the relative standings of the teams
    val team1Standings = teamStandings[team1Stats["team"]["id"]]!!
    val team2Standings = teamStandings[team2Stats["team"]["id"]]!!

    // Determine the underdog factor
    val underdogFactor = when {
        team1Standings > team2Standings -> 1 / (1 + Math.abs(team1Standings - team2Standings))
        team2Standings > team1Standings -> 1 + Math.abs(team1Standings - team2Standings)
        else -> 1.0
    }

    // Calculate the entertainment value based on various factors
    val scoreDiff = Math.abs(team1Stats["points"] as Int - team2Stats["points"] as Int)
    val marginFactor = Math.max(0.0, 1 - scoreDiff.toDouble() / 20)  // Scale factor based on score difference (max 20 points)
    val paceFactor = (team1Stats["fga"] as Int + team2Stats["fga"] as Int) / 100.0  // Scale factor based on field goal attempts
    val importanceFactor = (team1Standings + team2Standings) / 2.0  // Average of relative standings
    val comebackFactor = Math.max(0.0, 1 - Math.abs(team1Stats["biggestLead"] as Int - scoreDiff) / scoreDiff.toDouble())  // Scale factor for comeback potential
    val excitementFactor = (team1Stats["fastBreakPoints"] as Int + team2Stats["fastBreakPoints"] as Int) / 20.0  // Scale factor based on fast break points
    val scoringFactor = (team1Stats["points"] as Int + team2Stats["points"] as Int) / 200.0  // Scale factor based on total points scored (max 200 points)

    // Calculate the entertainment value rating with underdog factor
    var entertainmentValue = marginFactor * paceFactor * importanceFactor * comebackFactor * excitementFactor * underdogFactor * scoringFactor * 10

    // Round the rating to the nearest integer
    entertainmentValue = Math.round(entertainmentValue).toInt()

    // Ensure the rating is within the valid range of 1-10
    entertainmentValue = entertainmentValue.coerceIn(1, 10)

    return entertainmentValue
}
