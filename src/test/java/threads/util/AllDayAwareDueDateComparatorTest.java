package threads.util;

import threads.data.*;
import org.junit.Test;
import threads.data.Thread;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class AllDayAwareDueDateComparatorTest {
	private static final int ALL_DAY = 0;
	private static final int NINE_AM = 9;
	private static final int TEN_AM = 10;

	private final AllDayAwareDueDateComparator toTest = new AllDayAwareDueDateComparator();

	@Test
	public void testNormalDatesFirstBeforeNeitherDue() {
		assertEquals(-1, toTest.compare(buildHasDueDate(NINE_AM, false), buildHasDueDate(TEN_AM, false)));
	}

	@Test
	public void testNormalDatesFirstBeforeBothDue() {
		assertEquals(-1, toTest.compare(buildHasDueDate(NINE_AM, true), buildHasDueDate(TEN_AM, true)));
	}

	@Test
	public void testNormalDatesFirstBeforeFirstDue() {
		assertEquals(-1, toTest.compare(buildHasDueDate(NINE_AM, true), buildHasDueDate(TEN_AM, false)));
	}

	@Test
	public void testNormalDatesSecondFirstNeitherDue() {
		assertEquals(1, toTest.compare(buildHasDueDate(TEN_AM, false), buildHasDueDate(NINE_AM, false)));
	}

	@Test
	public void testNormalDatesSecondFirstBothDue() {
		assertEquals(1, toTest.compare(buildHasDueDate(TEN_AM, true), buildHasDueDate(NINE_AM, true)));
	}

	@Test
	public void testNormalDatesSecondFirstSecondDue() {
		assertEquals(1, toTest.compare(buildHasDueDate(TEN_AM, false), buildHasDueDate(NINE_AM, true)));
	}

	@Test
	public void testNormalDatesFirstAllDayNeitherDue() {
		assertEquals(-1, toTest.compare(buildHasDueDate(ALL_DAY, false), buildHasDueDate(TEN_AM, false)));
	}

	@Test
	public void testSpecialCaseFirstAllDaySecondDue() {
		assertEquals(1, toTest.compare(buildHasDueDate(ALL_DAY, false), buildHasDueDate(TEN_AM, true)));
	}

	@Test
	public void testNormalDatesSecondAllDayNeitherDue() {
		assertEquals(1, toTest.compare(buildHasDueDate(TEN_AM, false), buildHasDueDate(ALL_DAY, false)));
	}

	@Test
	public void testSpecialCaseSecondAllDayFirstDue() {
		assertEquals(-1, toTest.compare(buildHasDueDate(TEN_AM, true), buildHasDueDate(ALL_DAY, false)));
	}

	private HasDueDate buildHasDueDate(int timeOfDay, final boolean isDue) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, timeOfDay);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new HasDueDate() {
			@Override
			public UUID getId() {
				throw new UnsupportedOperationException();
			}

			@Override
			public ComponentType getType() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isActive() {
				return true;
			}

			@Override
			public void setActive(boolean active) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getText() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setText(String x_text) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getNotes() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Date getDueDate() {
				return calendar.getTime();
			}

			@Override
			public void setDueDate(Date dueDate) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setNotes(String p_notes) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Thread getParentThread() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isDue() {
				return isDue;
			}

			@Override
			public void addComponentChangeListener(ComponentChangeListener p_listener) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Date getModifiedDate() {
				throw new UnsupportedOperationException();
			}

			@Override
			public List<Component> getHierarchy() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Component duplicate(boolean p_addCopyText) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Component component() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
