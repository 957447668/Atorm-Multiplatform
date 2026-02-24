package com.zxhhyj.atorm

import com.zxhhyj.atorm.schema.Table

class User(
    var id: Int,
    var name: String,
    var location: String?,
    var type: Type,
) {
    enum class Type {
        User, Admin
    }
}

object UserTable : Table<User>("t_user") {
    val id = int("id").primaryKey(autoincrement = true).bindTo(User::id)
    val name = varchar("name").bindTo(User::name)
    val location = varchar("location").nullable().bindTo(User::location)
    val type = enum<User.Type>("type").bindTo(User::type)
}