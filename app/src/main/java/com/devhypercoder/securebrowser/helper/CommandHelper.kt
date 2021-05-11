package com.devhypercoder.securebrowser.helper

fun isCommand(url: String, prefix: Char = '.'): Boolean {
    return url.startsWith(prefix)
}