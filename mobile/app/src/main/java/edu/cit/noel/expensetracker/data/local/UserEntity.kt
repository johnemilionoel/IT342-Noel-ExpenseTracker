package edu.cit.noel.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstname: String,
    val lastname: String,
    val email: String,
    val isLoggedIn: Boolean = false
)
