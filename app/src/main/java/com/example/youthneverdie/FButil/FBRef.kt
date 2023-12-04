package com.example.youthneverdie.FButil

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {
    companion object {
        private val database = Firebase.database

        val todayRef = database.getReference("todayRef")
        val termRef = database.getReference("termRef")
        val feedbackRef = database.getReference("feedbackRef")
    }
}