package com.marknkamau.ledger.data.models

import org.junit.Assert
import org.junit.Test

class MpesaMessageTest {
    private val reversalMsg = "NG5328L88L confirmed. Reversal of transaction NG4727FZNP has been " +
            "successfully reversed  on 5/7/19  at 6:18 AM and Ksh1.00 is credited to your M-PESA" +
            " account. New M-PESA account balance is Ksh2,944.98."

    private val sendMsg = "NC358R7LF7 Confirmed. Ksh850.00 sent to JOSEPH 0792 on 3/3/19 at 9:22" +
            " AM. New M-PESA balance is Ksh2,278.98. Transaction cost, Ksh15.00."

    private val payBillMsg = "NBR15CQRMJ Confirmed. Ksh1,000.00 sent to SAFEBODA KENYA LTD  for " +
            "account 480872 on 27/2/19 at 8:07 AM New M-PESA balance is Ksh3,143.98. Transaction " +
            "cost, Ksh34.00."

    private val buyGoodsMsg = "NIG7MB9VTZ Confirmed. Ksh300.00 paid to NGONG ROAD VIDA. on " +
            "16/9/19 at 11:12 AM.New M-PESA balance is Ksh703.75. Transaction cost, Ksh0.00."

    private val withdrawMsg = "NHB2UFAWR8 Confirmed.on 11/8/19 at 1:36 PMWithdraw Ksh2,000.00 from " +
            "047143 - Total Marketing Kenya Dennis Pritt Road Total Service Station New M-PESA " +
            "balance is Ksh2,000.98. Transaction cost, Ksh28.00."

    private val receiveMsg = "Congratulations! MDC3SEYPI9 confirmed.You have received Ksh5.00 from LNM CASHBACK " +
            "PROMO- CUSTOMER REWARD on 12/4/18 at 12:34 PM.New M-PESA balance is Ksh4,827.98. " +
            "Buy goods with M-PESA."

    private val receiveMsgAlt = "MDA2R5TPVU Confirmed. You have received Ksh2,000.00 from Comm. Bank of Africa " +
            "MPesa Pymnts Ac on 10/4/18 at 2:43 PM. New M-PESA balance is Ksh4,822.98. Buy goods" +
            " with M-PESA."

    private val airtimeMessage = "NG562I2SJW confirmed.You bought Ksh200.00 of airtime on 5/7/19 " +
            " 1:04 PM.New M-PESA balance is Ksh2,744.98. Transaction cost, Ksh0.00. To reverse, " +
            "forward this message to 456."

    private val balanceMsg = "NI51DSRDGJ Confirmed.Your M-PESA balance was  Ksh2,992.98  on 5/9/19 " +
            "at 9:35 AM. Transaction cost, Ksh0.00."

    private val depositMsg = "DQ94ZE762 Confirmed. on 3/7/13 at 9:07 AM Give Ksh1,000.00 cash " +
            "to Digital Africa Services Jolet Supermarket New M-PESA balance is Ksh1,338.00"

    @Test
    fun `can get transaction ref`() {
        Assert.assertEquals("NG5328L88L", MpesaMessage(reversalMsg).code)
        Assert.assertEquals("NC358R7LF7", MpesaMessage(sendMsg).code)
        Assert.assertEquals("NBR15CQRMJ", MpesaMessage(payBillMsg).code)
        Assert.assertEquals("NIG7MB9VTZ", MpesaMessage(buyGoodsMsg).code)
        Assert.assertEquals("NHB2UFAWR8", MpesaMessage(withdrawMsg).code)
        Assert.assertEquals("MDC3SEYPI9", MpesaMessage(receiveMsg).code)
        Assert.assertEquals("MDA2R5TPVU", MpesaMessage(receiveMsgAlt).code)
        Assert.assertEquals("NG562I2SJW", MpesaMessage(airtimeMessage).code)
        Assert.assertEquals("NI51DSRDGJ", MpesaMessage(balanceMsg).code)
        Assert.assertEquals("DQ94ZE762", MpesaMessage(depositMsg).code)
    }

