package com.cookandroid.myproject

import com.google.gson.annotations.SerializedName

data class BusSystem(
        val response: BusSystemResponse
)

data class BusSystemResponse(
        val header: Header,
        val body: Body
)

data class Header(
        val resultCode: String,
        val resultMsg: String
)

data class Body(
        val items: Items,
        val numOfRows: Int,
        val pageNo: Int,
        val totalCount: Int
)

data class Items(
        val item: Item
)

data class Item(
        val bstopid: String,
        val bstopnm: String,
        val arsno: String,
        val gpsx: String,
        val gpsy: String,
        val stoptype: String
)

