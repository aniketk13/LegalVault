package com.teamdefine.legalvault.main.home.bottomsheet.model

data class GitHubUpdateFileStatusResponseModel(
    val workflow_runs: ArrayList<Workflow>
)

data class Workflow(
    val status: String
)