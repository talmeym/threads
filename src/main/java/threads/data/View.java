package threads.data;


import static threads.util.DateUtil.isBefore7DaysFromNow;

public enum View {
    DUE {
        @Override
        boolean accept(HasDueDate p_hasDueDate) {
            return p_hasDueDate.isDue();
        }
    },
    SEVENDAYS {
        @Override
        boolean accept(HasDueDate p_hasDueDate) {
            return isBefore7DaysFromNow(p_hasDueDate.getDueDate());
        }
    },
    ALL {
        @Override
        boolean accept(HasDueDate p_hasDueDate) {
            return true;
        }
    };

    abstract boolean accept(HasDueDate p_hasDueDate);
}
