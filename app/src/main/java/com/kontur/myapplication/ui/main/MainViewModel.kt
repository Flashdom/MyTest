package com.kontur.myapplication.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kontur.myapplication.Repository
import com.kontur.myapplication.models.Contact
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val progressLiveData = MutableLiveData<Boolean>()
    private val contactListLiveData = MutableLiveData<List<Contact>>()
    private val repo = Repository


    val contactList: LiveData<List<Contact>>
        get() = contactListLiveData
    val progress: LiveData<Boolean>
        get() = progressLiveData

    fun getInfo(file: File) {
        progressLiveData.postValue(true)
        repo.getInfo(file, getApplication()).subscribe({
            contactListLiveData.postValue(it)
            progressLiveData.postValue(false)

        }, {
            contactListLiveData.postValue(emptyList())
            progressLiveData.postValue(false)

        })
    }

    fun updateInfo(file: File) {
        repo.getInfo(file, getApplication()).subscribe({
            contactListLiveData.postValue(it)

        }, {
            contactListLiveData.postValue(emptyList())

        })
    }


    fun saveList(contacts: List<Contact>) {
        repo.saveTemporalQueryList(contacts)
    }

    fun getFilteredList(newText: String): List<Contact> {
        return repo.filter(newText)
    }

}