package com.example.practicafinallibros.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val BOOK_LIST = "book_list"
    const val BOOK_FORM = "book_form?bookId={bookId}"
    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val EXPLORE = "explore"
    const val ADMIN = "admin"
    const val SETTINGS = "settings"

    fun getFormRoute(bookId: Int? = null): String {
        return if (bookId != null) "book_form?bookId=$bookId" else "book_form"
    }

    fun getDetailRoute(bookId: Int): String {
        return "book_detail/$bookId"
    }
}