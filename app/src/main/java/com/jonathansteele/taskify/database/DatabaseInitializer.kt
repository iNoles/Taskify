package com.jonathansteele.taskify.database

suspend fun seedIfEmpty(taskListDao: TaskListDao) {
    val existing = taskListDao.getAll()
    if (existing.isEmpty()) {
        taskListDao.insert(TaskList(name = "Personal"))
        taskListDao.insert(TaskList(name = "Work"))
    }
}
