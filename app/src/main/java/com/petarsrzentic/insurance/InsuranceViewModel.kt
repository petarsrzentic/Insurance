package com.petarsrzentic.insurance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.petarsrzentic.insurance.data.Insurance
import com.petarsrzentic.insurance.data.InsuranceDatabase
import com.petarsrzentic.insurance.data.InsuranceRepository
import kotlinx.coroutines.launch

class InsuranceViewModel(application: Application): AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: InsuranceRepository
    // LiveData gives us updated insurance when they change.
    val allInsurance : LiveData<List<Insurance>>

    init {
        // Gets reference to InsuranceDao from InsuranceRoomDatabase to construct
        // the correct InsuranceRepository.
        val insuranceDao = InsuranceDatabase.getDatabase(application).insuranceDao()
        repository = InsuranceRepository(insuranceDao)
        allInsurance = repository.allInsurance
    }

    fun insert(arg: Insurance) = viewModelScope.launch {
        repository.insert(arg)
    }

    fun update(arg: Insurance) = viewModelScope.launch {
        repository.update(arg)
    }

    fun delete(arg: Insurance) = viewModelScope.launch {
        repository.delete(arg)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllInsurance()
    }


}
