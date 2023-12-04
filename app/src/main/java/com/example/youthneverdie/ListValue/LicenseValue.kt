package com.example.youthneverdie.ListValue

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "response", strict = false)
data class ApiResponse @JvmOverloads constructor(
    @field:Element(name = "header", required = false)
    var header:Header = Header(),
    @field:Element(name = "body", required = false)
    var body: Body = Body()
)
@Root(name = "header", strict = false)
data class Header @JvmOverloads constructor(
    @field:Element(name = "resultCode")
    var resultCode: String = "",
    @field:Element(name = "resultMsg")
    var resultMsg: String = ""
)
@Root(name = "body", strict = false)
data class Body @JvmOverloads constructor(
    @field:Element(name = "items", required = false)
    var items: Items = Items()

)

@Root(name = "items", strict = false)
data class Items @JvmOverloads constructor(
    @field:ElementList(entry = "item", inline = true, required = false)
var itemList: MutableList<Item> = mutableListOf()


)

@Root(name = "item", strict = false)
data class Item @JvmOverloads constructor(
    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "docexamdt", required = false)
    var docexamdt: String = "",

    @field:Element(name = "docpassdt", required = false)
    var docpassdt: String = "",

    @field:Element(name = "docregenddt", required = false)
    var docregenddt: String = "",

    @field:Element(name = "docregstartdt", required = false)
    var docregstartdt: String = "",

    @field:Element(name = "docsubmitentdt", required = false)
    var docsubmitentdt: String = "",

    @field:Element(name = "docsubmitstartdt", required = false)
    var docsubmitstartdt: String = "",

    @field:Element(name = "pracexamenddt", required = false)
    var pracexamenddt: String = "",

    @field:Element(name = "pracexamstartdt", required = false)
    var pracexamstartdt: String = "",

    @field:Element(name = "pracpassdt", required = false)
    var pracpassdt: String = "",

    @field:Element(name = "pracregenddt", required = false)
    var pracregenddt: String = "",

    @field:Element(name = "pracregstartdt", required = false)
    var pracregstartdt: String = "",
)

