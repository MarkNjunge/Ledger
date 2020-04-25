package com.marknjunge.ledger.data.models

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class MpesaMessageTest {
    private val reversalMsg = MpesaMessage.create(
        "NG5328L88L confirmed. Reversal of transaction NG4727FZNP has been successfully reversed  on 5/7/19  at 6:18 " +
                "AM and Ksh1.89 is credited to your M-PESA account. New M-PESA account balance is Ksh2,944.98."
    )

    private val sendMsg = MpesaMessage.create(
        "NC358R7LF7 Confirmed. Ksh850.89 sent to JOSEPH 0792 on 3/3/19 at 9:22 AM. New M-PESA balance is " +
                "Ksh2,278.98. Transaction cost, Ksh15.89."
    )

    private val payBillMsg = MpesaMessage.create(
        "NBR15CQRMJ Confirmed. Ksh1,000.89 sent to SAFEBODA KENYA LTD  for account 480872 on 27/2/19 at 8:07 AM New " +
                "M-PESA balance is Ksh3,143.98. Transaction cost, Ksh34.89."
    )

    private val buyGoodsMsg = MpesaMessage.create(
        "NIG7MB9VTZ Confirmed. Ksh300.89 paid to NGONG ROAD VIDA. on 16/9/19 at 11:12 AM.New M-PESA balance is " +
                "Ksh703.75. Transaction cost, Ksh0.89."
    )

    private val withdrawMsg = MpesaMessage.create(
        "NHB2UFAWR8 Confirmed.on 11/8/19 at 1:36 PMWithdraw Ksh2,000.89 from 047143 - Total Marketing Kenya Dennis " +
                "Pritt Road Total Service Station New M-PESA balance is Ksh2,000.98. Transaction cost, Ksh28.89."
    )

    private val receiveMsg = MpesaMessage.create(
        "Congratulations! MDC3SEYPI9 confirmed.You have received Ksh5.89 from LNM CASHBACK PROMO- CUSTOMER REWARD " +
                "on 12/4/18 at 12:34 PM.New M-PESA balance is Ksh4,827.98. " +
                "Buy goods with M-PESA."
    )

    private val receiveMsgAlt = MpesaMessage.create(
        "MDA2R5TPVU Confirmed. You have received Ksh2,000.89 from Comm. Bank of Africa MPesa Pymnts Ac on 10/4/18 " +
                "at 2:43 PM. New M-PESA balance is Ksh4,822.98. Buy goods with M-PESA."
    )

    private val airtimeMessage = MpesaMessage.create(
        "NG562I2SJW confirmed.You bought Ksh200.89 of airtime on 5/7/19  1:04 PM.New M-PESA balance is " +
                "Ksh2,744.98. Transaction cost, Ksh0.89. To reverse, forward this message to 456."
    )
    private val airtimeReceive = MpesaMessage.create("NKP47NVJCW confirmed. You have received Airtime of Ksh100.89 " +
            "from Upward Ltd Centre on 25/11/19 at 12:24 PM.")

    private val balanceMsg = MpesaMessage.create(
        "NI51DSRDGJ Confirmed.Your M-PESA balance was  Ksh2,992.98  on 5/9/19 at 9:35 AM. Transaction cost, Ksh0.89."
    )

    private val depositMsg = MpesaMessage.create(
        "DQ94ZE762 Confirmed. on 3/7/13 at 9:07 AM Give Ksh1,000.89 cash to Digital Africa Services Jolet " +
                "Supermarket New M-PESA balance is Ksh1,338.89."
    )

    private val fulizaPay = MpesaMessage.create("ODF7I6HADN\n" +
            "Confirmed. Ksh 488.43 from your M-PESA has been used to fully pay your outstanding Fuliza M-PESA. " +
            "Available Fuliza M-PESA limit is Ksh 500.00. M-PESA balance is Ksh539.57.", 1587803235
    )

    @Before
    fun setup(){
        // Tests are configured based on GMT +3
        // Running on CI with a different timezone will result in errors
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Nairobi"))
    }

    @Test
    fun `can get transaction ref`() {
        Assert.assertEquals("NG5328L88L", reversalMsg.code)
        Assert.assertEquals("NC358R7LF7", sendMsg.code)
        Assert.assertEquals("NBR15CQRMJ", payBillMsg.code)
        Assert.assertEquals("NIG7MB9VTZ", buyGoodsMsg.code)
        Assert.assertEquals("NHB2UFAWR8", withdrawMsg.code)
        Assert.assertEquals("MDC3SEYPI9", receiveMsg.code)
        Assert.assertEquals("MDA2R5TPVU", receiveMsgAlt.code)
        Assert.assertEquals("NG562I2SJW", airtimeMessage.code)
        Assert.assertEquals("NKP47NVJCW", airtimeReceive.code)
        Assert.assertEquals("NI51DSRDGJ", balanceMsg.code)
        Assert.assertEquals("DQ94ZE762", depositMsg.code)
        Assert.assertEquals("ODF7I6HADN", fulizaPay.code)
    }

    @Test
    fun `can determine transaction type`() {
        Assert.assertEquals(TransactionType.REVERSAL, reversalMsg.transactionType)
        Assert.assertEquals(TransactionType.SEND, sendMsg.transactionType)
        Assert.assertEquals(TransactionType.PAY_BILL, payBillMsg.transactionType)
        Assert.assertEquals(TransactionType.BUY_GOODS, buyGoodsMsg.transactionType)
        Assert.assertEquals(TransactionType.WITHDRAW, withdrawMsg.transactionType)
        Assert.assertEquals(TransactionType.RECEIVE, receiveMsg.transactionType)
        Assert.assertEquals(TransactionType.RECEIVE, receiveMsgAlt.transactionType)
        Assert.assertEquals(TransactionType.AIRTIME, airtimeMessage.transactionType)
        Assert.assertEquals(TransactionType.AIRTIME_RECEIVE, airtimeReceive.transactionType)
        Assert.assertEquals(TransactionType.BALANCE, balanceMsg.transactionType)
        Assert.assertEquals(TransactionType.DEPOSIT, depositMsg.transactionType)
        Assert.assertEquals(TransactionType.FULIZA_PAY, fulizaPay.transactionType)
    }

    @Test
    fun `can get amount`() {
        Assert.assertEquals(1.89, reversalMsg.amount, 0.0)
        Assert.assertEquals(850.89, sendMsg.amount, 0.0)
        Assert.assertEquals(1000.89, payBillMsg.amount, 0.0)
        Assert.assertEquals(300.89, buyGoodsMsg.amount, 0.0)
        Assert.assertEquals(2000.89, withdrawMsg.amount, 0.0)
        Assert.assertEquals(5.89, receiveMsg.amount, 0.0)
        Assert.assertEquals(2000.89, receiveMsgAlt.amount, 0.0)
        Assert.assertEquals(200.89, airtimeMessage.amount, 0.0)
        Assert.assertEquals(100.89, airtimeReceive.amount, 0.0)
        Assert.assertEquals(2992.98, balanceMsg.amount, 0.0)
        Assert.assertEquals(1000.89, depositMsg.amount, 0.0)
        Assert.assertEquals(488.43, fulizaPay.amount, 0.0)
    }

    @Test
    fun `can get account number`() {
        Assert.assertEquals(null, reversalMsg.accountNumber)
        Assert.assertEquals("JOSEPH 0792", sendMsg.accountNumber)
        Assert.assertEquals("SAFEBODA KENYA LTD  for account 480872", payBillMsg.accountNumber)
        Assert.assertEquals("NGONG ROAD VIDA.", buyGoodsMsg.accountNumber)
        Assert.assertEquals(
            "047143 - Total Marketing Kenya Dennis Pritt Road Total Service Station",
            withdrawMsg.accountNumber
        )
        Assert.assertEquals("LNM CASHBACK PROMO- CUSTOMER REWARD", receiveMsg.accountNumber)
        Assert.assertEquals("Comm. Bank of Africa MPesa Pymnts Ac", receiveMsgAlt.accountNumber)
        Assert.assertEquals(null, airtimeMessage.accountNumber)
        Assert.assertEquals("Upward Ltd Centre", airtimeReceive.accountNumber)
        Assert.assertEquals(null, balanceMsg.accountNumber)
        Assert.assertEquals("Digital Africa Services Jolet Supermarket", depositMsg.accountNumber)
        Assert.assertEquals(null, fulizaPay.accountNumber)
    }

    @Test
    fun `can get date`() {
        Assert.assertEquals(1562296680, reversalMsg.transactionDate)
        Assert.assertEquals(1551594120, sendMsg.transactionDate)
        Assert.assertEquals(1551244020, payBillMsg.transactionDate)
        Assert.assertEquals(1568621520, buyGoodsMsg.transactionDate)
        Assert.assertEquals(1565519760, withdrawMsg.transactionDate)
        Assert.assertEquals(1523525640, receiveMsg.transactionDate)
        Assert.assertEquals(1523360580, receiveMsgAlt.transactionDate)
        Assert.assertEquals(1562321040, airtimeMessage.transactionDate)
        Assert.assertEquals(1574673840, airtimeReceive.transactionDate)
        Assert.assertEquals(1567665300, balanceMsg.transactionDate)
        Assert.assertEquals(1372831620, depositMsg.transactionDate)
        Assert.assertEquals(1587803235, fulizaPay.transactionDate)
    }

    @Test
    fun `can get balance`() {
        Assert.assertEquals(2944.98, reversalMsg.balance, 0.0)
        Assert.assertEquals(2278.98, sendMsg.balance, 0.0)
        Assert.assertEquals(3143.98, payBillMsg.balance, 0.0)
        Assert.assertEquals(703.75, buyGoodsMsg.balance, 0.0)
        Assert.assertEquals(2000.98, withdrawMsg.balance, 0.0)
        Assert.assertEquals(4827.98, receiveMsg.balance, 0.0)
        Assert.assertEquals(4822.98, receiveMsgAlt.balance, 0.0)
        Assert.assertEquals(2744.98, airtimeMessage.balance, 0.0)
        Assert.assertEquals(0.0, airtimeReceive.balance, 0.0)
        Assert.assertEquals(2992.98, balanceMsg.balance, 0.0)
        Assert.assertEquals(1338.89, depositMsg.balance, 0.0)
        Assert.assertEquals(539.57, fulizaPay.balance, 0.0)
    }

    @Test
    fun `can get transaction cost`() {
        Assert.assertEquals(0.0, reversalMsg.transactionCost, 0.0)
        Assert.assertEquals(15.89, sendMsg.transactionCost, 0.0)
        Assert.assertEquals(34.89, payBillMsg.transactionCost, 0.0)
        Assert.assertEquals(0.89, buyGoodsMsg.transactionCost, 0.0)
        Assert.assertEquals(28.89, withdrawMsg.transactionCost, 0.0)
        Assert.assertEquals(0.0, receiveMsg.transactionCost, 0.0)
        Assert.assertEquals(0.0, receiveMsgAlt.transactionCost, 0.0)
        Assert.assertEquals(0.89, airtimeMessage.transactionCost, 0.0)
        Assert.assertEquals(0.0, airtimeReceive.transactionCost, 0.0)
        Assert.assertEquals(0.0, balanceMsg.transactionCost, 0.0)
        Assert.assertEquals(0.0, depositMsg.transactionCost, 0.0)
        Assert.assertEquals(0.0, fulizaPay.transactionCost, 0.0)
    }

}