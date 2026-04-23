package com.example.pfdb.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pfdb.data.AppDatabase
import com.example.pfdb.data.Asset
import com.example.pfdb.data.FamilyMember
import com.example.pfdb.data.Liability
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).financeDao()

    val familyMembers = dao.getAllFamilyMembers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAssets = dao.getAllAssets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLiabilities = dao.getAllLiabilities().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFamilyMemberId = MutableStateFlow<Int?>(null) // null for "Entire Family"
    val selectedFamilyMemberId = _selectedFamilyMemberId.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("₹")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    val filteredAssets = combine(allAssets, _selectedFamilyMemberId) { assets, memberId ->
        if (memberId == null) assets else assets.filter { it.familyMemberId == memberId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredLiabilities = combine(allLiabilities, _selectedFamilyMemberId) { liabilities, memberId ->
        if (memberId == null) liabilities else liabilities.filter { it.familyMemberId == memberId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculations
    val totalInvested = filteredAssets.map { assets -> assets.sumOf { it.investedAmount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMarketValue = filteredAssets.map { assets -> assets.sumOf { it.marketValue } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalLiabilities = filteredLiabilities.map { liabilities -> liabilities.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netWorth = combine(totalMarketValue, totalLiabilities) { market, liabilities -> market - liabilities }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val absoluteReturn = combine(totalMarketValue, totalInvested) { market, invested -> market - invested }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val percentageReturn = combine(totalMarketValue, totalInvested) { market, invested ->
        if (invested == 0.0) 0.0 else ((market - invested) / invested) * 100
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Actions
    fun selectFamilyMember(id: Int?) { _selectedFamilyMemberId.value = id }
    fun setCurrency(symbol: String) { _selectedCurrency.value = symbol }

    fun addFamilyMember(name: String) = viewModelScope.launch { dao.insertFamilyMember(FamilyMember(name = name)) }
    fun deleteFamilyMember(member: FamilyMember) = viewModelScope.launch { dao.deleteFamilyMember(member) }

    fun addAsset(asset: Asset) = viewModelScope.launch { dao.insertAsset(asset) }
    fun updateAsset(asset: Asset) = viewModelScope.launch { dao.updateAsset(asset) }
    fun deleteAsset(asset: Asset) = viewModelScope.launch { dao.deleteAsset(asset) }

    fun addLiability(liability: Liability) = viewModelScope.launch { dao.insertLiability(liability) }
    fun updateLiability(liability: Liability) = viewModelScope.launch { dao.updateLiability(liability) }
    fun deleteLiability(liability: Liability) = viewModelScope.launch { dao.deleteLiability(liability) }

    fun exportData(): String {
        return com.example.pfdb.data.DataExporter.exportToJson(
            familyMembers.value,
            allAssets.value,
            allLiabilities.value
        )
    }

    fun importData(json: String) = viewModelScope.launch {
        val data = com.example.pfdb.data.DataExporter.importFromJson(json)
        data.familyMembers.forEach { dao.insertFamilyMember(it) }
        data.assets.forEach { dao.insertAsset(it) }
        data.liabilities.forEach { dao.insertLiability(it) }
    }

    fun resetAllData() = viewModelScope.launch {
        dao.deleteAllAssets()
        dao.deleteAllLiabilities()
        dao.deleteAllFamilyMembers()
    }
}
