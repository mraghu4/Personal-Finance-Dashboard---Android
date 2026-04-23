package com.example.pfdb.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Family Members
    @Query("SELECT * FROM family_members")
    fun getAllFamilyMembers(): Flow<List<FamilyMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMember)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMember)

    // Assets
    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE familyMemberId = :memberId")
    fun getAssetsByFamilyMember(memberId: Int): Flow<List<Asset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset)

    @Update
    suspend fun updateAsset(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    // Liabilities
    @Query("SELECT * FROM liabilities")
    fun getAllLiabilities(): Flow<List<Liability>>

    @Query("SELECT * FROM liabilities WHERE familyMemberId = :memberId")
    fun getLiabilitiesByFamilyMember(memberId: Int): Flow<List<Liability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiability(liability: Liability)

    @Update
    suspend fun updateLiability(liability: Liability)

    @Delete
    suspend fun deleteLiability(liability: Liability)

    @Query("DELETE FROM family_members")
    suspend fun deleteAllFamilyMembers()

    @Query("DELETE FROM assets")
    suspend fun deleteAllAssets()

    @Query("DELETE FROM liabilities")
    suspend fun deleteAllLiabilities()
}
