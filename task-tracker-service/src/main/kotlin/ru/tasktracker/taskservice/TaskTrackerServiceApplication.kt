package ru.tasktracker.taskservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskTrackerServiceApplication

fun main(args: Array<String>) {
	runApplication<TaskTrackerServiceApplication>(*args)
}
