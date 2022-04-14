package com.gitbusters.pixelgram.api

data class Comments(
    val content: List<Content>,
    val pageable: Pageable,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
    val sort: Sort,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)
