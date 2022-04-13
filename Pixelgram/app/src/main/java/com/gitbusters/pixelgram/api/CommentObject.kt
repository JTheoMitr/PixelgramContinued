package com.gitbusters.pixelgram.api

data class CommentObject(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: PageableX,
    val size: Int,
    val sort: SortXXX,
    val totalElements: Int,
    val totalPages: Int
)