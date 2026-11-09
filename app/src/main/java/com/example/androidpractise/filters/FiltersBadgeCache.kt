package com.example.androidpractise.filters

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Сессионный кэш состояния фильтров: экран настроек помечает, что фильтры
 * отличаются от дефолтных, а экран списка читает флаг для показа бейджа.
 */
class FiltersBadgeCache {

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    fun setActive(active: Boolean) {
        _isActive.update { active }
    }
}
