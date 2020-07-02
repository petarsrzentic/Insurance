package com.petarsrzentic.insurance.data


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InsuranceDao {

    @Query("SELECT * FROM Insurance")
    fun fetchAll(): LiveData<List<Insurance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(arg: Insurance)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(arg: Insurance)

    @Delete
    suspend fun delete(arg: Insurance)

    @Query("DELETE FROM Insurance")
    fun deleteAllInsurance()


}

