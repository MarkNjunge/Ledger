package com.marknkamau.ledger.data.models

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

data class MessageGroup(val date: Long, val messages: MutableList<MpesaMessage>)