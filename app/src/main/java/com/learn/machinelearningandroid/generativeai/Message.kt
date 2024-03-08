package com.learn.machinelearningandroid.generativeai

data class Message(
    val text: String,
    val isLocalUser: Boolean,
    val timestamp: Long
)