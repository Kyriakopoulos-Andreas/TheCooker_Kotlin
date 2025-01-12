package com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val _userRepo: UserRepo,
    val _userDataProvider: UserDataProvider
) : ViewModel() {


    private val _information = mutableStateOf(false)
    val information: State<Boolean> get() = _information
    private val _editProfile = mutableStateOf(false)
    val editProfile: State<Boolean> get() = _editProfile

    private val _country by lazy { mutableStateOf("") }
    val country: State<String> get() = _country
    private val _city= mutableStateOf("")
    val city: State<String> get() = _city
    private val _chefLevelList = mutableListOf("Beginner", "Intermediate", "Advanced", "Expert")
    val chefLevelList: List<String> get() = _chefLevelList
    private val _chefLevel = mutableStateOf("")
    val chefLevel: State<String> get() = _chefLevel
    private val _specialties = mutableStateOf("")
    val specialties: State<String> get() = _specialties




    private val _goldenChefHats = mutableIntStateOf(0)
    val goldenChefHats: State<Int> get() = _goldenChefHats
    private val _saveInfoResult = mutableStateOf<String?>(null)
    val saveInfoResult: State<String?> get() = _saveInfoResult

    fun fetchedInfoFromFirebase(){
        _country.value = _userDataProvider.userData?.country.toString()
        _city.value = _userDataProvider.userData?.city.toString()
        _chefLevel.value = _userDataProvider.userData?.chefLevel.toString()
        _specialties.value = _userDataProvider.userData?.specialties.toString()
        _goldenChefHats.intValue = _userDataProvider.userData?.goldenChefHats ?: 0

    }

    suspend fun updatePhoto(imageUri: Uri, type: String): uploadDownloadResource<Unit> {


        val downloadUrl = withContext(Dispatchers.IO) {
            _userRepo.uploadImageAndGetUrl(imageUri, type)
        }

       val result = if (type == "profile") {
            _userDataProvider.userData?.profilePictureUrl = downloadUrl
            _userRepo.uploadProfilePicture(downloadUrl.toString())
        } else {
            _userDataProvider.userData?.backGroundPictureUrl = downloadUrl
            _userRepo.uploadBackgroundPicture(downloadUrl.toString())
        }
        return result


    }






    fun setInformation(info: Boolean){
        _information.value = info
    }

    fun setProfileManagement(edit: Boolean){
        _editProfile.value = edit
    }

    fun setCountry(country: String){
        Log.d("Country2", _userDataProvider.userData?.country.toString())
        _country.value = country
    }

    fun setCity(city: String){
        _city.value = city
    }

    fun setChefLevel(chefLevel: String){
        _chefLevel.value = chefLevel
    }

    fun setSpecialties(specialty: String){
        _specialties.value = specialty
    }




    suspend fun saveInformation(){
        _userDataProvider.userData?.city = _city.value
        _userDataProvider.userData?.country = _country.value
        _userDataProvider.userData?.chefLevel = _chefLevel.value
        _userDataProvider.userData?.specialties = _specialties.value
        _userDataProvider.userData?.goldenChefHats = _goldenChefHats.intValue
       val result = _userRepo.saveUserInformation(_userDataProvider)


        when(result){
            is uploadDownloadResource.Success ->{
                Log.d("ProfileViewModel", "User information saved successfully")
            }
            is uploadDownloadResource.Error ->{
                _saveInfoResult.value = "Something goes wrong \n Please try to save again"
            }
        }


    }
}