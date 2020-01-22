package com.example.chatapp

class UserStatus(val time: Long?, val state: String) {
    constructor() : this (null, "")
}