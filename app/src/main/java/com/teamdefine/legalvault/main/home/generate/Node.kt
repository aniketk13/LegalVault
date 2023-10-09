package com.teamdefine.legalvault.main.home.generate


class Node(
    value: String,
    nextNodeId: Any?,
    text: String,
    date: String,
    documentName: String,
    signersName: String,
    is_signed: Boolean
) {
    var value: String? = null
        private set
    var nextNodeId: Any? = null // Reference to the next node's document ID
        private set
    var text: String? = null
        private set
    var date: String? = null
        private set
    var documentName: String? = null
        private set
    var signers: String? = null
        private set
    var is_signed: Boolean? = false
        private set

    constructor() : this("", "", "", "", "", "", false)

    init {
        this.value = value
        this.nextNodeId = nextNodeId
        this.signers = signersName
        this.date = date
        this.text = text
        this.documentName = documentName
        this.is_signed = is_signed
    }
}

data class LinkedList(
    val nodes: List<Node>
)
