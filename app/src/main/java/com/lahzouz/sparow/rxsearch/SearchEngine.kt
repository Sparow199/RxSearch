package com.lahzouz.sparow.rxsearch

class SearchEngine(private val departments: Array<String>) {

    fun getAll(): List<String> {
        return departments.toList()
    }

    fun search(query: String): List<String> {
        return departments.filter { it.toLowerCase().contains(query.toLowerCase()) }
    }

}