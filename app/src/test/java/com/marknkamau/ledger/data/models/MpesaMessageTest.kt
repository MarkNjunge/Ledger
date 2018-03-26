package com.marknkamau.ledger.data.models

import junit.framework.Assert
import org.junit.Test

class MpesaMessageTest {
    private val msg1 = "MDI9WAFNGX Confirmed. Ksh1.00 sent to safaricom test 12  for account Order: " +
            "75e2c610-145f-45f0-9d6a-93b4697df578 on 18/4/18 at 11:05 AM New M-PESA balance is " +
            "Ksh4,826.98. Transaction cost, Ksh0.00."

    private val msg2 = "Congratulations! MDC3SEYPI9 confirmed.You have received Ksh5.00 from LNM CASHBACK " +
            "PROMO- CUSTOMER REWARD on 12/4/18 at 12:34 PM.New M-PESA balance is Ksh4,827.98. " +
            "Buy goods with M-PESA."

    private val msg3 = "MDA2R5TPVU Confirmed. You have received Ksh2,000.00 from Comm. Bank of Africa " +
            "MPesa Pymnts Ac on 10/4/18 at 2:43 PM. New M-PESA balance is Ksh4,822.98. Buy goods" +
            " with M-PESA."

    @Test
    fun should_getCode() {
        val mpesaMessage1 = MpesaMessage(msg1, 1L)
        Assert.assertEquals("MDI9WAFNGX", mpesaMessage1.code)

        val mpesaMessage2 = MpesaMessage(msg2, 1L)
        Assert.assertEquals("MDC3SEYPI9", mpesaMessage2.code)

        val mpesaMessage3 = MpesaMessage(msg3, 1L)
        Assert.assertEquals("MDA2R5TPVU", mpesaMessage3.code)
    }

    @Test
    fun should_getTransactionType() {
        val mpesaMessage1 = MpesaMessage(msg1, 1L)
        Assert.assertEquals(TransactionType.SEND, mpesaMessage1.type)

        val mpesaMessage2 = MpesaMessage(msg2, 1L)
        Assert.assertEquals(TransactionType.RECEIVE, mpesaMessage2.type)

        val mpesaMessage3 = MpesaMessage(msg3, 1L)
        Assert.assertEquals(TransactionType.RECEIVE, mpesaMessage3.type)
    }

    @Test
    fun should_getAmount() {
        val mpesaMessage1 = MpesaMessage(msg1, 1L)
        Assert.assertEquals(1.00, mpesaMessage1.amount)

        val mpesaMessage2 = MpesaMessage(msg2, 1L)
        Assert.assertEquals(5.00, mpesaMessage2.amount)

        val mpesaMessage3 = MpesaMessage(msg3, 1L)
        Assert.assertEquals(2000.00, mpesaMessage3.amount)
    }
}