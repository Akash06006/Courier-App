package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class PayUMoneyResponse {
    @SerializedName("status")
    val status: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("result")
    val result: Result? = null
    @SerializedName("errorCode")
    val errorCode: String? = null
    @SerializedName("responseCode")
    val responseCode: String? = null

    class Result {

        @SerializedName("postBackParamId")
        val postBackParamId: String? = null
        @SerializedName("mihpayid")
        val mihpayid: String? = null
        @SerializedName("paymentId")
        val paymentId: String? = null
        @SerializedName("mode")
        val mode: String? = null
        @SerializedName("status")
        val status: String? = null
        @SerializedName("unmappedstatus")
        val unmappedstatus: String? = null
        @SerializedName("key")
        val key: String? = null
        @SerializedName("txnid")
        val txnid: String? = null
        @SerializedName("amount")
        val amount: Double? = null
        @SerializedName("additionalCharges")
        val additionalCharges: String? = null
        @SerializedName("addedon")
        val addedon: String? = null
        @SerializedName("createdOn")
        val createdOn: String? = null
        @SerializedName("productinfo")
        val productinfo: String? = null
        @SerializedName("firstname")
        val firstname: String? = null
        @SerializedName("lastname")
        val lastname: String? = null
        @SerializedName("address1")
        val address1: String? = null
        @SerializedName("address2")
        val address2: String? = null
        @SerializedName("city")
        val city: String? = null
        @SerializedName("state")
        val state: String? = null
        @SerializedName("country")
        val country: String? = null
        @SerializedName("zipcode")
        val zipcode: String? = null
        @SerializedName("email")
        val email: String? = null
        @SerializedName("phone")
        val phone: String? = null
        @SerializedName("udf1")
        val udf1: String? = null
        @SerializedName("udf2")
        val udf2: String? = null
        @SerializedName("udf3")
        val udf3: String? = null
        @SerializedName("udf4")
        val udf4: String? = null
        @SerializedName("udf5")
        val udf5: String? = null
        @SerializedName("udf6")
        val udf6: String? = null
        @SerializedName("udf7")
        val udf7: String? = null
        @SerializedName("udf8")
        val udf8: String? = null
        @SerializedName("udf9")
        val udf9: String? = null
        @SerializedName("udf10")
        val udf10: String? = null
        @SerializedName("hash")
        val hash: String? = null
        @SerializedName("field1")
        val field1: String? = null
        @SerializedName("field2")
        val field2: String? = null
        @SerializedName("field3")
        val field3: String? = null
        @SerializedName("field4")
        val field4: String? = null
        @SerializedName("field5")
        val field5: String? = null
        @SerializedName("field6")
        val field6: String? = null
        @SerializedName("field7")
        val field7: String? = null
        @SerializedName("field8")
        val field8: String? = null
        @SerializedName("field9")
        val field9: String? = null
        @SerializedName("bank_ref_num")
        val bank_ref_num: String? = null
        @SerializedName("bankcode")
        val bankcode: String? = null
        @SerializedName("error")
        val error: String? = null
        @SerializedName("error_Message")
        val error_Message: String? = null
        @SerializedName("cardToken")
        val cardToken: String? = null
        @SerializedName("offer_key")
        val offer_offer_key: String? = null
        @SerializedName("offer_type")
        val offer_type: String? = null
        @SerializedName("offer_availed")
        val offer_availed: String? = null
        @SerializedName("pg_ref_no")
        val pg_ref_no: String? = null
        @SerializedName("offer_failure_reason")
        val offer_failure_reason: String? = null
        @SerializedName("name_on_card")
        val name_on_card: String? = null
        @SerializedName("cardnum")
        val cardnum: String? = null
        @SerializedName("cardhash")
        val cardhash: String? = null
        @SerializedName("card_type")
        val card_type: String? = null
        @SerializedName("card_merchant_param")
        val card_merchant_param: String? = null
        @SerializedName("version")
        val version: String? = null
        @SerializedName("postUrl")
        val postUrl: String? = null
        @SerializedName("calledStatus")
        val calledStatus: Boolean? = null
        @SerializedName("additional_param")
        val additional_param: String? = null
        @SerializedName("amount_split")
        val amount_split: String? = null
        @SerializedName("discount")
        val discount: Double? = null
        @SerializedName("net_amount_debit")
        val net_amount_debit: String? = null
        @SerializedName("fetchAPI")
        val fetchAPI: String? = null
        @SerializedName("paisa_mecode")
        val paisa_mecode: String? = null
        @SerializedName("meCode")
        val meCode: String? = null
        @SerializedName("payuMoneyId")
        val payuMoneyId: String? = null
        @SerializedName("encryptedPaymentId")
        val encryptedPaymentId: String? = null
        @SerializedName("id")
        val id: String? = null
        @SerializedName("surl")
        val surl: String? = null
        @SerializedName("furl")
        val furl: String? = null
        @SerializedName("baseUrl")
        val baseUrl: String? = null
        @SerializedName("retryCount")
        val retryCount: String? = null
        @SerializedName("merchantid")
        val merchantid: String? = null
        @SerializedName("payment_source")
        val payment_source: String? = null
        @SerializedName("isConsentPayment")
        val isConsentPayment: String? = null
        @SerializedName("giftCardIssued")
        val giftCardIssued: Boolean? = null
        @SerializedName("pg_TYPE")
        val pg_TYPE: String? = null
        @SerializedName("s2SPbpFlag")
        val s2SPbpFlag: Boolean? = null
    }
}