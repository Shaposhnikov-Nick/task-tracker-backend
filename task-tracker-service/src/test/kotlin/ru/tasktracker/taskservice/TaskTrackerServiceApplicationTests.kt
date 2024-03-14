package ru.tasktracker.taskservice

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TaskTrackerServiceApplicationTests: BaseTest() {

	@Test
	fun connectionEstablished() {
		Assertions.assertThat(container.isCreated()).isTrue()
		Assertions.assertThat(container.isRunning()).isTrue()
	}

	@Test
	fun contextLoads() {
	}

}
