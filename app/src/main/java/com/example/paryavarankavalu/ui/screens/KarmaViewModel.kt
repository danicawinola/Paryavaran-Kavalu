package com.example.paryavarankavalu.ui.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paryavarankavalu.data.KarmaStore
import com.example.paryavarankavalu.model.Badge
import com.example.paryavarankavalu.model.BadgeCatalog
import com.example.paryavarankavalu.model.KarmaEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KarmaUiState(
    val totalPoints: Int        = 0,
    val currentBadge: Badge     = BadgeCatalog.all.first(),
    val nextBadge: Badge?       = BadgeCatalog.all[1],
    val progressPercent: Float  = 0f,       // 0.0 – 1.0
    val earnedBadges: List<Badge> = emptyList(),
    val allBadges: List<Badge>  = BadgeCatalog.all,
    val recentEntries: List<KarmaEntry> = emptyList()
)

class KarmaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KarmaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val total   = KarmaStore.totalPoints.value
            val entries = KarmaStore.entries.toList()

            val current  = BadgeCatalog.currentBadge(total)
            val next     = BadgeCatalog.nextBadge(total)
            val earned   = BadgeCatalog.earnedBadges(total)

            // Progress between current badge threshold and next badge threshold
            val progress = if (next != null) {
                val range = (next.requiredPoints - current.requiredPoints).toFloat()
                val done  = (total - current.requiredPoints).toFloat()
                (done / range).coerceIn(0f, 1f)
            } else 1f   // already at Legend

            _uiState.value = KarmaUiState(
                totalPoints     = total,
                currentBadge    = current,
                nextBadge       = next,
                progressPercent = progress,
                earnedBadges    = earned,
                allBadges       = BadgeCatalog.all,
                recentEntries   = entries.take(20)
            )
        }
    }
}