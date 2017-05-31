package threads.util;

import threads.data.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveAwareUpdateOrHasDueDateComparatorTest {
	private static final Date NOW = new Date(System.currentTimeMillis());
	private static final Date ANY_DATE_1 = new Date(System.currentTimeMillis() + new Random().nextInt());
	private static final Date ANY_DATE_2 = new Date(System.currentTimeMillis() + new Random().nextInt());
	private static final Date BEFORE_NOW = new Date(System.currentTimeMillis() - 10);
	private static final Date AFTER_NOW = new Date(System.currentTimeMillis() + 10);
	private static final int FIRST_BEFORE = -1, FIRST_AFTER = 1, THE_SAME = 0;

	private ActiveAwareUpdateOrHasDueDateComparator toTest = new ActiveAwareUpdateOrHasDueDateComparator();

	@Mock
	Item mock1, mock2;

	@Test
	public void testFirstActiveSecondInactiveMeansSecondIsLess() {
		assertExpectedBehaviour(true, false, ANY_DATE_1, ANY_DATE_2, FIRST_BEFORE);
	}

	@Test
	public void testFirstInActiveSecondActiveMeansSecondIsMore() {
		assertExpectedBehaviour(false, true, ANY_DATE_1, ANY_DATE_2, FIRST_AFTER);
	}

	@Test
	public void testFirstAndSecondActiveMeansDatesCompared_FirstDateSame() {
		assertExpectedBehaviour(true, true, NOW, NOW, THE_SAME);
	}

	@Test
	public void testFirstAndSecondActiveMeansDatesCompared_FirstDateAfter() {
		assertExpectedBehaviour(true, true, NOW, BEFORE_NOW, FIRST_AFTER);
	}

	@Test
	public void testFirstAndSecondActiveMeansDatesCompared_firstDateBefore() {
		assertExpectedBehaviour(true, true, NOW, AFTER_NOW, FIRST_BEFORE);
	}

	private void assertExpectedBehaviour(boolean active1, boolean active2, Date date1, Date date2, int expectedResult) {
		when(mock1.getDueDate()).thenReturn(date1, date1);
		when(mock2.getDueDate()).thenReturn(date2, date2);

		when(mock1.isActive()).thenReturn(active1, active1);
		when(mock2.isActive()).thenReturn(active2, active2);

		assertEquals(expectedResult, toTest.compare(mock1, mock2));

		Mockito.verify(mock1, atLeastOnce()).isActive();
		Mockito.verify(mock2, atLeastOnce()).isActive();
		Mockito.verify(mock1, atMost(2)).getDueDate();
		Mockito.verify(mock2, atMost(2)).getDueDate();
	}
}
