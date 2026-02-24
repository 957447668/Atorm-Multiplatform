package com.zxhhyj.atorm.templates

import com.zxhhyj.atorm.User
import com.zxhhyj.atorm.UserTable
import com.zxhhyj.atorm.crud.extensions.deleteForm
import com.zxhhyj.atorm.crud.extensions.form
import com.zxhhyj.atorm.crud.extensions.updateWhere
import com.zxhhyj.atorm.crud.suspendTransaction
import com.zxhhyj.atorm.database.Database
import com.zxhhyj.atorm.database.sqlite.SQLiteConnection
import com.zxhhyj.atorm.dsl.ConditionalExpression.Companion.eq
import kotlin.test.assertEquals

class CrudTestTemplate(connection: SQLiteConnection) {
    private val database = Database.connect(connection = connection)

    init {
        database.createTable(UserTable, true)
    }

    private suspend fun run1() {
        val user = User(
            id = 1,
            name = "user 1",
            location = "City 1",
            type = User.Type.User
        )

        suspendTransaction {
            database.deleteForm(UserTable)
        }

        suspendTransaction {
            database.insert(UserTable, user)
        }

        suspendTransaction {
            database.updateWhere(UserTable, { row[UserTable.name] = "ZXHHYJ" }, { UserTable.id eq 1 })
        }

        val resultSet = suspendTransaction {
            database.form(UserTable).selectAll().asKotlinSequence().toList().first()
        }

        val name = resultSet[UserTable.name]

        assertEquals(name, "ZXHHYJ")
    }

    private suspend fun run2() {
        val resultSets = suspendTransaction(database) {
            insert(UserTable) {
                row[UserTable.name] = "ZXHHYJ"
                row[UserTable.location] = null
                row[UserTable.type] = User.Type.User
            }

            database.form(UserTable).selectAll().asKotlinSequence().toList()
        }
        println()
    }

    suspend fun run() {
        run1()
        run2()
    }
}