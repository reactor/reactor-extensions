package reactor.scheduler.clock;

import java.time.Duration;

import org.junit.Assert;
import org.junit.Test;
import reactor.test.scheduler.VirtualTimeScheduler;

public class SchedulerClockTest {

	@Test
	public void shouldReturnExpectedTime() {
		VirtualTimeScheduler scheduler = VirtualTimeScheduler.create();
		SchedulerClock clock = SchedulerClock.of(scheduler);

		Assert.assertEquals(0, clock.millis());
		Assert.assertEquals(0, clock.instant().toEpochMilli());

		scheduler.advanceTimeBy(Duration.ofSeconds(1));

		Assert.assertEquals(1000, clock.millis());
		Assert.assertEquals(1000, clock.instant().toEpochMilli());
	}
}
