package com.zxhhyj.atorm.templates

import com.zxhhyj.atorm.User
import com.zxhhyj.atorm.UserTable
import com.zxhhyj.atorm.crud.extensions.batchInsert
import com.zxhhyj.atorm.crud.extensions.deleteForm
import com.zxhhyj.atorm.crud.extensions.form
import com.zxhhyj.atorm.crud.suspendTransaction
import com.zxhhyj.atorm.database.Database
import com.zxhhyj.atorm.database.sqlite.SQLiteConnection
import kotlin.time.measureTime

class BenchmarkTestTemplate(connection: SQLiteConnection) {
    private val database = Database.connect(connection = connection)

    private val repeatSize = 5

    init {
        database.createTable(UserTable, true)
    }

    suspend fun benchmarkAutoMappingBatchInsert() {
        val list = buildList {
            repeat(100_000) { index ->
                val userIndex = index + 1
                add(
                    User(
                        id = userIndex,
                        name = "user$userIndex",
                        location = "City${(index % 100) + 1}",
                        type = User.Type.Admin
                    )
                )
            }
        }

        println("Reflection Test")

        repeat(repeatSize) {

            val duration = measureTime {
                suspendTransaction {
                    database.deleteForm(UserTable)

                    database.batchInsert(UserTable, list)
                }
            }

            println("repeat$it execution time: $duration.")
        }

        println("Non-reflection Test")

        repeat(repeatSize) {

            val duration = measureTime {
                suspendTransaction(database) {
                    deleteForm(UserTable)

                    batchInsert(UserTable, list) {
                        row[UserTable.id] = it.id
                        row[UserTable.name] = it.name
                        row[UserTable.location] = it.location
                        row[UserTable.type] = it.type
                    }
                }
            }

            println("repeat$it execution time: $duration.")
        }
    }

    suspend fun benchmarkSelectAllUsers() {
        val executionTime = measureTime {
            val users = suspendTransaction {
                database
                    .form(UserTable)
                    .selectAll()
                    .asKotlinSequence()
                    .toList()
            }
            println("Selected ${users.size} users.")
        }

        println("Query execution time: $executionTime.")
    }
}