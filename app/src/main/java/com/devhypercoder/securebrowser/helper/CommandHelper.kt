package com.devhypercoder.securebrowser.helper

import com.devhypercoder.securebrowser.data.handleHistoryCommand

fun handleCommand(cmd: String): String {
    return when (cmd) {
        "hist" -> {
            return handleHistoryCommand()
        }
        else -> "404"
    }
}

fun isCommand(url: String, prefix: Char = '.'): Boolean {
    return url.startsWith(prefix)
}