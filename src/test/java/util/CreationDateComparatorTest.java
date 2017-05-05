package util;

import data.Item;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreationDateComparatorTest {
	private static final Date NOW = new Date(System.currentTimeMillis());
	private static final Date AFTER_NOW = new Date(System.currentTimeMillis() + 10);
	private static final Date BEFORE_NOW = new Date(System.currentTimeMillis() - 10);
	private static final int FIRST_DATE_BEFORE = -1, FIRST_DATE_AFTER = 1, DATES_THE_SAME = 0;

	@Mock
	private Item mockItem1, mockItem2;

	CreationDateComparator toTest = new CreationDateComparator();

	@Test
	public void testFirstDateAfter() {
		assertOffsetsCausesExpectedResult(AFTER_NOW, NOW, FIRST_DATE_BEFORE);
	}

	@Test
	public void testFirstDateBefore() {
		assertOffsetsCausesExpectedResult(BEFORE_NOW, NOW, FIRST_DATE_AFTER);
	}

	@Test
	public void testDatesTheSame() {
		assertOffsetsCausesExpectedResult(NOW, NOW, DATES_THE_SAME);
	}

	private void assertOffsetsCausesExpectedResult(Date date1, Date date2, int expectedResult) {
		when(mockItem1.getCreationDate()).thenReturn(date1);
		when(mockItem2.getCreationDate()).thenReturn(date2);

		Assert.assertEquals(expectedResult, toTest.compare(mockItem1, mockItem2));

		verify(mockItem1).getCreationDate();
		verify(mockItem2).getCreationDate();
	}
}
