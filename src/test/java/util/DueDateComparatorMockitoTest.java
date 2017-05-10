package util;

import data.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DueDateComparatorMockitoTest {
	@Mock
	HasDueDate o_mock1;

	@Mock
	HasDueDate o_mock2;

	private DueDateComparator<Reminder> o_toTest = new DueDateComparator<>();

	@Test
	public void testSecondDatesAfterEqualsNegativeResult() {
		assertOffsetsCausesResult(10, -1);
	}

	@Test
	public void testSecondDateBeforeEqualsPositiveResult() {
		assertOffsetsCausesResult(-10, 1);
	}

	@Test
	public void testSecondDateSameEqualsZeroResult() {
		assertOffsetsCausesResult(0, 0);
	}

	@Test(expected = IllegalStateException.class)
	public void testException() {
		long now = System.currentTimeMillis();
		when(o_mock1.getDueDate()).thenReturn(new Date(now));
		when(o_mock2.getDueDate()).thenThrow(new IllegalStateException("Something"));
		o_toTest.compare(o_mock1, o_mock2);
		verify(o_mock1).getDueDate();
		verify(o_mock2).getDueDate();
	}

	private void assertOffsetsCausesResult(int secondDateMilliOffset, int expectedResult) {
		long now = System.currentTimeMillis();
		when(o_mock1.getDueDate()).thenReturn(new Date(now));
		when(o_mock2.getDueDate()).thenReturn(new Date(now + secondDateMilliOffset));
		assertEquals(expectedResult, o_toTest.compare(o_mock1, o_mock2));
		verify(o_mock1).getDueDate();
		verify(o_mock2).getDueDate();
	}
}
