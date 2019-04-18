package shaunaksharma.app.quotesoverdogs;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ImageClient
{
    @GET("woof.json")
    Call<ImageResponseContent> getImage();
}
