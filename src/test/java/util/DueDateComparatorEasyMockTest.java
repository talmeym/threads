package util;

import data.*;
import org.easymock.*;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(EasyMockRunner.class)
public class DueDateComparatorEasyMockTest extends EasyMockSupport {
	@Mock
	HasDueDate o_mock1;

	@Mock
	HasDueDate o_mock2;

	private DueDateComparator<Reminder> o_toTest = new DueDateComparator<Reminder>();

	@Test
	public void testNegative() {
		setUpExpectionDates(10);
		assertEquals(-1, o_toTest.compare(o_mock1, o_mock2));
		verifyAll();
	}

	@Test
	public void testPositive() {
		setUpExpectionDates(-10);
		assertEquals(1, o_toTest.compare(o_mock1, o_mock2));
		verifyAll();
	}

	@Test
	public void testSame() {
		setUpExpectionDates(0);
		assertEquals(0, o_toTest.compare(o_mock1, o_mock2));
		verifyAll();
	}

	@Test(expected = IllegalStateException.class)
	public void testException() {
		long now = System.currentTimeMillis();
		expect(o_mock1.getDueDate()).andReturn(new Date(now));
		expect(o_mock2.getDueDate()).andThrow(new IllegalStateException("Something"));
		replay(o_mock1, o_mock2);
		o_toTest.compare(o_mock1, o_mock2);
		verifyAll();
	}

	private void setUpExpectionDates(int offset) {
		long now = System.currentTimeMillis();
		expect(o_mock1.getDueDate()).andReturn(new Date(now));
		expect(o_mock2.getDueDate()).andReturn(new Date(now + offset));
		replay(o_mock1, o_mock2);
	}
}
