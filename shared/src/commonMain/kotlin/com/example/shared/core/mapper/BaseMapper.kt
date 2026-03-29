package com.example.shared.core.mapper

interface BaseMapper<R, E> {
    fun toEntity(response: R): E
}