    @Test
    fun `can determine transaction type`() {
        Assert.assertEquals(TransactionType.REVERSAL, MpesaMessage(reversalMsg).type)
        Assert.assertEquals(TransactionType.SEND, MpesaMessage(sendMsg).type)
        Assert.assertEquals(TransactionType.PAY_BILL, MpesaMessage(payBillMsg).type)
        Assert.assertEquals(TransactionType.BUY_GOODS, MpesaMessage(buyGoodsMsg).type)
        Assert.assertEquals(TransactionType.WITHDRAW, MpesaMessage(withdrawMsg).type)
        Assert.assertEquals(TransactionType.RECEIVE, MpesaMessage(receiveMsg).type)
        Assert.assertEquals(TransactionType.RECEIVE, MpesaMessage(receiveMsgAlt).type)
        Assert.assertEquals(TransactionType.AIRTIME, MpesaMessage(airtimeMessage).type)
        Assert.assertEquals(TransactionType.BALANCE, MpesaMessage(balanceMsg).type)
        Assert.assertEquals(TransactionType.DEPOSIT, MpesaMessage(depositMsg).type)
    }

    @Test
    fun `can get amount`() {
        Assert.assertEquals(1.0, MpesaMessage(reversalMsg).amount, 0.0)
        Assert.assertEquals(850.0, MpesaMessage(sendMsg).amount, 0.0)
        Assert.assertEquals(1000.0, MpesaMessage(payBillMsg).amount, 0.0)
        Assert.assertEquals(300.0, MpesaMessage(buyGoodsMsg).amount, 0.0)
        Assert.assertEquals(2000.0, MpesaMessage(withdrawMsg).amount, 0.0)
        Assert.assertEquals(5.0, MpesaMessage(receiveMsg).amount, 0.0)
        Assert.assertEquals(2000.0, MpesaMessage(receiveMsgAlt).amount, 0.0)
        Assert.assertEquals(200.0, MpesaMessage(airtimeMessage).amount, 0.0)
        Assert.assertEquals(2992.98, MpesaMessage(balanceMsg).amount, 0.0)
        Assert.assertEquals(1000.0, MpesaMessage(depositMsg).amount, 0.0)
    }

    @Test
    fun `can get account number`(){
        Assert.assertEquals(null, MpesaMessage(reversalMsg).accountNumber)
        Assert.assertEquals("joseph 0792", MpesaMessage(sendMsg).accountNumber)
        Assert.assertEquals("safeboda kenya ltd  for account 480872", MpesaMessage(payBillMsg).accountNumber)
        Assert.assertEquals("ngong road vida.", MpesaMessage(buyGoodsMsg).accountNumber)
        Assert.assertEquals("047143 - total marketing kenya dennis pritt road total service station", MpesaMessage(withdrawMsg).accountNumber)
        Assert.assertEquals("lnm cashback promo- customer reward", MpesaMessage(receiveMsg).accountNumber)
        Assert.assertEquals("comm. bank of africa mpesa pymnts ac", MpesaMessage(receiveMsgAlt).accountNumber)
        Assert.assertEquals(null, MpesaMessage(airtimeMessage).accountNumber)
        Assert.assertEquals(null, MpesaMessage(balanceMsg).accountNumber)
        Assert.assertEquals("digital africa services jolet supermarket", MpesaMessage(depositMsg).accountNumber)
    }

    @Test
    fun `can get date`(){
        Assert.assertEquals(1562296680, MpesaMessage(reversalMsg).transactionDate)
        Assert.assertEquals(1551594120, MpesaMessage(sendMsg).transactionDate)
        Assert.assertEquals(1551244020, MpesaMessage(payBillMsg).transactionDate)
        Assert.assertEquals(1568621520, MpesaMessage(buyGoodsMsg).transactionDate)
        Assert.assertEquals(1565519760, MpesaMessage(withdrawMsg).transactionDate)
        Assert.assertEquals(1523525640, MpesaMessage(receiveMsg).transactionDate)
        Assert.assertEquals(1523360580, MpesaMessage(receiveMsgAlt).transactionDate)
        Assert.assertEquals(1562321040, MpesaMessage(airtimeMessage).transactionDate)
        Assert.assertEquals(1567665300, MpesaMessage(balanceMsg).transactionDate)
        Assert.assertEquals(1372831620, MpesaMessage(depositMsg).transactionDate)
    }
}