package com.teamdefine.legalvault.main.home.history.epoxy

import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.BR

@EpoxyModelClass(layout = R.layout.item_sign_requests_new)
abstract class EpoxyModelNew : DataBindingEpoxyModel() {

    @EpoxyAttribute
    var agreementTitle: String = ""

    @EpoxyAttribute
    var agreementBetween: String = ""

    @EpoxyAttribute
    var date: String = ""

    override fun setDataBindingVariables(binding: ViewDataBinding) {
        binding.setVariable(BR.agreementName, agreementTitle)
        binding.setVariable(BR.partiesName, agreementBetween)
        binding.setVariable(BR.date, date)
    }
}
