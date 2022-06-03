package com.yarmouk.bainah.ui.viewModels

import android.net.Uri
import androidx.lifecycle.*
import com.yarmouk.bainah.models.*
import com.yarmouk.bainah.repo.Repository
import com.yarmouk.bainah.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: Repository)
    : ViewModel(),LifecycleObserver {

    private val _infoProgress = MutableLiveData(0)
    val infoProgress:LiveData<Int>
    get() = _infoProgress

    private val _imagesPath = MutableLiveData<ArrayList<Uri>>(ArrayList())
    val imagesPath : LiveData<ArrayList<Uri>>
    get() = _imagesPath

    val request = MutableLiveData(Request())

    private val _requestResponse = MutableLiveData<Resource<RequestResponse>>()
    val requestResponse:LiveData<Resource<RequestResponse>>
        get() = _requestResponse

    private val _requestGetResponse = MutableLiveData<Resource<RequestGetResponse>>()
    val requestGetResponse:LiveData<Resource<RequestGetResponse>>
        get() = _requestGetResponse

    private val _participantResponse = MutableLiveData<Resource<RequestResponse>>()
    val participantResponse:LiveData<Resource<RequestResponse>>
        get() = _participantResponse

    private val _imagesResponse = MutableLiveData<Resource<ImagesResponse>>()
    val imagesResponse:LiveData<Resource<ImagesResponse>>
        get() = _imagesResponse

    private val _driverLicenseResponse = MutableLiveData<Resource<ImageResponse>>()
    val driverLicenseResponse:LiveData<Resource<ImageResponse>>
        get() = _driverLicenseResponse

    private val _carLicenseResponse = MutableLiveData<Resource<ImageResponse>>()
    val carLicenseResponse:LiveData<Resource<ImageResponse>>
        get() = _carLicenseResponse

    private val _accidentImagesUploaded = MutableLiveData(false)
    val accidentImagesUploaded:LiveData<Boolean>
        get() = _accidentImagesUploaded

    private val _reportsResponse = MutableLiveData<Resource<ReportsResponse>>()
    val reportsResponse:LiveData<Resource<ReportsResponse>>
        get() = _reportsResponse

    fun getAllReports() = viewModelScope.launch {
        _reportsResponse.postValue(Resource.loading(null))
        val response = repo.getAllReports()
        if(response.isSuccessful){
            _reportsResponse.postValue(Resource.success(response.body()))
        }
        else{
            _requestGetResponse.postValue(Resource.error(response.message(),null))
        }
    }

    fun getRequest(requestId: String) = viewModelScope.launch {
        _requestGetResponse.postValue(Resource.loading(null))
        val response = repo.getRequest(requestId)
        if(response.isSuccessful){
            _requestGetResponse.postValue(Resource.success(response.body()))
        }
        else{
            _requestGetResponse.postValue(Resource.error(response.message(),null))
        }
    }

    fun addNewParticipant(requestId: String,participant: Participant) = viewModelScope.launch {
        _participantResponse.postValue(Resource.loading(null))
        val response = repo.addNewParticipant(requestId,participant)
        if(response.isSuccessful){
            _participantResponse.postValue(Resource.success(response.body()))
        }
        else{
            _participantResponse.postValue(Resource.error(response.message(),response.body()))
        }
    }

    fun updateInfoProgress(newValue:Int){
        _infoProgress.postValue(newValue)
    }

    fun updateImages(newImages:ArrayList<Uri>){
        _imagesPath.postValue(newImages)
    }

}