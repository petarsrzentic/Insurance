package com.petarsrzentic.insurance.data

import android.util.Log

class InsuranceExcelService(val insuDao: InsuranceDao) {

    private fun createExcel () {
//        //iscitaj tablu Insurance koristeci InsuraceRepository
//        val insurance = InsuranceRepository().allInsurance;
        val inDao = insuDao.fetchAll()
        Log.i("InsuranceService", "$inDao")
////        val insuranceList: List<Insurance>? = insurance.value;
////        to do: appach poi (nauci za excel)
    }
}