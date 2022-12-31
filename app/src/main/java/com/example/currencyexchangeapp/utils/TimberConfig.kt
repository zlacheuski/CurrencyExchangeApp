package com.example.currencyexchangeapp.utils

import timber.log.Timber

class TimberConfig : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        with(element) {
            return "($fileName:$lineNumber)$methodName()"
        }
    }
}