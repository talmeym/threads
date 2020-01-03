package threads.data;

import java.util.UUID;

public class AutoSortRule {
    private final String o_textToken;
    private final UUID o_threadId;
    private final Matcher o_matcher;

    public AutoSortRule(String o_textToken, UUID o_threadId, Matcher o_matcher) {
        this.o_textToken = o_textToken;
        this.o_threadId = o_threadId;
        this.o_matcher = o_matcher;
    }

    public String getTextToken() {
        return o_textToken;
    }

    public UUID getThreadId() {
        return o_threadId;
    }

    public Matcher getMatcher() {
        return o_matcher;
    }

    public enum Matcher {
        contains() {
            @Override
            public boolean matches(String p_itemText, String p_token) {
                return p_itemText.toLowerCase().contains(p_token.toLowerCase());
            }
        };

        public abstract boolean matches(String p_itemText, String p_token);
    }
}
