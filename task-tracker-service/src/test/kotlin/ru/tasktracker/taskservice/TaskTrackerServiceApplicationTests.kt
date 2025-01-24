package ru.tasktracker.taskservice

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

//@SpringBootTest
//class TaskTrackerServiceApplicationTests: BaseTest() {
//
//	@Test
//	fun connectionEstablished() {
//		Assertions.assertThat(container.isCreated()).isTrue()
//		Assertions.assertThat(container.isRunning()).isTrue()
//	}
//
//	@Test
//	fun contextLoads() {
//	}
//
//}


@SpringBootTest
class TaskTrackerServiceApplicationTests: BaseTest() {

	@Autowired
	lateinit var applicationContext: ApplicationContext

		@Test
	fun connectionEstablished() {
		assertThat(container.isCreated()).isTrue()
		assertThat(container.isRunning()).isTrue()
	}

	@Test
	fun contextLoads() {
		assertThat(applicationContext).isNotNull()
	}
}