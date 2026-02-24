# Storm Multiplatform

用 Kotlin 编写的跨平台可嵌入关系数据库管理系统，支持 Android、iOS、MacOS (Native) 和 JVM 平台。

## 核心特性

### 数据库能力

- **关系型数据库**: 完整的表、索引、约束支持
- **SQL 支持**: 支持标准 SQL 查询语言
- **ACID 事务**: 保证事务的原子性、一致性、隔离性和持久性
- **嵌入式架构**: 无需独立服务器，直接嵌入应用程序

### 跨平台支持

- **多平台兼容**: 统一的 Kotlin API 支持 Android、iOS、MacOS (Native)、JVM
- **零配置部署**: 简单集成，无需复杂配置
- **一致行为**: 所有平台上提供相同的数据库行为和性能特征

### 开发体验

- **类型安全**: Kotlin DSL 提供编译时类型检查
- **自动映射**: 数据库行与 Kotlin 对象之间的自动转换
- **Kotlin 原生**: 充分利用 Kotlin 语言特性（协程、Flow、数据类等）
- **低学习曲线**: 简洁直观的 API 设计

### 性能优化

- **双模式映射**: 支持反射和手动映射两种模式，平衡易用性和性能
- **批处理**: 高效的批量插入和更新操作
- **内存效率**: 使用 Sequence 模式减少内存占用

## 快速开始

### 1. 定义数据模型

```kotlin
// 数据模型
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

// 表定义
object UserTable : Table<User>("t_user") {
    val id = int("id").primaryKey(autoincrement = true).bindTo(User::id)
    val name = varchar("name").bindTo(User::name)
    val location = varchar("location").nullable().bindTo(User::location)
    val type = enum<User.Type>("type").bindTo(User::type)
}
```

### 2. 建立连接

```kotlin
// 创建数据库连接
val connection = SQLiteConnection(databasePath = "app.db")
val database = Database.connect(connection)

// 创建表
database.createTable(UserTable, ifNotExists = true)
```

### 3. 执行操作

```kotlin
// 插入数据
suspendTransaction(database) {
    val user = User(
        id = null, // autoincrement
        name = "Alice",
        location = "Beijing",
        type = User.Type.Admin
    )
    database.insert(UserTable, user)
}

// 查询数据
val users = database
    .form(UserTable)
    .select { UserTable.type eq User.Type.Admin }
    .asKotlinSequence()
    .toList()

// 批量插入
val userList = buildList {
    repeat(1000) { index ->
        add(
            User(
                id = 0,
                name = "User$index",
                location = "City${index % 10}",
                type = User.Type.User
            )
        )
    }
}

batchInsert(UserTable, userList) {
    row[UserTable.name] = it.name
    row[UserTable.location] = it.location
    row[UserTable.type] = it.type
}
```

## 性能基准

### 批量插入性能（100,000 条记录）

**测试环境**: Apple M4 芯片, JVM, SQLite 3.42.0

#### 反射模式测试结果

```
repeat0 execution time: 162.269625ms
repeat1 execution time: 98.698833ms
repeat2 execution time: 72.788541ms
repeat3 execution time: 72.346291ms
repeat4 execution time: 75.235292ms
平均执行时间: ~96.27ms
```

#### 非反射模式测试结果

```
repeat0 execution time: 87.694208ms
repeat1 execution time: 77.945417ms
repeat2 execution time: 73.521292ms
repeat3 execution time: 73.942833ms
repeat4 execution time: 69.849333ms
平均执行时间: ~76.59ms
```

### 查询性能

**查询 100,000 条用户记录**

```kotlin
val users = database
    .form(UserTable)
    .selectAll()
    .asKotlinSequence()
    .toList()
```

**执行时间**: 68.529458ms

### 性能总结

| 操作   | 数据量  | 反射模式  | 非反射模式 | 查询    |
|------|------|-------|-------|-------|
| 批量插入 | 100k | ~96ms | ~77ms | -     |
| 查询所有 | 100k | -     | -     | ~69ms |

## 与其他数据库对比

| 特性         | Storm | Room | SQLite |
|------------|-------|------|--------|
| 跨平台        | ✅     | ❌    | ⚠️     |
| Kotlin 原生  | ✅     | ✅    | ❌      |
| 类型安全       | ✅     | ✅    | ❌      |
| 嵌入式        | ✅     | ✅    | ✅      |
| Android 支持 | ✅     | ✅    | ✅      |
| iOS 支持     | ✅     | ❌    | ✅      |
| Native 支持  | ✅     | ❌    | ✅      |
| JVM 支持     | ✅     | ❌    | ✅      |
| 零配置        | ✅     | ⚠️   | ❌      |

## 社区和支持

- **问题反馈**: [Gitee Issues](https://gitee.com/ZXHHYJ/storm-multiplatform/issues)
- **讨论交流**: [QQ Group](https://qm.qq.com/q/xCaK1dki8S)

## 许可证

本项目采用 Apache 许可证。详见 [LICENSE](./LICENSE) 文件。