package com.petarsrzentic.insurance.data

import android.os.AsyncTask
import androidx.lifecycle.LiveData

class InsuranceRepository(private val insuranceDao: InsuranceDao) {

    val allInsurance: LiveData<List<Insurance>> = insuranceDao.fetchAll()

    suspend fun insert(arg: Insurance) {
        insuranceDao.insert(arg)
    }

    suspend fun update(arg: Insurance) {
        insuranceDao.update(arg)
    }

    suspend fun delete(arg: Insurance) {
        insuranceDao.delete(arg)
    }


    fun deleteAllInsurance() {
        DeleteAllNotesAsyncTask(
            insuranceDao
        ).execute()
    }

    private class DeleteAllNotesAsyncTask(val insuranceDao: InsuranceDao) :
        AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg p0: Unit?) {
            insuranceDao.deleteAllInsurance()
        }
    }


}