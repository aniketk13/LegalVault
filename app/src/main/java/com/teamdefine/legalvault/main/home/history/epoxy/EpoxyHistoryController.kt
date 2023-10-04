package com.teamdefine.legalvault.main.home.history.epoxy

import com.airbnb.epoxy.Typed2EpoxyController
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.Utility

class EpoxyHistoryController() :
    Typed2EpoxyController<ArrayList<Pair<SignatureRequest, ArrayList<SignatureRequest>>>, String>() {
    override fun buildModels(
        signatureData: ArrayList<Pair<SignatureRequest, ArrayList<SignatureRequest>>>?,
        data2: String?
    ) {
        signatureData?.let {
            it.forEach {
                buildViews(it.first, it.second)
            }
        }
    }

    private fun buildViews(first: SignatureRequest, second: java.util.ArrayList<SignatureRequest>) {
        epoxyModelNew {
            agreementTitle(first.subject)
            agreementBetween(getContractBetween(first))
            date(Utility.convertTimestampToDateInIST(first.created_at))
        }
        second.forEach {
            epoxyModelOld {
                agreementTitle(it.subject)
                agreementBetween(getContractBetween(it))
                date(Utility.convertTimestampToDateInIST(it.created_at))
            }
        }
    }

    private fun getContractBetween(first: SignatureRequest): String {
        return when (first.signatures.size) {
            2 -> "${first.signatures[0]?.signer_name?.substringBefore(" ")} - ${
                first.signatures[1]?.signer_name?.substringBefore(
                    " "
                )
            }"

            1 -> "${first.signatures[0]?.signer_name?.substringBefore(" ")}"
            else -> "Dummy User"
        }
    }
}