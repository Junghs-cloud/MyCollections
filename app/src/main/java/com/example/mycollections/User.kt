package com.example.mycollections

class User(val id: String, var password: String, val email: String, var name: String)

object CurrentUser {
    var user: User? = null
}