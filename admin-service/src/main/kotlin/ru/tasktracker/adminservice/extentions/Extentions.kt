package ru.tasktracker.adminservice.extentions

import io.jsonwebtoken.Claims


fun Claims.getString(key: String): String {
    return get(key, String::class.java) ?: ""
}