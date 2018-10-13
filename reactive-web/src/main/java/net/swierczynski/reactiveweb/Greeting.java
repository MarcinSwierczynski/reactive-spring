package net.swierczynski.reactiveweb;

/**
 * Date: 12/10/2018 at 11:57
 *
 * @author Marcin Świerczyński
 */
public class Greeting {
    private final String text;

    public Greeting(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
