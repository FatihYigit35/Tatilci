package com.androiddevelopers.villabuluyorum.viewmodel.serch

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.convertUUIDToByte
import com.androiddevelopers.villabuluyorum.model.FilterModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNREACHABLE_CODE")
@HiltViewModel
class SearchViewModel @Inject
constructor(
    val repo : FirebaseRepoInterFace,
    val auth : FirebaseAuth
): ViewModel() {
    private var _searchResult = MutableLiveData<List<Villa>>()
    val searchResult : LiveData<List<Villa>>
        get() = _searchResult

    private var _filterResult = MutableLiveData<List<Villa>>()
    val filterResult : LiveData<List<Villa>>
        get() = _filterResult

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage : LiveData<Resource<Boolean>>
        get() = _firebaseMessage


    init {
        getVillas(20)
    }

    fun getVillas(limit : Long) = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllVillasFromFirestore(limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let {
                        villa -> villaList.add(villa)
                    }
                }
                _searchResult.value = villaList
                _firebaseMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun searchVillasByCity(filter : FilterModel, limit : Long) = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(null)
        repo.getVillasByCity(filter.city ?: "Ankara",limit)
            .addOnSuccessListener {
                println("searchVillasByCity")
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let {
                        villa -> villaList.add(villa)
                    }
                }
                filterVillas(villaList,filter)
            }.addOnFailureListener { exception ->
                println("error  :"+exception)
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun filterVillas(villas : List<Villa>,filters : FilterModel){
        val villaList = ArrayList<Villa>()
        if (villas.isNotEmpty()){
            for (villa in searchResult.value!!) {
                if (villa.nightlyRate!!.toInt() in filters.minPrice!!.toInt()..filters.maxPrice!!.toInt() &&
                    (filters.bathrooms == 99 || filters.bathrooms == villa.bathroomCount) &&
                    (filters.bedrooms == 99 || filters.bedrooms == villa.bedroomCount) &&
                    (filters.beds == 99 || filters.beds == villa.bedCount)
                ) {
                    println("add")
                    villaList.add(villa)
                }
            }
        }
        if (villaList.isEmpty()){
            _firebaseMessage.value = Resource.error("Belge alınamadı. Hata:", null)
        }else{
            _filterResult.value = villaList
            _firebaseMessage.value = Resource.success( null)
        }
    }
    fun searchInLİst(query : String){
        try {
            val newList = searchResult.value?.let {list->
                list.filter {
                    it.villaName!!.contains(query, ignoreCase = true)
                }
            }
            _filterResult.value = newList?.toList()
        }catch (e : Exception){
            _firebaseMessage.value = Resource.error("Belge alınamadı. Hata:", null)
        }
    }
}