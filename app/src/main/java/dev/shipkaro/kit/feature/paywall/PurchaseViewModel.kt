package dev.shipkaro.kit.feature.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Package
import dev.shipkaro.kit.core.billing.BillingErrorCode
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.billing.PurchaseOutcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives [PaywallScreen]. Wraps [PurchaseManager] — exposes premium status, the current
 * offering, and a [Status] the screen renders.
 */
class PurchaseViewModel(private val purchases: PurchaseManager) : ViewModel() {

    /** UI status for the paywall. */
    sealed interface Status {
        data object Idle : Status
        data object Working : Status
        data class Error(val code: BillingErrorCode) : Status

        /** Restore ran but found no active entitlement. */
        data object NothingRestored : Status

        /** Entitlement is now active — screen should navigate onward. */
        data object Entitled : Status
    }

    val isConfigured: Boolean get() = purchases.isConfigured
    val isPremium: StateFlow<Boolean> = purchases.isPremium
    val currentOffering: StateFlow<Offering?> = purchases.currentOffering

    private val _status = MutableStateFlow<Status>(Status.Idle)
    val status: StateFlow<Status> = _status.asStateFlow()

    fun purchase(activity: Activity, pkg: Package) {
        viewModelScope.launch {
            _status.update { Status.Working }
            _status.update { purchases.purchase(activity, pkg).toStatus() }
        }
    }

    fun restore() {
        viewModelScope.launch {
            _status.update { Status.Working }
            _status.update { purchases.restore().toStatus() }
        }
    }

    private fun PurchaseOutcome.toStatus(): Status = when (this) {
        PurchaseOutcome.Success -> Status.Entitled
        PurchaseOutcome.NothingToRestore -> Status.NothingRestored
        PurchaseOutcome.Cancelled -> Status.Idle
        is PurchaseOutcome.Failure -> Status.Error(code)
    }
}
