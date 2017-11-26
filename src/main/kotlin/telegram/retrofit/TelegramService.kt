package telegram.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface to represent Telegram API to generate Retrofit2 requests.
 * Created on 11/26/17.
 *
 * @author Evgenii Kanivets
 */
interface TelegramService {

    @GET("sendMessage")
    fun sendMessage(@Query("chat_id") chatId: Long, @Query("text") text: String): Call<ResponseBody>

}
