package com.sarabyeet.toget.arch

sealed class ToGetEvents {
    data class DbTransaction(val isSaved: Boolean): ToGetEvents()
}
