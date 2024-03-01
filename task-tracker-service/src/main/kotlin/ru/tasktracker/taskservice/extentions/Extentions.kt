package ru.tasktracker.taskservice.extentions

import io.jsonwebtoken.Claims


fun Claims.getString(key: String): String {
    return get(key, String::class.java) ?: ""
}