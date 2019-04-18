package shaunaksharma.app.quotesoverdogs;

public class QuoteResponseContent
{
    public String getQuoteText() {
        return quoteText;
    }

    public String getQuoteAuthor() {

        if(quoteAuthor.length() < 1) {return "Anonymous"; }
        return quoteAuthor;
    }

    String quoteText;
    String quoteAuthor;
}
