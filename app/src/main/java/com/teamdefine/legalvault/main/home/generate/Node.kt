package com.teamdefine.legalvault.main.home.generate

data class Node(
    val value: String?,
    val nextNodeId: String? = null // Reference to the next node's document ID
)

data class LinkedList(
    val nodes: List<Node>
)
