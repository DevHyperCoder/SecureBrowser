package com.devhypercoder.securebrowser.helper

import com.devhypercoder.securebrowser.data.AppDatabase
import com.devhypercoder.securebrowser.data.HistoryDao
import com.devhypercoder.securebrowser.data.handleHistoryCommand

suspend fun handleCommand(db: AppDatabase, historyDao: HistoryDao, cmd: String): String {
    return when (cmd) {
        "hist" -> {
            return handleHistoryCommand(db,historyDao)
        }
        else -> "404"
    }
}

fun isCommand(url: String, prefix: Char = '.'): Boolean {
    return url.startsWith(prefix)
}