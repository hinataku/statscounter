package com.hinataku.statscounter.data

import kotlinx.serialization.Serializable

@Serializable
data class Player(val id: Long, val name: String)
