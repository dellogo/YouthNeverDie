package com.example.youthneverdie.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseReF {

    companion object{
        
        private val database = Firebase.database

        val boardRef = database.getReference("board")
        val commentRef = database.getReference("comment")
    }
}