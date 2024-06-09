package com.example.wefly.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DataFirebase {

    fun getDatabaseUtenti() : DatabaseReference{
        return FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app").getReference("Utenti")
    }
    fun getDatabaseViaggi() : DatabaseReference{
        return FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app").getReference("ViaggiTotali")
    }

    fun getDatabaseChats() : DatabaseReference{
        return FirebaseDatabase.getInstance("https://wefly-d50f7-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chats")
    }

    fun getStorageUtenti() : StorageReference {
        return FirebaseStorage.getInstance("gs://wefly-d50f7.appspot.com").getReference("Utenti")
    }

    fun getStorageViaggi() : StorageReference {
        return FirebaseStorage.getInstance("gs://wefly-d50f7.appspot.com").getReference("ViaggiTotali")
    }
}