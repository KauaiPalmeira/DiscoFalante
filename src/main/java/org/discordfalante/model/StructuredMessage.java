package org.discordfalante.model;

public class StructuredMessage {

    private final String authorName;
    private final String content;
    private final String repliedToAuthor;

    public StructuredMessage(String authorName, String content, String repliedToAuthor) {
        this.authorName = authorName;
        this.content = content;
        this.repliedToAuthor = repliedToAuthor;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public String getRepliedToAuthor() {
        return repliedToAuthor;
    }
}