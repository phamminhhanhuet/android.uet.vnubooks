package com.uet.android.mouspad.Model;

public class NotificationSetup {
    private boolean inbox;
    private boolean comment;
    private boolean library;
    private boolean vote;
    private boolean new_follower;
    private boolean updates_from_following;
    private boolean message_board;

    public NotificationSetup() {
    }

    public NotificationSetup(boolean inbox, boolean comment, boolean library, boolean vote, boolean new_follower, boolean updates_from_following, boolean message_board) {
        this.inbox = inbox;
        this.comment = comment;
        this.library = library;
        this.vote = vote;
        this.new_follower = new_follower;
        this.updates_from_following = updates_from_following;
        this.message_board = message_board;
    }

    public boolean isInbox() {
        return inbox;
    }

    public void setInbox(boolean inbox) {
        this.inbox = inbox;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isLibrary() {
        return library;
    }

    public void setLibrary(boolean library) {
        this.library = library;
    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public boolean isNew_follower() {
        return new_follower;
    }

    public void setNew_follower(boolean new_follower) {
        this.new_follower = new_follower;
    }

    public boolean isUpdates_from_following() {
        return updates_from_following;
    }

    public void setUpdates_from_following(boolean updates_from_following) {
        this.updates_from_following = updates_from_following;
    }

    public boolean isMessage_board() {
        return message_board;
    }

    public void setMessage_board(boolean message_board) {
        this.message_board = message_board;
    }
}
