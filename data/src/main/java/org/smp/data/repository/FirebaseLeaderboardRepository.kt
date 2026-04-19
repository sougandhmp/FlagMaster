package org.smp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.smp.domain.model.UserStats
import org.smp.domain.repository.LeaderboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLeaderboardRepository @Inject constructor() : LeaderboardRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val statsCollection = firestore.collection("user_stats")

    override fun observeUserStats(uid: String): Flow<UserStats?> = callbackFlow {
        val subscription = statsCollection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val stats = snapshot?.toObject(UserStatsDto::class.java)?.toDomain(uid)
            trySend(stats)
        }
        awaitClose { subscription.remove() }
    }

    override fun observeTopPlayers(limit: Int): Flow<List<UserStats>> = callbackFlow {
        val query = statsCollection
            .orderBy("totalPoints", Query.Direction.DESCENDING)
            .limit(limit.toLong())

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val players = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(UserStatsDto::class.java)?.toDomain(doc.id)
            } ?: emptyList()
            trySend(players)
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateUserStats(stats: UserStats) {
        statsCollection.document(stats.uid).set(UserStatsDto.fromDomain(stats)).await()
    }
}

private data class UserStatsDto(
    val displayName: String? = null,
    val photoUrl: String? = null,
    val totalPoints: Int = 0,
    val bestStreak: Int = 0,
    val gamesPlayed: Int = 0,
    val lastUpdate: Long = 0
) {
    fun toDomain(uid: String) = UserStats(
        uid = uid,
        displayName = displayName,
        photoUrl = photoUrl,
        totalPoints = totalPoints,
        bestStreak = bestStreak,
        gamesPlayed = gamesPlayed,
        lastUpdate = lastUpdate
    )

    companion object {
        fun fromDomain(stats: UserStats) = UserStatsDto(
            displayName = stats.displayName,
            photoUrl = stats.photoUrl,
            totalPoints = stats.totalPoints,
            bestStreak = stats.bestStreak,
            gamesPlayed = stats.gamesPlayed,
            lastUpdate = stats.lastUpdate
        )
    }
}
