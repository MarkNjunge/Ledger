package com.marknjunge.ledger.data.models.exception

class MessageParseException(override val message: String, val body: String) : Exception(message)