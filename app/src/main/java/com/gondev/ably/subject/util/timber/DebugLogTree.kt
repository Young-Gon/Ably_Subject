package com.gondev.ably.subject.util.timber

import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement) =
        "Timber: (${element.fileName}:${element.lineNumber})"
}