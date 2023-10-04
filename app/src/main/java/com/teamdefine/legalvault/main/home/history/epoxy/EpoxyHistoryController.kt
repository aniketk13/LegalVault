package com.teamdefine.legalvault.main.home.history.epoxy

import com.airbnb.epoxy.Typed2EpoxyController
import com.teamdefine.legalvault.main.home.generate.Node

class EpoxyHistoryController() :
    Typed2EpoxyController<Pair<Node, ArrayList<Node>>, String>() {
    override fun buildModels(
        signatureData: Pair<Node, ArrayList<Node>>?,
        data2: String?
    ) {
        signatureData?.let {
            buildViews(it.first, it.second)
        }
    }

    private fun buildViews(first: Node, second: java.util.ArrayList<Node>) {
        epoxyModelNew {
            id(first.value)
            first.documentName?.let { agreementTitle(it) }
            first.signers?.replace(",", "-")?.let { agreementBetween(it) }
            first.date?.let { date(it) }
        }
        second.forEach {
            epoxyModelOld {
                id(it.value)
                it.documentName?.let { agreementTitle(it) }
                it.signers?.replace(",", "-")?.let { agreementBetween(it) }
                it.date?.let { date(it) }
            }
        }
    }

//    private fun getContractBetween(first: SignatureRequest): String {
//        return when (first.signatures.size) {
//            2 -> "${first.signatures[0]?.signer_name?.substringBefore(" ")} - ${
//                first.signatures[1]?.signer_name?.substringBefore(
//                    " "
//                )
//            }"
//
//            1 -> "${first.signatures[0]?.signer_name?.substringBefore(" ")}"
//            else -> "Dummy User"
//        }
//    }
}