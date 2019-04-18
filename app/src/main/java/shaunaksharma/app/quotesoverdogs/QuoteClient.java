package shaunaksharma.app.quotesoverdogs;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteClient
{
    @GET("?method=getQuote&lang=en&format=json")
    Call<QuoteResponseContent> getQuote();
}
